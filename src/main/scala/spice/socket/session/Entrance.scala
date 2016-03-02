package spice.socket.session

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousSocketChannel, CompletionHandler, AsynchronousServerSocketChannel}
import rx.lang.scala.{Subscriber, Observable}
import spice.socket.presentation.{SearchProto, EnCoding}
import spice.socket.session.exception.{ResultNegativeException, UUIDNotEnoughException}
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

/**
  * begin server socket listen
  * Q: concurrent in which stage?
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
    * read method will occurred a EnCoding Protocol if get at least one Protocol
    * 2G for one million connection, every client occupy 2k ByteBuffer which divide into 1k read ByteBuffer and 1k write ByteBuffer.
    * Q: Does it need separate ByteBuffer for read and write?
    * A:
    * 1. assume no matter of capacity,
    * If use one ByteBuffer
    * For concurrent, double buffer win, read and write at different thread, so use one ByteBuffer need a lock for Race Condition(modify data at the same time)
    * For easy, double buffer not consider flag of read/write.
    * For flexible, single buffer will use fully whether read operation or write operation is frequency.
    * For double-direction, double buffer will work well because it can write and read at the same time.
    *
    * FINALLY I decide use double buffer for double-direction feature. It does more close to channel means - two ways.
    *   FUTUREMORE, it should be able to custom a socket communicate ways
    * NOTE one socket read or write should be single stream, next read/write should be
    */
  def read(byteBuffer: ByteBuffer, socketChannel: AsynchronousSocketChannel, readAttach: ReadAttach): Observable[Long] = {


    val readSubs = Subscriber
//    val p = Promise[EnCoding]
    val p = Promise[Long]
    socketChannel.read(byteBuffer, readAttach, new CompletionHandler[Integer, ReadAttach] {
      override def completed(result: Integer, readAttach: ReadAttach): Unit = {
        if (result != -1) {
          /**
            * every time at the beginning of deal with bytebuffer should be as this:
            * bytebuffer > ------------------------------------
            *             0^ poistion
            *                     eg.300^ limit
            *                                             1024^ capacity
            *    as that example, 0 ~ 15 is last uncompleted protocl, by the way, 0 ~ 8 byte is the protocol's uuid
            */
          val buffer = readAttach.byteBuffer
          val readLeftProto = readAttach.readLeftProto

          //does uuid contains in the proto buffer, use absolute way not affect position
          val uuidOpt = if (buffer.position() >= 8) Some(buffer.getLong) else None
//          readLeftProto.uuid = uuidOpt//save if it was not a completed protocol, saved it for next judgement

          uuidOpt match {
            case Some(protocol) =>
              // according the protocol uuid decide does the buffer contains the protocol completely. If does, read the complete
//              SearchProto.byUUID(protocol).map { classInfo =>
//                classInfo.getClassLoader.
//              }
              p.trySuccess(protocol)
            case None =>
              p.tryFailure(new UUIDNotEnoughException())
          }
        } else {
          p.tryFailure(new ResultNegativeException())
        }
      }

      override def failed(exc: Throwable, attachment: ReadAttach): Unit = {
        println("read I/O operations fails")
        p.tryFailure(exc)
      }
    })

    Observable.apply[Long]({ s =>
      p.future.recover {
        case e: UUIDNotEnoughException => e.uuid
      }.onComplete {
        case Failure(e) =>
          s.onError(e)
        case Success(c) =>
          s.onNext(c)
      }
    })
  }

  //connection observable - when connection occurred emit it.
  //this action as ObservablesHot
  //actually is a cold Obs(great then hot)
  //hot - if accept a connection new a Observable
  private def socketObservable(server: AsynchronousServerSocketChannel): Observable[AsynchronousSocketChannel] = {

    val f = connection(server)

    //todo what's the difference between apply and create?
    Observable.apply[AsynchronousSocketChannel]( { s =>

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
      Subscriber()
    }).doOnCompleted{
      println("socket connection - doOnCompleted")
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