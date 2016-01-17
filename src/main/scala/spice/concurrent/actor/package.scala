package spice.concurrent

import akka.actor.ActorSystem

/**
 *
 */
package object actor {
  lazy val ourSystem = ActorSystem("OurExampleSysytem")
}
