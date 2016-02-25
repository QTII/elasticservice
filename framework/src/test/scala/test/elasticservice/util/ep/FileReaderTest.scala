package test.elasticservice.util.ep

import java.io.File
import elasticservice.util.ep.FileReader

object FileReaderTest {
  def main(args: Array[String]): Unit = {
    val file = new File("C:/Dev/scala_sdk_workspace/elastic-scala-1.5.1/테스트/ep.json")
    val fileReader = FileReader(file, None)
    println(fileReader.read())
  }
}
