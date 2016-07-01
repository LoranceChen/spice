package spice.language.keywords

import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * for
  */
object forWords extends App {
  //for consume same type
//  for{
//    a <- Option(10)
//    b <- Try(100.toString)
//  } yield {
//    println(s"a - $a, b - $b")
//
//    for{
//      x <- Option("x")
////      y <- Future.successful("y")
//    }yield {
//      println(s"x - $x")//, y - $y")
//
//    }
//  }

  for{
    x <- Option(10)
    if x > 20
    y <- {println("hi"); Option("ha")}//not arrived
  } {
    println("rst - " + x)
  }

  val r = Option(10) match {
    case None => 2
    case Some(x) => x
  }
}
