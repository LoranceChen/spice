package spice.concurrent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, blocking}

/**
  * book: Learning Concurrent Programming in Scala
  * Some legacy APIs do not use callbacks to asynchronously return
  * results. Instead, such APIs expose the blocking methods.
  * With the blocking call around the sleep call, the global execution
  * context spawns additional threads when it detects that there is more
  * work than the worker threads
  *
  * Question:
  * 1. What's the block operation definition?
  * 2. Why `sleep(1000)` can block other thread, does other thread can't dispatched by CPU automatically and huge on those blocked thread?
  */
object Blocking extends App{
  def blockingOperate(needBlock: Boolean) = {
    val startTime = System.nanoTime
    println("startTime - " + startTime)
    def blockWithBlock(needBlocking: Boolean, curCount: Int, maxCount: Int, fs: List[Future[Unit]]): List[Future[Unit]] = {
      println("curCount - " + curCount + " - sleep")

      val f = Future {
        if (needBlocking) blocking(Thread.sleep(1000)) else Thread.sleep(1000)
      }
      f.onComplete {
        case x => println("thread name - " + Thread.currentThread().getName + " - " + curCount)
      }
      println("curCount - " + curCount + " - awake")
      if (curCount <= maxCount) blockWithBlock(needBlocking, curCount + 1, maxCount, f :: fs)
      else fs
    }
    val fs = blockWithBlock(needBlock, 1, 20, Nil)
    for (f <- fs) Await.ready(f, Duration.Inf)
    val endTime = System.nanoTime
    println("end - startTime - " + endTime)

    println(s"Total time = ${
      (endTime - startTime) /
        1000000
    } ms")
    println(s"Total CPUs = ${Runtime.getRuntime.availableProcessors}")
  }

  blockingOperate(true)
  println("=====================")
  blockingOperate(false)
}
