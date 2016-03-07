package elasticservice.util

object StringUtil {
  def splitWithoutEmpty(str: String, regex: String): Array[String] =
    str.split(regex).filter { x => x.trim() != "" }

  def isBlank(str: String): Boolean =
    str == null || str.isEmpty || str.forall(c => c.isSpaceChar || c.isWhitespace)

  def indexOfNonSpace(str: String): Int =
    if (isBlank(str)) -1
    else str.indexWhere { x => !x.isSpaceChar && !x.isWhitespace }

  def trimFront(str: String): String =
    indexOfNonSpace(str) match {
      case -1 => str
      case i  => str.substring(i)
    }

  def trimBack(str: String): String = {
    val s = str.reverse
    indexOfNonSpace(s) match {
      case -1 => str
      case i  => s.substring(i).reverse
    }
  }
}