package spice.socket.presentation

import java.nio.charset.StandardCharsets
import java.nio.ByteBuffer

/**
  * Give up this way beacause ByteBuffer has contains most of them except String(I have fill String)
  * data saved place
  * 1. exchange data between Byte and below type
  * 2. deserialize data, support : (socket meta data is byte)
  *      Network Type    Volume        Scala Type   NO(值 - 空间大小 )
  *   1) boolean         1-byte         Boolean     1 + 1byte
  *   2) string          utf-8          String      2 + length: Int = 1byte + 4byte + string所占的byte空间(涉及编码格式)(最大可表示2^32个utf-8字符长度)
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
  * 4. besides,ensure a ByteData able to complete write to ByteBuffer rather then a half of them
  */
//class ByteData {
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

//  abstract class NO{ def volume: Long}
//  case class Bool() extends NO {
//    override val volume = 1L
//  }
//
//  case class String() extends NO {
//    def volume = {
//      netData.getLong(0)
//    }//queue.dequeue(64).toLong
//  }

//  private var queue = new mutable.Queue[Byte]()
//  def read[T] = {
//    val queue.dequeue
//    netData.getFloat()
//  }

//  def intToByte(byteBuffer: ByteBuffer) = {
//    val newBytes = new Array[Byte](1 + 4)
//    try byteBuffer.put(newBytes) catch {
//      case e: BufferOverflowException =>
////        val bigByteBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2)
////        bigByteBuffer.put(newBytes)
//        //todo
//        e.printStackTrace()
//    }
//  }
//
//  def byteToInt(byteBuffer: ByteBuffer) = {
//    byteBuffer.flip()
//    byteBuffer.get
//  }

  //two style of transform data
  //1. data as parameter: avoid data copy, quick speed and save memory
  //String must be according length
//  def stringToByte(src: String, dst: ByteBuffer) = {
//
//  }

  //my choice
  //2. return data: flexible, concurrent, easy to control
//  def stringToByte(src: String): ByteBuffer = {
//    val bytes = src.getBytes(StandardCharsets.UTF_8)
//    val lengthData = bytes.length
//    val bytesLength = new Array[Byte](4)
//    //Big Endian
//    bytesLength(0) = ((lengthData >> 24) & 0xFF).toByte
//    bytesLength(1) = ((lengthData >> 16) & 0xFF).toByte
//    bytesLength(2) = ((lengthData >> 8) & 0xFF).toByte
//    bytesLength(3) = (lengthData & 0xFF).toByte
//    val result = bytesLength ++ bytes
//
//    ByteBuffer.wrap(result, 0, result.length)
//  }
//}

