package spice.concurrent.time_task

import java.util.concurrent.ConcurrentLinkedQueue

trait CommandQueue[T] {
  val myQueue = new ConcurrentLinkedQueue[T]()
  val lock = new Object()
  object QueueThread extends Thread {
    setDaemon(true)

    override def run = {
      while(true) {
        if (myQueue.size() == 0) {
          lock.synchronized(lock.wait())
        } else {
          val theTask = myQueue.poll()
          timerTaskLogger.log(s"poll task cmd queue - $theTask")

          receive(theTask)
        }
      }
    }
  }

  QueueThread.start()

  def tell(cmd: T) = {
    myQueue.add(cmd)
    timerTaskLogger.log(s"tell cmd - $cmd - current count - ${myQueue.size()}")
    lock.synchronized(lock.notify())
  }

  //must sync operation
  protected def receive(t: T): Unit
}
