package spice.concurrent.actor

import akka.actor._
import com.typesafe.config._

object RemotingConfig {
  def remoteConfig(port: Int) = ConfigFactory.parseString(s"""
    akka {
      actor.provider = "akka.remote.RemoteActorRefProvider"
      remote {
        enable-transports = "[akka.remote.netty.tcp]"
        netty.tcp {
          hostname = "127.0.0.1"
          port = $port
        }
      }
    }"""
  )

  def remotingSystem(name: String, port: Int): ActorSystem =
    ActorSystem(name, remoteConfig(port))
}

object RemotingPongySystem extends App {
  val system = RemotingConfig.remotingSystem("PongyDimension", 24321)
  val pongy = system.actorOf(Props[Pongy], "pongy")
  Thread.sleep(25000)
  system.terminate()
}

//used to get ActorRef
class Runner extends Actor with Logger {
  val pingy = context.actorOf(Props[Pingy], "pingy")
  def receive = {
    case "start" =>
      val pongySys = "akka.tcp://PongyDimension@127.0.0.1:24321"
      val pongyPath = "/user/pongy"
      val url = pongySys + pongyPath
      val selection = context.actorSelection(url)
      selection ! Identify(0)
    case ActorIdentity(0, Some(ref)) => pingy ! ref
    case ActorIdentity(0, None) =>
      log.info("Something's wrong - aim't no pongy anywhere")
      context.stop(self)
    case "pong" =>
      log.info("got a pong from another dimension")
      context.stop(self)
  }
}

object RemotingPingySystem extends App {
  val system = RemotingConfig.remotingSystem("PingyDimension", 24567)
  val runner = system.actorOf(Props[Runner], "running")
  runner ! "start"
  Thread.sleep(10000)
  system.terminate()
}