package spice

/**
 *
 */
package object concurrent {
  def log(msg: String): Unit = {
    println(s"${Thread.currentThread.getName}: $msg")
  }
}
