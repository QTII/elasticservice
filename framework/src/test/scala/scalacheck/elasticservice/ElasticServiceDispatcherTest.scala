package scalacheck.elasticservice

import org.scalacheck.Properties
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import org.scalacheck.Prop.AnyOperators
import elasticservice.ElasticServiceDispatcher
import elasticservice.util.ep.ElasticParams
import elasticservice.ParamKey

object ElasticServiceDispatcherTest extends Properties("ElasticServiceDispatcher") {
	val genServiceName = Gen.oneOf(
			"elasticservice.service.sqlrepo.AllIdsFromDisk",
			"elasticservice.service.sqlrepo.AllPkgIdsFromDisk",
			"elasticservice.service.sqlrepo.GetXML",
			"elasticservice.service.sqlrepo.RemoveSql",
			"elasticservice.service.sqlrepo.RenamePkg",
			"elasticservice.service.sqlrepo.SaveSql",
			"elasticservice.service.sqlrepo.TemplatePkgIds",
			"elasticservice.service.sqlrepo.TemplateSqlIds",
			"elasticservice.service.EchoService",
			"elasticservice.service.ErrorService",
			"elasticservice.service.QueryService"
	)
	
  property("loadService") = forAll (genServiceName) { 
		(serviceName) =>
			val ep = ElasticParams()
			ep += (ParamKey.KEY_SERVICE, serviceName)
			
			ElasticServiceDispatcher.loadService(serviceName).get ?= ElasticServiceDispatcher.loadService(ep).get
  }
}