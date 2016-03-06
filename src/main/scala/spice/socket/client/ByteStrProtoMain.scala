package spice.socket.client

import java.net.{InetSocketAddress, SocketAddress}
import java.nio.ByteBuffer
import java.nio.channels.{CompletionHandler, AsynchronousSocketChannel}

import spice.javasocket.SocketHelper

/**
  *
  */
object ByteStrProtoMain extends App {
  val channel: AsynchronousSocketChannel = AsynchronousSocketChannel.open
  val serverAddr: SocketAddress = new InetSocketAddress("localhost", 10001)
  def getCompletionHandler = new CompletionHandler[Void, Int] {
    override def completed(result: Void, attachment: Int): Unit = {
      val proto = new Array[Byte](2)
      proto(0) = 1.toByte
      proto(1) = 2.toByte
      val bs = "hello server!".getBytes
      println(s"bs - ${bs.length}")
      val attach = new Attachment(channel, ByteBuffer.wrap(proto ++ bs), null, false)
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
