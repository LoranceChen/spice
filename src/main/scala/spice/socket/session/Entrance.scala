package spice.socket.session

import java.net.InetSocketAddress
import java.nio.channels.{AsynchronousSocketChannel, CompletionHandler, AsynchronousServerSocketChannel}
import rx.lang
import rx.lang.scala.{Subscriber, Observable}
import spice.socket.handle.ConnectionHandler

import scala.annotation.tailrec
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

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
  //test
//  val callback = new ConnectionHandler()//as CompletionHandler
//
//  server.accept(server, callback)
    //need a stream to get all connection
//    server.accept(server, new ConnectionHandler())
    val o = socketObservable(server)
//    o.subscribe(s => s.accept(s, new ConnectionHandler))

    o.subscribe(s => println("a ha,a client come here, what should I do ... "))
//    o.subscribe(s => println("a ha,2a client come here, what should I do ... "))
//    o.subscribe(s => println("a ha,3a client come here, what should I do ... "))
    o.doOnCompleted(println("all connect completed"))
//    o.doOnNext{ a =>
//      println(a.isRead)
//    }

  }

  //connection observable - when connection occurred emit it.
  //this action as ObservablesHot
  //actually is a cold Obs(great then hot)
  def socketObservable(server: AsynchronousServerSocketChannel): Observable[AsynchronousSocketChannel] = {

    val f = connection(server)

    //todo what's the difference between apply and create?
    Observable.apply[AsynchronousSocketChannel]( { s =>
      //todo its not a tailrec Q: 1.How to transform to a tailrec 2. does its a matter if NOT transform to a tailrec?
      def connectForever(f: Future[AsynchronousSocketChannel]): Unit = {
        f.onComplete {
          case Failure(e) =>
            s.onError(e)
          case Success(c) =>
            s.onNext(c)
            val nextConn = connection(server)
            connectForever(nextConn)
        }
      }
      connectForever(f)
      Subscriber()
    }).doOnCompleted{
      println("doOnCompleted")
    }
  }

  // when completed continue accept next one
  // a way
  //connection forever
  def connection(server: AsynchronousServerSocketChannel) = {
    //bridge of future connection
    val p = Promise[AsynchronousSocketChannel]
    val callback = new CompletionHandler[AsynchronousSocketChannel, AsynchronousServerSocketChannel] {
      override def completed(result: AsynchronousSocketChannel, attachment: AsynchronousServerSocketChannel): Unit = {
        println("connect - success")

        p.trySuccess(result)
      }

      override def failed(exc: Throwable, attachment: AsynchronousServerSocketChannel): Unit = {
        println("connect - failed")
        p.tryFailure(exc)
      }
    }

    server.accept(server, callback)
    p.future
  }

}
