package spice.rx

import rx.lang.scala.{Subscriber, Subject, Observable}
import rx.lang.scala.schedulers.NewThreadScheduler
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
  val original = Observable.just('a', 'b', 'c', 'd').takeUntil(x => x == 'c')
  val mapped = original.map(x => x.toUpper)
  //how to let `mapped` Observable stop emit event?
  //do something
  mapped.subscribe(x => println(x)) //make it only print A ,B
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
