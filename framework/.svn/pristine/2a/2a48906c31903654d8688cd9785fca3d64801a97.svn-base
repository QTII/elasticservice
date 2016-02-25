package elasticservice.util.ep.xml

import elasticservice.Key
import elasticservice.util.CommonUtil
import elasticservice.util.DataValid.isNotEmpty
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.MkString
import elasticservice.DefaultVal

object XMLMkString extends MkString {
  def mkString(ep: ElasticParams): String = {
    val sb = new StringBuilder()
    sb.append("<?xml version=\"1.0\" encoding=\"" + DefaultVal.Charset + "\"?>").append(CommonUtil.NEW_LINE)
    sb.append("<Root>").append(CommonUtil.NEW_LINE)
    sb.append(mkStringFromParameters(ep.parameters))
    sb.append(mkStringFromDatasets(ep.datasets))
    sb.append("</Root>")
    sb.toString()
  }

  def EPTypeName = "PlatformXml"

  def mkStringFromParameters(m: Map[String, Any]): String = {
    val sb = new StringBuilder()
    val it = m.iterator

    sb.append("\t<Parameters>").append(CommonUtil.NEW_LINE)
    while (it.hasNext) {
      val kv = it.next() match {
        case ("epType", v) => ("epType", EPTypeName)
        case kv            => kv
      }
      sb.append("\t\t<Parameter id=\"" + kv._1 + "\">" + kv._2 + "</Parameter>").append(CommonUtil.NEW_LINE)
    }
    sb.append("\t</Parameters>").append(CommonUtil.NEW_LINE)
    sb.toString()
  }

  private def mkStringFromDatasets(m: Map[String, Any]): String = {
    val sb = new StringBuilder()
    val it = m.iterator

    while (it.hasNext) {
      val i = it.next()
      sb.append("\t<Dataset id=\"" + i._1 + "\">").append(CommonUtil.NEW_LINE)
      sb.append(mkString(i._2))
      sb.append("\t</Dataset>").append(CommonUtil.NEW_LINE)
    }
    sb.toString()
  }

  def mkString(ds: Dataset): String = {
    val sb = new StringBuilder()

    sb.append(mkStringFromColumnInfos(ds.colInfos))
    sb.append(mkStringFromRows(ds.rows))
    sb.toString()
  }

  private def mkStringFromColumnInfos(cs: List[ColumnInfo]): String = {
    val sb = new StringBuilder()

    sb.append("\t\t<ColumnInfo>").append(CommonUtil.NEW_LINE)
    for (c <- cs) sb.append(mkString(c)).append(CommonUtil.NEW_LINE)
    sb.append("\t\t</ColumnInfo>").append(CommonUtil.NEW_LINE)
    sb.toString()
  }

  def mkString(c: ColumnInfo): String = {
    val sb = new StringBuilder()

    sb.append("\t\t\t<Column id=\"").append(c.id).append("\"")
    if (isNotEmpty(c.text)) sb.append(" text=\"").append(c.text).append("\"")
    if (isNotEmpty(c.typeClass)) sb.append(" type=\"").append(c.typeName).append("\"")
    if (isNotEmpty(c.size)) sb.append(" size=\"").append(c.size).append("\"")
    sb.append(" />")
    sb.toString()
  }

  private def mkStringFromRows(rs: List[Map[String, Any]]): String = {
    val sb = new StringBuilder()

    sb.append("\t\t<Rows>").append(CommonUtil.NEW_LINE)
    for (r <- rs) sb.append(mkString(r)).append(CommonUtil.NEW_LINE)
    sb.append("\t\t</Rows>").append(CommonUtil.NEW_LINE)
    sb.toString()
  }

  def mkString(m: Map[String, Any]): String = {
    val sb = new StringBuilder()
    val it = m.iterator

    sb.append("\t\t\t<Row>").append(CommonUtil.NEW_LINE)
    while (it.hasNext) {
      val i = it.next()
      sb.append("\t\t\t\t<Col id=\"" + i._1 + "\">").append(i._2).append("</Col>").append(CommonUtil.NEW_LINE)
    }
    sb.append("\t\t\t</Row>")
    sb.toString()
  }

  private def mkString(kv: Any): String = {
    (kv match {
      case null       => ""
      case d: Dataset => mkString(d)
      case a          => a.toString()
    })
  }
}