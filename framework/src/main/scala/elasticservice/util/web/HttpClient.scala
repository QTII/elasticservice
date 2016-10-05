package elasticservice.util.web

import java.io.{BufferedReader, InputStreamReader, PrintStream, PrintWriter}
import java.net.{HttpURLConnection, URL}

import elasticservice.DefaultVal
import elasticservice.util.{StringUtil, URLParamsUtil}

case class HttpClient(hostname: String, port: Int, uri: String,  reqEncoding: Option[String]) {
  private val cookieMgr = CookieMgr

  private val cookieBase = CookieMgr.getCookieBase(uri)
  private var httpConn: HttpURLConnection = null
  private var debug = false
  private var debugOutput: PrintStream = null

  val urlStr = "http://" + hostname +
    (if (port == 80) "" else ":" + port) +
    (if (uri != null && uri.charAt(0) != '/') "/" + uri else uri)

  private var url = new URL(urlStr)

  def setDebug(enable: Boolean) {
    this.debug = enable
    this.debugOutput = System.out
  }

  def setDebug(enable: Boolean, debugOutput: PrintStream) {
    this.debug = enable
    this.debugOutput = debugOutput
  }

  def getWriter(): PrintWriter = new PrintWriter(httpConn.getOutputStream())

  def getReader(): BufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), reqEncoding.getOrElse(DefaultVal.Charset)))

  def connectInGET(queryStr: String) {
    if (debug)
      debugOutput.println("connect to " + urlStr)
    url = new URL(urlStr + "?" + queryStr)

    httpConn = url.openConnection().asInstanceOf[HttpURLConnection]
    httpConn.setDoOutput(true)
    httpConn.setDoInput(true)
    httpConn.setUseCaches(false)
    httpConn.setRequestMethod("GET")
    // httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

    val cookieStr = cookieMgr.getCookieString(cookieBase)
    if (!StringUtil.isBlank(cookieStr))
      httpConn.setRequestProperty("CookieSet", cookieStr)
    if (debug)
      debugOutput.println("CookieSet: " + cookieStr)
  }

  def connectInPOST() {
    if (debug)
      debugOutput.println("connect to " + urlStr)
    url = new URL(urlStr)

    httpConn = url.openConnection().asInstanceOf[HttpURLConnection]
    httpConn.setDoOutput(true)
    httpConn.setDoInput(true)
    httpConn.setUseCaches(false)
    httpConn.setRequestMethod("POST")
    // httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

    val cookieStr = cookieMgr.getCookieString(cookieBase)
    if (!StringUtil.isBlank(cookieStr))
      httpConn.setRequestProperty("CookieSet", cookieStr)
    if (debug)
      debugOutput.println("CookieSet: " + cookieStr)
  }

  def close() {
    if (httpConn != null)
      httpConn.disconnect()
  }

  def sendReqInGET() {
    val out: PrintWriter = getWriter()
    try {
      if (debug) {
        debugOutput.print("[REQUEST] ")
        debugOutput.println(url.toString())
      }
      out.flush()
    } finally {
      out.close()
    }
  }

  def sendReqInPOST(queryStr: String) {
    val out: PrintWriter = getWriter()
    try {
      if (!StringUtil.isBlank(queryStr)) {
        if (debug) {
          debugOutput.print("[REQUEST] ")
          debugOutput.println(queryStr)
        }
        out.print(queryStr)
      }
      out.flush()
    } finally {
      out.close()
    }
  }

  def recvRes(): String = {
    val sb = new StringBuilder()
    val br: BufferedReader = getReader()
    try {
      var line = "_"
      while (!StringUtil.isBlank(line)) {
        line = br.readLine()
        if (!StringUtil.isBlank(line)) sb ++= line
      }

      cookieMgr.setCookie(httpConn)

      val resStr = sb.toString()
      if (debug) {
        debugOutput.print("[RESPONSE] ")
        debugOutput.println(resStr)
        debugOutput.println()
      }
      resStr
    } finally {
      br.close()
    }
  }

  def reqAndGetRes(method: String, queryStr: String): String = {
    if ("GET".equalsIgnoreCase(method)) {
      connectInGET(queryStr)
      sendReqInGET()
    } else {
      connectInPOST()
      sendReqInPOST(queryStr)
    }
    recvRes()
  }

  def reqAndGetRes(method: String, params: Map[String, Any], record: Map[String, Any]): String = {
    reqAndGetRes(method,
      URLParamsUtil.toQueryString(params, None, reqEncoding) + "&" +
        URLParamsUtil.toQueryString(record, Some("S1COL_"), reqEncoding))
  }

  def reqAndGetRes(method: String, params: Map[String, Any], rows: List[Map[String, Any]]): String = {
    val sb = new StringBuilder()
    rows.foreach { record =>
      if (!sb.isEmpty) sb += '&'
      sb ++= URLParamsUtil.toQueryString(record, Some("S1COL_"), reqEncoding)
    }
    reqAndGetRes(method,
      URLParamsUtil.toQueryString(params, None, reqEncoding) + "&" + sb.toString)
  }
}

object HttpClient {
  def main(args: Array[String]): Unit = {
    // http://localhost:9092/elastic?service=elasticservice.service.QueryService&sqlId=wms.inventory_select
    val hc = HttpClient("localhost", 9092, "/elastic",  Some("UTF-8"))
    val params = Map("service"->"elasticservice.service.QueryService", "sqlId"->"wms.inventory_select")
    val records = List(Map("ITEM"->"0151"), Map("ITEM"->"0153"))
    for (i <- 0 until 1) {
      val ret = hc.reqAndGetRes("POST", params, records)
      println(ret)
      Thread.sleep(5000)
    }
  }
}