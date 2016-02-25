package test.elasticservice.util.ep

import java.util.Date
import elasticservice.util.DataValid.isNotEmpty
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.MkString

object TestMkString extends MkString {
  def mkString(ep: ElasticParams): String = {
    "*{" + "\"parameters\":" + mkStringFromParameters(ep.parameters) +
      ", \"datasets\":" + mkStringFromDatasets(ep.datasets) + "}*"
  }
  
  def EPTypeName = "test"

  def mkStringFromParameters(m: Map[String, Any]): String = mkString(m)

  def mkStringFromDatasets(m: Map[String, Any]): String = mkString(m)

  def mkString(ds: Dataset): String =
    "*{" + "\"colInfos\":" + mkStringFromColumnInfos(ds.colInfos) +
      ", \"rows\":" + mkStringFromRows(ds.rows) + "}"

  def mkStringFromColumnInfos(cs: List[ColumnInfo]): String = cs.mkString("*[", ",", "]*")

  def mkStringFromRows(rs: List[Map[String, Any]]): String = rs.mkString("*[", ",", "]*")

  def mkString(c: ColumnInfo): String = {
    val sb = new StringBuilder()
    sb.append("*{")
    sb.append("\"id\":\"").append(c.id).append("\"")
    if (isNotEmpty(c.text)) sb.append(", ").append("\"text\":\"").append(c.text).append("\"")
    if (isNotEmpty(c.typeClass)) sb.append(", ").append("\"type\":\"").append(c.typeName).append("\"")
    if (isNotEmpty(c.size)) sb.append(", ").append("\"size\":\"").append(c.size).append("\"")
    if (isNotEmpty(c.userAttrs)) sb.append(", ").append(" ").append(mkString(c.userAttrs, null, null))
    sb.append("}*")
    sb.toString()
  }

  def mkStringFromRow(m: Map[String, Any]): String = mkString(m)

  def mkString(m: Map[String, Any]): String = mkString(m, "*{", "}*")

  private def mkString(m: Map[String, Any], start: String, end: String): String = {
    var first = true
    val sb = new StringBuilder()

    if (isNotEmpty(start)) sb.append(start)

    def mkString(kv: (String, Any)): String = {
      "\"" + kv._1 + "\":" +
        (kv._2 match {
          case null      => ""
          case s: String => "\"" + s + "\""
          case d: Date   => "\"" + d.toString() + "\""
          case a         => a.toString()
        })
    }

    m.foreach(kv => {
      if (!first) sb.append(", ")
      sb.append(mkString(kv))
      first = false
    })

    if (isNotEmpty(end)) sb.append(end)
    sb.toString()
  }
}
