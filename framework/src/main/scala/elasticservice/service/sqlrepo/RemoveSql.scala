package elasticservice.service.sqlrepo

import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.ElasticService
import elasticservice.ElasticServiceUtil
import elasticservice.util.ep.ElasticParams
import elasticservice.util.sqlrepo.SqlRepo

/**
 * URL Parameter: sqlId(Full SQL ID)
 */
class RemoveSql extends ElasticService with LazyLogging {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      ifMissingThrow("sqlId", req).foreach(throw _)

      val fullSqlId = req.get("sqlId").getOrElse("").toString

      if (SqlRepo.removeSql(fullSqlId))
        ElasticServiceUtil.epWithCodeMessage(0, "Successful")
      else
        throw new Exception("failed to remove sql")
    }
  }
}
