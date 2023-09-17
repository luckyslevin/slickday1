import java.util.concurrent.Executors

import scala.concurrent._
import scala.util.Failure
import scala.util.Success

import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile

object Main extends App {
  implicit val executor: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))
  val account = Account("Account@gmail.com")
  val profile =
    Profile(account.id, Some("firstName"), Some("lastName"), '♀')
  def run(dal: DAL with ProfileComponent) = {
    val db = dal.db
    println("Running test against " + dal.profile)
    val dbio = for {
      _ <- dal.createTable
      _ <- dal.createAccount(account)
      _ <- dal.createProfile(Profile(
        account.id,
        Some("firstName"),
        Some("lastName"),
        '♀'
      ))
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
        println(Thread.currentThread().getName())
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
    case Success(value) => aaa(effect.accountService)
    case Failure(exception) => println(exception)
  }

  def aaa(services: effect.Services) {
    val db = services.db
    db.run(services.service.find(account.id)).map { a =>
      println(account.id, a, "=" * 30)
    }.onComplete {
      case Success(value) =>
      case Failure(exception) => println(exception)
    }
  }

}
