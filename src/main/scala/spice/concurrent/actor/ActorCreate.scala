package spice.concurrent.actor

import akka.actor.{Props, ActorRef}
/**
 *
 */
object ActorCreate extends App{
  val hiActor: ActorRef = ourSystem.actorOf(HelloActor.props("hi"), name = "greeter")
  hiActor ! "hi"
  Thread.sleep(1000)
  hiActor ! HelloActor.Bye
  Thread.sleep(1000)
  hiActor ! "good night!"
  Thread.sleep(1000)
  hiActor ! "hola"
  Thread.sleep(1000)
  ourSystem.terminate()
}

object ActorsHierarchy extends App {
  val parent = ourSystem.actorOf(Props[ParentActor], "parent")
  parent ! "create"
  parent ! "create"
  Thread.sleep(1000)
  parent ! "sayhi"
  Thread.sleep(1000)
  parent ! "stop"
  Thread.sleep(1000)
  ourSystem.terminate()
}

object ActorCheck extends App {
  val checker = ourSystem.actorOf(Props[CheckActor], "checker")
  checker ! "../*" //same hierarchy
  Thread.sleep(1000)
  checker ! "../../*"
  Thread.sleep(1000)
  checker ! "/system/*" // absolute path
  Thread.sleep(1000)
  checker ! "/user/checker2" // not exist
  Thread.sleep(1000)
  ourSystem.terminate()
}

object ActorLifecycle extends App {
  val testy = ourSystem.actorOf(Props[LifecycleActor], "testy")
  testy ! math.Pi
  Thread.sleep(1000)

  testy ! "hi there"
  Thread.sleep(1000)

  testy ! Nil
  Thread.sleep(1000)

  testy ! "fine?"
  Thread.sleep(1000)
  ourSystem.terminate()
}

object ActorPingAndPong extends App {
  val master = ourSystem.actorOf(Props[Master], "master")
  master ! "start"
  Thread.sleep(2000)
  ourSystem.terminate()
}