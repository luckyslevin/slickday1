import java.util.UUID

import slick.dbio.DBIO
import slick.jdbc.{ JdbcBackend, JdbcProfile }
import slick.jdbc.H2Profile

object services {
  trait AccountService {
    def service: AccountService.Service
  }

  object AccountService {
    trait Service {
      def create(account: Account): DBIO[Int]
      def find(id: UUID): DBIO[Option[Account]]
    }
    trait LiveService extends Service { this: ProfileComponent with AccountRepo =>
      def create(account: Account): DBIO[Int] = accounts.create(account)
      def find(id: UUID): DBIO[Option[Account]] = accounts.find(id)
    }
    trait Live extends AccountService { self: ProfileComponent =>
      override final lazy val service = new LiveService with AccountRepo with ProfileComponent {
        override val profile: JdbcProfile = self.profile
        override val db: JdbcBackend#DatabaseDef = self.db
      }
    }
  }
}

object effect {
  type Services = services.AccountService with ProfileComponent
  val accountService =
    new services.AccountService.Live with ProfileComponent {
      override val profile: JdbcProfile = H2Profile
      import profile.api._
      override val db = Database.forConfig("user-db_conf")
    }
}
