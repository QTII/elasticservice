package elasticservice.util.ep.json12

import java.util.Date

import elasticservice.util.DataValid.isNotEmpty
import elasticservice.util.JsUtil
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.MkString
import elasticservice.util.ep.Record

object JSON12MkString extends MkString {
  def mkString(ep: ElasticParams): String = {
    "{" + "\"parameters\":" + mkStringFromParameters(ep.parameters) +
      ", \"datasets\":" + mkString(ep.datasets) + "}"
  }

  def EPTypeName = "json12"

  def mkStringFromParameters(m: Map[String, Any]): String = mkString(m + ("epType" -> EPTypeName), "{", "}")

  def mkString(ds: Dataset): String =
    "{" + "\"colInfos\":" + mkStringFromColumnInfos(ds.colInfos) +
      ", \"rows\":" + mkStringFromRows(ds.rows) + "}"

  private def mkStringFromColumnInfos(cs: List[ColumnInfo]): String = {
    val sb = new StringBuilder()
    sb.append("[")

    var first = true
    for (c <- cs) {
      if (!first) sb.append(", ")
      else first = false
      sb.append(mkString(c))
    }

    sb.append("]")
    sb.toString
  }

  def mkString(c: ColumnInfo): String = {
    val sb = new StringBuilder()
    sb.append("{")
    sb.append("\"id\":").append(JsUtil.encode(c.id))
    if (isNotEmpty(c.text)) sb.append(", ").append("\"text\":").append(JsUtil.encode(c.text))
    if (isNotEmpty(c.typeClass)) sb.append(", ").append("\"type\":").append(JsUtil.encode(c.typeName))
    if (isNotEmpty(c.size)) sb.append(", ").append("\"size\":").append(c.size)
    if (isNotEmpty(c.userAttrs)) sb.append(", ").append(" ").append(mkString(c.userAttrs, null, null))
    sb.append("}")
    sb.toString
  }

  private def mkStringFromRows(rs: List[Map[String, Any]]): String = {
    val sb = new StringBuilder()
    sb.append("[")

    var first = true
    for (r <- rs) {
      if (!first) sb.append(", ")
      else first = false
      sb.append(mkString(r))
    }

    sb.append("]")
    sb.toString
  }

  def mkString(m: Map[String, Any]): String = mkString(m, "{", "}")

  private def mkString(m: Map[String, Any], start: String, end: String): String = {
    var first = true
    val sb = new StringBuilder()

    if (isNotEmpty(start)) sb.append(start)

    m.foreach(kv => {
      if (!first) sb.append(", ")
      sb.append(mkString(kv._1, kv._2))
      first = false
    })

    if (isNotEmpty(end)) sb.append(end)
    sb.toString()
  }

  private def mkString(kv: (String, Any)): String = {
    "\"" + kv._1 + "\":" +
      (kv._2 match {
        case null          => "null"
        case s: String     => JsUtil.encode(s)
        case d: Date       => JsUtil.encode(d.toString)
        case d: Dataset    => mkString(d)
        case m: Record[_]  => mkString(m)
        case c: ColumnInfo => mkString(c)
        case a             => JsUtil.encode(a.toString)
      })
  }
}