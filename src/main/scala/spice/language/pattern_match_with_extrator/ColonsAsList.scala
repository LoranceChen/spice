package spice.language.pattern_match_with_extrator

import scala.collection.mutable

object ColonsAsList extends App {
  //add extractor for Seq[Byte] - it works
//  object :: {
//    def unapply(queue: Seq[Byte]): Option[(Byte, Seq[Byte])] = {
//      if (queue.nonEmpty)
//        Some(queue.head, queue.tail)
//      else None
//    }
//  }

  //case class
  case class ::: (heads: Byte, theTail: mutable.Queue[Byte]) extends mutable.Queue[Byte]// works

  case class ::::: ( heads: Byte, tailss: Seq[Byte]) extends Seq[Byte] { // not works
    override def length: Int = 0
    override def apply(idx: Int) = {
      1.toByte
    }
    def unapply(queue: Seq[Byte]): Option[(Byte, Seq[Byte])] = {
      if (queue.nonEmpty)
        Some(queue.head, queue.tail)
      else None
    }
    override def iterator: Iterator[Byte] = Iterator.empty
  }

  final case class :::: (override val head: Int, override val tail: Seq[Int]) extends Seq[Int] { //not works
    override def length: Int = tail.length + 1
    override def iterator: Iterator[Int] = (head :: tail.toList).toIterator
    override def apply(idx: Int) = {
      1.toByte // just for simple
    }
  }

  //QUESTION: Why can't do this?
  //  case class ::: (heads: Byte, theTail: mutable.Queue[Byte])

  def doMatchWithCustom(queue: Seq[Byte]) = {
    queue match {
      case h :: t => println("custom - Good! ^_^")
      case _ => println("custom - God! >_<")
    }
  }

  def doMatchWithCaseClass(queue: Seq[Int]) = {
    queue match {
      case h :::: t => println("case class - Good! ^_^")
      case x =>
        println(s"case class - God >_<! $x")
    }
  }

  doMatchWithCustom(Seq(1,2,3))
  doMatchWithCaseClass(Seq(1,2,3))
}
