package spice.mongodb

import org.bson.types.ObjectId
import org.mongodb.scala.bson.BsonArray
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{Completed, MongoDatabase, MongoClient}

/**
  * find(_id -> .., array.item01 -> ...)
  */
object InsertArray extends App {
  val mongoClient: MongoClient = MongoClient("10.1.1.222")

  val database: MongoDatabase = mongoClient.getDatabase("test")

  val collection = database.getCollection("hr_invitations")

  val documents =  1 to 100000 map(x => Document("name" -> s"name_$x", "array" -> List("a" -> s"a_$x", "b" -> s"b_$x")))//.++(Document())))
  val insertObv = collection.insertMany(documents)

  insertObv.subscribe((a: Completed) => {
    println(s"inserts - $a")
  })

  Thread.currentThread().join()
}


object InsertArray2 extends App {
  val mongoClient: MongoClient = MongoClient("mongodb://10.1.1.222")

  val database: MongoDatabase = mongoClient.getDatabase("resume")

  val collection = database.getCollection("hr_invitations")

  //result:(NOT expect)
  /**
    * {
    *"_id" : ObjectId("577647c281097f530523247e"),
    *"name" : "name_1",
    *"array" : [
    *[ //NOT expect
    *{
    *"a" : "a_1",
    *"b" : "b_1"
    *}
    *],
    *[
    *{
    *"a" : "a_2",
    *"b" : "b_2"
    *}
    *],...
    *]
    *}
    */
//  val documentsArrayOlder =  "array" -> (-10 to -1 map(x =>  List(Document("a" -> s"a_$x", "b" -> s"b_$x"))))

  //long time needs
  println("begin time - " + System.currentTimeMillis() / 1000)
  val documentsArray =  "invites" -> (0 to 100000 map(x =>  Document("career_id" -> new ObjectId(), "job_id" -> x.toLong)))

//  val documentsArray2 =  "invites" -> (0 to 100000 map(x =>  Document("career_id" -> new ObjectId(), "job_id" -> x.toLong)))
  println("end time - " + System.currentTimeMillis() / 1000)

  println("==================")

  println("begin time - " + System.currentTimeMillis() / 1000)
  val documents = 1 to 10 map(x => Document(documentsArray))
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
