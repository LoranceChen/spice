package spice.concurrent.time_task

/**
  *
  */
trait Task {
  val taskId: TaskKey //account and custom name
  def execute(): Unit
  def nextTask: Option[Task] //able to execute next time, completed as None
  override def toString = {
    super.toString + s"-$taskId"
  }
}

object Task {
  def delay (millis: Long) = {
    System.currentTimeMillis() + millis
  }
}

case class TaskKey(id: String, systemTime: Long)