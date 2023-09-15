import java.util.UUID

case class Account(id: UUID, email: String)

object Account {
  val tupled = (apply: (UUID, String) => Account).tupled
  def apply(email: String): Account = apply(UUID.randomUUID(), email)
}
