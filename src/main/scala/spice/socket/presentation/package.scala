package spice.socket

import java.nio.ByteBuffer

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * UUID use 0x1000,0100L ~ 0x1000,01ffL
  */
package object protocol {

  /**
    * 1.dequeue elems
    * 2.enqueue Seq elems
    */
  implicit class mutableQueueAuthix(queue: mutable.Queue[Byte]) {
    final def dequeue(count: Int): mutable.Queue[Byte] = {
      @tailrec def tailrecDequeue(count: Int, newQuene: mutable.Queue[Byte]): mutable.Queue[Byte] = {
        /*require用于表达API契约,是对输入的断言;assert是对处理的结果的断言*/
        require(queue.length >= count)
        count match {
          case x if x <= 0 => mutable.Queue.empty[Byte]
          case x =>
            newQuene.enqueue(queue.dequeue())
            tailrecDequeue(count - 1, newQuene)
        }
      }

      tailrecDequeue(count, mutable.Queue.empty[Byte])
    }

    @tailrec final def enqueue(bytes: Seq[Byte]): Unit = {
      bytes match {
        case _ if bytes.isEmpty => Unit
        case ::(head, tail) =>
          queue.enqueue(head)
          queue.enqueue(tail)
      }
    }

    //todo it seems not easy to deal with
    def toInt() = {
      val bb = ByteBuffer.allocate(32)
      bb.getInt()
    }

    object :: {
      def unapply(queue: Seq[Byte]): Option[(Byte, Seq[Byte])] = {
        if (queue.nonEmpty)
          Some(queue.head, queue.tail)
        else None
      }
    }
//  private[protocal] case class ::: (heads: Byte, theTail: mutable.Queue[Byte]) extends mutable.Queue[Byte]// works
  }
}






