package spice.socket.protocal

import java.nio.ByteBuffer

/**
  * interface of network bytes exchange with T protocol
  */
trait BasicProtocol[T] {
  def enCode: Unit
  def deCode(bf: ByteBuffer): T
}

/*
 * example: define Login Protocol
 */
final class Login(account: String, password: String) extends BasicProtocol[Login]{

  def enCode: Unit = {
    //make password to bytes with String protocol
  }

  def deCode(bf: ByteBuffer): Login = {
    //get two string with length first
    bf.get(1)
    new Login("123", "admin")
  }
}