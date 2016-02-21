package spice.language.pattern_match_with_extrator

/**
  *
  */
object PatternMatchAndExtractorMain extends App {
  trait User {
    def name: String
  }
  class FreeUser(val name: String) extends User
  class PremiumUser(val name: String) extends User

  object FreeUser {
    def unapply(user: FreeUser): Option[String] = Some(user.name)
  }
  object PremiumUser {
    def unapply(user: PremiumUser): Option[String] = Some(user.name)
  }

  val user: User = new PremiumUser("Daniel")
  val rst = user match {
    case FreeUser(name) => "Hello " + name
    case PremiumUser(name) => "Welcome back, dear " + name
  }
  println(rst)
}
