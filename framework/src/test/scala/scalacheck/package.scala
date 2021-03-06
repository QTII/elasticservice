import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen._
import org.scalacheck.Gen

import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.Record

package object scalacheck {
  implicit val arbClass: Arbitrary[Class[_]] = 
		Arbitrary(oneOf(
				classOf[String], classOf[Int], classOf[Long], classOf[Double], classOf[Float],
				classOf[Boolean], classOf[java.sql.Timestamp], classOf[java.sql.Time],
				classOf[java.sql.Date]))
				
//	val genMap = Gen.mapOf[String,AnyVal]{ //Gen.nonEmptyMap[String,AnyVal]{
//  	for {
//  			str <- Gen.alphaStr
//  			anyVal <- Arbitrary.arbAnyVal.arbitrary
//  	} yield (str, anyVal)
//  }
//  
//  implicit val arbMapStringAnyVal: Arbitrary[Map[String,AnyVal]] = Arbitrary(genMap)
				
	val genMap = Gen.mapOf[String,Any]{
  	for {
  			str <- Gen.alphaStr
  			any <- Gen.oneOf(arbitrary[AnyVal], Gen.const(new Object))
  	} yield (str, any)
  }
  
  implicit val arbMapStringAny: Arbitrary[Map[String,Any]] = Arbitrary(genMap)
  
  val genClassNames = oneOf("Char","Character","Int","Integer","Long",
		"BigDecimal","Double","Float","Boolean","String",
		"VARCHAR","Date","File","Map","List")
  		
  val genSqlTypes = oneOf("CHAR","CHARACTER","INT","BIGINT","LONG",
  		"DOUBLE","FLOAT","BOOLEAN","VARCHAR","STRING",
  		"TIMESTAMP","TIME","DATE")
  		
  val genColumnInfo = for {
  	id <- alphaStr
  	text <- alphaStr
  	typeClass <- arbClass.arbitrary
  	size <- posNum[Int]
  } yield ColumnInfo(id, text, typeClass, size)
  
  implicit val arbColumnInfo: Arbitrary[ColumnInfo] = Arbitrary(genColumnInfo)
  
  val genDataset = for {
  	name <- alphaStr
  	_colInfos <- arbitrary[List[ColumnInfo]]
  } yield Dataset(name, _colInfos)
  
  implicit val arbDataset: Arbitrary[Dataset] = Arbitrary(genDataset)
  
  val genElasticParams = for {
  	a <- alphaChar
  } yield ElasticParams()
  
  val genElasticParams2 = for {
  	m <- arbitrary[Map[String, AnyVal]]
  } yield { 
  	val ep = ElasticParams()
  	ep  ++= m
  	ep
  }
  
  implicit val arbElasticParams: Arbitrary[ElasticParams] = Arbitrary(genElasticParams)
  
  val genAlphaUpperString = Gen.listOf(Gen.alphaUpperChar).map { x => x.mkString }
  
  
  val genRecord = for {
    m <- genMap
  } yield Record(m)
  
//  implicit val arbRecord: Arbitrary[Record] = Arbitrary(genRecord)
  
}