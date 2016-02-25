package elasticservice

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.util.ExceptionDetail
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.ElasticParamsUtil
import elasticservice.util.ep.GenTo
import elasticservice.util.ep.InputSourceMeta
import elasticservice.util.ep.json12.GenToJSON12

object ElasticServiceUtil extends LazyLogging {
  def logRequest(inSrc: InputSourceMeta) {
    logger.info("request[" + inSrc.contentType + "]: " + inSrc.text)
  }

  def logResponse(resText: String, cTypeOpt: Option[String]) {
    logger.info("response[" + cTypeOpt.getOrElse("") + "]: " + resText)
  }

  private def copySessionToService(sessionMap: Map[String, String], svc: ElasticService) {
    svc.session ++= sessionMap
  }

  def execService(inSrc: InputSourceMeta, ssMap: Map[String, String]): (Option[ElasticService], String, Option[String]) = {
    var genToOpt: Option[GenTo] = None
    var svcOpt: Option[ElasticService] = None

    val resTextTry: Try[String] = Try {
      val genFromOpt = ElasticParamsUtil.detectGenFrom(inSrc.contentType, inSrc.text)
      if (genFromOpt == None)
        throw new Exception("unknown request format")

      val reqEP = genFromOpt.get.gen(inSrc.text, inSrc.encodingOpt)

      val resType = ElasticServiceUtil.getResponseType(reqEP).getOrElse("json13")
      genToOpt = ElasticParamsUtil.detectGenTo(resType)

      ElasticServiceDispatcher.loadService(reqEP) match {
        case Success(svc) =>
          svcOpt = Some(svc)
          copySessionToService(ssMap, svc)
          val svcTry = svc.execute(reqEP)
          val resEP = ElasticServiceUtil.getElasticParams(svcTry)

          genToOpt match {
            case Some(genTo) => genTo.gen(resEP)
            case None        => resEP.toString
          }
        case Failure(e) => throw e
      }
    }

    val (resText, cTypeOpt) = resTextTry match {
      case Success(resText) =>
        genToOpt match {
          case Some(genTo) => (resText, Some(genTo.contentType))
          case None        => (resText, None)
        }
      case Failure(e) =>
        logger.error(ExceptionDetail.getDetail(e))
        genToOpt match {
          case Some(genTo) => (genTo.gen(ElasticServiceUtil.epWithCodeMessage(999, e.toString)), Some(genTo.contentType))
          case None        => (GenToJSON12.gen(ElasticServiceUtil.epWithCodeMessage(999, e.toString)), Some(GenToJSON12.contentType))
        }
    }

    (svcOpt, resText, cTypeOpt)
  }

  /**
   * Http 요청으로부터 파라미터 'service'의 값을 구한다.
   */
  def getServiceName(req: ElasticParams): Try[String] = {
    req.get(ParamKey.KEY_SERVICE) match {
      case Some(svcName) => Success(svcName.asInstanceOf[String])
      case None          => Failure(new Exception("missing parameter '" + ParamKey.KEY_SERVICE + "'"))
    }
  }

  /**
   * Http 요청으로부터 파라미터 'service.resType'의 값을 구한다.
   */
  def getResponseType(req: ElasticParams): Try[String] = {
    req.get(ParamKey.KEY_SERVICE_RES_TYPE) match {
      case Some(resType) => Success(resType.asInstanceOf[String])
      case None          => Failure(new Exception("missing parameter '" + ParamKey.KEY_SERVICE_RES_TYPE + "'"))
    }
  }

  def getCode(ep: ElasticParams): Int = ep.get("code").getOrElse("0").toString.toInt

  def getMessage(ep: ElasticParams): String = ep.get("message").getOrElse("").toString

  def setCodeAndMsg(ep: ElasticParams, code: Int, message: String) {
    ep += "code" -> code
    ep += "message" -> message
  }

  def getElasticParams(svcTry: Try[ElasticParams]): ElasticParams = {
    svcTry match {
      case Success(res) => res
      case Failure(e) =>
        logger.error(ExceptionDetail.getDetail(e))
        epWithCodeMessage(999, e.getMessage)
    }
  }

  def epWithCodeMessage(code: Int, message: String): ElasticParams = {
    val ep = ElasticParams() //ElasticParams.empty
    ElasticServiceUtil.setCodeAndMsg(ep, code, message)
    ep
  }
}