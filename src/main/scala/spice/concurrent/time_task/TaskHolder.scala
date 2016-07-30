package spice.concurrent.time_task

import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.schedulers.ExecutionContextScheduler
import concurrent.ExecutionContext.Implicits.global

/**
  * execute need wait task, also replaced by new task.You can create multi Dispatch if you willing(eg. for preference)
  *
  * detail ability:
  * 1. put a task to wait thread and execute if time-on
  * 2. cancel the waiting task
  *
  * Notice: task should be recover by `Manage` if stop, also its matter of `Manage`
  */
class TaskHolder {
  private val subject = Subject[Task]() // emit completed event

  private var sleepTime: Option[Long] = None //execute if delay is navigate
  private var action: Option[() => Task] = None
  private var canceling = false

  private val godLock = new AnyRef //ensure variables execute

  private var currentTask: Option[Task] = None

  private val cancelTask = new Task {
    override def execute(): Unit = {}

    def nextTask: Option[Task] = None

    override val taskId: TaskKey = TaskKey("0.0.0.0:0000", 0)
  }

//  val afterExecute: Observable[Task] = subject.observeOn(ExecutionContextScheduler(global)) //emit the executed task
  val afterExecute: Observable[Task] = subject.
    subscribeOn(ExecutionContextScheduler(global)).
    observeOn(ExecutionContextScheduler(global)) //emit the executed task

  def ready(newTask: Task): (Boolean, Option[Task]) = {
    readyUnsafe(newTask, {
      case None =>
        timerTaskLogger.log(s"none task under waiter - $newTask", 170, Some("dispatch-ready"))
        true
      case Some(nowTask) if newTask.taskId.systemTime < nowTask.taskId.systemTime =>
        timerTaskLogger.log(s"replace older waiting task - $nowTask; the task - $newTask", 170, Some("dispatch-ready"))
        true
      case Some(nowTask) =>
        timerTaskLogger.log(s"can't replace older task - $nowTask; the task - $newTask", 170, Some("dispatch-ready"))
        false
    })
  }

  /**
    * put task to waiting thread
    * detail:
    *   1. set the next task status - 1) sleepTime 2) action it will execute
    *   2. canceling current task - and take it back to Manage
    *
    * @param predicate a security ways ready new task
    * @return ready new task success if Boolean is true
    *         `None` if predicate fail or no task under waiting
    *         `Some(task)` if predicate success, `task` represent the task only just waiting
    *
    */
  def readyUnsafe(newTask: Task, predicate: (Option[Task]) => Boolean): (Boolean, Option[Task]) = godLock.synchronized {
    val currentTaskRef = currentTask
    //set current status
    if(predicate(currentTaskRef)) {
      action = Some(() => {
        newTask.execute()
        newTask
      })

      canceling = true
      val replacedTask = currentTaskRef
      currentTask = Some(newTask)
      sleepTime = Some(newTask.taskId.systemTime - System.currentTimeMillis())

      timerTaskLogger.log(s"ready - $newTask; currentTime - ${System.currentTimeMillis()}", 120, Some("dispatch-readyUnsafe"))
      //after set all status, let's continue with new task
      godLock.synchronized(godLock.notify())
      (true, replacedTask)
    } else (false, None)
  }

  def cancelCurrentTask = ready(cancelTask)._2

  def cancelCurrentTaskIf(predicate: Task => Boolean) = {
    readyUnsafe(cancelTask, task => task.exists { t =>
      predicate(t)
    })
  }

  private def initStatus(): Unit = godLock.synchronized {
    canceling = false
    action = None
    sleepTime = None
    currentTask = None
  }

  //waiting task execute time and execute it
  private object Waiter extends Thread {
    setDaemon(true)
    setName("Thread-Waiter")
    override def run(): Unit = {
      timerTaskLogger.log("Waiter thread begin to run", 200)
      godLock.synchronized {
        while(true) {
          timerTaskLogger.log(s"loop - ${System.currentTimeMillis()}; cancel - $canceling", 150)

          sleepTime match {
            case None =>
              initStatus()
              timerTaskLogger.log(s"Waiter sleep - $sleepTime", 150)
              godLock.wait()
              timerTaskLogger.log(s"Waiter awake - ${System.currentTimeMillis()}", 150)
            case Some(delay) =>
              if (!canceling) { //canceling handle execute action and wait.
                if (delay > 0L) {
                  timerTaskLogger.log(s"sleep with delay Time - $sleepTime", 150, Some("Waiter"))
                  godLock.wait(delay)
                  timerTaskLogger.log(s"awake with delay Time - ${System.currentTimeMillis()}", 150, Some("Waiter"))
                }
                //ensure doesn't canceling after awake
                if (!canceling) { //canceling is false - needn't canceling
                var tempTask: Option[Task] = None // use the temp ref because do initStatus will lose the task
                  //action is sync if you want async,please do it yourself under execute()
                  action.foreach { t =>
                    tempTask = Some(t())
                  } //execute action
                  timerTaskLogger.log("executed action - ", 150, aim = Some("Waiter"))
                  initStatus()
                  tempTask.foreach(subject.onNext)
                }
              } else {
                //canceling as true - cancel mean skip the action. it does!
                //after skip the action we set canceling as false
                timerTaskLogger.log(s"cancel waiter - ${System.currentTimeMillis()}", 150, aim = Some("Waiter"))
                canceling = false
              }
          }
        }
      }
    }
  }

  Waiter.start()
}
