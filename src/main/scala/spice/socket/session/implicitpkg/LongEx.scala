package spice.socket.session.implicitpkg

/**
  *
  */
class LongEx(long: Long) {
  //enCode byte to Array[Byte]
  def getByteArray = {
    val bytes = new Array[Byte](8)
    //Big Endian
    bytes(0) = ((long >> 56) & 0xFF).toByte
    bytes(1) = ((long >> 48) & 0xFF).toByte
    bytes(2) = ((long >> 40) & 0xFF).toByte
    bytes(3) = ((long >> 32) & 0xFF).toByte
    bytes(4) = ((long >> 24) & 0xFF).toByte
    bytes(5) = ((long >> 16) & 0xFF).toByte
    bytes(6) = ((long >> 8) & 0xFF).toByte
    bytes(7) = (long & 0xFF).toByte
    bytes
  }
}
