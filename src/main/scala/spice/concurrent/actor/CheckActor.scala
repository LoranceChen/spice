package spice.concurrent.actor

import akka.actor.{ActorIdentity, Identify, Actor}
import akka.event.Logging

/**
 *
 */
class CheckActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case path: String =>
      log.info(s"checking path $path")
      context.actorSelection(path) ! Identify(path)
    case ActorIdentity(path, Some(ref)) =>
      log.info(s"found actor $ref at $path")
    case ActorIdentity(path, None) =>
      log.info(s"could not find any actor at $path")
  }
}
