package spice.functional

/**
  * Is lazy
  */
object MapStreamIsLazy extends App {

  //10000 - 80000 : 5000
  spice.test.TimeTest.timeTest({
    List(1,2,3).map{x => val y = x+1; val yy = y+1; yy+1}
  },"combined")

  //10000 - 80000 : 50000
  spice.test.TimeTest.timeTest({
    List(1,2,3).map(_ + 1).map(_ + 1).map(_ + 1).map(_ + 1)
  }, "map")

}

object MapStreamIsLazy2 extends App {

  //10000 - 80000 : 50000
  spice.test.TimeTest.timeTest({
    List(1,2,3).map(_ + 1).map(_ + 1).map(_ + 1).map(_ + 1)
  }, "map")

}
