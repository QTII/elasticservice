package elasticservice.web.servlet

import java.io.InputStream
import java.net.URLDecoder
import java.nio.channels.Channels
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import com.typesafe.scalalogging.LazyLogging
import elasticservice.util.ExceptionDetail
import elasticservice.util.nio.ChannelUtil
import javax.servlet.http.HttpServletRequest
import elasticservice.ElasticConfigurator
import elasticservice.Key
import elasticservice.DefaultVal

object ElasticRequestUtil extends LazyLogging {
  def getCharacterEncoding(request: HttpServletRequest): Option[String] = {
    request.getCharacterEncoding() match {
      case null | "" => None
      case enc       => Some(enc)
    }
  }

  def getQueryString(request: HttpServletRequest): String = {
    val encoding = getCharacterEncoding(request).orElse(ElasticConfigurator.Charset).getOrElse(DefaultVal.Charset)

    if (request.getMethod().equalsIgnoreCase("GET"))
      URLDecoder.decode(request.getQueryString(), encoding)
    else
      getQueryString(request.getInputStream(), encoding, request.getContentLength())
  }

  def getQueryBytes(is: InputStream, cLen: Int): Option[Array[Byte]] = {
    Try(Channels.newChannel(is)) match {
      case Success(channel) => {
        val bytes = ChannelUtil.readUntilEndOfStream(channel, if (cLen > 0) cLen else 1024)
        channel.close()
        bytes
      }
      case Failure(e) => {
        logger.error(ExceptionDetail.getDetail(e))
        None
      }
    }
  }

  def getQueryString(is: InputStream, encoding: String, cLen: Int): String =
    getQueryBytes(is, cLen) map {
      e => if (e.length == 0) "" else new String(e, encoding)
    } getOrElse ("")

  /**
   * 클라이언트가 요청할 때 사용한 서버의 서비스 주소. 예를들어 서버 앞에 L4스위치가 있고 서버의 실제 주소와 L4스위치의 서비스
   * 주소가 다를 경우 L4스위치가 서비스하는 주소를 반환한다.
   *
   * @param request
   * @return
   */
  def getServiceAddress(request: HttpServletRequest): Option[String] =
    if (request == null) None
    else Some(request.getServerName())

  def getRemoteAddr(request: HttpServletRequest): Option[String] =
    Option(request.getRemoteAddr()).orElse(Option(request.getRemoteHost))

  /**
   * ex) User-Agent: Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like
   * Gecko
   *
   * @param request
   * @return
   */
  def getUserAgent(request: HttpServletRequest): String = request.getHeader("User-Agent")

  /**
   * Content-Type: multipart/form-data; boundary=SEPARATOR6a88c036-1c12b766
   *
   * @param contentType
   * @return
   */
  def isMultipartRequest(contentType: String): Boolean = contentType != null && contentType.indexOf("multipart/form-data") >= 0
}