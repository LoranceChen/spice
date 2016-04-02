package spice.rx

import java.util.concurrent.Executors

import rx.lang.scala.{Subscriber, Subject, Observable}
import rx.schedulers.Schedulers
import spice.concurrent.log
/**
  * test all operation of ReactiveX
  * come from http://reactivex.io/documentation/operators.html
  */
object OnNextAndEmit extends App {
  val x = Observable[String]{s =>
    s.onNext("x")
    s.onNext("y")
  }

  x.subscribe(s => log(s))
  x.subscribe(s => log(s))
}


object PublishAndConnect extends App {
  val obv = Observable[String]{s =>
//    def l: Unit = {
      s.onNext("hi~")
//      Thread.sleep(1000)
//      l
//    }
//    l
  }//.subscribeOn(NewThreadScheduler())

  val obvMapped = obv.map{ o =>
    println("map 01")
    o + "2"
  }


  val obvPublish = obv.publish
//  obvPublish.connect

  obv.subscribe(s => println("obv - sub01" + s))
  obvPublish.subscribe(s => println("sub01 - " + s))
  obvPublish.subscribe(s => println("sub02 - " + s))

  Thread.sleep(1000)
  obvPublish.connect


  val obvMappedPublish = obvMapped.publish

  Thread.currentThread().join()
}

object LoppMeans extends App {
  val obvTest = Observable[String] { s =>
    def loop  {
      s.onNext("hi~")
      Thread.sleep(1000)
      loop
    }
    loop
  }//.subscribeOn(NewThreadScheduler()) test it see boo

  obvTest.subscribe{s => log("foo -" + s)}
  obvTest.subscribe{s => log("boo -" + s)}

  Thread.currentThread().join()
}

object TakeUntil extends App {
  //until print a,b,c
  val original = Observable.just('a', 'b', 'c', 'd')

  println("===============")
  val untilObv = original.takeUntil(x => x == 'c')
  untilObv.subscribe(x => println(x))

  //while print a,b
  println("===============")
  val whileObv = original.takeWhile(x => x != 'c')
  whileObv.subscribe(x => println(x))

}

/**
  * Subject is hot Observable
  */
object SubjectIsHot extends App {
  val subject = Subject[String]()
  subject.onNext("one - ")

  subject.subscribe(new Subscriber[String] {
    override def onNext(value: String): Unit = log(value + "hi")
  })
  subject.subscribe(x => log(x+"2"))

  subject.onNext("two - ")
  subject.onCompleted()

  subject.onNext("three - ")
  subject.subscribe(x => log(x+"3"))

  Thread.currentThread().join()
}

object SchedulersTest extends App {
  import scala.concurrent.ExecutionContext
  import scala.concurrent.ExecutionContext.Implicits.global
  val executors = Executors.newFixedThreadPool(5)
//  val e = ExecutionContext.fromExecutor(executors)
  val e = ExecutionContext.fromExecutor(executors)
  import rx.lang.scala.schedulers.ExecutionContextScheduler
  val re = ExecutionContextScheduler(e)
  val reI = ExecutionContextScheduler(global)

  def diff1 = {
    var i = 0
    val obv = Observable[Int] { s =>
      log("observable body - " + i)
      i = i + 1
      s.onNext(i)
    }

    obv.subscribe(o => log(s"subscriber - $o"))
    obv.subscribe(o => log(s"subscriber2 - $o"))
    obv.subscribe(o => log(s"subscriber3 - $o"))
  }

  def diff2 = {
    var i = 0
    val obv = Observable[Int] { s =>
      i = i + 1
      log("observable body - " + i)

      s.onNext(i)

      //      Thread.sleep(1000)
//      s.onNext(100 + 1)
    }.publish

    obv.subscribe(o => log(s"subscriber - $o"))
    obv.subscribe(o => log(s"subscriber2 - $o"))
    obv.connect

    obv.subscribe(o => log(s"subscriber3 - $o"))
  }

  def diff3WithMultiThread = {
    var i = 0
    val obv = Observable[Int] { s =>
      i = i + 1
      log("observable body - " + i)//出现竞态条件

      s.onNext(i)
    }.observeOn(re).subscribeOn(re)

    obv.subscribe(o => log(s"subscriber - $o"))
    obv.subscribe(o => log(s"subscriber2 - $o"))
    obv.subscribe(o => log(s"subscriber3 - $o"))
  }

  def diff4WithMultiThread = {
    var i = 0
    val obv = Observable[Int] { s =>
      i = i + 1
      log("observable body - " + i)

      s.onNext(i)
    }.observeOn(re).subscribeOn(re).publish

    obv.subscribe(o => log(s"subscriber - $o"))
    obv.subscribe(o => log(s"subscriber2 - $o"))
    obv.connect
    obv.subscribe(o => log(s"subscriber3 - $o"))
  }


  //比较单线程和多线程的区别
  def singleThread = {
    val obv = Observable[Int] { s =>
      log("observable body - " + 1)
      s.onNext(1)
    }.publish

    obv.subscribe(o => log(s"subscriber1 - $o"))
    obv.subscribe(o => log(s"subscriber2 - $o"))
    obv.connect

    obv.subscribe(o => log(s"subscriber3 - $o"))
  }

  //multi subscribe thread will "break" limit of connect sequence
  //当使用subscribeOn的时候,observable body的触发就会在多线程中,也就是说,这时候main线程是继续在运行的.
  // 就会出现执行body的线程还没运行到onNext,主线程就会先把subscriber3注册进去.所以看起来是connect导致热的Observable"没起作用"
  //另外,如果在connect方法后让主函数休息100ms,那么订阅者3就不会执行到了.
  //实用性:0.5星.实际使用中极少考虑这种边界情况.
  def multiThread = {
    val obv = Observable[Int] { s =>
      log("observable body - " + 1)

      s.onNext(1)
    }.subscribeOn(re).observeOn(re).publish

    obv.subscribe(o => log(s"subscriber1 - $o"))
    obv.subscribe(o => log(s"subscriber2 - $o"))
    obv.connect
//    Thread.sleep(100)
    obv.subscribe(o => log(s"subscriber3 - $o"))
  }

//  singleThread//4WithMultiThread
  multiThread
  Thread.currentThread().join()
}
