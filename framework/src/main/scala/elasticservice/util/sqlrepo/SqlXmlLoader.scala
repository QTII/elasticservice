package elasticservice.util.sqlrepo

import java.io.File
import java.sql.Types

import scala.collection.mutable.Buffer
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.xml.Elem
import scala.xml.Node
import scala.xml.Text

import com.typesafe.scalalogging.LazyLogging

import elasticservice.util.GenXML
import elasticservice.util.StringUtil
import elasticservice.util.TextUtil
import elasticservice.util.XMLUtil
import elasticservice.util.ep.ColumnAttributes
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.DatasetUtil
import elasticservice.util.sqlrepo.sql.ChkValDynaPart
import elasticservice.util.sqlrepo.sql.CmpValDynaPart
import elasticservice.util.sqlrepo.sql.ColMap
import elasticservice.util.sqlrepo.sql.DynaText
import elasticservice.util.sqlrepo.sql.IterateDynaPart
import elasticservice.util.sqlrepo.sql.MultiPart
import elasticservice.util.sqlrepo.sql.MultiPartImpl
import elasticservice.util.sqlrepo.sql.SelectKeyPart
import elasticservice.util.sqlrepo.sql.Sql
import elasticservice.util.sqlrepo.sql.SqlType
import elasticservice.util.sqlrepo.sql.TextPart

class SqlXmlLoader extends LazyLogging {

  private var compile_nextPos = 0
  private var compile_detectedVar = ""

  def loadFile(pkg: String, sqlId: String): Try[Sql] = {
    val xmlPath = SqlRepo.filePath(pkg, sqlId)
    GenXML.toString(new File(xmlPath), None) match {
      case Success(xmlStr) => loadString(xmlStr)
      case Failure(e)      => Failure(e)
    }
  }

  def loadString(xmlStr: String): Try[Sql] = {
    Try {
      GenXML.loadString(xmlStr) match {
        case Success(xml) =>
          xml.find(SqlType.isValidStmt) match {
            case Some(n) => parseToSql(n) match {
              case Success(s) => s
              case Failure(e) => throw e
            }
            case None => throw new Exception("inappropriate XML")
          }
        case Failure(e) => throw e
      }
    }
  }

  private def parseToSql(stmtNode: Node): Try[Sql] = {
    Try {
      val sql = new Sql()
      sql.sqlType = SqlType.forName(stmtNode.label)

      parseStmtPart(sql, stmtNode) match {
        case Success(p) => sql.setMultiPart(p)
        case Failure(e) => throw e
      }
      sql
    }
  }

  private def parseStmtPart(sql: Sql, stmtNode: Node): Try[MultiPart] = {
    Try {
      var multiPart = stmtNode.label.toLowerCase match {
        // prepend="a.table_name IN" open="(" close=")" conjunction=","
        case "iterate" =>
          val openAttr = (stmtNode \ "@open").text
          val closeAttr = (stmtNode \ "@close").text
          val conjunctionAttr = (stmtNode \ "@conjunction").text
          IterateDynaPart(openAttr, closeAttr, conjunctionAttr)

        case "selectKey" =>
          val keyPropertyAttr = (stmtNode \ "@keyProperty").text
          val resultClassAttr = (stmtNode \ "@resultClass").text
          sql.selectKeyPart = SelectKeyPart(keyPropertyAttr, resultClassAttr)
          if (keyPropertyAttr != null) {
            sql.selectKeyPart.foreach(skp =>
              sql.addColMap(ColMap(skp.keyProperty, skp.resultClass, 1, Types.VARCHAR)))
          }
          sql.selectKeyPart.get

        case _ =>
          val propertyAttrNode = (stmtNode \ "@property")
          val valAttrNode = (stmtNode \ "@compareValue")

          if (!propertyAttrNode.isEmpty && !valAttrNode.isEmpty) {
            CmpValDynaPart(propertyAttrNode.text, VisibleCondition.forName(stmtNode.label), valAttrNode.text)
          } else if (!propertyAttrNode.isEmpty && valAttrNode.isEmpty) {
            ChkValDynaPart(propertyAttrNode.text, VisibleCondition.forName(stmtNode.label))
          } else {
            MultiPartImpl()
          }
      }

      (stmtNode \ "@prepend").text match {
        case null | "" =>
        case p         => multiPart.setPrepend(p)
      }

      for (c <- stmtNode.child) {
        c match {
          case t: Text =>
            if (StringUtil.indexOfNonSpace(t.text) != -1)
              multiPart += parseTextPart(sql, t.text, multiPart.isInstanceOf[DynaText])
          case e: Elem =>
            parseStmtPart(sql, e) match {
              case Success(subMultiPart) => multiPart += subMultiPart
              case Failure(e)            => throw e
            }
        }
      }
      multiPart
    }
  }

