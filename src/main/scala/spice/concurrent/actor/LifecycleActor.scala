package spice.concurrent.actor

import akka.actor.{Props, ActorRef, Actor}

/**
 *
 */
class LifecycleActor extends Actor with Logger {
  var child: ActorRef = _
  def receive = {
    case num: Double => log.info(s"got a Double - $num")
    case num: Int => log.info(s"got a Int - $num")
    case lst: List[_] => log.info(s"got a List - ${lst.head}, ...")
    case txt: String => child ! txt
  }

  override def preStart(): Unit = {
    log.info("about to start")

    child = context.actorOf(Props[StringPrinter], "kiddo")
    //child actor does NOT create in current thread
0  }

  override def preRestart(t: Throwable, msg: Option[Any]): Unit = {
    log.info(s"about to restart because of $t, during message $msg")
    super.preRestart(t, msg)
  }

  override def postRestart(t: Throwable): Unit = {
    log.info(s"just restarted due to $t")
    super.postRestart(t)
  }

  override def postStop() = log.info("just stopped")
  //all child will stop on the follow
}

class StringPrinter extends Actor with Logger {
  def receive = {
    case msg: String => log.info(s"printer got message $msg")
  }
  override def preStart(): Unit = {
    log.info(s"printer preStart")
  }

  //parent exception does't affect child
  override def postRestart(t: Throwable): Unit = {
    log.info(s"printer postRestart due to $t")
  }

  override def postStop(): Unit = {
    log.info(s"printer postStop")
  }
}
