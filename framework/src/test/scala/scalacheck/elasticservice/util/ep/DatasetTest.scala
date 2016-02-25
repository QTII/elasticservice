package scalacheck.elasticservice.util.ep

import org.scalacheck.Prop.BooleanOperators
import org.scalacheck.Prop.AnyOperators
import org.scalacheck.Prop.forAll
import org.scalacheck.Prop.all
import org.scalacheck.Properties
import elasticservice.epMkString
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import scalacheck._
import org.scalacheck.Gen
import org.scalacheck.Arbitrary

object DatasetTest extends Properties("Dataset") {
  property("apply1") = forAll (Gen.alphaStr, Gen.listOf(genColumnInfo)) { 
  	(name, colInfos) =>
  		val ds = Dataset(name, colInfos)
  		
  		all(
 				ds.name ?= name,
  			ds._colInfos ?= colInfos
  		)
  }
  
  property("addColInfo") = forAll { (name: String, ci: ColumnInfo) =>
  	val ds = Dataset(name)
		ds.addColInfo(ci)
		
		val cis = ds.colInfos
		
		ds.colInfo(cis.size - 1) ?= ci
  }
  
  property("rows_=") = forAll { (ds: Dataset, ls: List[Map[String, Any]]) =>
		ds.rows_=(ls)
		
		ds.nrOfRowss ?= ls.size
  }
  
  property("+=(row: Map[String, Any])") = forAll { (ds: Dataset, m: Map[String, Any]) =>
  	val originRowsCount = ds.nrOfRowss
  	
		ds += m
		
		ds.nrOfRowss ?= originRowsCount + 1
  }
  
  implicit val arbAny: Arbitrary[Any] = Arbitrary(Gen.const(new Object))
  
  property("+=(colId: String, value: Any)") = forAll { (ds: Dataset, colId: String, value: Any) =>
		ds += (colId, value)
		ds.rows.exists { x => x.exists { case (k, v) => k == colId && v == value } }
  }
}