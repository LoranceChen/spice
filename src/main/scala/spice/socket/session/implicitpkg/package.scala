package spice.socket.session

import java.nio.ByteBuffer

/**
  *
  */
package object implicitpkg {
  implicit def toByteBufferEx(byteBuffer: ByteBuffer): ByteBufferEx = new ByteBufferEx(byteBuffer)

  /**
    * use Upper word at beginning make it as a dependence function
    */
  implicit def StringToByteBuffer(string: String): ByteBuffer = ByteBufferEx.stringToByteBuffer(string)
}
