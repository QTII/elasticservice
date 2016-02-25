package elasticservice

import scala.xml.Elem
import com.typesafe.scalalogging.LazyLogging
import elasticservice.util.ReflectionUtil
import elasticservice.util.ep.ElasticParams
import scala.util.Try

object ElasticServiceDispatcher {

  def loadService(svcName: String): Try[ElasticService] = {
    Try {
      ReflectionUtil.newInstance(svcName, Nil).asInstanceOf[ElasticService]
    }.map { svc =>
      svc.setScopeOfRequest(ElasticService.ScopeKey_ReqTime -> System.currentTimeMillis())
      svc
    }
  }

  def loadService(req: ElasticParams): Try[ElasticService] =
    ElasticServiceUtil.getServiceName(req).flatMap(loadService(_))
}
