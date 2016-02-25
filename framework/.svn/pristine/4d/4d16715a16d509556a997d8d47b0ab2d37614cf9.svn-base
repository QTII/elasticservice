package elasticservice.service.sqlrepo

import scala.util.Try
import com.typesafe.scalalogging.LazyLogging
import elasticservice.ElasticService
import elasticservice.ElasticServiceUtil
import elasticservice.epMkString
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.Record
import elasticservice.util.sqlrepo.SqlRepo
import elasticservice.util.TextFileUtil
import java.io.File
import scala.util.Failure
import scala.util.Success

/**
 * URL Parameter: sqlId(Full SQL ID)
 */
class GetXML extends ElasticService with LazyLogging {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      ifMissingThrow("sqlId", req).foreach(throw _)

      val fullSqlId = req.get("sqlId").getOrElse("").toString
      val xmlPath = SqlRepo.filePath(SqlRepo.pkgAndIdFromFullSqlId(fullSqlId))
      val encodingOpt = TextFileUtil.detectEncodingOfFile(xmlPath)

      TextFileUtil.textFrom(new File(xmlPath), encodingOpt) match {
        case Success(xmlText) =>
          val resEP = ElasticServiceUtil.epWithCodeMessage(0, "Successful")
          resEP.addDatasetRow("1", Record(("sqlId", fullSqlId), ("xml", xmlText)))
          resEP
        case Failure(e) => throw e
      }
    }
  }
}
