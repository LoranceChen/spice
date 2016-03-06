package spice.socket.session

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousSocketChannel, CompletionHandler, AsynchronousServerSocketChannel}
import rx.lang.scala.{Subscriber, Observable}
import spice.socket.presentation.{SearchProto, EnCoding}
import spice.socket.session.exception.{TmpBufferOverLoadException, ResultNegativeException}
import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import spice.socket.session.Configration._

/**
  * this class create a listen, listen to server, read loop, write operation, disconnect
  * begin server socket listen
  * Q: concurrent in which stage?
  */
class Entrance(host: String, port: Int) {
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

  def startReading(socketChannel: AsynchronousSocketChannel): Observable[Vector[CompletedProto]] = {
    val readAttach = new ReadAttach(
      ByteBuffer.allocate(1024),
      new LeftProto(None, None, 0, 0, new TempBuffer(EmptyByteBuffer, TEMPBUFFER_LIMIT)),
      socketChannel
    )

    val readerDispatch = new ReaderDispatch()

    /**
      * @param rawAttach should be raw form callback
      * @return completed protocol todo type choice: Array[Byte] or ByteBuffer
      * //TODO make the buffer as directAllocation because the operation is simple. simple not!
      * NOTE: returned data use new operation to allocate memory to represent the unique data stream was begin.
      */
    /**def dispatchBuffer(rawAttach: ReadAttach,completedProto: List[ProtoDatagram]): List[ProtoDatagram] = {
      val buffer = rawAttach.byteBuffer
      val leftProto = rawAttach.leftProto
//      buffer.flip()//position = 0, limit = length of the bytes
      //last proto does completed
      val lastCompleted = leftProto match {
        //it's beginning,let's make example,
        //只包含最多一个协议 :: 内容 one protocol with 500 bytes, UUID = 0x0001, length = 500 - 4 - 4 = 492
        //eg1. step1.1(第一个例子的第一个步骤): get 500 bytes :: position = 500, limit = capacity = 1024, mark = -1
        //eg2. step2.1-1(第二个例子的第一个步骤的第一次读): get 3 bytes :: position = 3, limit = capacity = 1024, mark = -1
        //eg2. step2.1-2(第二个例子的第一个步骤的第二次读): get 497
        //eg3. step3.1...: get 7 bytes :: position = 7, limit = capacity = 1024, mark = -1
        //eg4. step3.1...: get 200 bytes :: position = 200, limit = capacity = 1024, mark = -1
        //包含了两个协议 :: 测试多个协议的读取 :: 关键在于读完第一个协议是否能将buffer的状态置为最原始的状态,这样能保证一个递归/循环操作是有效的
        //two protocol with 700 bytes,
          // first :: full data 400 :: UUID = 0x0001, length = 500 - 4 - 4 = 492
          // second :: full data 300 :: UUID = 0x0001, length = 300 - 4 - 4 = 292
        //eg5. step5.1-1(第一次读) : get 200 bytes :: position = 200, limit = capacity = 1024, mark = -1
        //     step5.1-2(第二次读) : get 500 bytes :: ???position = 7, limit = capacity = 1024, mark = -1
        //eg6. step6.1-1(第一次读) : get 500 bytes :: position = 500, limit = capacity = 1024, mark = -1
        //     step6.1-2(第一次读) : get 200 bytes :: ???position = 7, limit = capacity = 1024, mark = -1
        case LeftProto(None, _, _, _, tmpBuffer) =>
          //not left
          //step1.1 : rw :: position = 0, limit = 500, capacity = 1024, mark = 500
          //step2.1-1 : rw :: position = 0, limit = 3, capacity = 1024, mark = 3
          //step2.1-2 : rw :: position = 3, limit = 3, capacity = 1024, mark = -1
          //step3.1 : rw :: position = 0, limit = 7, capacity = 1024, mark = -1
          buffer.mark()

          //step2.1-2 : rw :: position = 0, limit = 3, capacity = 1024, mark = 3
          buffer.flip()
          //step1.2 : r :: limit = 500
          //step2.2-1 : r :: limit = 3
          val limit = buffer.limit()
          limit match {
            case x if x < 4 =>
            //step2.3-1 : rw :: position = 3, limit = 3, capacity = 1024, mark = 3
              //为了socket.read操作能够按照继续向ByteBuffer中存储
            buffer.reset()
//              leftProto.protoId = Some(buffer.getLong)
            case x if 4 <= x && x < 8 =>
              leftProto.protoId = Some(buffer.getInt)
              leftProto.length = None
            case x if 8 <= x =>
              //是否够处理?
                //无论如何协议和长度都要读出来了.
              //step1.3 :rw :: position = 4, limit = 500, capacity = 1024, mark = 500
              val protoId = buffer.getInt
              //step1.4 :rw :: position = 8, limit = 500, capacity = 1024, mark = 500 :: length = 492
              val length = buffer.getInt
//              leftProto.length = Some(length)
              //当前缓冲区不够用或传输过来的协议不完整.这里不能用capacity判断,因为可能不完全填满.
              //step1.5 :r :: limit = 500 :: 492 > 500 - 8 false
              if (length > buffer.limit() - 8) {
                //TODO
                tmpBuffer.put(buffer)
              }
              else //可以获取这次完整的协议
              //step1.6 :r :: position = 8 :: 500 - 8 >= 492 true
                if (limit - buffer.position >= length) {//需要的数据已经完全在缓冲区内了.
                  //step1.7 存入List : rw :: position = 500, limit = 500, capacity = 1024, mark = 500
                  val theProto = ProtoDatagram(protoId, length, buffer.getString(length))
                  //step1.8 还原位置: rw :: position = 0, limit = capacity = 1024, mark = -1 completed
                  buffer.clear()

                  theProto :: completedProto

                  // p:16(当前索引为16,含有效数据),limit = eg.20(索引20不含有效数据), length = 4,这时候是刚好够的
                  //触发该协议
                  //read
              } else {//需要的数据未完全到达???是否可以归为第5步,:: 参照eg.4 + eg.1
                //标记并进行下次读写
              }
          }
        case LeftProto(Some(id), None, _, _, _) =>
          //only read uuid
          //eg. position:500, limit= capacity = 1024, mark = ?0(doesn't matter)
          buffer.mark()//make current position. :: rw :: position:500, limit= capacity = 1024, mark = 500(doesn't matter)
          buffer.flip()//make limit to current position,::rw :: position:0, limit = 500, mark = 500
          val limit = buffer.limit()// :: r :: 500
          if (limit < 4) {//attempt get length data but not enough
            //reset all data as change before, mark value is doesn't matter
            buffer.reset()// :: rw :: position:500, limit = 500, mark = 500(let it done)
//            buffer.
//            buffer.put()
          } else {

          }
        case LeftProto(Some(id), Some(length), _, _, tmpBuffer) =>
          //last time deal with a length but not read all of the load.
          //TODO need a ArrayBuffer save long data.Its also useful to define a custom buffer transfer big data.
          //as cost, we must manage the data, make it will be collection back
          //1.这次能拼接完成之前的协议
          //1.1存入缓存
          tmpBuffer.put(buffer)
          //1.2 并触发这个协议(这个协议应该交给ReadAttach完成?)
          return "ok"//返回一个onNext
          //2.依然需要缓存
          //同tag:1
          tmpBuffer.put(buffer)
      }
    }*/

    Observable.apply[Vector[CompletedProto]]({ s =>
      def readForever: Unit = read(socketChannel, readAttach) onComplete {
        case Failure(f) =>
          println(s"read exception - $f")
          s.onError(f)
        case Success(c) =>
          val src = c.byteBuffer
          readerDispatch.receive(src).foreach(s.onNext)
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
