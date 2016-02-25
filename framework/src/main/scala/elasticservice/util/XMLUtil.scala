package elasticservice.util

import scala.xml.Utility

object XMLUtil {
  private val CH_LESS = "&lt;"
  private val CH_GREATER = "&gt;"
  private val CH_AMP = "&amp;"

  def unescape(text: String): String = {
    val sb = new StringBuilder()
    var idx = 0

    while (idx < text.size) {
      val tmp = text.indexOf('&', idx)
      if (tmp >= 0) {
        sb.append(text.substring(idx, tmp))

        if (text.startsWith(CH_LESS, tmp)) {
          sb.append('<')
          idx = tmp + CH_LESS.length()
        } else if (text.startsWith(CH_GREATER, tmp)) {
          sb.append('>')
          idx = tmp + CH_GREATER.length()
        } else if (text.startsWith(CH_AMP, tmp)) {
          sb.append('&')
          idx = tmp + CH_AMP.length()
        } else {
          sb.append('&')
          idx = tmp + 1
        }
      } else {
        sb.append(text.substring(idx))
        idx = text.size
      }
    }
    sb.toString()
  }

  def escape(text: String): String = Utility.escape(text)
}