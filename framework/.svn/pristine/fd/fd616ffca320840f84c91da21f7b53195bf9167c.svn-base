package elasticservice.util.ep

trait ElasticParamsReader {
  def read(): InputSourceMeta
}

sealed case class InputSourceMeta(
  text: String,
  contentType: String,
  encodingOpt: Option[String],
  source: Any)
