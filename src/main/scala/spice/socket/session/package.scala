package spice.socket

import spice.socket.session.implicitpkg._


/**
  * handle connect and break connection, same as finagle Mux module.
  * function such as:
  * 1. connect/ break connection
  * 2. make the connection to wait queue
  * 3. which connection should be first connect/ refuse straight
  * 4. verify data-completion, if a protocol just arrived half let it done then emit the event.
  * 5. Q: need a Endian setting for user? as default JVM use Big Endian and my String to ByteBuffer is also this way, need I give a chance to custom Endian?
  *    A: No. protocol make our work straight and clean, but the Endian does not, it pay us attention to this meaningless thing. I shouldn't help them.
  */
package object session {

}

object UseCase extends App {
  val b = StringToByteBuffer("hahahah中国汉字~UTF-8")
  println(s"b - $b")

  import spice.socket.session._
  println(s"s - ${b.getString}")
}