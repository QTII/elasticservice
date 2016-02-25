package elasticservice.util

object TextUtil {
  val LineSeparator = sys.props("line.separator")

  def removeComment(text: String,
                    lineCommentMark: String,
                    blockCommentOpenMark: String,
                    blockCommentCloseMark: String): String = {
    var dst = text
    if (DataValid.isNotEmpty(lineCommentMark))
      dst = removeLineComment(text, lineCommentMark)

    if (DataValid.isNotEmpty(blockCommentOpenMark)
      && DataValid.isNotEmpty(blockCommentCloseMark))
      dst = removeBlockComment(dst, blockCommentOpenMark,
        blockCommentCloseMark)

    dst
  }

  def removeLineComment(text: String, lineCommentMark: String): String = {
    val lines = text.split("\n")
    val sb = new StringBuilder()
    for (line <- lines) {
      val idx = line.indexOf(lineCommentMark)
      if (idx == 0) {
      } else if (idx > 0) {
        sb.append(line.substring(0, idx)).append("\n")
      } else {
        sb.append(line).append("\n")
      }
    }
    sb.toString()
  }

  def removeBlockComment(text: String,
                         blockCommentOpenMark: String, blockCommentCloseMark: String): String = {
    val sb = new StringBuilder()
    var pos = 0
    while (pos < text.size) {
      val idx = text.indexOf(blockCommentOpenMark, pos)
      if (idx >= 0) {
        sb.append(text.substring(pos, idx))
        val idx2 = text.indexOf(blockCommentCloseMark, idx
          + blockCommentOpenMark.length())
        if (idx2 > idx) {
          pos = idx2 + blockCommentCloseMark.length()
        } else {
          sb.append(text.substring(idx))
          return sb.toString()
        }
      } else {
        sb.append(text.substring(pos))
        return sb.toString()
      }
    }
    return sb.toString()
  }

  def indexOfNonSpace(text: String): Int = {
    if (StringW.isBlank(text)) -1
    else text.indexWhere { x => !x.isSpaceChar && !x.isWhitespace }
  }
}