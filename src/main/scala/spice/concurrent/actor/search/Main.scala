package spice.concurrent.actor.search

import akka.actor.Props
import spice.concurrent.actor._

/**
  *
  */
object Main{
  def main(args: Array[String]) = {
    val headNode = ourSystem.actorOf(Props[HeadNode], "HeadNode")

    headNode ! SearchQuery("2",2)
//    router ! "Hey"
    Thread.sleep(1000)
  }
}
