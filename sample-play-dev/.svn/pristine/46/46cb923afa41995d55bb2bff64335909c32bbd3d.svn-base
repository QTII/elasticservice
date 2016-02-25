package sample.elasticservice.service

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.util.ep.ElasticParams

class SessionGetService extends ElasticService {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      val res = ElasticParams()
      res ++= session.all
      res
    }
  }
}