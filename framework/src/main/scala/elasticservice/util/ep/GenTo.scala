package elasticservice.util.ep

trait GenTo {
  def gen(ep: ElasticParams): String
  def contentType: Option[String]
}