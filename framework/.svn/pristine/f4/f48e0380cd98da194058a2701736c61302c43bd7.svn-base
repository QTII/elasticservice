package elasticservice.util.ep.json12

import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.MkString

object JSONP12MkString extends MkString {

  def mkString(ep: ElasticParams): String =
    ep.get("_jsonpKey_").getOrElse("") + "(" + JSON12MkString.mkString(ep) + ");"

  def EPTypeName = JSON12MkString.EPTypeName

  def mkStringFromParameters(m: Map[String, Any]): String = JSON12MkString.mkStringFromParameters(m)

  def mkString(d: Dataset): String = JSON12MkString.mkString(d)

  def mkString(c: ColumnInfo): String = JSON12MkString.mkString(c)

  def mkString(m: Map[String, Any]): String = JSON12MkString.mkString(m)
}