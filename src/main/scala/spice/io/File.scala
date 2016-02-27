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
  val sb = channelRead("./src/main/scala/spice/io/test",storeBB, new StringBuilder())

  println(sb.toString())
  //why it also have Garbled when reading and writing set as UTF-8? see NOTE at top comment
  channelWrite(sb.toString(), "./src/main/scala/spice/io/test_write.test")
}