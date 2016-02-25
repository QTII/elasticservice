package elasticservice.util.ep.xml

import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenTo
import elasticservice.ElasticConfigurator

object GenToXML extends GenTo {
  def gen(ep: ElasticParams): String = ep.toString(XMLMkString)

  def contentType = ElasticConfigurator.Charset match {
    case Some(c) => "text/xml; charset=" + c
    case None    => "text/xml"
  }
}