package spice.concurrent.future

import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
object OnCompleteExeThread extends App {
  def singleExecutionContent = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(1)

    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable) {}
  }

  val a = singleExecutionContent
  val b = singleExecutionContent

  for(i <- 1 to 20)
    Future{println(s"body under - ${Thread.currentThread().getName}")}(a).onComplete(s => println(s"completed under - ${Thread.currentThread().getName}"))(b)

  Thread.currentThread().join()
}
