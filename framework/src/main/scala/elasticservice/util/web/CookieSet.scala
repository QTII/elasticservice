package elasticservice.util.web

import elasticservice.util.ep.Record

object CookieSet {
  val EXPIRES = "EXPIRES"
  val PATH = "PATH"
  val DOMAIN = "DOMAIN"
}

case class Cookie(name: String, value: String, expires: Option[String], path: Option[String], domain: Option[String], booleanAttrs: Option[Set[String]])

/**
  * o Response Header
  *   Set-Cookie: NAME=VALUE; expires=DATE; path=PATH; domain=DOMAIN_NAME; secure; HTTPOnly
  *
  * o Request Header
  *   Cookie: CUSTOMER=WILE_E_COYOTE; PART_NUMBER=ROCKET_LAUNCHER_0001; SHIPPING=FEDEX
  */
class CookieSet {
  import CookieSet._

  val map = Record[Record[Cookie]]()

  def set(nvPairs: String): Unit = {
    val cookies = Record[String]()
    val attributes = Record[String]()
    var booleanAttrs = Set[String]()

    def set(n: String, v: Option[String]) {
      n.toUpperCase match {
        case EXPIRES | PATH | DOMAIN => attributes += n -> v.getOrElse("")
        case name if v == None => booleanAttrs += n
        case name if v != None => cookies += n -> v.get
      }
    }

    nvPairs.split(";").foreach {
      token => token.indexOf("=") match {
        case -1 => set(token.trim, None)
        case i => set(token.substring(0, i).trim, Some(token.substring(i + 1)))
      }
    }

    def setCookie(cookie: Cookie) {
      map.get(cookie.path.getOrElse("/")) match {
        case Some(r) => r += cookie.name -> cookie
        case None => map += cookie.path.getOrElse("/") -> Record[Cookie](cookie.name -> cookie)
      }
    }

    cookies.foreach { case (n, v) =>
      val cookie = Cookie(n, v, attributes.get(EXPIRES), attributes.get(PATH), attributes.get(DOMAIN), if(booleanAttrs.isEmpty) None else Some(booleanAttrs))
      setCookie(cookie)
    }
  }

  /*
  def updateBy(cookieSet: CookieSet): Unit = {
    cookieSet.map.foreach {
      case (path, m) =>  m.foreach { case (name, cookie) => setCookie(cookie) }
    }
  }
  */

  def getCookieStringByPath(path: String) = {
    val sb = new StringBuilder()
    map.get(path).foreach { _.foreach {
        case (name, cookie) =>
          if (!sb.isEmpty) sb ++= "; "
          sb ++= name + "=" + cookie.value
      }
    }
    sb.toString
  }

  def getCookieStringByURI(uri: String): String = {
    val sb = new StringBuilder()
    map.foreach {
      case (path, m) =>
        if (uri.startsWith(path)) {
          m.foreach {
            case (name, cookie) =>
              if (!sb.isEmpty) sb ++= "; "
              sb ++= name + "=" + cookie.value
          }
        }
    }
    sb.toString
  }

  override def toString: String = {
    val sb = new StringBuilder()
    map.foreach {
      case (path, m) => m.foreach {
        case (name, cookie) =>
          if (!sb.isEmpty) sb ++= "\n"
          sb ++= cookie.toString
      }
    }
    sb.toString
  }
}