package elasticservice.util.sqlrepo.sql

import elasticservice.util.DataValid.isEmpty
import elasticservice.util.ep.ColumnAttributes
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.sqlrepo.SqlXmlLoader

case class TextPart(textList: List[Any], cols: Array[ColumnInfo], substitutions: Array[String]) {

  def text(record: Map[String, Any], preparedStmt: Boolean): String = {
    def appendParamValue(sb: StringBuilder, col: ColumnInfo, value: Any) = {
      value match {
        case s: String         => sb ++= "'" + s + "'"
        case d: java.sql.Date  => sb ++= "'" + d.toString + "'"
        case d: java.util.Date => sb ++= "'" + d.toString + "'"
        case _ => if (col.containsAttr(ColumnAttributes.OUT))
          sb ++= col.id + "(" + col.userAttrs + "):" + col.typeClass
        else
          sb ++= value.toString
      }
    }

    val sb = new StringBuilder()
    var altIdx = 0
    var colIdx = 0

    textList.foreach { x =>
      x match {
        case s: String => sb ++= s
        case SqlXmlLoader.CD_TEXT_SUBSTITUTION => {
          if (isEmpty(record))
            sb ++= "${" + substitutions(altIdx) + "}"
          else
            sb ++= record.getOrElse(substitutions(altIdx), "").toString
          altIdx += 1
        }
        case SqlXmlLoader.CD_SQL_PARAM => {
          if (preparedStmt) {
            sb ++= "?"
          } else {
            if (isEmpty(record))
              appendParamValue(sb, cols(colIdx), "")
            else
              appendParamValue(sb, cols(colIdx), record.getOrElse(cols(colIdx).id, ""))
            colIdx += 1
          }
        }
      }
    }
    sb.toString
  }

  def text: String = toString
  
  override def toString = text(Map[String, Any](), true)
}