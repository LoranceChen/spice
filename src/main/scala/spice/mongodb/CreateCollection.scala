package spice.mongodb

import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.{Completed, ServerAddress, MongoClientSettings, MongoClient, MongoDatabase}

/**
  *
  */
object CreateCollection extends App {
  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("test")

  val createStream = database.createCollection("test_index").recover {
    case e: Throwable =>
      println("recovery - ")
      Completed()
  }

  //notice createStream execute when has someone subscribe it.
  createStream.subscribe(
    (x: Completed) => {
      println("result - " + x)
    },
    (e: Throwable) => {
      println("throwable - " + e)
    },
    () => {
      println("completed - ")
    }
  )

  Thread.currentThread().join()
}
