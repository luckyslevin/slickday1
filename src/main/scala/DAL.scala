import java.util.UUID

import slick.jdbc.JdbcProfile
class DAL(val profile: JdbcProfile)
  extends AccountRepo with ProfileRepo with ProfileComponent {
  import profile.api._

  def createTable =
    (accounts.schema ++ profiles.schema).createIfNotExists

  def createAccount(account: Account) = accounts += account
  def createProfile(profile: Profile) = profiles += profile
  def findAccount(id: UUID) = accounts.filter(_.id === id).result.headOption
  def findProfile(accountId: UUID) = profiles.filter(_.accountId === accountId).result.headOption
  def getProfileByGender(gender: Char) = profiles.filter(_.gender === gender).result
  def findProfileByEmail(email: String) = for {
    profile <- profiles
    account <- accounts if account.email === email
  } yield profile

  def getAccountsByEmail(email: String) = accounts.filter(_.email like s"%$email%").result

  def `findFirstName&AndLastName`(email: String) = findProfileByEmail(email).map(r => (r.firstName, r.lastName)).result.headOption

  def getAccountProfiles = (for {
    profile <- profiles
    account <- profile.accounts
  } yield (account, profile)).result
}
