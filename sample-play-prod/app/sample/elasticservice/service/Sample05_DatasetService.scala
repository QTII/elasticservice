package sample.elasticservice.service

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams

class Sample05_DatasetService extends ElasticService {
  def execute(reqEP: ElasticParams): Try[ElasticParams] = {
    Try {
      val row1 = Map(("col1" -> 1111), ("col2" -> "AAAA"))
      val row2 = Map(("col1" -> None), ("col2" -> "BBBB"))
      val row3 = Map(("col1" -> 3333), ("col2" -> "BBBB"))
      val list = List(row1, row2, row3)

      /*
			 * Setting Output ElasticParams
			 */
      val resEP = ElasticParams()

      // Set Dataset named '1'
      val resDS1 = Dataset("1")
      resEP.setDataset(resDS1)

      resDS1 ++= list

      resEP
    }
  }
}