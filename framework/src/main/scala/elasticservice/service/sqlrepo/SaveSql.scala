package elasticservice.service.sqlrepo

import java.io.File

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.ElasticService
import elasticservice.util.ep.ElasticParams
import elasticservice.util.sqlrepo.SqlRepo

/**
 * URL Parameter: sqlId(Full SQL ID), xml(XML Content Text)
 */
class SaveSql extends ElasticService with LazyLogging {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      ifMissingThrow("sqlId", req).foreach(throw _)
      ifMissingThrow("xml", req).foreach(throw _)
      ifMissingThrow("newSql", req).foreach(throw _)

      val fullSqlId = req.get("sqlId").getOrElse("").toString
      val xml = req.get("xml").get.toString
      val newSql = req.get("newSql").get.toString.toBoolean

      if (newSql && new File(SqlRepo.filePath(SqlRepo.pkgAndIdFromFullSqlId(fullSqlId))).exists) {
        throw new Exception("The same sqlId already exists")
      } else {
        SqlRepo.saveSql(fullSqlId, xml) match {
          case Some(e) => throw e
          case None =>
            new AllIdsFromDisk().execute(req) match {
              case Success(ep) => ep
              case Failure(e)  => throw e
            }
        }
      }
    }
  }
}
