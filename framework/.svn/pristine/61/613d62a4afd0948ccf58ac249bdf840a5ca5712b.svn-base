package elasticservice.util.ep.json13

import elasticservice.ElasticConfigurator
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenTo

object GenToJSONP13 extends GenTo {
  def gen(ep: ElasticParams): String = ep.toString(JSONP13MkString)

  def contentType = ElasticConfigurator.Charset match {
    case Some(c) => "text/javascript; charset=" + c
    case None    => "text/javascript"
  }
}