package scalacheck.elasticservice.util.ep

import org.scalacheck._
import org.scalacheck.Gen._
import org.scalacheck.Prop._
import elasticservice.util.ep.ColumnAttributes
import org.scalacheck.Test.{check, Parameters}

object ColumnAttributesTest extends Properties("ColumnAttributes") {
//	val genAttrList = nonEmptyListOf(
//		frequency(
//			9 -> alphaChar, 
//			1  -> const('&')
//		)
//	).map { x => x.mkString }
	
	val genAttrList = for {
			s <- nonEmptyListOf(alphaChar) 
			d <- const("&")
	} yield (s.mkString(d))
	  
  property("toMap") = forAll (genAttrList) { 
//	val toMap = forAll (genAttrList) {
		(attrStr) =>
			val ca = new ColumnAttributes(attrStr)
			val m = ca.toMap
			
			attrStr.split("&").forall(x => m.exists { case (k, v) => k.compareToIgnoreCase(x) == 0 && v == true } )
  }
	
//	val myParams = new Parameters.Default {
//	  override val minSuccessfulTests = 400
//	}
//	
//	org.scalacheck.Test.check(myParams, toMap)
}