package spice.concurrent.actor

import akka.actor.{Props, ActorRef, Actor}
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._
/**
 * 默认的!操作表示非阻塞的消息发送方式,使用?操作会等待对方做出回应.
 * 通常,这个回应是一个Future,以尽可能的减少当前线程的阻塞.
 */
class Pongy extends Actor with Logger {
  def receive = {
    case "ping" =>
      log.info("Got a ping -- ponging back")
      log.info(s"sender - $sender")
      sender ! "pong"
      context.stop(self)
  }

  override def postStop() = log.info("pongy going down")
}

class Pingy extends Actor with Logger {
  def receive = {
    case pongyRef: ActorRef =>
      implicit val timeout = Timeout(2.seconds)
      val f = pongyRef ? "ping"
      import context.dispatcher
      f.pipeTo(sender)
//      //不在在闭包/异步/其他对象中访问actor的资源
//      f.map{ rsp =>
//        println("map - " + this.toString)
//        log.info(s"sender - $sender, asked $rsp")
//        rsp
//      }.pipeTo(sender)
//
//      f onComplete {
//        case v =>
//          println("onComplete - " + this.toString) //this.toString is Pingy@....
//
//          //"Actor 对象的状态只能由Actor自己访问"-P253
//          // 那么这里的访问log的是谁?
//          log.info(s"Response $v")
//      }
    case "pong" => sender ! "ping"
  }
}

class Master extends Actor with Logger {
  val pingy = ourSystem.actorOf(Props[Pingy], "pingy")
  val pongy = ourSystem.actorOf(Props[Pongy], "pongy")
  def receive = {
    case "start" => pingy ! pongy
    case "pong" =>
      log.info(s"sender - $sender")
      context.stop(self)
  }
  override def postStop() = log.info(s"master going down $sender")
}
