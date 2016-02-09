package spice.concurrent.actor.search

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import akka.actor.{Props, ActorRef, Actor}
import akka.pattern._
import spice.concurrent.actor.Logger
import scala.collection.immutable.TreeMap
import scala.collection.mutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration._

case class SearchQuery(query: String, maxResults: Int)
case class ScoredDocument(score: Double, document: String)

/**
  * different SearchNode has different index, it seems should be subclass of SearchNode
  * as common, index as data form dao, such as database, memory cache.
  *
  * @param index
  */
class SearchNode(index: Map[String, Seq[ScoredDocument]]) extends Actor with Logger {
  override def receive = {
    case SearchQuery(query, maxResults) =>
      log.info(s"index - $index")
      sender ! index.getOrElse(query, Seq()).take(maxResults)
  }
}

object SearchNode {
  def props(scoredDoc: Map[String, Seq[ScoredDocument]]) = Props(classOf[SearchNode], scoredDoc)
}
/**
  *
  */
class HeadNode extends Actor with Logger {
  /**获取其他Actor的指针,直接操控其函数是危险的.很可能引起共享内存的问题.通过消息传递是好的通信方式*/
//  val nodes: Seq[SearchNode] = createSearchNode//new SearchNode {} :: Nil

  val nodes: Seq[ActorRef] =
    for(i<- 1 to 3) yield {
      context.actorOf(SearchNode.props(TreeMap(i.toString -> Seq(ScoredDocument(i, s"doc-$i"),ScoredDocument(i+1, s"doc-${i+1}")),
        (i+1).toString -> Seq(ScoredDocument(i+1, s"doc-${i*10+1}"),ScoredDocument(i+2, s"doc-${i*10+2}")))))
    }
  override def receive = {
    case s @ SearchQuery(query, maxResults) =>
      implicit val timeout = Timeout(2.seconds)

      //from now on, myself use Await to get result and deal with result in actor.
      //It's bad to use Await, but where should I Await?
      //1. give Future to non Actor system
      //2. use a AwaitActor for any Future
      val futureResults = nodes.map(n => n ? s).map{Await.result(_,Duration.apply(10,TimeUnit.SECONDS))}
      def combineResults(current: Seq[ScoredDocument],
                         next: Seq[ScoredDocument]) =
        (current ++ next).view.sortBy(_.score).take(maxResults).force
      val rst = futureResults.foldLeft(Seq[ScoredDocument]()) {
        (current, next) =>
          combineResults(current, next.asInstanceOf[Seq[ScoredDocument]])
      }
      rst.foreach{r => log.info(r.toString)}
  }
}

object HeadNode {

}