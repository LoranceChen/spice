package spice.socket.session

import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousServerSocketChannel, AsynchronousSocketChannel}

import spice.socket.presentation.{DeCoding, EnCoding}

/**
  * transfer those data into connected socket operator
  *
  * @param server invocation a new socket accept after accepted a client success
  * @param client the socket accepted/connected
  * @param buffer data channel for communication
  * @param status it is a order of how to operator <code>buffer</code>, its used when WriteReadHandler call back which point
  *               we should decide read or write the <code>buffer</code>.
  */
case class Attachment (
  var server: AsynchronousServerSocketChannel,
  var client: AsynchronousSocketChannel,
  var buffer: ByteBuffer,
  var status: BufferState.Value = BufferState.Spare) {

  /**
    * default constructor used at beginning as a reference wait fill info.
    */
  def this() = {
    this(null, null, null, BufferState.Spare)
  }
}
object BufferState extends Enumeration{
  val OnRead = Value(1, "read")
  val OnWrite = Value(2, "write")
  val Spare = Value(3, "spare")

}

/**
  * 1. used to save data from network and record protocol flag state
  * 2. every socket map to one batch of this protocol
  * 3. these class data access always in single thread(used at read call back once by once),so it doesn't matter use var
  * @param byteBuffer
  * @param readLeftProto last uncompleted protocol
  * @param asynchronousSocketChannel
  */
class ReadAttach( val byteBuffer: ByteBuffer, val readLeftProto: ReadLeftProto, val asynchronousSocketChannel: AsynchronousSocketChannel )
case class ReadLeftProto(var protoId: Option[Long], var length: Option[Long], var lastTo: Int, var lastNeed: Int)

class WriteAttach( byteBuffer: ByteBuffer, asynchronousSocketChannel: AsynchronousSocketChannel )

class ConnectionAttach