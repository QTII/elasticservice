package elasticservice.util.ep.xml

import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenTo
import elasticservice.ElasticConfigurator

object GenToXML extends GenTo {
  def gen(ep: ElasticParams): String = ep.toString(XMLMkString)

  def contentType: Option[String] = ElasticConfigurator.Charset match {
    case Some(c) => Some("text/xml; charset=" + c)
    case None    => Some("text/xml")
  }
}