package sample.elasticservice.service

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.util.ep.ElasticParams

class Sample06_ParameterService extends ElasticService {
  def execute(reqEP: ElasticParams): Try[ElasticParams] = {
    Try {
      /*
			 * Setting Output ElasticParams
			 */
      val resEP = ElasticParams()

      resEP += ("col1" -> 1111)
      resEP += ("col2" -> "AAAA")

      resEP
    }
  }
}