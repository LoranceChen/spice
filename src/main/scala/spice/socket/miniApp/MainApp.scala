package spice.socket.miniApp

import spice.socket.session._

/**
  * create a mini app verify socket does use smoothly.
  */
class MainApp {
  val entrance = Entrance("localhost", 10001)
  val obsStart = entrance.startListen

  //two example of Connection event.
  var count = 0
  val obsCount = obsStart.map{s => count = count + 1;count}
  val obsPrintCount = obsCount.map{s => println(s"connect count - $count")}


}
