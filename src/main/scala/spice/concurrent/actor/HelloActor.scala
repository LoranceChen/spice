package spice.concurrent.actor

import akka.actor._
import akka.event.Logging

/**
 *
 */
class HelloActor(val hello: String) extends Actor {
  val log = Logging(context.system, this)
  def receive = helloState

  def helloState: Actor.Receive = {
    case `hello` => log.info(s"Received a '$hello'... $hello")
    // `hello` represent val hello in constructor

    case HelloActor.Bye => context.become(byeState)

    case msg =>
      log.info(s"Unexpected message '$msg'")
      context.stop(self)
  }

  def byeState: PartialFunction[Any, Unit] = {
    case "good night!" => log.info(s"Received a good night")
  }

  override def unhandled(msg: Any) = {
    log.info(s"cannot handle message $msg in this state.")
  }
}

//避免在Actor类中创建Props开销
object HelloActor {
  def props(hello: String) = Props(new HelloActor(hello))
  //hello参数作为自由变量时会引起额外的序列化开销

  //该构造方式可以安全的避免props方法的闭包问题
  def propsAlt(hello: String) = Props(classOf[HelloActor], hello)

  case class Bye()

}
