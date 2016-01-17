package spice.concurrent.actor

import akka.actor.{Props, Actor}
import akka.event.Logging

/**
 *
 */
class ParentActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "create" =>
      context.actorOf(Props[ChildActor])
      log.info(s"created a kid; children = ${context.children}")
    case "sayhi" =>
      log.info("Kids, say hi")
      for(c <- context.children) c ! "sayhi"
    case "stop" =>
      log.info("parent stopping")
      context.stop(self)
  }
}

class ChildActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "sayhi" =>
      val parent = context.parent
      log.info(s"my parent $parent send me say hi")
  }
  override def postStop(): Unit = {
    log.info("child stopped!")
  }
}
