package elasticservice.util.ep.json13

import java.net.URLDecoder
import elasticservice.epMkString
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenFrom
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json
import play.api.libs.json.Reads
import elasticservice.Key
import elasticservice.DefaultVal

object GenFromJSON13 extends GenFrom {
  def gen(text: String, encodingOpt: Option[String]): ElasticParams =
    GenFromJSON13.gen(Json.parse(URLDecoder.decode(text, encodingOpt.getOrElse(DefaultVal.Charset))))

  def gen(json: JsValue): ElasticParams = {
    val ep: ElasticParams = ElasticParams() //ElasticParams.empty
    ep ++= (json \ "parameters").as[Map[String, Any]]
    ep.datasets = (json \ "datasets").as[List[Dataset]]
    ep
  }

  implicit val mapReads = new Reads[Map[String, Any]] {
    def reads(json: JsValue) =
      JsSuccess(
        value = json.as[Map[String, JsValue]].mapValues { v =>
          v match {
            case s: JsString  => s.as[String]
            case n: JsNumber  => n.as[Double]
            case b: JsBoolean => b.as[Boolean]
            case _            => v
          }
        })
  }

  implicit val columnInfoReads = new Reads[ColumnInfo] {
    def reads(json: JsValue) = {
      JsSuccess(ColumnInfo(
        (json \ "id").as[String],
        (json \ "text").asOpt[String].getOrElse((json \ "id").as[String]),
        (json \ "type").asOpt[String].getOrElse("String"),
        (json \ "size").asOpt[Int].getOrElse(0)))
    }
  }

  implicit val datasetReads = new Reads[List[Dataset]] {
    def reads(json: JsValue) =
      JsSuccess(
        json.as[List[JsValue]].map { v =>
        val d = Dataset((v \ "name").as[String], (v \ "colInfos").as[List[ColumnInfo]])
        d.rows = (v \ "rows").as[List[Map[String, Any]]]
        d
        })
  }
}
