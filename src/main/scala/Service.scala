import java.util.UUID

import slick.jdbc.{ JdbcBackend, JdbcProfile }
import slick.jdbc.H2Profile

object services {
  trait AccountService {
    def accountService: AccountService.Service
  }

  object AccountService {
    import slick.dbio.DBIO
    trait Service {
      def create(account: Account): DBIO[Int]
      def find(id: UUID): DBIO[Option[Account]]
      def get(email: String): DBIO[Seq[Account]]
    }
    trait LiveService extends Service { this: ProfileContext with AccountRepo =>
      def create(account: Account): DBIO[Int] = accounts.create(account)
      def find(id: UUID): DBIO[Option[Account]] = accounts.find(id)

      def get(email: String): DBIO[Seq[Account]] = accounts.get(email)
    }
    trait Live extends AccountService { self: ProfileContext =>
      override final lazy val accountService = new LiveService with AccountRepo with ProfileContext {
        override val profile: JdbcProfile = self.profile
      }
    }
  }

  trait ProfileService {
    def profileService: ProfileService.Service
  }

  object ProfileService {

    trait Service {
      import slick.dbio.DBIO
      def create(account: Profile): DBIO[Int]
      def find(id: UUID): DBIO[Option[Profile]]
      def find(email: String): DBIO[Option[Profile]]
      def findNameByEmail(email: String): DBIO[Option[(Option[String], Option[String])]]
      def get(char: Char): DBIO[Seq[Profile]]
      def getAccountProfiles: DBIO[Seq[(Account, Profile)]]
    }
    trait LiveService extends Service { this: ProfileContext with AccountRepo with ProfileRepo =>
      import profile.api._
      def create(profile: Profile): DBIO[Int] = profiles.create(profile)
      def find(id: UUID): DBIO[Option[Profile]] = profiles.find(id)
      def find(email: String): DBIO[Option[Profile]] = profiles.find(email)
      def findNameByEmail(email: String): DBIO[Option[(Option[String], Option[String])]] =
        profiles.get(email).map(r => (r.firstName, r.lastName)).result.headOption
      def get(gender: Char): DBIO[Seq[Profile]] = profiles.get(gender)
      def getAccountProfiles: DBIO[Seq[(Account, Profile)]] = profiles.withAccounts

    }
    trait Live extends ProfileService { self: ProfileContext =>
      override lazy val profileService = new LiveService with AccountRepo with ProfileRepo with ProfileContext {
        override val profile: JdbcProfile = self.profile
      }
    }
  }
}

object Boundary {
  import services._
  type Services = AccountService with ProfileService with ProfileContext with DatabaseContext
  object Make extends AccountService.Live with ProfileService.Live with ProfileContext with DatabaseContext {

    override val profile = H2Profile
    import profile.api._
    override val db = Database.forConfig("user-db_conf")
  }
}
