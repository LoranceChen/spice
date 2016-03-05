package spice.socket.session.implicitpkg

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

 /**
  * extends ByteBuffer for get string by Length-Data protocol
  */
class ByteBufferEx(byteBuffer: ByteBuffer) {
  def getString = {
    val length = byteBuffer.getInt()
    val stringBytes = new Array[Byte](length)
    byteBuffer.get(stringBytes)
    new String(stringBytes, StandardCharsets.UTF_8)
  }

   /**
     * 未读部分的长度
     */
  def unReadLength = {
     byteBuffer.limit() - byteBuffer.position()
   }
}

object ByteBufferEx {
  /**
    * String has a mehtod getBytes, for avoid mislead I put bytes transform put here and use the long name
    */
  def stringToByteBuffer(src: String): ByteBuffer = {
    val result = stringToByteArray(src)
    ByteBuffer.wrap(result, 0, result.length)
  }

  def stringToByteArray(src: String): Array[Byte] = {
    val bytes = src.getBytes(StandardCharsets.UTF_8)
    val lengthData = bytes.length
    val bytesLength = new Array[Byte](4)
    //Big Endian
    bytesLength(0) = ((lengthData >> 24) & 0xFF).toByte
    bytesLength(1) = ((lengthData >> 16) & 0xFF).toByte
    bytesLength(2) = ((lengthData >> 8) & 0xFF).toByte
    bytesLength(3) = (lengthData & 0xFF).toByte
    bytesLength ++ bytes
  }
//  def copy
}