package elasticservice.service.sqlrepo

import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.ElasticService
import elasticservice.ElasticServiceUtil
import elasticservice.epMkString
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.Record
import elasticservice.util.sqlrepo.SqlRepo

/**
 * URL Parameter: NOT REQUIRED
 */
class AllPkgIdsFromDisk extends ElasticService with LazyLogging {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      val resEP = ElasticServiceUtil.epWithCodeMessage(0, "Successful")
      SqlRepo.allPkgIdsFromDisk().foreach { pkgId =>
        resEP.addDatasetRow("1", Record(("id", pkgId), ("idType", "package")))
      }
      resEP
    }
  }
}
