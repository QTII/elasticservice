package elasticservice.util.ep

trait GenFrom {
  def gen(text: String, encodingOp: Option[String]): ElasticParams
}