package elasticservice.util.ep.json12

import elasticservice.ElasticConfigurator
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenTo

object GenToJSON12 extends GenTo {
  def gen(ep: ElasticParams): String = ep.toString(JSON12MkString)

  def contentType = ElasticConfigurator.Charset match {
    case Some(c) => "text/json; charset=" + c
    case None    => "text/json"
  }
}