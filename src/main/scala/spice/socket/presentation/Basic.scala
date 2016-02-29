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
class Login(account: String, password: String) extends BasicProtocol{
  private val byteLength = {

    account.length + password.length + 2
  }//

  def enCode(dst: ByteBuffer) = {
    //make password to bytes with String protocol

  }

  def deCode(bf: ByteBuffer): Login = {
    //verify does bf able to write all of capcity
    val bfLeft = bf.capacity() - bf.limit()
    if (byteLength <= bfLeft)
    //get two string with length first
    bf.get(1)
    new Login("123", "admin")
  }
}