package spice.socket.protocal

import java.nio.ByteBuffer

import scala.collection.mutable

/**
  * data saved place
  * 1. exchange data between Byte and below type
  * 2. deserialize data, support : (socket meta data is byte)
  *      Network Type    Volume        Scala Type   NO(值 - 空间大小 )
  *   1) boolean         1-byte         Boolean     1 - 1byte
  *   2) string          utf-8          String      2 with length: Long - 1byte+8byte(最大可表示2^64个utf-8字符长度)
  *   3) Short           2-byte         Short       3 - 1byte
  *   4) Int             4-byte         Int         4 - ...
  *   5) Long            8-byte         Long        5 - ...
  *   6) float           4-byte         Float       6 - ...
  *   7) double          8-byte         Double      7 - ...
  *   PS: NO. represent how many bytes should I read form network byte[], such as 001 map 1 byte should be read to boolean.
  *   Note: Version 1.0 will support 1) 2) 3) 4) 5),its easy to deal with
  *   to 6) and 7) may be I should read IEEE to do exchange byte[] to float/double
  *   Exception: 1. NO.Error: read not defined, after that scoket should rebuild, because data was not reliable.
  * 3. Question:
  *   3.1 Need I support concurrent?
  *   3.2 Should I support concurrent?
  *
  *   At version 1.0 it won't support concurrnt for simplify.
  *   !!! Don't operate this instance in multi thread.
  *
  * 4. besides,
  */
class ByteProtocol(val netData: ByteBuffer) {
//  object NO extends Enumeration {
//    val Bool = Value(1, "boolean")
//    val String = Value(2, "string")
//    val Short = Value(3, "short")
//    val Int = Value(4, "short")
//    val Long = Value(5, "long")
//
//    def volume(value: NO.Value) = {
//      case Bool => 1
//      case String => ???
//      case Short => 16
//      case Int => 32
//      case Long => 64
//    }
//  }

  abstract class NO{ def volume: Long}
  case class Bool() extends NO {
    override val volume = 1L
  }

  case class String() extends NO {
    def volume = {
      netData.getLong(0)
    }//queue.dequeue(64).toLong
  }

  private var queue = new mutable.Queue[Byte]()
  def read[T] = {
//    val queue.dequeue
    netData.getFloat()
  }

}
