package spice.socket

import spice.socket.session.Entrance

/**
  * We chat too
  */
object WeChatToo extends App {
  Entrance("localhost", 10001)
  Thread.currentThread.join
}
