package elasticservice.web.play

import java.io.File

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import elasticservice.DefaultVal
import elasticservice.ElasticConfigurator
import elasticservice.Key
import elasticservice.util.DataValid
import elasticservice.util.FilePathUtil
import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.guice.GuiceApplicationLoader

class PlayConfigListener extends GuiceApplicationLoader() {

  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val extra = Configuration("a" -> 1)
    val builder = initialBuilder
      .in(context.environment)
      .loadConfig(extra ++ context.initialConfiguration)
      .overrides(overrides(context): _*)

    PlayConfigListener.loadElasticConf(context.initialConfiguration, context.environment.rootPath)

    builder
  }
}

object PlayConfigListener { //extends LazyLogging {
  def loadElasticConf(conf: Configuration, rootDir: File) {
    detectXmlPath(conf, rootDir) match {
      case Success(f) => ElasticConfigurator.init(f, Some(rootDir))
      case Failure(e) => throw e //logger.error(ExceptionDetail.getDetail(e))
    }
  }

  def detectXmlPath(conf: Configuration, rootDir: File): Try[File] = {
    Try {
      var esConfigOpt = conf.getString(Key.PropESConfig)

      if (esConfigOpt == None) {
        val p = System.getenv(Key.PropESConfig)
        if (DataValid.isNotEmpty(p))
          esConfigOpt = Some(p)
      }

      if (esConfigOpt == None) {
        esConfigOpt = Some(Key.PropESConfig + File.separator + DefaultVal.ESXMLFile)
      }

      var absolutePath = esConfigOpt.get.charAt(0) match {
        case '/' | '\\' => esConfigOpt.get
        case _          => rootDir.getAbsolutePath + File.separator + esConfigOpt.get
      }

      val xmlPath = FilePathUtil.adaptPath(absolutePath)
      val xmlFile = new File(xmlPath)

      if (!xmlFile.exists)
        throw new Exception("file not found: " + xmlPath)
      else if (!xmlFile.isFile)
        throw new Exception("not file: " + xmlPath)
      else
        xmlFile
    }
  }
}
