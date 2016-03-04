package spice.socket.session

import java.nio.ByteBuffer

/**
  * most of them is decode and encode.
  * All of them use Big-Endian for unification
  */
package object implicitpkg {
  implicit def toByteBufferEx(byteBuffer: ByteBuffer): ByteBufferEx = new ByteBufferEx(byteBuffer)
  implicit def IntToByteBuffer(bf: Int) = new IntEx(bf)
  /**
    * use Upper word at beginning make it as a dependence function
    */
  implicit def StringToByteBuffer(string: String): ByteBuffer = ByteBufferEx.stringToByteBuffer(string)
  implicit def StringToByteArray(string: String): Array[Byte] = ByteBufferEx.stringToByteArray(string)
}
