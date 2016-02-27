package spice.socket.session

import java.net.InetSocketAddress
import java.nio.channels.{CompletionHandler, AsynchronousServerSocketChannel}
import rx.lang
import rx.lang.scala.{Subscriber, Observable}
import spice.socket.handle.ConnectionHandler

/**
  * 1.start a socket to listen
  * 2.accept client byte[], and print it
  * 3.send input data to client and print it
  * 4.close socket
  */
object Entrance {
//  var on = false
  def start(host: String, port: Int) = {
    val server: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open
    val sAddr: InetSocketAddress = new InetSocketAddress(host, port)
    server.bind(sAddr)
    println(s"Server is listening at - $sAddr")
//    val attach: Attachment = Attachment(server, null, null, true)

    //need a stream to get all connection
//    server.accept(server, new ConnectionHandler())
    val o = socketSource(server)
//    o.subscribe(s => s.accept(s, new ConnectionHandler))
    o.subscribe(s => println("a ha,a client come here, what should I do ... "))
    o.doOnCompleted(println("connect completed"))
//    o.doOnNext{ a =>
//      println(a.isRead)
//    }
  }
  def socketSource(server: AsynchronousServerSocketChannel): Observable[AsynchronousServerSocketChannel] = {
    Observable.apply((subscriber: Subscriber[AsynchronousServerSocketChannel]) => {
      val callback = new ConnectionHandler()//as CompletionHandler
      server.accept(server, callback)
      println("subscriber - on")
    }).doOnCompleted{
      println("doOnCompleted")
    }
  }
}
