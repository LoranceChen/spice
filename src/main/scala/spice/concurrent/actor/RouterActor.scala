package spice.concurrent.actor

import akka.actor.{Props, Actor}

/**
  *这里介绍转发模型,用于路由器Actor对象, 复制器Actor对象,负载均衡对象(本例).
  * ————P254有简单的介绍
  */
class RouterActor extends Actor with Logger {
  var i = 0
  val children = for(i <- 0 until 4) yield
    context.actorOf(Props[StringPrinter])
  def receive = {
    case msg =>
      children(i) forward msg
      i = (i + 1) % 4
  }
}

//use [Scala Console]/ Run/ sbt, test the example
object RounterActor extends App {
  val router = ourSystem.actorOf(Props[RouterActor], "router")
  router ! "Hola"
  router ! "Hey"
  Thread.sleep(1000)
}