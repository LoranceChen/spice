package spice.concurrent.rx

import rx.lang.scala._
import spice.concurrent._
/**
 *
 */
object ObservableItems extends App{
  //use sync which called in main thread
  val o = Observable.just("Java", "Scala")
  o.subscribe(name => log(s"learn $name"))
  o.subscribe(name => log(s"finish $name"))
}

import scala.concurrent.duration._
object ObservableTimer extends App {
  //occurred in others thread
  val o = Observable.timer(1.second)
  o.subscribe(_ => log("Timeout!"))
  o.subscribe(_ => log("Another timeout!"))
  Thread.sleep(2000)
}

object ObservableCreate extends App {
  val vms = Observable.create[String] { obs =>
    obs.onNext("JVM")
    obs.onNext("DartVm")
    obs.onNext("V8")
    obs.onCompleted()
    Subscription()
  }
  vms.subscribe(log, e => log(s"oops - $e"), () => log("Done!"))
}