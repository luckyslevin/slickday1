import java.util.UUID

case class Profile(accountId: UUID, firstName: Option[String], lastName: Option[String], gender: Char)
