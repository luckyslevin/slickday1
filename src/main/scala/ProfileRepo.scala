import java.util.UUID

trait ProfileRepo { this: AccountRepo with ProfileContext =>
  import profile.api._
  private val accountsTable = accounts
  final class Profiles(tag: Tag) extends Table[Profile](tag, "PROFILES") {
    def accountId = column[UUID]("ID", O.PrimaryKey)
    def firstName = column[Option[String]]("FIRST_NAME", O.Length(255))
    def lastName = column[Option[String]]("LAST_NAME", O.Length(255))
    def gender = column[Char]("GENDER")
    def accounts = foreignKey("accounts_fk_id", accountId, accountsTable)(_.id)
    def * = (accountId, firstName, lastName, gender).mapTo[Profile]
  }

  object profiles extends TableQuery(new Profiles(_)) {
    @inline def create(profile: Profile) = this += profile
    @inline def find(accountId: UUID) = this.filter(_.accountId === accountId).result.headOption
    @inline def find(email: String) = get(email).result.headOption
    @inline def get(gender: Char) = this.filter(_.gender === gender).result
    @inline def get(email: String) = for {
      profile <- this
      account <- accounts if account.email === email && account.id === profile.accountId
    } yield profile
    @inline def withAccounts = (for {
      profile <- profiles
      account <- profile.accounts
    } yield (account, profile)).result
  }
}
