import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile

object Main extends App {
  def run(dal: DAL with ProfileComponent) = {
    val db = dal.db
    println("Running test against " + dal.profile)

    val dbio = for {
      _ <- dal.createTable
      val account = Account("Account@gmail.com")
      val profile = Profile(account.id, Some("firstName"), Some("lastName"), '♀')
      _ <- dal.createAccount(account)
      _ <- dal.createProfile(Profile(account.id, Some("firstName"), Some("lastName"), '♀'))
      accountOpt <- dal.findAccount(account.id)
      profileOpt <- dal.findProfile(account.id)
      genders <- dal.getProfileByGender(profile.gender)
      accountWithProfiles <- dal.getAccountProfiles
      profileByEmail <- dal.findProfileByEmail(account.email).result.headOption
      accountsByEmail <- dal.getAccountsByEmail("Acc")
      `firstName&LastName` <- dal.`findFirstName&AndLastName`(account.email)
    } yield (accountOpt, profileOpt, genders, accountWithProfiles, profileByEmail, accountsByEmail, `firstName&LastName`)
    db.run(dbio).map {
      case (
            accountOpt,
            profileOpt,
            genders,
            accountWithProfiles,
            profileByEmail,
            accountsByEmail,
            firstLastName
          ) =>
        println("=" * 190)
        println("Find account: ", accountOpt)
        println("=" * 190)
        println("Find profile: ", profileOpt)
        println("=" * 190)
        println("Get profile by gender: ", genders)
        println("=" * 190)
        println("Get accounts with profiles: ", accountWithProfiles)
        println("=" * 190)
        println("Find profile by email: ", profileByEmail)
        println("=" * 190)
        println("GET accounts by email: ", accountsByEmail)
        println("=" * 190)
        println("Find firstname and lastname by email: ", firstLastName)
    }
  }

  run(new DAL with ProfileComponent {
    override val profile: JdbcProfile = H2Profile
    override val db = Database.forConfig("user-db_conf")
  }).onComplete {
    case Success(value)     =>
    case Failure(exception) => println(exception)
  }
}
