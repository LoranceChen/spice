package spice.socket.presentation

import java.nio.ByteBuffer
import spice.socket.miniApp.protocol.Login
import spice.socket.session
import spice.socket.session.implicitpkg._

/**
  * interface of network bytes exchange with T protocol
  * TODO does it could be combine in single class?
  */
trait EnCoding {
  protected val UUID: Int

  def overload: Int// length of the body, useful when
  def enCode: ByteBuffer
}

trait DeCoding[Refer <: EnCoding] {
  def deCode(bf: ByteBuffer): Refer
}

object NonEnCoding extends EnCoding {
  val UUID: Int = 0x1010
  def overload: Int = 0
  def enCode = session.EmptyByteBuffer
}

object SearchProto {
  val protocol: Map[Int, Class[_ <: EnCoding]] = Map(1 -> classOf[Login])//todo register the map info with each EnCoding protocol
  def byUUID(uuid: Int) = {
    protocol.get(uuid)
  }
}
