package test.elasticservice.util.sqlrepo

import java.io.File
import scala.util.Failure
import scala.util.Success
import elasticservice.util.GenXML
import elasticservice.util.sqlrepo.SqlXmlLoader

object SqlXmlLoaderTest {
  def main(args: Array[String]) {
    val p = "D:\\Dev\\workspace\\elasticservice\\esConfig\\sqlrepo\\sample\\select_isEmpty.xml"
    GenXML.toString(new File(p), None) match {
      case Success(xmlStr) => 
        val sqlTry = new SqlXmlLoader().loadString(xmlStr)
        sqlTry.foreach { sql => print(sql.toString()) }
      case Failure(e) => 
        print(e)
    }
  }
}