  private def parseTextPart(sql: Sql, srcText: String, dynaText: Boolean): TextPart = {
    val colInfoBuf = Buffer[ColumnInfo]()
    val altBuf = Buffer[String]()
    val textBuf = Buffer[Any]()

    val text = TextUtil.removeComment(srcText, "--", "/*", "*/")
    val lines = text.split("\n")

    for (line <- lines) {
      StringUtil.indexOfNonSpace(line) match {
        case -1 =>
        case c =>
          val targetLine = XMLUtil.unescape(StringUtil.trimBack(line))
          var sb = new StringBuilder()
          var pos = 0
          while (pos < targetLine.size) {
            if (compileVer1(targetLine, pos, '#')
              || compileVer2(targetLine, pos, '#')) {
              val col = DatasetUtil.parseColumnExpression(compile_detectedVar.toUpperCase)
              col.inDynaText = dynaText
              colInfoBuf += col
              if (col.containsAttr(ColumnAttributes.OUT)) {
                sql.sqlType = SqlType.toCallable(sql.sqlType)
              }

              if (!sb.isEmpty) {
                textBuf += sb.toString
                sb = new StringBuilder()
              }
              textBuf += SqlXmlLoader.CD_SQL_PARAM
              pos = compile_nextPos
            } else if (compileVer1(targetLine, pos, '$')
              || compileVer2(targetLine, pos, '$')) {
              altBuf += compile_detectedVar.toUpperCase()
              if (!sb.isEmpty) {
                textBuf += sb.toString
                sb = new StringBuilder()
              }
              textBuf += SqlXmlLoader.CD_TEXT_SUBSTITUTION
              pos = compile_nextPos
            } else {
              sb.append(targetLine.charAt(pos))
              pos += 1
            }
          }
          if (!sb.isEmpty) textBuf += sb.toString
      }
      textBuf += SqlXmlLoader.CdNewLine
    }

    TextPart(textBuf.toList, colInfoBuf.toArray, altBuf.toArray)
  }

  private def compileVer1(text: String, pos: Int, mark: Char): Boolean = {
    if (text.charAt(pos) == mark && pos + 2 < text.length()) {
      if (text.charAt(pos + 1) != '{') {
        val close = text.indexOf(mark, pos + 1)
        if (close > 0) {
          val name = text.substring(pos + 1, close)
          if (SqlXmlLoader.validVariableName(name)) {
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
    if (text.charAt(pos) == mark && pos + 3 < text.length()) {
      if (text.charAt(pos + 1) == '{') {
        val close = text.indexOf('}', pos + 1)
        if (close > 0) {
          val name = text.substring(pos + 2, close)
          if (SqlXmlLoader.validVariableName(name)) {
            compile_detectedVar = name
            compile_nextPos = close + 1
            return true
          }
        }
      }
    }
    false
  }
}

object SqlXmlLoader {
  val CD_SQL_PARAM = 0
  val CD_TEXT_SUBSTITUTION = 1
  val CdNewLine = 2

  private def validVariableName(str: String): Boolean = {
    validVariableName(str, 0, str.size)
  }

  private def validVariableName(str: String, fromIdx: Int, untilIdx: Int): Boolean = {
    str.substring(fromIdx, untilIdx).forall { validVariableChar(_) }
  }

  private def validVariableChar(ch: Char): Boolean = {
    if ((48 <= ch && ch <= 57) || (65 <= ch && ch <= 90) || ch == '_'
      || ch == '[' || ch == ']' || ch == '.' || ch == ':'
      || ch == '?' || (97 <= ch && ch <= 122))
      true
    else
      false
  }
}