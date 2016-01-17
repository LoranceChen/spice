package spice.concurrent.actor

import akka.actor._
import akka.pattern.gracefulStop

import scala.util.{Failure, Success}
import scala.concurrent.duration._
import spice.concurrent.log
/**
  *
  */
class GracefulPingy extends Actor with Logger {
  val pongy = context.actorOf(Props[Pongy], "pongy")
  def receive = {
    case "Die, Pingy" => context.stop(pongy)
    case Terminated(`pongy`) =>
      log.info("pongy terminated")
      context.stop(self)
  }
}

object GracefulPingyStop extends App {
  implicit val global = scala.concurrent.ExecutionContext.global

  val grance = ourSystem.actorOf(Props[GracefulPingy], "grance")
  val stopped = gracefulStop(grance, 3.seconds, "Die, Pingy")

  //Why failure??
  stopped onComplete {
    case Success(x) =>
      log("granceful shutdown successful")
      ourSystem.terminate()
    case Failure(t) =>
      log("grance not stopped!")
      ourSystem.terminate()
  }

  Thread.sleep(1000)
}