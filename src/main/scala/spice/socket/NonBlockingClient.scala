package spice.socket

import java.io.{BufferedReader, InputStreamReader}
import java.net.{InetSocketAddress, InetAddress}
import java.nio.ByteBuffer
import java.nio.channels.{Selector, SocketChannel, SelectionKey}
import java.nio.charset.{CharsetDecoder, Charset}

import scala.annotation.tailrec

/**
  *
  */
object NonBlockingClient {
  var userInputReader: BufferedReader = null;

  def processReadySet(readySet: java.util.Set[SelectionKey]): Boolean = {
    val iterator = readySet.iterator();
    while (iterator.hasNext()) {
      val key = iterator.next()
      iterator.remove();
      if (key.isConnectable()) {
        val connected = processConnect(key);
        if (!connected) {
          return true; // Exit
        }
      }
      if (key.isReadable()) {
        val msg = processRead(key);
        System.out.println("[Server]: " + msg);
      }
      if (key.isWritable()) {
        System.out.print("Please enter a message(Bye to quit):");
        val msg = userInputReader.readLine();

        if (msg.equalsIgnoreCase("bye")) {
          return true; // Exit
        }
        val sChannel =  key.channel().asInstanceOf[SocketChannel];
        val buffer = ByteBuffer.wrap(msg.getBytes());
        sChannel.write(buffer);
      }
    }
    return false; // Not done yet
  }

  def processConnect(key: SelectionKey): Boolean = {
    val channel = key.channel().asInstanceOf[SocketChannel]
    while (channel.isConnectionPending()) {
      channel.finishConnect();
    }
    true;
  }

  def processRead(key: SelectionKey): String = {
    val sChannel = key.channel().asInstanceOf[SocketChannel];
    val buffer = ByteBuffer.allocate(1024);
    sChannel.read(buffer);
    buffer.flip();
    val charset = Charset.forName("UTF-8");
    val decoder = charset.newDecoder();
    val charBuffer = decoder.decode(buffer);
    val msg = charBuffer.toString();
    msg;
  }
  def main(args: Array[String]): Unit = {
    val serverIPAddress = InetAddress.getByName("localhost");
    val port = 19000;
    val serverAddress = new InetSocketAddress(serverIPAddress, port);
    val selector = Selector.open();
    val channel = SocketChannel.open();
    channel.configureBlocking(false);
    channel.connect(serverAddress);
    val operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    channel.register(selector, operations);

    userInputReader = new BufferedReader(new InputStreamReader(System.in));

    //    while (true) {
//      if (selector.select() > 0) {
//        val doneStatus = processReadySet(selector.selectedKeys());
//        if (doneStatus) {
//          break;
//        }
//      }
//    }

    @tailrec def deal(doneStatus: Boolean): Unit = {
      if (doneStatus) Unit else
      if(selector.select() > 0) {
        processReadySet(selector.selectedKeys())
        deal(true)
      } else deal(false)
    }

    deal(false)
    Thread.sleep(10000)
    channel.close()
  }
}