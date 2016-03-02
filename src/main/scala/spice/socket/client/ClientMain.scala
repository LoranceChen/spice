package spice.socket.client

import java.net.{InetSocketAddress, SocketAddress}
import java.nio.ByteBuffer
import java.nio.channels.{CompletionHandler, AsynchronousSocketChannel}

import spice.javasocket.SocketHelper

/**
  *
  */
object ClientMain extends App {
  val channel: AsynchronousSocketChannel = AsynchronousSocketChannel.open
  val serverAddr: SocketAddress = new InetSocketAddress("localhost", 10001)
  def getCompletionHandler = new CompletionHandler[Void, Int] {
    override def completed(result: Void, attachment: Int): Unit = {
      val bs = "hello server!".getBytes
      val attach = new Attachment(channel, ByteBuffer.wrap(bs), null, false)
      channel.write(attach.buffer, attach, new CompletionHandler[Integer, Attachment]{
        override def completed(result: Integer, attachment: Attachment): Unit = {
          println(s"attachment - $attachment")
        }

        override def failed(exc: Throwable, attachment: Attachment): Unit = {
          println(s"CompletionHandler - failed")
        }
      })
    }

    override def failed(exc: Throwable, attachment: Int): Unit = {
      println(exc.toString)
    }
  }

  val resultchannel = channel.connect(serverAddr, 1, getCompletionHandler)

  SocketHelper.log("channel: AsynchronousSocketChannel - LocalAddress - " + channel.getLocalAddress)
  SocketHelper.log("channel: AsynchronousSocketChannel - RemoteAddress - " + channel.getRemoteAddress)

  Thread.currentThread.join
}

class Attachment(
  var channel: AsynchronousSocketChannel,
  var buffer: ByteBuffer,
  var mainThread: Thread,
  var isRead: Boolean = false)

object Transfer {
//  def write(context: BasicProtocol) = {

//    write(con)
//  }
}

object WeChatTooTaskClient {
//  val channel: AsynchronousSocketChannel =
}