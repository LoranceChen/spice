package spice.socket

import java.net.{InetAddress, InetSocketAddress}
import java.nio.ByteBuffer
import java.nio.channels.{SocketChannel, SelectionKey, ServerSocketChannel, Selector}

/**
  *
  */
object NonBlockingServer extends App {
  val selector = Selector.open()
  val ssChannel = ServerSocketChannel.open()
  ssChannel.configureBlocking(false)

  val hostIPAddress = InetAddress.getByName("localhost")
  val port = 19000

  ssChannel.socket().bind(new InetSocketAddress(hostIPAddress, port))
  ssChannel.register(selector, SelectionKey.OP_ACCEPT)

  while (true) {
    if (selector.select() <= 0) {
      val sKeys = selector.selectedKeys()
      processReadySet(sKeys)
    }
  }

  def processReadySet(readySet: java.util.Set[SelectionKey]) = {
    val iterator = readySet.iterator
    while (iterator.hasNext) {
      val key = iterator.next()
      iterator.remove()
      if (key.isAcceptable()) {
        val ssChannel = key.channel().asInstanceOf[ServerSocketChannel]
        val sChannel = ssChannel.accept()
        sChannel.configureBlocking(false);
        sChannel.register(key.selector(), SelectionKey.OP_READ);
      }
      if (key.isReadable()) {
        val msg = processRead(key);
        if (msg.length() > 0) {
          val sChannel = key.channel().asInstanceOf[SocketChannel];
          val buffer = ByteBuffer.wrap(msg.getBytes());
          sChannel.write(buffer);
        }
      }
    }
  }

  def processRead(key: SelectionKey) = {
    val sChannel = key.channel().asInstanceOf[SocketChannel]
    val buffer = ByteBuffer.allocate(1024);
    val bytesCount = sChannel.read(buffer);
    if (bytesCount > 0) {
      buffer.flip();
      new String(buffer.array());
    }
    "NoMessage";
  }
}

