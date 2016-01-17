package spice.concurrent.actor

import akka.actor.{Props, ActorRef, Actor}

/**
  * preStart invoked in begin and after preRestart(on Exception), used to some logical operation.
  * newTest executed in new object operation, for simple operation
  */
class LifecycleActor extends Actor with Logger {
  var child: ActorRef = _
  val newTest = log.info("allocation - construct in begin")

  def receive = {
    case num: Double => log.info(s"receive - got a Double - $num")
    case num: Int => log.info(s"receive - got a Int - $num")
    case lst: List[_] => log.info(s"reveive - got a List - ${lst.head}, ...")
    case txt: String => child ! txt
  }

  override def preStart(): Unit = {
    log.info("preStart - about to start")

    //child actor does NOT create in current thread
    child = context.actorOf(Props[StringPrinter], "kiddo")
  }

  override def preRestart(t: Throwable, msg: Option[Any]): Unit = {
    log.info(s"preRestart - about to restart because of $t, during message $msg")
    super.preRestart(t, msg)
  }

  override def postRestart(t: Throwable): Unit = {
    log.info(s"postRestart - just restarted due to $t")
    super.postRestart(t)
  }

  //all child will stop on the follow
  override def postStop() = log.info("postStop - just stopped")
}

class StringPrinter extends Actor with Logger {
  def receive = {
    case msg: String => log.info(s"receive - printer got message $msg")
  }
  override def preStart(): Unit = {
    log.info(s"preStart - printer preStart")
  }

  //parent exception does't affect child
  override def postRestart(t: Throwable): Unit = {
    log.info(s"postRestart - printer postRestart due to $t")
  }

  override def postStop(): Unit = {
    log.info(s"postStop - printer postStop")
  }
}
