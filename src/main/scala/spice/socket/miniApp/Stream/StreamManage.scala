package spice.socket.miniApp.Stream

/**
  *
  */
object StreamManage {
  /** Vector not change but events will change*/
  private var streams = scala.collection.mutable.Map[String, BaseStream]()

  def addStream(baseStream: BaseStream) = streams.synchronized {
    streams += (baseStream.toString -> baseStream)
  }

  def startListen = streams.foreach(_._2.listen)
}
