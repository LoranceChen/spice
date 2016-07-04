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


object InsertArray2 extends App {
  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("test")

  val collection = database.getCollection("test_index2")

  //result:(NOT expect)
  /**
    * {
        "_id" : ObjectId("577647c281097f530523247e"),
        "name" : "name_1",
        "array" : [
            [ //NOT expect
                {
                    "a" : "a_1",
                    "b" : "b_1"
                }
            ],
            [
                {
                    "a" : "a_2",
                    "b" : "b_2"
                }
            ],...
        ]
      }
    */
  val documentsArrayOlder =  "array" -> (-10 to -1 map(x =>  List(Document("a" -> s"a_$x", "b" -> s"b_$x"))))

  //long time needs
  println("begin time - " + System.currentTimeMillis() / 1000)
  val documentsArray =  "array" -> (-10 to -1 map(x =>  Document("a" -> s"a_$x", "b" -> s"b_$x")))
  println("end time - " + System.currentTimeMillis() / 1000)

  println("==================")

  println("begin time - " + System.currentTimeMillis() / 1000)
  val documents = 1 to 50 map(x => Document(documentsArray) ++ Document("name" -> s"name_$x") )
  println("end time - " + System.currentTimeMillis() / 1000)

  println("ready insert ...")
  val insertObv = collection.insertMany(documents)
  println("ready insert completed")
  insertObv.subscribe(
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
