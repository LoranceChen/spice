package spice.socket.protocal

import java.nio.ByteBuffer

/**
  * interface of network bytes exchange with T protocol
  */
trait BasicProtocol {
  def enCode(dst: ByteBuffer): Unit
  def deCode(src: ByteBuffer): BasicProtocol
}

/*
 * example: define Login Protocol
 */
final class Login(account: String, password: String) extends BasicProtocol{

  def enCode(dst: ByteBuffer) = {
    //make password to bytes with String protocol
  }

  def deCode(bf: ByteBuffer): Login = {
    //get two string with length first
    bf.get(1)
    new Login("123", "admin")
  }
}