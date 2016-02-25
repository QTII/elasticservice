package sample.elasticservice.service

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.util.ep.ElasticParams

class SessionSetService extends ElasticService {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      session ++= req.parameters.filterKeys { k =>
        !k.toLowerCase.startsWith("service")
      }.map(kv => kv._1 -> kv._2.toString)

      val res = ElasticParams()
      res ++= session.all
      res
    }
  }
}