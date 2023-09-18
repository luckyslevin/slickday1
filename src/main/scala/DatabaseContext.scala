import slick.jdbc.{ JdbcBackend, JdbcProfile }

/** The slice of the cake which provides the Slick profile */
trait DatabaseContext {
  val db: JdbcBackend#Database
}
trait ProfileContext {
  val profile: JdbcProfile
}
