package spice.concurrent.time_task

import scala.collection.mutable.ListBuffer
import concurrent.ExecutionContext.Implicits.global

object MainTest extends App {
  timerTaskLogger.logLevel = 10000
  val task = new Task {
    override def execute(): Unit = {
      timerTaskLogger.log("executed")
    }

    //does continues calculate
    override def nextTask: Option[Task] = None

    override val taskId: TaskKey = TaskKey("test_01", 1469795009732L)
  }

  val taskManager = new TaskManager()
  taskManager.addTask(task)

  Thread.currentThread().join()
}

/**
  * output sequence:
  * task2 execute
  * task3 execute
  */
object TestManager extends App {
  timerTaskLogger.logLevel = 10000
  timerTaskLogger.logAim = ListBuffer("dispatch-ready")

  val manager = new TaskManager()

//  for(i <- 1 to 20) {
    manager.addTask(TestTask(5000, s"task_test1"))
    manager.addTask(TestTask(7000, s"task_test2"))
    manager.addTask(TestTask(8000, s"task_test3"))
//  }

  Thread.sleep(3000)
  manager.cancelTask("task_test1").map{x =>
    println("canceled task_test - " + x)
  }

  manager.tasksCount.foreach(x =>
    println("tasks count - " + x)
  )


  Thread.sleep(8000)
  manager.tasksCount.foreach(x =>
    println("sleep 8s tasks count - " + x)
  )
  Thread.currentThread().join()
}

case class TestTask(delay: Long, val id: String) extends Task{
  //does continues calculate
  override def nextTask: Option[Task] = None //Some(this.copy(delay = 2000))

  override val taskId = TaskKey(id, Task.delay(delay))

  override def execute(): Unit = {
    timerTaskLogger.log(s"executing the task - $taskId")
  }
}
