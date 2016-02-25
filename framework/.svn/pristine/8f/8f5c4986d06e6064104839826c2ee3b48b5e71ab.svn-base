package elasticservice.web.servlet

import java.io.OutputStreamWriter
import java.io.PrintWriter

import com.typesafe.scalalogging.LazyLogging

import elasticservice.EEnv
import elasticservice.ElasticConfigurator
import elasticservice.ElasticServiceUtil
import elasticservice.util.DataValid
import elasticservice.util.ExceptionDetail
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class DefaultServlet extends HttpServlet with LazyLogging {

  override def init(servletConfig: ServletConfig) {
    super.init(servletConfig)

    try {
      val config = ElasticConfigurator.getXml
      EEnv.init(config)

      val servletName = servletConfig.getServletName()
      servletConfig.getServletContext().setAttribute("MainServletName", servletName)
    } catch {
      case e: ServletException =>
        logger.error(ExceptionDetail.getDetail(e))
        throw e
      case e: Exception =>
        logger.error(ExceptionDetail.getDetail(e))
        throw new ServletException(e)
    }
  }

  override def destroy() {
    super.destroy()
  }

  override def doGet(req: HttpServletRequest, res: HttpServletResponse) { handle(req, res) }
  override def doPost(req: HttpServletRequest, res: HttpServletResponse) { handle(req, res) }

  def handle(request: HttpServletRequest, response: HttpServletResponse) {
    val inSrc = ServletReader(request).read
    ElasticServiceUtil.logRequest(inSrc)

    val ssMap = toMap(request.getSession(false))

    val (svcOpt, resText, cTypeOpt) = ElasticServiceUtil.execService(inSrc, ssMap)

    svcOpt.foreach { svc =>
      if (svc.session.isEmpty) {
        val httpSession = request.getSession(false)
        if (httpSession != null) httpSession.invalidate()
      } else {
        val httpSession = request.getSession(true)
        svc.session.deleted.foreach { httpSession.removeAttribute(_) }
        svc.session.all.foreach { kv => httpSession.setAttribute(kv._1, kv._2) }
      }
    }

    ElasticServiceUtil.logResponse(resText, cTypeOpt)
    respond(response, resText, cTypeOpt)
  }

  private def toMap(httpSession: HttpSession): Map[String, String] = {
    val ssMap = scala.collection.mutable.Map.empty[String, String]
    if (httpSession != null) {
      val attrs = httpSession.getAttributeNames
      while (attrs.hasMoreElements()) {
        val name = attrs.nextElement()
        val value = httpSession.getAttribute(name)
        ssMap += name -> value.toString
      }
    }
    ssMap.toMap
  }

  private def respond(response: HttpServletResponse, resText: String, cTypeOpt: Option[String]) {
    val encoding = response.getCharacterEncoding()
    val out = if (DataValid.isEmpty(encoding)) {
      new PrintWriter(response.getOutputStream())
    } else {
      new PrintWriter(new OutputStreamWriter(response.getOutputStream(), encoding))
    }

    cTypeOpt.foreach { response.setContentType(_) }
    out.print(resText)
    out.flush()
  }
}