package spice.socket.presentation

import rx.lang.scala.subjects

/**
  * sallow session's protocol, dispatch it to registered presentation EnCoding object.
  * as follow, create a dispatch stream.
  */
class Dispatch {
  /**
    * decoupling from context: 1.for session as subscriber 2.for Encoding as observable
    */
  val processes = subjects.AsyncSubject[DeCoding[_]]()
  def stream(raw: Array[Byte]) = {
//    Subject

  }

//  def send()
}

class ReadDispatch extends Dispatch{
  def stream()
}