package elasticservice.util

import java.util.Locale

class CaseInsensitiveString(val original: String) {

  override def equals(obj: Any): Boolean = {
    obj match {
      case null => false
      case s: CaseInsensitiveString => original.equalsIgnoreCase(s.original)
      case s: String => original.equalsIgnoreCase(s)
      case _ => false
    }
  }

  override def hashCode = 31 * original.toLowerCase(Locale.ENGLISH).hashCode

  override def toString = original
}

object CaseInsensitiveString {
//  def main(args: Array[String]) = {
//    println("Aa".equals(new CaseInsensitiveString("aA")))
//    println(new CaseInsensitiveString("aA").equals("Aa"))
//  }
}