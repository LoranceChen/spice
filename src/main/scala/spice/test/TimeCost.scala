package spice.test

/**
  *
  */
trait TimeCost {
  def timeTest[T](body: => T, functionName: String) = {
    val start = System.nanoTime()
    val rst = body
    val end = System.nanoTime()
    println(s"$functionName - ${(end - start) / 1000000}ms - ${Thread.currentThread.getName}" )
    rst
  }
}
