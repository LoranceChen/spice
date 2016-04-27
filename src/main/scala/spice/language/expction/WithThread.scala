package spice.language.expction

/**
  *
  */
object WithThread extends App{
  def newExp = new Thread{
    override def run() {
      throw new Exception("sub thread")
    }

    start()
  }

  try{
    newExp
  } catch {
    case  e: Exception =>
      println(s"catch it! - " + e.toString)
  }
}
