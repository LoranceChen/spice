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
  protected val UUID: Long

  def overload: Long// length of the body, useful when
  def enCode: ByteBuffer
}

trait DeCoding[Refer <: EnCoding] {
  def deCode(bf: ByteBuffer): Refer
}

object NonEnCoding extends EnCoding {
  val UUID = 0x10000100L
  def overload: Long = 0L
  def enCode = session.NonByteBuffer
}

object SearchProto {
  val protocol: Map[Long, Class[_ <: EnCoding]] = Map(1L -> classOf[Login])//todo register the map info with each EnCoding protocol
  def byUUID(uuid: Long) = {
    protocol.get(uuid)
  }
}
