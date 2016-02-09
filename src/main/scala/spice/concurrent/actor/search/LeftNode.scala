package spice.concurrent.actor.search

import akka.actor.{Props, Actor, ActorRef}

import scala.collection.mutable

/**
  * main soul is the search node combine with LeafNode and parent node,
  *
  */
trait LeafNode {
  self: AdaptiveSearchNode =>

  var documents: Vector[String] = Vector()
  var index: mutable.HashMap[String,Seq[ScoredDocument]] = mutable.HashMap()

  def leafNode: PartialFunction[Any, Unit] = {
    case SearchQuery(query, maxDocs, handler) =>
      executeLocalQuery(query, maxDocs, handler)
    case SearchableDocument(content) =>
      addDocumentToLocalIndex(content)
  }

  private def executeLocalQuery(query: String, maxDocs: Int, handler: ActorRef) = {
    val result = for {
      results <- index.get(query).toList
      resultList <- results
    } yield resultList
    handler ! QueryResponse(result take maxDocs)
  }

  private def addDocumentToLocalIndex(content: String) = {
    documents = documents :+ content
    if(documents.size > MAX_DOCUMENTS) split()
    else for( (key, value) <- content.split("\\s+").groupBy(identity)) {
      val list = index.getOrElse(key, Seq())
      index += ((key, ScoredDocument(value.length.toDouble, content) +: list))
    }
  }

  protected def split(): Unit
}

case class SearchQuery(query: String, maxResults: Int, handler: ActorRef)

trait ParentNode {
  self: AdaptiveSearchNode =>
  var children = IndexedSeq[ActorRef]()
  def parentNode: PartialFunction[Any, Unit] = {
    case SearchQuery(q, max, responder) =>
      val gather: ActorRef = context.actorOf(Props(new GathererNode{
        val maxdics = max,
        val maxResponses = children.size,
        val query = q,
        val client = responder
      }))
      for(node <- children) {
        node ! SearchQuery(q, max, gather)
      }
    case s @ SearchableDocument(_) => getNextChild ! s
  }
}

class AdaptiveSearchNode extends Actor with ParentNode with LeafNode {
  def receive = leafNode

  protected def split(): Unit = {
    children = (for(docs <- documents grouped 5) yield {
      val child = context.actorOf(Props(classOf[AdaptiveSearchNode]))
      docs foreach (child ! SearchableDocument(_))
      child
    }).toIndexedSeq
    clearIndex()
    context.become(parentNode)
  }

  def makeTree = {
    val searchTree = context.actorOf(Props(new AdaptiveSearchNode {
      context.dispatcher = searchnodeddispatcher
    }))
    submitInitialDocuments(searchTree)
    searchTree
  }
}