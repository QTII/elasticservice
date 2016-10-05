package elasticservice

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.ElasticParamsUtil
import elasticservice.util.ep.GenTo
import elasticservice.util.ep.InputSourceMeta
import elasticservice.util.ep.json13.GenToJSON13

object ElasticServiceUtil extends LazyLogging {
  def logRequest(inSrc: InputSourceMeta) {
    logger.info("request[" + inSrc.contentType + "]: " + shorten(inSrc.text))
  }

  def logResponse(resText: String, cTypeOpt: Option[String]) {
    logger.info("response[" + cTypeOpt.getOrElse("") + "]: " + shorten(resText))
  }

  private def shorten(msg: String, max: Int = 200) = if (msg.size > max) msg.substring(0, max) + "..." else msg

  def tryGenFrom(inSrc: InputSourceMeta): Try[ElasticParams] =
    Try {
      ElasticParamsUtil.detectGenFrom(inSrc.contentType, inSrc.text).
        getOrElse(throw new Exception("unknown request format")).
        gen(inSrc.text, inSrc.encodingOpt)
    }

  def tryDetectGenTo(reqEP: ElasticParams): Try[GenTo] =
    ElasticServiceUtil.getResponseType(reqEP).
      orElse(Try { "json13" }).
      flatMap { ElasticParamsUtil.tryDetectGenTo }

  val DefaultGenTo = GenToJSON13

  def execService(inSrc: InputSourceMeta, ssMap: Map[String, String]): (Try[ElasticService], String, Option[String]) = {
    tryGenFrom(inSrc) match {
      case Success(reqEP) =>
        val svc = ElasticServiceDispatcher.loadService(reqEP)
        svc.foreach { s => s.session ++= ssMap }

        val genTo = tryDetectGenTo(reqEP) match {
          case Success(genTo) => genTo
          case Failure(e) => DefaultGenTo
        }

        svc.flatMap { s => s.execute(reqEP) } match {
          case Success(resEP) => (svc, genTo.gen(resEP), genTo.contentType)
          case Failure(e) => (svc, genTo.gen(ElasticServiceUtil.epWithCodeMessage(999, e.toString)), genTo.contentType)
        }
      case Failure(e) => (new Failure(e), DefaultGenTo.gen(ElasticServiceUtil.epWithCodeMessage(999, e.toString)), DefaultGenTo.contentType)
    }
  }

  /**
   * Http 요청으로부터 파라미터 'service'의 값을 구한다.
   */
  def getServiceName(req: ElasticParams): Try[String] = {
    req.get(ParamKey.KEY_SERVICE) match {
      case Some(svcName) => Success(svcName.asInstanceOf[String])
      case None => Failure(new Exception("missing parameter '" + ParamKey.KEY_SERVICE + "'"))
    }
  }

  /**
   * Http 요청으로부터 파라미터 'service.resType'의 값을 구한다.
   */
  def getResponseType(req: ElasticParams): Try[String] = {
    req.get(ParamKey.KEY_SERVICE_RES_TYPE) match {
      case Some(resType) => Success(resType.asInstanceOf[String])
      case None => Failure(new Exception("missing parameter '" + ParamKey.KEY_SERVICE_RES_TYPE + "'"))
    }
  }

  def getCode(ep: ElasticParams): Int = ep.get("code").getOrElse("0").toString.toInt

  def getMessage(ep: ElasticParams): String = ep.get("message").getOrElse("").toString

  def setCodeAndMsg(ep: ElasticParams, code: Int, message: String) {
    ep += "code" -> code
    ep += "message" -> message
  }
  /*
  def getElasticParams(svcTry: Try[ElasticParams]): ElasticParams = {
    svcTry match {
      case Success(res) => res
      case Failure(e) =>
        logger.error(ExceptionDetail.getDetail(e))
        epWithCodeMessage(999, e.getMessage)
    }
  }
*/
  def epWithCodeMessage(code: Int, message: String): ElasticParams = {
    val ep = ElasticParams() //ElasticParams.empty
    ElasticServiceUtil.setCodeAndMsg(ep, code, message)
    ep
  }
}