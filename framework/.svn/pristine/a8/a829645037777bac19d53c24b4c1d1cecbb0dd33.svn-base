package scalacheck.elasticservice.util.ep

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.BooleanOperators
import org.scalacheck.Prop.AnyOperators
import org.scalacheck.Prop.forAll
import org.scalacheck.Prop.propBoolean
import org.scalacheck.Properties
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import scalacheck.arbColumnInfo
import scalacheck.arbDataset
import scalacheck.arbElasticParams
import scalacheck.genAlphaUpperString
import scalacheck.genElasticParams
import org.scalacheck.Arbitrary

object ElasticParamsTest extends Properties("ElasticParams") {
	private val genUpperKeyMap = Gen.mapOf (
		for {
			k <- genAlphaUpperString
			//v <- arbitrary[AnyVal]
		} yield (k -> new Object)
	)
	
	property("++=") = forAll (genElasticParams, genUpperKeyMap) { 
		(ep, map) =>
			ep ++= map
			
			s"ep.parameters : $ep.parameters" |: map == ep.parameters
  }
	
	implicit val arbAny: Arbitrary[Any] = Arbitrary(Gen.const(new Object))
	
	property("+=") = forAll { (ep: ElasticParams, param: (String, Any)) =>
		ep += param
		
		Map(param) == ep.parameters
  }
	
	property("-=") = forAll { (ep: ElasticParams, param: (String, Any)) =>
		ep += param
		ep -= param._1
		
		ep.parameters.size ?= 0
  }
	
	property("get") = forAll { (ep: ElasticParams, param: (String, Any)) =>
		ep += param
		
		ep.get(param._1).get ?= param._2
  }
	
	property("contains") = forAll { (ep: ElasticParams, param: (String, Any)) =>
		ep += param
		
		ep.contains(param._1)
  }
	
	property("isEmpty") = forAll { (ep: ElasticParams) =>
		ep += ("a" -> List())
		ep += ("b" -> Array())
		ep += ("c" -> "")
		ep += ("d" -> "   ")
		
		Prop.all(
			ep.isEmpty("a"),
			ep.isEmpty("b"),
			ep.isEmpty("c"),
			ep.isEmpty("d"),
			ep.isEmpty("111")
		)
  }
	
//	val genDatasetList = Gen.listOfN(10, genDataset)
	
	property("datasetsToList") = forAll { 
		(ep: ElasticParams, datasets: List[Dataset]) =>
			ep.datasets_=(datasets)
			val dsList = ep.datasetsToList
			
			Prop.atLeastOne(
				Prop.lzy(datasets.size == dsList.size),
				Prop.lzy(datasets.forall { x1 => dsList.exists { x2 => x1.name == x2.name } })
			)
  }
	
	property("datasets_=(datasets: Map[String, Dataset])") = forAll { 
		(ep: ElasticParams, datasets: Map[String, Dataset]) =>
			ep.datasets_=(datasets)
			
			ep.datasets.size ?= datasets.size
  }
	
	property("setDatasetRows") = forAll //(genElasticParams, Gen.alphaStr, Gen.listOfN(10, genMap)) 
	{ 
		(ep: ElasticParams, dsName: String, rows: List[Map[String, Any]]) =>
			ep.setDatasetRows(dsName, rows)
			
			val ds = ep.getDataset(dsName)
			
			val rowsCount = rows.size
			
			Prop.all(
				ds != None,
				ds.get.rows.size == rowsCount
			)
  }
	
	property("addDatasetRows") = forAll { 
		(ep: ElasticParams, dsName: String, rows: List[Map[String, Any]]) =>
			ep.addDatasetRows(dsName, rows)
			
			val ds = ep.getDataset(dsName)
			
			val rowsCount = rows.size
			
			Prop.all(
				"ds is None" |: ds != None,
				s"ds.get.rows.size is ${ds.get.rows.size}, rowsCount is ${rowsCount}" |: ds.get.rows.size == rowsCount
			)
  }

	property("addDatasetRow") = forAll { 
		(ep: ElasticParams, dsName: String, row: Map[String, Any]) =>
			ep.addDatasetRow(dsName, row)
			
			ep.getDataset(dsName).map {
				_.rows.exists { r => r == row }
			}.getOrElse(false)
  }
	
	property("addDatasetColumn") = forAll { 
		(ep: ElasticParams, dsName: String, colInfo: ColumnInfo, value: Any) =>
			ep.addDatasetColumn(dsName, colInfo, value)
			
			ep.getDataset(dsName).map {
				_.colInfos.exists { x => x == colInfo }
			}.getOrElse(false)
  }
}