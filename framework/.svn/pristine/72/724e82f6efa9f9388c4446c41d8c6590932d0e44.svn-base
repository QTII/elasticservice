package scalacheck.elasticservice.util.ep

import scala.annotation.migration

import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.BooleanOperators
import org.scalacheck.Prop.AnyOperators
import org.scalacheck.Prop.all
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import elasticservice.epMkString
import elasticservice.util.ep.ColumnInfo

import scalacheck._

object ColumnInfoTest extends Properties("ColumnInfo") {
  property("apply1") = forAll (Gen.alphaStr, Gen.alphaStr, genClassNames, Gen.posNum[Int]) { 
  	(id, text, typeName, size) =>
  		val ci = ColumnInfo(id, text, typeName, size)
  		
  		all(
	  		ci.id ?= id,
	  		ci.text ?= text,
	  		//("typeClass" |: ci.typeClass.getSimpleName.toUpperCase() == typeName.toUpperCase()) &&
	  		ci.size ?= size
	  	)
  }
  
  property("apply4") = forAll (Gen.alphaStr, Gen.alphaStr, genClassNames, Gen.posNum[Int]) { 
  	(id, text, typeName, size) =>
  		val m = Map("id" -> id, "text" -> text, "type" -> typeName, "size" -> size)
  		val ci = ColumnInfo(m)
  		
  		all(
  			ci.id ?= id,
  			ci.text ?= text,
  			ci.size ?= size
  		)
  }
  
  property("userAttrs_") = forAll { (m: Map[String, AnyVal], ci: ColumnInfo) =>
  		ci.userAttrs_=(m)
  		
  		m.size ?= ci.userAttrs.size
  }
  
  property("containsAttr") = forAll { (m: Map[String, AnyVal], ci: ColumnInfo) =>
  		ci.userAttrs_=(m)
  		
  		all(m.keys.map { x => Prop(ci.containsAttr(x)) }.toList: _*)
  }
  
  property("getSubColumnInfo") = forAll { (ci: ColumnInfo, sub: ColumnInfo) =>
  		ci.addSubColumnInfo(sub)
  		
  		ci.getSubColumnInfo(sub.id).get ?= sub
  }
}