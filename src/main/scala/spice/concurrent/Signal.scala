package spice.concurrent

import java.util.concurrent.Semaphore

/**
  * lock and unlock by signal
  */
object Signal extends App {
  val semp = new Semaphore(1)

  def sendMsg = {
    semp.acquire()
    val th = new Thread {
      override def run(): Unit = {
        Thread.sleep(5000)
        semp.release()
        println("complete - " + System.currentTimeMillis())
      }
    }
    th.start()
  }

  for(i <- 1 to 10) {
    sendMsg
  }
}
