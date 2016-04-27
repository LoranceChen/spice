package spice.json

import net.liftweb.json._
/**
  *
  */
object Extractors extends App {
  implicit val formats = net.liftweb.json.DefaultFormats
  val jStr = """{"id":"552cec73d4c6f97a57508d85","careerName":"com333","isManager":true,"companyId":"15"}"""
  case class Req(id: String, careerName: String, isManager: Boolean, companyId: String)
  val p = parse(jStr)
  val r = parse(jStr).extractOpt[Req]
  r
}
