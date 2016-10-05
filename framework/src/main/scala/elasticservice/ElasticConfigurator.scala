package elasticservice

import java.io.File

import scala.util.Failure
import scala.util.Success
import scala.xml.Elem

import com.typesafe.scalalogging.LazyLogging

import elasticservice.util.DataValid
import elasticservice.util.ExceptionDetail
import elasticservice.util.FilePathUtil
import elasticservice.util.GenXML
import elasticservice.util.XmlEnv
import elasticservice.util.sqlrepo.SqlRepo

object ElasticConfigurator extends LazyLogging {
  private var _xml: Elem = <Config></Config>
  private var appRootPath: Option[String] = None
  private var xmlDirOpt: Option[String] = None
  private var charsetOpt: Option[String] = None
  private var loginRequiredOpt: Option[Boolean] = None

  def init(xmlFile: File, appRootDir: Option[File]) {
    logger.info("************** elasticservice config file: " + xmlFile.getAbsolutePath)

    appRootPath = appRootDir.map(_.getAbsolutePath)
    xmlDirOpt = Some(FilePathUtil.getBasePath(xmlFile))

    GenXML.loadFile(xmlFile, None) match {
      case Success(xml) =>
        _xml = xml

        (_xml \\ "xmlEnv" \ "variable").foreach { node =>
          XmlEnv += (node \ "@name").text -> (node \ "@value").text.replace("&", "&amp;")
        }
        XmlEnv.iterator.foreach { kv => logger.trace("XmlEnv(" + kv._1 + "): " + kv._2) }

        (_xml \\ "charset").foreach { node =>
          if (DataValid.isNotEmpty(node.text))
            charsetOpt = Some(node.text)
        }
        logger.trace("charsetOpt: " + charsetOpt)

        (_xml \\ "loginRequired").foreach { node =>
          if (DataValid.isNotEmpty(node.text))
            loginRequiredOpt = Some(node.text.toBoolean)
        }
        logger.trace("loginRequiredOpt: " + loginRequiredOpt)

        (_xml \\ "sqlRepo").foreach { node =>
          SqlRepo.init(xmlDirOpt.get + File.separatorChar + node.text)
        }

      case Failure(e) => logger.error("file: " + xmlFile.getAbsolutePath + "\n" + ExceptionDetail.getDetail(e))
    }
  }

  def AppRootPath = appRootPath

  def Charset = charsetOpt

  def LoginRequiredOpt = loginRequiredOpt

  def getXml: Elem = _xml
}
