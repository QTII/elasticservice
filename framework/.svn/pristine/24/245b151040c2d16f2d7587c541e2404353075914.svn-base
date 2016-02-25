package scalacheck.elasticservice.util.ep

import java.io.File

import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import org.scalacheck.Prop.propBoolean
import org.scalacheck.Properties

import elasticservice.util.TextFileUtil
import elasticservice.util.ep.FileReader

object FileReaderTest extends Properties("FileReader") {
  val genFile = Gen.oneOf(new File(".").listFiles().filter { f => f.isFile() })
  
  val genEncodingType = Gen.oneOf(None, Option("UTF-8"), Option("EUC-KR"))
  
  property("read") = forAll (genFile, genEncodingType) { 
		(file, encodingOpt) =>
			val fr = FileReader(file, encodingOpt)
			val ism = fr.read()
			
			ism.text == TextFileUtil.textFrom(file, encodingOpt).get
  }
}