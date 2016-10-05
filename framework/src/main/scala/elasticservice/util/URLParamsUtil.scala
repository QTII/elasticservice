package elasticservice.util

import java.net.URLDecoder
import java.net.URLEncoder

import com.typesafe.scalalogging.LazyLogging

import elasticservice.DefaultVal

object URLParamsUtil extends LazyLogging {
  def toQueryString(record: Map[String, Any], namePrefix: Option[String], encoding: Option[String]): String = {
    val sb = new StringBuilder()
    record.foreach {
      _ match {
        case (key, value) =>
          val v = URLEncoder.encode(value.toString, encoding.getOrElse("UTF-8"))
          if (!sb.isEmpty) sb += '&'
          sb ++= namePrefix.getOrElse("") + key + "=" + v
        case _ =>
      }
    }
    sb.toString()
  }

  def toQueryString(records: List[Map[String, Any]], namePrefix: Option[String], encoding: Option[String]): String = {
    val sb = new StringBuilder()
    records.foreach { record =>
      if (!sb.isEmpty) sb += '&'
      sb ++= toQueryString(record, namePrefix, encoding)
    }
    sb.toString()
  }

  def queryStringToMap(queryStr: String, encodingOpt: Option[String]): Map[String, Seq[String]] = {
    val encoding = encodingOpt.getOrElse(DefaultVal.Charset)
    var map = Map.empty[String, Seq[String]]
    try {
      queryStr.split("&").foreach { nvPair =>
        val nvArr = nvPair.split("=")
        val (n, v) = if (nvArr.length > 1)
          (URLDecoder.decode(nvArr(0), encoding), URLDecoder.decode(nvArr(1), encoding))
        else
          (URLDecoder.decode(nvPair, encoding), "")

        map.get(n) match {
          case None => map += n -> Seq(v)
          case Some(s) => map += n -> (s ++ Seq(v))
        }
      }
    } catch {
      case e: Throwable => logger.error(ExceptionDetail.getDetail(e))
    }
    map
  }

  def main(args: Array[String]) {
    val queryStr = "a=aa&b=bb&c:Integer=3";
    val map = URLParamsUtil.queryStringToMap(queryStr, None);
    val a = map.get("a");
    val c = map.get("c");

    println(map);
    println("a type=" + a.getClass());
    println("c type=" + c.getClass());
  }
}