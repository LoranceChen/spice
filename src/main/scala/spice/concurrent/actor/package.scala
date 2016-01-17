package spice.concurrent

import akka.actor.{Actor, ActorSystem}
import akka.event.Logging

/**
 *
 */
package object actor {
  lazy val ourSystem = ActorSystem("OurExampleSysytem")

  trait Logger {
    this: Actor =>
    val log = Logging(context.system, this)
  }
}
