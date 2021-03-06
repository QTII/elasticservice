package elasticservice.web.servlet

import java.io.File

import com.typesafe.scalalogging.LazyLogging

import elasticservice.DefaultVal
import elasticservice.ElasticConfigurator
import elasticservice.Key
import elasticservice.util.DataValid
import elasticservice.util.ExceptionDetail
import elasticservice.util.FilePathUtil
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class ServletConfigListener extends ServletContextListener with LazyLogging {
  def contextDestroyed(sce: ServletContextEvent) { /* FileWatcherTimer.cancelAll() */ }

  def contextInitialized(sce: ServletContextEvent) {
    try {
      val context = sce.getServletContext()

      var esConfig = context.getInitParameter(Key.PropESConfig)

      if (DataValid.isEmpty(esConfig))
        esConfig = Key.PropESConfig + File.separator + DefaultVal.ESXMLFile

      var absolutePath = esConfig.charAt(0) match {
        case '/' | '\\' => esConfig
        case _          => context.getRealPath("/") + File.separator + esConfig
      }

      val xmlPath = FilePathUtil.adaptPath(absolutePath)
      val xmlFile = new File(xmlPath)

      if (!xmlFile.exists)
        throw new Exception("file not found: " + xmlPath)
      else if (!xmlFile.isFile)
        throw new Exception("not file: " + xmlPath)
      else
        ElasticConfigurator.init(new File(xmlPath), Some(new File(context.getRealPath("/"))))
    } catch {
      case e: Throwable =>
        logger.error(ExceptionDetail.getDetail(e))
        throw e
    }
  }
}