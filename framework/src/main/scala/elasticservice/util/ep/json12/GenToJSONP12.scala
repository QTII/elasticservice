package elasticservice.util.ep.json12

import elasticservice.ElasticConfigurator
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenTo

object GenToJSONP12 extends GenTo {
  def gen(ep: ElasticParams): String = ep.toString(JSONP12MkString)

  def contentType = ElasticConfigurator.Charset match {
    case Some(c) => Some("text/javascript; charset=" + c)
    case None    => Some("text/javascript")
  }
}