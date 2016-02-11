package spice.concurrent.test

import scala.concurrent.{Future, blocking}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
object Blocking extends App{
  println("begin")
  def accpets(curCount: Int, maxCount: Int): Unit = {
    println(curCount + " - sleep")
    Future(blocking(Thread.sleep(1000))).onSuccess{
      case x => println(Thread.currentThread().getName)
    }
    println(curCount + " - awake")
    if (curCount <= maxCount) accpets(curCount + 1, maxCount)
  }
  accpets(1, 10)

  println("end")
}
