import java.util.UUID

trait AccountRepo { this: ProfileComponent =>
  import profile.api._

  final class Accounts(tag: Tag) extends Table[Account](tag, "ACCOUNTS") {
    def id = column[UUID]("ID", O.PrimaryKey)
    def email = column[String]("EMAIL", O.Length(255))
    def * = (id, email).mapTo[Account]
  }

  object accounts extends TableQuery(new Accounts(_)) {
    @inline def create(account: Account) = this += account
    @inline def find(id: UUID) = this.filter(_.id === id).result.headOption
    @inline def get(email: String) = this.filter(_.email like s"%$email%").result
  }
}
