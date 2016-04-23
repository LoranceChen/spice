package spice.concurrent.future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Future execute does not seq as 1 to 100
  */
object Sequence extends App {

  for( i <- 1 to 100) {
    Future{
      println(i)
    }
  }

  Thread.currentThread().join()
}
