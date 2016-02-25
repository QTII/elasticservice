package elasticservice.util.ep

import java.io.File

import scala.util.Failure
import scala.util.Success

import elasticservice.util.TextFileUtil

case class FileReader(file: File, encodingOpt: Option[String]) extends ElasticParamsReader {
  def read(): InputSourceMeta = {
    TextFileUtil.textFrom(file, encodingOpt) match {
      case Success(text) =>
        InputSourceMeta(
          text,
          getContentType(file.getName().reverse.takeWhile(_ != '.').reverse),
          encodingOpt,
          file)
      case Failure(e) => throw e
    }
  }

  private def getContentType(ext: String): String = "text/" + ext.toLowerCase()
}