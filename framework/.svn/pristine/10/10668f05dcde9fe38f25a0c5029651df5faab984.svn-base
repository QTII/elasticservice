package test.elasticservice.util

object ValidVariableNameTest {
  
  def main(args: Array[String]) {
    println(validVariableName("ctml>\n<a     111${ABC"))
  }
  
  private def validVariableChar(ch: Char): Boolean = {
    if (ch.isWhitespace || ch.isSpaceChar)
      false
    else if ((48 <= ch && ch <= 57) || (65 <= ch && ch <= 90) || ch == '_'
      || ch == '[' || ch == ']' || ch == '.' || ch == ':'
      || ch == '?' || (97 <= ch && ch <= 122))
      true
    else
      false
  }

  private def validVariableName(str: String): Boolean = validVariableName(str, 0, str.size)

  private def validVariableName(str: String, from: Int, until: Int): Boolean = {
    val size = str.size
    val s = (from, until) match {
      case (0, size) => str
      case _            => str.substring(from, until)
    }
    !s.exists { !validVariableChar(_) }
  }
}