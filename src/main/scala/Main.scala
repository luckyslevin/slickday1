import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database

object Main extends App {
  def run(dal: DAL, db: Database) = {
    println("Running test against " + dal.profile)
    val dbio = for {
      _ <- db.run(dal.createTable)
      val account = Account("Account@gmail.com")
      val profile = Profile(account.id, Some("firstName"), Some("lastName"), '♀')
      _ <- db.run(dal.createAccount(account))
      _ <- db.run(dal.createProfile(Profile(account.id, Some("firstName"), Some("lastName"), '♀')))
      accountOpt <- db.run(dal.findAccount(account.id))
      profileOpt <- db.run(dal.findProfile(account.id))
      genders <- db.run(dal.getProfileByGender(profile.gender))
      a = dal.getAccountProfiles.statements.head
      accountWithProfiles <- db.run(dal.getAccountProfiles)
      profileByEmail <- db.run(dal.findProfileByEmail(account.email).result.headOption)
      accountsByEmail <- db.run(dal.getAccountsByEmail("Acc"))
      `firstName&LastName` <- db.run(dal.`findFirstName&AndLastName`(account.email))
    } yield {
      println("=" * 100)
      println("Find account: ", accountOpt)
      println("=" * 100)
      println("Find profile: ", profileOpt)
      println("=" * 100)
      println("Get profile by gender: ", genders)
      println("=" * 100)
      println("Get accounts with profiles: ", accountWithProfiles)
      println("=" * 100)
      println("Find profile by email: ", profileByEmail)
      println("=" * 100)
      println("GET accounts by email: ", accountsByEmail)
      println("=" * 100)
      println("Find firstname and lastname by email: ", `firstName&LastName`)
    }
    dbio
  }
  run(new DAL(H2Profile), Database.forConfig("user-db_conf")).onComplete {
    case Success(value)     =>
    case Failure(exception) => println(exception)
  }
}
