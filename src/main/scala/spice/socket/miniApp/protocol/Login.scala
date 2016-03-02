package spice.socket.miniApp.protocol

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import spice.socket.presentation.{DeCoding, EnCoding}
import spice.socket.session.implicitpkg._


/*
 * example: define Login Protocol
 */
class Login(val account: String, val password: String) extends EnCoding {
  override val UUID = ProtoEum.ACCOUNT_LOGIN

  private val uuidBA = UUID.getByteArray
  private val accountBA = StringToByteArray(account)
  private val passwordBA = StringToByteArray(password)

  override val overload: Long = accountBA.length + passwordBA.length
  override val enCode = {
    ByteBuffer.wrap(uuidBA ++ accountBA ++ passwordBA)
  }
}

object Login extends DeCoding[Login] {

  def apply(account: String, password: String) = new Login(account, password)

  /**
    * needn't decode UUID because if user know it was a Login, he must be deCoded the UUID as 1
    */
  override def deCode(bf: ByteBuffer): Login = {
    new Login(bf.getString, bf.getString)
  }
}

object UseCase extends App {
  val login = Login("admin", "12345")
  val loginEnCoded = login.enCode
  val uuid = loginEnCoded.getLong()
  val deCodeLogin = Login.deCode(loginEnCoded)
  Thread.currentThread().join()
}