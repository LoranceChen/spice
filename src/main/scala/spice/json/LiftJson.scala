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