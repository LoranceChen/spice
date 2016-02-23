package spice.utility.charset

import java.lang.Character.UnicodeScript
import java.nio.charset.StandardCharsets

import scala.annotation.tailrec

/**
  * many language in the global, it's utility to distinguish usage by language
  */
object MatchCharset {

  /**
    * @param s aim String
    * @param codeScripts see doc about these enum https://docs.oracle.com/javase/7/docs/api/java/lang/Character.UnicodeScript.html
    * @return
    */
  def containsLanguage(s: String, codeScripts: Seq[UnicodeScript]): Boolean = {
    val bytes = s.getBytes()
    val strUTF8 = new String(bytes, StandardCharsets.UTF_8)
    @tailrec def countsHanScriptHelper(s: String, index: Int): Boolean = {
      if (s.length <= index) false
      else {
        val codePoint = s.codePointAt(index)
        val nextIndex = index + Character.charCount(codePoint)

        /**
          * alternative : see Character.isIdeographic match all of the CKJA language
          * HANGUL - 韩语
          * HAN - 汉语
          * HIRAGANA - 平假名
          * KATAKANA - 片假名
          */
        if (codeScripts.contains(Character.UnicodeScript.of(codePoint))) true
        else countsHanScriptHelper(s, nextIndex)
      }
    }
    countsHanScriptHelper(strUTF8, 0)
  }
}
