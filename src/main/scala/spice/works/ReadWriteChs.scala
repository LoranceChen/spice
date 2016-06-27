package spice.works

import java.io.{FileOutputStream, PrintStream}
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}

import net.liftweb.json.JsonAST._
import net.liftweb.json.{DefaultFormats, JsonParser, pretty}

/**
 * Edit location file
 */
object ReadWriteChs extends App {
  val path111 = "/Users/lorancechen/git/util/base/src/main/resources/location/111.json"
  val pathChs = "/Users/lorancechen/git/util/base/src/main/resources/location/chs.json"
  val pathchs_changed = "/Users/lorancechen/git/util/base/src/main/resources/location/chs_changed.json"
  val pathStatistic = "/Users/lorancechen/git/util/base/src/main/resources/location/statistic.json"

  def readFile( path: String, encoding: Charset) = {
    val encoded = Files.readAllBytes(Paths.get(path))
    new String(encoded, encoding)
  }

  def ready = {
//    val sourceCHS = Source.fromInputStream(getClass.getResourceAsStream("/location/chs.json"))(Codec.UTF8)
    val sourceNewCHS = readFile(pathChs,java.nio.charset.StandardCharsets.UTF_8)

//    val sourceStatistic = Source.fromInputStream(getClass.getResourceAsStream("/location/statistic.json"))(Codec.UTF8)
    val sourceNewStatistic = readFile(pathStatistic, java.nio.charset.StandardCharsets.UTF_8)

    val dealSource = (content: String) => {
      JsonParser.parse(content) match {
        case jObject: JObject => jObject
        case other => sys.error("Unrecognized Locale file content: " + other)
      }
    }

    val prasedCHS = dealSource(sourceNewCHS)
    val prasedStatistic = dealSource(sourceNewStatistic)

    implicit val formats = DefaultFormats
    val stas = prasedStatistic.extract[Statistic]
    (prasedCHS, prasedStatistic, stas)
  }

  def deal(chs: JObject, stastic: Statistic) = {
    val addon = chs.obj.map{ country =>
      val has = if (stastic.statistic.exists(_._id == country.name)) true else false
      val addition = country.value.merge(JObject(JField("hasUniversity", JBool(has)) :: Nil))
      JField(country.name, addition)
    }
    pretty(render(JObject(addon)))
  }

  def saveToFile(jStr: String, path: String = "/Users/lorancechen/git/util/base/src/main/resources/location/chs_changed.json") = {
    try {
      val out = new PrintStream(new FileOutputStream(path))
      out.print(jStr);
    }
  }

  val jStr = ReadWriteChs.deal(ReadWriteChs.ready._1, ReadWriteChs.ready._3)
  saveToFile(jStr)
}

//for lift json extractor
case class IdAndCount(_id: String, count: Int)
case class Statistic(statistic: List[IdAndCount])