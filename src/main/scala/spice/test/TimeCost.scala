package spice.test

/**
  *
  */
trait TimeCost {
  def timeTest[T](body: => T, functionName: String) = {
    for(i <- 1 to 10000){
      body
    }//make code hot
    val start = System.nanoTime()
    val rst = body
    val end = System.nanoTime()
    println(s"$functionName - ${end - start}ns - ${Thread.currentThread.getName}" )
    rst
  }
}
