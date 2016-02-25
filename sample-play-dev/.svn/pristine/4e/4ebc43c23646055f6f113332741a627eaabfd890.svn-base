package sample.elasticservice.service

import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.ElasticService
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams

class Sample04_ElasticParamsService extends ElasticService with LazyLogging {
  def execute(reqEP: ElasticParams): Try[ElasticParams] = {
    Try {
      /*
			 * Checking Input ElasticParams
			 */
      logger.trace("Input Parameter ABC: " + reqEP.get("ABC"))
      logger.trace("Input Parameters: " + reqEP.parameters)
      logger.trace("Input Dataset named '1': " + reqEP.getDataset("1"))

      /*
			 * Setting Output ElasticParams
			 */
      val resEP = ElasticParams()

      // Set Dataset named '1'
      val resDS1 = Dataset("1")
      resEP.setDataset(resDS1)

      // Set row1 which is a Row object
      resDS1 += Map(("c1" -> 111), ("c2" -> reqEP.get("ABC").getOrElse("")))

      // Set row2 which is a Map object
      resDS1 += Map(("c1" -> 222), ("c2" -> reqEP.get("ABC").getOrElse("")))

      resEP
    }
  }
}