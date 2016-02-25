package elasticservice.util.ep.json13

import java.net.URLDecoder
import elasticservice.Key
import elasticservice.util.URLParamsUtil
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenFrom
import play.api.libs.json.Json
import elasticservice.DefaultVal

object GenFromJSONP13 extends GenFrom {
  def gen(inText: String, encodingOpt: Option[String]): ElasticParams = {
    val jsonStrDecoded = parseJsonp(inText)
    val jsonStrEncoded = URLDecoder.decode(jsonStrDecoded, encodingOpt.getOrElse(DefaultVal.Charset))
    val json = Json.parse(jsonStrEncoded)
    val ep = GenFromJSON13.gen(json)
    ep += ("_jsonpKey_", parseJsonpKey(inText, encodingOpt))
    ep
  }

  def parseJsonp(qStr: String): String = {
    val jsonFilter = """.*&(.+)&.*""".r
    qStr match {
      case jsonFilter(a) => a
      case _             => ""
    }
  }

  def parseJsonpKey(inText: String, encodingOpt: Option[String]): String =
    URLParamsUtil.queryStringToMap(
      inText,
      encodingOpt.getOrElse(DefaultVal.Charset)).getOrElse("_jsonpKey_", "").toString()
}