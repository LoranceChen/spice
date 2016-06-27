package spice.concurrent.thread

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor}

import scala.collection.mutable

/**
  * simple time task
  */
object TimerTask extends App {
  val taskExecutor = new ThreadPoolExecutor(10, 20, 10,TimeUnit.HOURS, new LinkedBlockingQueue[Runnable]())

  val tasks = mutable.Queue[TimerTrigger]()

  object Worker extends Thread {
    setDaemon(true)

    override def run() = while (tasks.nonEmpty) {
      taskExecutor.execute(tasks.dequeue())
    }
  }

  Worker.start()

  Thread.currentThread().join()
}

class TimerTrigger(millis: Long, action: => Unit) extends Runnable{
  override def run(): Unit = {
    scala.concurrent.blocking(Thread.sleep(millis))
    action
  }
}
