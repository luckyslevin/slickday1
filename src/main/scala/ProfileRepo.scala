import java.util.UUID

trait ProfileRepo { this: AccountRepo with ProfileComponent =>
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

  object profiles extends TableQuery(new Profiles(_))
}
