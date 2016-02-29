package spice.socket

import spice.socket.session.Entrance

/**
  *
  */
object WeChatToo extends App {
  Entrance.start("localhost", 10001)

  Thread.currentThread.join
}
