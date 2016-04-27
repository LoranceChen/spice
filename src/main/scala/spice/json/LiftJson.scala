package spice.json

import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Extraction._

import scala.reflect.Manifest

/**
  *
  */
trait LiftJson {
  implicit val formats = net.liftweb.json.DefaultFormats

  /**
    * Extractor
    */
  def valueSerializer[A](value: A)(implicit mf: Manifest[A]): String = compactRender(decompose(value))
  def valueDeserializer[A](value0: String)(implicit mf: Manifest[A]): A = parse(value0).extract[A]

}

object test extends App with LiftJson {
  /**
    * render list
    */
  def list = {
    val x= prettyRender("templates" -> List(
      ("intentId" -> "i01") ~
        ("miniResume" ->
          List("m01", "m02")
          ),
      ("intentId2" -> "i02") ~
        ("miniResume" ->
          List("m01", "m03")
          )
    ))
  }

  /**
    * extractor
    */
  case class ResumeMini(title: String, lpUrl: String)
  case class HotTemplatesSnapShot(intentionId: String, resumeMinis: List[ResumeMini])

  val ser = valueSerializer(HotTemplatesSnapShot("1", ResumeMini("a", "b"):: ResumeMini("a1","b1"):: Nil) ::
    HotTemplatesSnapShot("2", ResumeMini("a", "b"):: ResumeMini("a1","b1"):: Nil) :: Nil
  )
  val deser = valueDeserializer(ser)
  println(ser+ "\n" + deser)
}

object Remove extends App {
  val jstr = """{
        "sid": 9527,
        "uid": null,
        "user_receive_time": "2014-03-17 22:55:21",
        "error_msg": "",
        "mobile": "15205201314",
        "report_status": "SUCCESS"
    }"""

  parseOpt(jstr).foreach { jValue =>
    jValue match {
      case jo: JObject =>
        val x = jo.obj.removeField(_.name == "sid")
        x
      case _ => ???
    }
  }
}