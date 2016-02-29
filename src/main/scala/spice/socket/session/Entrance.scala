package spice.socket.session

import java.net.InetSocketAddress
import java.nio.channels.{AsynchronousSocketChannel, CompletionHandler, AsynchronousServerSocketChannel}
import rx.lang.scala.{Subscriber, Observable}
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

/**
  * begin server socket listen
  */
class Entrance(host: String, port: Int) {
  /**
    * listen connection and emit every connection event.
    */
  def start = {
    val server: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open
    val sAddr: InetSocketAddress = new InetSocketAddress(host, port)
    server.bind(sAddr)
    println(s"Server is listening at - $sAddr")

    socketObservable(server)
  }

  /**
    *
    */
//  def send()

  //connection observable - when connection occurred emit it.
  //this action as ObservablesHot
  //actually is a cold Obs(great then hot)
  //hot - if accept a connection new a Observable
  private def socketObservable(server: AsynchronousServerSocketChannel): Observable[AsynchronousSocketChannel] = {

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

  //make call back to future
  private def connection(server: AsynchronousServerSocketChannel) = {
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

object Entrance {
  def apply(host: String, port: Int) = new Entrance(host, port)
}