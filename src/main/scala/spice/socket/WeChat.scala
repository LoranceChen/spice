package spice.socket

import spice.socket.session.Entrance

/**
  *
  */
object WeChat extends App {
  Entrance.start("localhost", 10001)

  Thread.currentThread.join
}
