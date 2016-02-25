package elasticservice.util.ep

import com.typesafe.scalalogging.LazyLogging

import elasticservice.util.ep.json12.GenFromJSON12
import elasticservice.util.ep.json12.GenToJSON12
import elasticservice.util.ep.json12.GenToJSONP12
import elasticservice.util.ep.json13.GenFromJSON13
import elasticservice.util.ep.json13.GenToJSON13
import elasticservice.util.ep.json13.GenToJSONP13
import elasticservice.util.ep.urlparams.GenFromURLParams
import elasticservice.util.ep.xml.GenFromXML
import elasticservice.util.ep.xml.GenToXML
import play.api.libs.json.JsError
import play.api.libs.json.JsLookupResult.jsLookupResultToJsLookup
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.JsValue.jsValueToJsLookup

object ElasticParamsUtil extends LazyLogging {
  def detectGenFrom(contentType: String, text: String): Option[GenFrom] = contentType.toLowerCase match {
    case c if c.contains("multipart/form-data") => Some(GenFromURLParams)
    case c if c.contains("application/json")    => detectGenFromInJSON(text)
    case c if c.contains("text/json")           => detectGenFromInJSON(text)
    case c if c.contains("application/xml")     => Some(GenFromXML)
    case c if c.contains("text/xml")            => Some(GenFromXML)
    case c if c.contains("application/x-www-form-urlencoded") =>
      if (text.startsWith("<?xml "))
        Some(GenFromXML)
      else if (text.contains("_jsonpKey_="))
        detectGenFromInJSON(text)
      else
        Some(GenFromURLParams)
    case "" | null =>
      if (text.contains("_jsonpKey_="))
        detectGenFromInJSON(text)
      else
        Some(GenFromURLParams)
    case _ => None
  }

  private def detectGenFromInJSON(bodyText: String): Option[GenFrom] = {
    detectEPTypeInJSON(bodyText).map { epType =>
      epType.toLowerCase match {
        case "json12" => GenFromJSON12
        case "json13" => GenFromJSON13
      }
    }
  }

  def detectGenFrom(json: JsValue): Option[GenFrom] = {
    (json \ "parameters" \ "epType").validate[String] match {
      case JsSuccess("json12", _) => Some(GenFromJSON12)
      case JsSuccess("json13", _) => Some(GenFromJSON13)
      case err @ JsError(_) =>
        logger.error(err.toString)
        None
      case _ => None
    }
  }

  private def detectEPTypeInJSON(jsonText: String): Option[String] = {
    val filter = "\"epType\":\""
    jsonText.indexOf(filter) match {
      case -1 => None
      case i =>
        jsonText.indexOf("\"", i + filter.size) match {
          case -1 => None
          case i2 => Some(jsonText.substring(i + filter.size, i2))
        }
    }
  }

  def detectGenTo(resType: String): Option[GenTo] = resType.toLowerCase match {
    case "json12"      => Some(GenToJSON12)
    case "jsonp12"     => Some(GenToJSONP12)
    case "json13"      => Some(GenToJSON13)
    case "jsonp13"     => Some(GenToJSONP13)
    case "platformxml" => Some(GenToXML)
    case _             => None
  }
}