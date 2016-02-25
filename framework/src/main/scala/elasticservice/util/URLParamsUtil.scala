package elasticservice.util

import java.net.URLDecoder

import com.typesafe.scalalogging.LazyLogging

object URLParamsUtil extends LazyLogging {
  def queryStringToMap(queryStr: String, charset: String): Map[String, Seq[String]] = {
    var map = Map.empty[String, Seq[String]]
    try {
      queryStr.split("&").foreach { nvPair =>
        val nvArr = nvPair.split("=")
        val (n, v) = if (nvArr.length > 1)
          (URLDecoder.decode(nvArr(0), charset), URLDecoder.decode(nvArr(1), charset))
        else
          (URLDecoder.decode(nvPair, charset), "")

        map.get(n) match {
          case None    => map += n -> Seq(v)
          case Some(s) => map += n -> (s ++ Seq(v))
        }
      }
    } catch {
      case e: Throwable => logger.error(ExceptionDetail.getDetail(e))
    }
    map
  }
}