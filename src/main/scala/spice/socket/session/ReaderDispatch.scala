package spice.socket.session

import java.nio.ByteBuffer
import spice.socket.session

/**
  * one map to for every socket
  */
class ReaderDispatch(private var tmpProto: PaddingProto,maxLength: Int) {//extends Subject[]{
  def this(maxLength: Int) {
    this(PaddingProto(None, None, session.EmptyByteBuffer), maxLength)
  }

  def this() {
    this(PaddingProto(None, None, session.EmptyByteBuffer), Int.MaxValue)
  }


  def receive(src: ByteBuffer) = {
    src.flip()
    val rst = receiveHelper(src, None)
    src.clear()
    rst
  }
  /**
    * read all ByteBuffer form src, put those data to Observer or cache to dst
    * handle src ByteBuffer from network
    *
    * @param src
    * @return None, if uuidOpt or lengthOpt is None
    */
  private def receiveHelper(src: ByteBuffer, completes: Option[Vector[CompletedProto]]): Option[Vector[CompletedProto]] = {
    def tryGetByte(bf: ByteBuffer) = if(bf.limit() > 1) Some(bf.get()) else None

    tmpProto match {
      case PaddingProto(None, _, _) =>
        val uuidOpt = tryGetByte(src)
        val lengthOpt = uuidOpt.flatMap{uuid => tryGetByte(src)}
        val loadOpt = lengthOpt.flatMap { length =>
          if(src.remaining() < length) {
            val newBf = ByteBuffer.allocate(length)
            tmpProto = PaddingProto(uuidOpt, lengthOpt, newBf.put(src))
            completes
          } else {
            tmpProto = PaddingProto(None,None, session.EmptyByteBuffer)
            val newBf = src.get(new Array[Byte](length), 0, length)
            val completed = CompletedProto(uuidOpt.get, lengthOpt.get, newBf)
            if (completes.isEmpty) receiveHelper(src, Some(Vector(completed)))
            else receiveHelper(src, completes.map(_ :+ completed))
          }
        }
        loadOpt
      case PaddingProto(Some(uuid), None, tmpBf) =>
        val lengthOpt = tryGetByte(src)
        val loadOpt = lengthOpt.flatMap { length =>
          if (src.remaining() < length) {
            val newBf = ByteBuffer.allocate(length)
            tmpProto = PaddingProto(Some(uuid), lengthOpt, newBf.put(src))
            completes
          } else {
            tmpProto = PaddingProto(None,None, session.EmptyByteBuffer)
            val newBf = src.get(new Array[Byte](length), 0, length)
            val completed = CompletedProto(uuid, lengthOpt.get, newBf)
            if (completes.isEmpty) receiveHelper(src, Some(Vector(completed)))
            else receiveHelper(src, completes.map(_ :+ completed))
          }
        }
        loadOpt
      case PaddingProto(Some(uuid), Some(length), padding) =>
        if (padding.remaining() + src.remaining() < length) {
          tmpProto = PaddingProto(Some(uuid), Some(length), padding.put(src))
          completes
        } else {
          tmpProto = PaddingProto(None, None, session.EmptyByteBuffer)
          val needLength =  length - padding.remaining()
          val left = src.get(new Array[Byte](length), 0, needLength)
          val completed = CompletedProto(uuid, length, padding.put(left))
          if (completes.isEmpty) receiveHelper(src, Some(Vector(completed)))
          else receiveHelper(src, completes.map(_ :+ completed))
        }
    }
  }
}

//todo load field to Buffer
abstract class BufferedProto
case class PaddingProto(uuidOpt: Option[Byte],lengthOpt: Option[Byte],loading: ByteBuffer)
//object PaddingProto {
//  def apply(uuidOpt: Option[Byte],lengthOpt: Option[Byte],loading: ByteBuffer) = new PaddingProto(uuidOpt,lengthOpt,loading)
//  def unapply(paddingProto: PaddingProto): Option[(Option[Byte], Option[Byte], ByteBuffer)] = {
//    paddingProto.
//  }
//}
case class CompletedProto(var uuid: Byte,var length: Byte,var loaded: ByteBuffer) extends BufferedProto//PaddingProto(Some(uuid), Some(length), loaded)
