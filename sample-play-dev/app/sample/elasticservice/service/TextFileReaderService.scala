package sample.elasticservice.service

import java.io.File

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import elasticservice.ElasticConfigurator
import elasticservice.ElasticService
import elasticservice.ElasticServiceUtil
import elasticservice.epMkString
import elasticservice.util.TextFileUtil
import elasticservice.util.ep.ElasticParams

class TextFileReaderService extends ElasticService {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      //ifNotAccessibleThrow.foreach(throw _)
      ifMissingThrow("path", req).foreach(throw _)

      val path = req.get("path").get
      val encodingOpt = req.get("encoding").map(_.toString)

      val res = ElasticParams()

      TextFileUtil.textFrom(
        new File(ElasticConfigurator.AppRootPath.getOrElse("") + File.separator + path),
        encodingOpt) match {
          case Success(text) => res += "text" -> text
          case Failure(e)    => ElasticServiceUtil.setCodeAndMsg(res, 999, e.getMessage)
        }

      res
    }
  }
}