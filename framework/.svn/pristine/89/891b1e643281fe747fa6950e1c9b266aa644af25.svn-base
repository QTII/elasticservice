package elasticservice.service

import scala.util.Success
import scala.util.Try
import com.typesafe.scalalogging.LazyLogging
import elasticservice.util.ep.ElasticParams
import elasticservice.ElasticService

class EchoService extends ElasticService with LazyLogging {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Success(req)
  }
}