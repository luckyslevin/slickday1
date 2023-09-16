import java.util.UUID

import slick.jdbc.{ JdbcBackend, JdbcProfile }
abstract class DAL
  extends AccountRepo with ProfileRepo { this: ProfileComponent =>
  import profile.api._
  def createTable =
    (accounts.schema ++ profiles.schema).createIfNotExists
  def createAccount(account: Account) = accounts.create(account)
  def createProfile(profile: Profile) = profiles.create(profile)
  def findAccount(id: UUID) = accounts.find(id)
  def findProfile(accountId: UUID) = profiles.find(accountId)
  def getProfileByGender(gender: Char) = profiles.get(gender)
  def findProfileByEmail(email: String) = for {
    profile <- profiles
    account <- accounts if account.email === email
  } yield profile

  def getAccountsByEmail(email: String) = accounts.get(email)

  def `findFirstName&AndLastName`(email: String) = profiles.get(email).map(r => (r.firstName, r.lastName)).result.headOption

  def getAccountProfiles = profiles.withAccounts
}
