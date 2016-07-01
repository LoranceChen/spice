package spice.mongodb

import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{Completed, MongoDatabase, MongoClient}

/**
  * find(_id -> .., array.item01 -> ...)
  */
object InsertArray extends App {
  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("test")

  val collection = database.getCollection("test_index2")

  val documents =  1 to 100000 map(x => Document("name" -> s"name_$x", "array" -> List("a" -> s"a_$x", "b" -> s"b_$x")))//.++(Document())))
  val insertObv = collection.insertMany(documents)

  insertObv.subscribe((a: Completed) => {
    println(s"inserts - $a")
  })

  Thread.currentThread().join()
}
