package sample.elasticservice.service

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.util.ep.ElasticParams

class SessionCleanService extends ElasticService {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      session.clear()

      val res = ElasticParams()
      res ++= session.all
      res
    }
  }
}