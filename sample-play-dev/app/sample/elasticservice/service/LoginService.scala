package sample.elasticservice.service

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.epMkString
import elasticservice.service.QueryService
import elasticservice.service.sqlrepo.AllIdsFromDisk
import elasticservice.service.sqlrepo.AllPkgIdsFromDisk
import elasticservice.service.sqlrepo.GetXML
import elasticservice.service.sqlrepo.RemoveSql
import elasticservice.service.sqlrepo.RenamePkg
import elasticservice.service.sqlrepo.SaveSql
import elasticservice.service.sqlrepo.TemplatePkgIds
import elasticservice.service.sqlrepo.TemplateSqlIds
import elasticservice.util.ep.ElasticParams

class LoginService extends ElasticService {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      ifMissingThrow("id", req).foreach(throw _)
      ifMissingThrow("pw", req).foreach(throw _)

      val id = req.get("id").getOrElse("").toString
      val pw = req.get("pw").getOrElse("").toString

      session.setAccessibleService(classOf[QueryService])
      session.setAccessibleService(classOf[AllIdsFromDisk])
      session.setAccessibleService(classOf[AllPkgIdsFromDisk])
      session.setAccessibleService(classOf[GetXML])
      session.setAccessibleService(classOf[RemoveSql])
      session.setAccessibleService(classOf[RenamePkg])
      session.setAccessibleService(classOf[SaveSql])
      session.setAccessibleService(classOf[TemplatePkgIds])
      session.setAccessibleService(classOf[TemplateSqlIds])
      session.setAccessibleService(classOf[TextFileReaderService])

      val res = ElasticParams()
      res ++= session.all
      res
    }
  }
}