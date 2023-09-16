import slick.jdbc.{ JdbcBackend, JdbcProfile }

/** The slice of the cake which provides the Slick profile */
trait ProfileComponent {
  val profile: JdbcProfile
  val db: JdbcBackend#Database
}
