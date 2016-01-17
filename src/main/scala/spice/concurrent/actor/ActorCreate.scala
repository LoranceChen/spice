package spice.concurrent.actor

import akka.actor.ActorRef
//import spice.concurrent.actor
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
