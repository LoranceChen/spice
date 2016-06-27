package spice.test

import spice.security.AES
/**
  *
  */
object AESTest extends App{
  @volatile var x: Array[Byte] = null
  @volatile var y: Array[Byte] = null
  val content2 = ("hello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello world" +
    "hello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello world" +
    "hello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello worldhello world").getBytes("utf-8")
  val content = "hello world~hello world~".getBytes("utf-8")
  val key = "12345".getBytes("utf-8")
  for(i <- 1 to 10000) {
    x = AES.encrypt(content, key)
  }

  val beginTime = System.nanoTime()
  x = AES.encrypt(content, key)
  val endTime = System.nanoTime()
  println(s"encrypt cost time - ${endTime - beginTime}")
  println(new String(x, "utf-8"))

  for(i <- 1 to 10000) {
    y = AES.decrypt(x, key)
  }

  val beginTime2 = System.nanoTime()
  y = AES.decrypt(x, "123".getBytes("utf-8"))
  val endTime2 = System.nanoTime()

  println(s"dencrypt cost time - ${endTime2 - beginTime2}")
  println(new String(y, "utf-8"))
}
