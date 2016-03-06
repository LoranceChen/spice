package spice.socket.miniApp

import java.nio.charset.StandardCharsets

import spice.socket.session._

/**
  * create a mini app verify socket does use smoothly.
  */
object MainApp extends App {
  val entrance = Entrance("localhost", 10001)
  val obsStart = entrance.startListen

  val obsRead = obsStart.flatMap(entrance.startReading)

  //two example of Connection event.
  var count = 0
  val obsCount = obsStart.map{s => count = count + 1;count}
  val obsPrintCount = obsCount.map{s => println(s"connect count - $count")}

  //println read data
  obsRead.subscribe(s => {
    s.foreach{ item =>
      val decode = new String(item.loaded.array(), StandardCharsets.UTF_8)
      println(decode)
    }
  })

  Thread.currentThread().join()
}
