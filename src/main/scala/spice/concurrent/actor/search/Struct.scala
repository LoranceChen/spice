package spice.concurrent.actor.search

import akka.actor.{ActorRef, Actor}

/**
  *
  */
trait GathererNode extends Actor {
  val maxDocs: Int
  val maxResponses: Int
  val client: ActorRef
}
