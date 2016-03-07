package elasticservice.util

import scala.collection.mutable.Buffer

import elasticservice.epMkString
import elasticservice.util.ep.Record

class ReplaceableText(val srcText: String) {
  private val CD_VAR = 1

  private var values: Record[Any] = Record.empty[Any]
  private var compiled: TextCompiled = null
  private var compile_nextPos = 0
  private var compile_detectedVar: String = null;

  private var _text = srcText

  var varMark = '$'

  def variables = values

  class TextCompiled(val textList: List[Any], val vars: Array[String]) {
    def getText(values: Map[String, Any]): String = {
      val sb: StringBuilder = new StringBuilder()

      if (textList.size == 1) {
        sb.append(textList(0))
      } else {
        var varIdx = 0
        for (obj <- textList) {
          obj match {
            case s: String => sb.append(obj)
            case CD_VAR =>
              val key = vars(varIdx)
              varIdx += 1
              val value = if (values != null) values.getOrElse(key, null) else null
              if (value != null) {
                sb.append(value)
              } else {
                sb.append(varMark + "{" + key + "}")
              }
          }
        }
      }
      sb.toString()
    }

    override def toString(): String = getText(null)
  }

  def setValues(map: Map[String, Any]): ReplaceableText = {
    if (map != null) values ++= map
    this
  }

  def setValue(kv: (String, Any)): ReplaceableText = {
    values += kv
    this
  }

  override def toString(): String = {
    if (compiled == null)
      compileText()

    compiled.getText(values)
  }

  private def compileText() {
    val textsBuf = Buffer.empty[Any]
    val varsBuf = Buffer.empty[String]

    val lines = _text.split("\n")

    var lineIdx = 0
    for (line <- lines) {
      val c = StringUtil.indexOfNonSpace(line);

      if (c == -1) {
        textsBuf += line
      } else {
        if (c > 0)
          textsBuf += line.substring(0, c)

        val sb = new StringBuilder()
        var pos = c
        while (pos < line.size) {
          if (compileVer1(line, pos, varMark)
            || compileVer2(line, pos, varMark)) {
            varsBuf += compile_detectedVar
            if (!sb.isEmpty) {
              textsBuf += sb.toString()
              sb.clear()
            }
            textsBuf += CD_VAR
            pos = compile_nextPos
          } else {
            sb.append(line.charAt(pos))
            pos += 1
          }
        }

        if (!sb.isEmpty)
          textsBuf += sb.toString()

        if (lineIdx < lines.size - 1)
          textsBuf += "\n"
        lineIdx += 1
      }
    }
    compiled = new TextCompiled(textsBuf.toList, varsBuf.toArray);
  }

  private def compileVer1(text: String, pos: Int, mark: Char): Boolean = {
    if (text.charAt(pos) == mark && pos + 2 < text.size) {
      if (text.charAt(pos + 1) != '{') {
        val close = text.indexOf(mark, pos + 1)
        if (close > 0) {
          val name = text.substring(pos + 1, close)
          if (validVariableName(name)) {
            compile_detectedVar = name
            compile_nextPos = close + 1
            return true
          }
        }
      }
    }
    false
  }

  private def compileVer2(text: String, pos: Int, mark: Char): Boolean = {
    if (text.charAt(pos) == mark && pos + 3 < text.size) {
      if (text.charAt(pos + 1) == '{') {
        val close = text.indexOf('}', pos + 1)
        if (close > 0) {
          val name = text.substring(pos + 2, close);
          if (validVariableName(name)) {
            compile_detectedVar = name
            compile_nextPos = close + 1
            return true
          }
        }
      }
    }
    false
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
      case _         => str.substring(from, until)
    }
    !s.exists { !validVariableChar(_) }
  }
}

object ReplaceableText {
  def apply(str: String): ReplaceableText = new ReplaceableText(str)
}