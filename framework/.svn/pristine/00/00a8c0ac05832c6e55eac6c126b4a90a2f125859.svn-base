package elasticservice.util

object JsUtil {
  def encode(str: String): String = {
    if (str.isEmpty) return "\"\"";

    val sb = new StringBuilder
    sb.append('"')

    var before = ' '
    str.foreach { c =>
      c match {
        case '\\' | '"' => sb.append('\\').append(c)
        case '/' =>
          if (before == '<') sb.append('\\')
          sb.append(c)
        case '\b' => sb.append('\\').append('b')
        case '\t' => sb.append('\\').append('t')
        case '\n' => sb.append('\\').append('n')
        case '\f' => sb.append('\\').append('f')
        case '\r' => sb.append('\\').append('r')
        case ch if ch < ' ' =>
          val t = "000" + Integer.toHexString(ch)
          val tLength = t.length()
          sb.append('\\')
          sb.append('u')
          sb.append(t.charAt(tLength - 4))
          sb.append(t.charAt(tLength - 3))
          sb.append(t.charAt(tLength - 2))
          sb.append(t.charAt(tLength - 1))
        case _ =>
          sb.append(c)
      }
      before = c
    }
    sb.append('"')
    sb.toString
  }
}