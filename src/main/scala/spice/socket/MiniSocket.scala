package spice.socket

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{CompletionHandler, AsynchronousSocketChannel, AsynchronousServerSocketChannel}

import rx.lang.scala.Observable

import scala.concurrent.{Promise, Future}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import spice.socket.session.implicitpkg._
/**
  * for test data send subtle regular
  */
class MiniSocket {
  /**
    * listen connection and emit every connection event.
    */
  def startListen = {
    val server: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open
    val sAddr: InetSocketAddress = new InetSocketAddress("127.0.0.1", 10002)
    server.bind(sAddr)
    println(s"Server is listening at - $sAddr")

    socketObservable(server)
  }

  private def socketObservable(server: AsynchronousServerSocketChannel): Observable[AsynchronousSocketChannel] = {

    val f = connection(server)
    //    Observable.create()
    //todo what's the difference between apply and create?
    Observable.apply[AsynchronousSocketChannel]({ s =>

      //Q: its not a tailrec 1.How to transform to a tailrec 2. does its a matter if NOT transform to a tailrec?
      //A: It's not a recursion actually because future will return immediately. Next call `connectForever` occurred next onComplete, yeah, it's
      //another dependency `connectForever` method
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
    }).doOnCompleted {
      println("socket connection - doOnCompleted")
    }
  }

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

object MiniSocket extends App {
  val socket = new MiniSocket()
  val connObs = socket.startListen
  connObs.subscribe{s =>
    val bf = ByteBuffer.allocate(10)
    println(s"bf - limit= ${bf.limit()}, position=${bf.position()}, mark=${bf.mark()}")
    val fR = s.read(bf)
    fR.get()
    bf.flip()
    val string = bf.allToString
    println(s"bf - limit= ${bf.limit()}, position=${bf.position()}, mark=${bf.mark()}")

    //Conclusion:
//    val bf2 = ByteBuffer.allocate(10)
//    println(s"bf - limit= ${bf2.limit()}, position=${bf2.position()}, mark=${bf2.mark()}")
//    val fR2 = s.read(bf2)
//    fR2.get()
//    println(s"bf - limit= ${bf2.limit()}, position=${bf2.position()}, mark=${bf2.mark()}")
//    1
    bf.flip()
    //add for data
    bf.put(101.toByte)
    bf.put(101.toByte)
    bf.put(101.toByte)
    bf.put(101.toByte)
    val fR2 = s.read(bf)
    fR2.get()
    bf.flip()
    val string2 = bf.allToString
//    bf.position = 0

    val fR3 = s.read(bf)
    fR3.get()
    println(s"bf - limit= ${bf.limit()}, position=${bf.position()}, mark=${bf.mark()}")
    while(true) {
      //why get 0 when no data send to server? doesn't the async operate will call back when receive data? how it works?
      val fR3 = s.read(bf)
      fR3.get()
    }
  }
  Thread.currentThread().join()
}

object BFTEST extends App {
  val bf = ByteBuffer.allocateDirect(10)
  bf.put(1.toByte)
  bf.mark()
  bf.flip()
  bf
}