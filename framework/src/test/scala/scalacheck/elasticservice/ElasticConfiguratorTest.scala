package scalacheck.elasticservice

import java.io.File

import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.AnyOperators
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import elasticservice.ElasticConfigurator

object ElasticConfiguratorTest extends Properties("ElasticConfigurator") {
  //	val parent = "C:/Dev/mars_workspace/elasticservice-sample-0.1/esConfig"
  val parent = "../elasticservice-sample-0.1/esConfig"
  val genFile = Gen.const(new File(parent + File.separator + "elasticservice.xml"))

  property("init") = forAll(genFile) {
    (file) =>
      ElasticConfigurator.init(file, None)

      val xml = ElasticConfigurator.getXml

      Prop.all(
        ElasticConfigurator.Charset ?= Some("UTF-8"),
        (xml \ "sqlRepo").text ?= "sqlrepo")
  }
}