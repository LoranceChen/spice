package spice.concurrent.time_task

import java.util.Comparator
import java.util.concurrent._

import scala.concurrent.Promise

/**
  * todo remove the first task after it was ensure not nextTask
  * some principle:
  *   1. a task exist if it in DataSet
  *   2. remove a task from DataSet only it was executed or cancel by user.
  */
class TaskManager {
  import TaskCommandQueue._
  object TaskCommandQueue {
    trait Action
    case class Cancel(id: String, promise: Promise[Option[Task]]) extends Action
    case class Get(id: String, promise: Promise[Option[Task]]) extends Action
    case class AddTask(task: Task) extends Action
    case class GetCount(promise: Promise[Int]) extends Action
    case class NextTask(lastTask: Task) extends Action
  }

  class TaskCommandQueue extends CommandQueue[TaskCommandQueue.Action]{
    import TaskCommandQueue._
    private def addTaskSync(task: Task) = {
      DataSet.put(task)
      dispatch.ready(task)
      timerTaskLogger.log("addTask - " + task, 150)
    }
    protected override def receive(action: Action): Unit = action match {
      case Cancel(id: String, promise: Promise[Option[Task]]) =>
        timerTaskLogger.log(s"ready cancel task - $id", 150)
        val tryGetTask = List(
          dispatch.cancelCurrentTaskIf((waitingTask) => {
            waitingTask.taskId.id == id
          })._2,
          DataSet.remove(id)).flatten

        promise.trySuccess(tryGetTask.headOption)
      case AddTask(task: Task) =>
        addTaskSync(task)
      case Get(id: String, promise: Promise[Option[Task]]) =>
        promise.trySuccess(DataSet.get(id))
      case GetCount(promise: Promise[Int]) =>
        val x = DataSet.size
        timerTaskLogger.log(s"GetCount - $x", 300)
        promise.trySuccess(x)
      case NextTask(lastTask) =>
        DataSet.get(lastTask.taskId) match {
          case None => //has removed, DON'T calculate nextTask even though the Task has next task
            timerTaskLogger.log("has removed and needn't get `nextTask`- " + lastTask, 300)
          case Some(_) =>
            timerTaskLogger.log("get last task - " + lastTask, 300)
            DataSet.update(lastTask.taskId, lastTask.nextTask)
        }

        //get next task, put it to task set
        DataSet.getFirst.foreach { task =>
          timerTaskLogger.log("ready task - " + task, 170, Some("manager"))
          addTaskSync(task) //can't use `tell` because it will break `NextTask` actions
        }

        timerTaskLogger.log("DataSet count after `NextTask` - " + DataSet.size, 150)
    }


    private object DataSet {
      private val tasks = new ConcurrentSkipListMap[TaskKey, Task](new Comparator[TaskKey]() {
        override def compare(o1: TaskKey, o2: TaskKey): Int = {
          val compare = (o1.systemTime - o2.systemTime).toInt
          //return 1 will cause dead lock, we should always promise compare result is great or little
          if (compare == 0) {
            val comp = o1.hashCode() - o2.hashCode()
            comp
          } else compare //distinct same time task
        }
      })

      private val auxiliaryMap = new ConcurrentHashMap[String, TaskKey]()

      def getFirst = {
        Option(tasks.firstEntry()).map(_.getValue)
      }

      def put(task: Task): Unit = {
        auxiliaryMap.put(task.taskId.id, task.taskId)
        tasks.put(task.taskId, task)
        timerTaskLogger.log(s"put to tasksMap - ${tasks.get(task.taskId)}", 300)
      }

      def get(taskKey: TaskKey) = {
        Option(tasks.get(taskKey))
      }

      def get(taskId: String) = {
        Option(auxiliaryMap.get(taskId)).flatMap(key =>
          Option(tasks.get(key))
        )
      }

      def remove(taskKey: TaskKey) = {
        Option{
          val removed = tasks.remove(taskKey)
          timerTaskLogger.log(s"remove - $taskKey - form tasksMap - $removed; tasks.size = ${tasks.size()}")
          removed
        }
      }

      def remove(taskId: String) = {
        Option(auxiliaryMap.get(taskId)).flatMap(taskKey =>
          Option{
            auxiliaryMap.remove(taskId)
            val removed = tasks.remove(taskKey)
            timerTaskLogger.log(s"remove - $taskKey - form tasksMap - $removed; tasks.size = ${tasks.size()}")
            removed
          }
        )
      }

      def update(older: TaskKey, newTask: Option[Task]) = {
        assert(newTask.fold(true)(_.taskId.id == older.id))
        remove(older)
        newTask.foreach{task => DataSet.put(task)}
      }

      def size = tasks.size()
    }
  }

  private val dataSetOperateQueue = new TaskCommandQueue()

  private val dispatch = new TaskHolder()

  //notice the observer execute at Dispatch Thread if `afterExecute` not use `observeOn`
  dispatch.afterExecute.subscribe ( (lastTask) =>
    dataSetOperateQueue.tell(NextTask(lastTask))
  )

  def tasksCount = {
    val promise = Promise[Int]()
    dataSetOperateQueue.tell(GetCount(promise))
    promise.future
  } //DataSet.size

  def findTask(id: String) = {
    val promise= Promise[Option[Task]]()
    dataSetOperateQueue.tell(Get(id, promise))
    promise.future
  }

  /**
    * 1. add to DataSet
    * 2. try add to TaskHolder
    */
  def addTask(task: Task): Unit = {
    dataSetOperateQueue.tell(AddTask(task))
  }

  def cancelTask(id: String) = {
    val promise= Promise[Option[Task]]()
    dataSetOperateQueue.tell(Cancel(id, promise))
    promise.future
  }
}
