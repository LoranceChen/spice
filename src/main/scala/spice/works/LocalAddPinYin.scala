package spice.works

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import net.liftweb.json.JsonAST.{JField, JObject, JString}
import net.liftweb.json.JsonParser

/**
  *
  */
object LocalAddPinYin extends App{
  val miniPath = "/Users/lorancechen/version_control_project/_works/util/base/src/test/resources/chs_mini.json"
  val miniPathCHs = "/Users/lorancechen/version_control_project/_works/util/base/src/main/resources/location/chs.json"

  def readFile( path: String, encoding: Charset) = {
    val encoded = Files.readAllBytes(Paths.get(path))
    new String(encoded, encoding)
  }

  //    val sourceCHS = Source.fromInputStream(getClass.getResourceAsStream("/location/chs.json"))(Codec.UTF8)
  val sourceMiniCHS = readFile(miniPathCHs,java.nio.charset.StandardCharsets.UTF_8)

  //    val sourceStatistic = Source.fromInputStream(getClass.getResourceAsStream("/location/statistic.json"))(Codec.UTF8)
//  val sourceNewStatistic = readFile(pathStatistic, java.nio.charset.StandardCharsets.UTF_8)

  val dealSource = {
    JsonParser.parse(sourceMiniCHS) match {
      case jObject: JObject => jObject
      case other => sys.error("Unrecognized Locale file content: " + other)
    }
  }
//
  val getCountryAndName = {
    dealSource.obj.map{country =>
      val t = (country.value \ "n").values
      ((country.value \ "n").values.asInstanceOf[String], (country.value \ "py").values.asInstanceOf[String])
    }
  }

  //todo get form html and filter to this format
  val x= Map("中国,中华" -> "zhong guo")

  val testJson = """{
      "CHS": {
        "n": "中国",
        "11": {
          "n": "北京",
          "1": {
            "n": "东城"
          }
        },
        "hasUniversity": true
      },
      "CAF": {
        "n": "中非共和国",
        "0": {
          "n": "",
          "BB": {
            "n": "巴明吉-班戈兰"
          }
        },
        "hasUniversity": false
      }
    }"""

  //todo get JObject from chs.json file
  val miniJ = JsonParser.parse(testJson).asInstanceOf[JObject]
  miniJ.obj.map{ country =>
    x.find(_._1.trim == (country.value \ "n").asInstanceOf[String]) match {
      case Some((n, py)) => country.value.merge(JObject(JField("py", JString(py))::Nil))
      case None => country.value.merge(JObject(JField("py", JString("未知"))::Nil))
    }
  }

  //todo save to chs2.json

  //todo use chs2.json replace chs.json
}
