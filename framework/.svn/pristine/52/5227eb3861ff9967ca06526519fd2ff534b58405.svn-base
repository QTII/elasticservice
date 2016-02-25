package elasticservice.web.servlet

import elasticservice.util.ep.ElasticParamsReader
import elasticservice.util.ep.InputSourceMeta
import javax.servlet.http.HttpServletRequest

case class ServletReader(request: HttpServletRequest) extends ElasticParamsReader {
  def read(): InputSourceMeta = {
    val text = ElasticRequestUtil.getQueryString(request)
    val contentType = request.getContentType()
    val encodingOpt = ElasticRequestUtil.getCharacterEncoding(request)

    InputSourceMeta(text, contentType, encodingOpt, request)
  }

}