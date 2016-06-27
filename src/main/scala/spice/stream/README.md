Rx series is not for my taste.
a important reason is this code:
```scala
import rx.lang.scala.{Subscriber, Observable}
import spice.concurrent.log
object PublishAndConnect extends App {
  val obv = Observable[Int]{s =>
    val x = log("obv - ")
		s.onNext(1)
		s.onNext(2)
  }//.publish

  val obvMapped = obv.map{ o =>
    log("map 01")
    o + "2"
  }

  var x = 1
  val obvPublish = obv.map{s => log(s"obv - publish"); 2*s}//.publish

  obv.subscribe(s => log("obv - sub01" + s))
  obv.subscribe(s => log("obv - sub02" + s))
  obvPublish.subscribe(s => log("sub01 - " + s))
  obvPublish.subscribe(s => log("sub02 - " + s))
////obvPublish.connect
  //obv.connect

  println("x - "+x)
  Thread.currentThread().join()
}
```
a problem is log(s"obv - publish") execute 4 times because it emit 2 event to 2 subscribe.It is	strange why exe 4 times, execute for every emit is grate.
Besides, we can use publish(see )