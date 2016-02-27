package spice.io

import java.io.{IOException, RandomAccessFile}
import java.nio.ByteBuffer
import java.nio.charset.{StandardCharsets, Charset}

import scala.StringBuilder
import scala.annotation.tailrec
import scala.collection.mutable

/**
  *
  */
trait File {

  /**
    * deal file String every 48 bytes, it's very useful deal large file
    * such as merge sort // todo: add merge sort example
    * NOTE : it's not support UTF-8, because allocate maybe get a half of char. this method only worked when every char encode as same length
    * @param filePath
    * @param body
    * @tparam T
    */
  def channelRead[T](filePath: String, body: (ByteBuffer, T) => T, rst: T): T = {
    val aFile = new RandomAccessFile(filePath, "rw")
    val inChannel = aFile.getChannel()

    val buf = ByteBuffer.allocate(64)

    var bytesRead = -1
    //use while loop can't return T,as below attempt,because scala not support var as param in a method
    @tailrec def readUntilEnd(body: (ByteBuffer, T) => T, rst:T): T = {
      bytesRead = inChannel.read(buf)

      //-28 -72 -83 -27 -101 -67
      if (bytesRead != -1) {
        buf.flip()
        val newRst = body(buf,rst)
        buf.clear()

        readUntilEnd(body, newRst)
      }
      else {
        rst
      }
    }
    readUntilEnd(body, rst)
    aFile.close()
    rst
  }

  /**
    * deal with block context.
    *
    * @param context
    * @param dstPath
    */
  def channelWrite(context: String, dstPath: String) = {
    val aFile = new RandomAccessFile(dstPath, "rw")
    val inChannel = aFile.getChannel()

    val buf = ByteBuffer.allocate(2048)

//    todo make string to ByteBuffer
    val bytes = context.getBytes(StandardCharsets.UTF_8)
    buf.put(bytes)
    buf.flip()
    var bytesRead = inChannel.write(buf)
    aFile.close()
  }
}

object test extends App with File {

  def storeBB(bb:ByteBuffer,sb: StringBuilder): StringBuilder = {
    //获取position -> limit之间的Byte
    val position = bb.position()
    println("position - " + position)
    val limit = bb.limit()
    println("limit - " + limit)
    val des = new Array[Byte](limit)
    bb.get(des, position, limit)
    sb.append(new String(des, StandardCharsets.UTF_8))
  }
//  println(Charset.defaultCharset())
  val sb = channelRead("./src/main/scala/spice/io/test",storeBB, new StringBuilder())

//  val s = "In terms of socket,it's important of these three workflow.\n1. wait/going connection - connected\n2. write data to endpoint\n3. read data from endpoint\n\nSurrounding those workflow, we able to build a fledged communication\nwe should care about:\n1. encode/decode data which from application or transfer to application\n2. message queue - socket communicate operation is single thread but application is multi-thread, so its important to define a\nqueue to let socket deal with data step by step.\n3. streams - after these operation clicked(触发),we need a RX programming style(may be others) make logic lighter.\n4. manage all of sockets, contains close/search/send or write data with specify socket(s).\n\nexception deal\n1. where handle exception?\n2. how to custom exception\nConnectTimeoutException\nTimeoutException\n\nhttp://ifeve.com/buffers/ 关于ByteBuffer的入门介绍(仅供参考)\nmark()与reset()方法\n::通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。"
  println(sb.toString())
  //why it also have Garbled when reading and writing set as UTF-8? see NOTE at top comment
  channelWrite(sb.toString(), "./src/main/scala/spice/io/test_write.test")
}