package spice.socket

import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel

/**
  * 1.start a socket to listen
  * 2.accept client byte[], and print it
  * 3.send input data to client and print it
  * 4.close socket
  */
object ServerMain extends App {
  ServerMain.main(null)
  val server = AsynchronousServerSocketChannel.open
  def start(sAddr: InetSocketAddress) = {
    server.bind(sAddr)
  }

  def accept() = {
    val f = server.accept()
//    f.fla
  }

  def send() ={

  }

  def close ={
    server.close()
  }
}
