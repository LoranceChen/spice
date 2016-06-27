package spice.concurrent.future

import java.util.concurrent.Executors

import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

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

object PromiseComplete extends App {
  val p = Promise[Int]
  p.future.onComplete(x => println("complete" + x))

  Thread.sleep(5000)

  p.trySuccess(10)
}