package elasticservice.util.web

import java.net.URLConnection

import scala.collection.JavaConversions._

import com.typesafe.scalalogging.LazyLogging

object CookieMgr extends LazyLogging {
  private val cookieSet = new CookieSet()

  def getCookieBase(uri: String): String = {
    uri match {
      case null | "" => "/"
      case _ =>
        uri.lastIndexOf('/') match {
          case -1 | 0 => "/"
          case i => uri.substring(0, i)
        }
    }
  }

  def setCookie(conn: URLConnection) {
    conn.getHeaderFields.filter { nv =>
        nv._1 != null && nv._1.toUpperCase == "SET-COOKIE"
      }.foreach {
        case (hdrName, values) => values.foreach {
          nvPairs => cookieSet.set(nvPairs)
          logger.trace("Set-Cookie: {0}", nvPairs)
      }
    }
  }

  def getCookieString(uri: String): String = cookieSet.getCookieStringByURI(uri)
}
