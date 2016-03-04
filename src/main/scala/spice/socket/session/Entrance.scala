package spice.socket.session

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousSocketChannel, CompletionHandler, AsynchronousServerSocketChannel}
import rx.lang.scala.{Subscriber, Observable}
import spice.socket.presentation.{SearchProto, EnCoding}
import spice.socket.session.exception.{ResultNegativeException, UUIDNotEnoughException}
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import spice.socket.session.Configration._
/**
  * begin server socket listen
  * Q: concurrent in which stage?
  */
class Entrance(host: String, port: Int) {
  /**
    * save temp readBuffer uncompleted data
    */
  val bufferHeap = mutable.Map[AsynchronousServerSocketChannel, Array[Byte]]()

  /**
    * listen connection and emit every connection event.
    */
  def startListen = {
    val server: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open
    val sAddr: InetSocketAddress = new InetSocketAddress(host, port)
    server.bind(sAddr)
    println(s"Server is listening at - $sAddr")

    socketObservable(server)
  }

  def startReading(socketChannel: AsynchronousSocketChannel): Observable[ReadAttach] = {
    val readAttach = new ReadAttach(
      ByteBuffer.allocate(1024),
      new LeftProto(None, None, 0, 0, new TempBuffer(null, TEMPBUFFER_LIMIT)),
      socketChannel
    )

    /**
      * @param rawAttach should be raw form callback
      * TODO make the buffer as directAllocation because the operation is simple
      */
    def dispatchBuffer(rawAttach: ReadAttach) = {
      val buffer = rawAttach.byteBuffer
      val leftProto = rawAttach.leftProto
//      buffer.flip()//position = 0, limit = length of the bytes
      //last proto does completed
      val lastCompleted = leftProto match {
        case LeftProto(None, _, _, _, tmpBuffer) =>
          //not left
          buffer.flip()
          val limit = buffer.limit()
          limit match {
            case x if x < 4 =>
//              leftProto.protoId = Some(buffer.getLong)
            case x if 4 <= x && x < 8 =>
              leftProto.protoId = Some(buffer.getInt)
              leftProto.length = None
            case x if 4 <= x =>
              //是否够处理?
                //无论如何协议和长度都要读出来了.
              val protoId = buffer.getInt
              val length = buffer.getInt
//              leftProto.length = Some(length)
              //缓冲区一次不够放
              if (length > buffer.capacity() - 8) {//tag:1
                //该协议使用临时空间处理.
                //1. 为该次通信协议使用一个恰好的Array[Byte](length)//队列更好一些,因为只是存数据和读数据的操作
                tmpBuffer.bf = ByteBuffer.allocate(length)
                //2. 为该socket的通信协议分配一个队列
                //3. 为所有链接的socket分配一个Map[socketChannel -> Queue[Byte]]统一存储和回收
                //TODO
              }
              else //缓存区够放
                if (limit - buffer.position >= length){//需要的数据已经完全在缓冲区内了.
                  // p:16(当前索引为16,含有效数据),limit = eg.20(索引20不含有效数据), length = 4,这时候是刚好够得
                  //触发该协议

              } else {//需要的数据未完全到达
                //标记并进行下次读写
              }

          }
//          if (limit < 8)
//          else
//          leftProto.protoId = if (limit >= 8) Some(buffer.getLong) else None
//          leftProto.
        case LeftProto(Some(id), None, _, _, _) =>
          //only read uuid
        case LeftProto(Some(id), Some(length), _, _, _) =>
          //last time deal with a length but not read all of the load.
          //TODO need a ArrayBuffer save long data.Its also useful to define a custom buffer transfer big data.
          //as cost, we must manage the data, make it will be collection back

      }
    }

    /**
      * used when success read uuid and length but just part of protocol overload
      */
    def tempSpace = {

    }
    Observable.apply[ReadAttach]({ s =>
      def readForever: Unit = read(socketChannel, readAttach) onComplete {
        case Failure(f) =>
          println(s"read exception - $f")
          s.onError(f)
        case Success(c) =>
          s.onNext(c)
          readForever
      }
      readForever
    }).doOnCompleted {
      println("read socket - doOnCompleted")
    }
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
  private def read(socketChannel: AsynchronousSocketChannel, readAttach: ReadAttach): Future[ReadAttach] = {
    val p = Promise[ReadAttach]
    val callback = new CompletionHandler[Integer, ReadAttach] {
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
          //          val buffer = readAttach.byteBuffer
          //          val readLeftProto = readAttach.readLeftProto

          //does uuid contains in the proto buffer, use absolute way not affect position
          //          val uuidOpt = if (buffer.position() >= 8) Some(buffer.getLong) else None
          //          readLeftProto.uuid = uuidOpt//save if it was not a completed protocol, saved it for next judgement

          //          uuidOpt match {
          //            case Some(protocol) =>
          //              // according the protocol uuid decide does the buffer contains the protocol completely. If does, read the complete
          ////              SearchProto.byUUID(protocol).map { classInfo =>
          ////                classInfo.getClassLoader.
          ////              }
          //              p.trySuccess(protocol)
          //            case None =>
          //              p.tryFailure(new UUIDNotEnoughException())
          //          }
          p.trySuccess(readAttach)
        } else {
          p.tryFailure(new ResultNegativeException())
        }
      }

      override def failed(exc: Throwable, attachment: ReadAttach): Unit = {
        println("read I/O operations fails")
        p.tryFailure(exc)
      }
    }

    socketChannel.read(readAttach.byteBuffer, readAttach, callback)
    p.future
  }

  //connection observable - when connection occurred emit it.
  //this action as ObservablesHot
  //actually is a cold Obs(great then hot)
  //hot - if accept a connection new a Observable
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