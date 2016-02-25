package elasticservice.web.play.controllers

import com.typesafe.scalalogging.LazyLogging

import elasticservice.ElasticServiceUtil
import elasticservice.web.play.PlayReader
import play.api.mvc.Action
import play.api.mvc.Controller

class DefaultApplication extends Controller with LazyLogging {

  def index = Action(parse.raw) { request =>
    val inSrc = PlayReader(request).read
    ElasticServiceUtil.logRequest(inSrc)

    val (svcOpt, resText, cTypeOpt) = ElasticServiceUtil.execService(
      inSrc,
      request.session.data.toMap)

    var result = cTypeOpt match {
      case Some(contentType) => Ok(resText).as(contentType)
      case None              => Ok(resText)
    }

    svcOpt.foreach { svc =>
      if (svc.session.isEmpty) {
        result = result.withNewSession
      } else {
        result = result.withSession {
          svc.session.deleted.foldLeft(request.session)((s, k) => s - k)
        }
        result = result.withSession {
          svc.session.all.foldLeft(request.session) { (s, kv) => s + kv }
        }
      }
    }

    ElasticServiceUtil.logResponse(resText, cTypeOpt)
    result
  }
}
