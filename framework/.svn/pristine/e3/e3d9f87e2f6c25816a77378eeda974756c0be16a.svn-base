package elasticservice.service

import scala.util.Failure
import scala.util.Try
import elasticservice.util.ep.ElasticParams
import elasticservice.ElasticService

class ErrorService extends ElasticService {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Failure(new Exception("error: 999"))
  }
}