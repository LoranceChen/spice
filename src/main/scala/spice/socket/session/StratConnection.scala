package spice.socket.session

import java.nio.channels.AsynchronousSocketChannel

import rx.lang.scala.{Subscriber, Subscription}

/**
  *
  */
class StratConnection {
  def start() = {
    val enter = Entrance.apply("localhost", 10001)
    val listen = enter.startListen
    listen.subscribe(new ListenSubscriber[AsynchronousSocketChannel]())
  }
}

class ListenSubscriber[T] extends Subscriber[T] {
  override def onNext(value: T): Unit = println(value.toString + "onNext")
  override def onError(error: Throwable): Unit = error.printStackTrace()
  override def onCompleted(): Unit = println("onNext + completed")
}
