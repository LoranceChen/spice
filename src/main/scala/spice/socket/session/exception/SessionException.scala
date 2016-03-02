package spice.socket.session.exception

/**
  * UUID use 0x1000000L ~ 0x1000,00ffL
  */
class SessionException(msg: String) extends Throwable(msg)

class ReadByteBufferException(msg: String,val uuid: Long) extends SessionException(msg)

class UUIDNotEnoughException(msg: String = "ByteBuffer NOT contains UUID Length(8Byte) yet") extends ReadByteBufferException(msg, 0x10000001L)
class ResultNegativeException(msg: String = "CompletedHandler return -1") extends ReadByteBufferException(msg, 0x10000002L)
