package spice.socket.handle

import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousServerSocketChannel, CompletionHandler, AsynchronousSocketChannel}

import spice.socket.session.{BufferState, Entrance, Attachment}

/**
  *
  */
class ConnectionHandler extends CompletionHandler[AsynchronousSocketChannel, AsynchronousServerSocketChannel] {

  /**
    * callback by AsynchronousServerSocketChannel.accept(attach, new ConnectionHandler());
    *
    * @param client is connected client driver by AsynchronousServerSocketChannel,
    * @param attach same as attach in callback
    */
  def completed(client: AsynchronousSocketChannel, server: AsynchronousServerSocketChannel) {
    println(s"someone linked - ${client.getRemoteAddress}")

    server.accept(server, this)
    val rwHandler: ReadWriteHandler = new ReadWriteHandler()
    //TODO how to use ByteBuffer.allocateDirect? it seems not security under aio call back.
    val newAttach = Attachment(server, client,ByteBuffer.allocate(2048), BufferState.Spare)
    client.read(newAttach.buffer, newAttach, rwHandler)
  }

  def failed(e: Throwable, server: AsynchronousServerSocketChannel) {
    System.out.println("Failed to accept a  connection.")
    e.printStackTrace
  }
}