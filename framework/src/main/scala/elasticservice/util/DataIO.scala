package elasticservice.util

import java.io.InputStream
import scala.io.Source

object DataIO {
  def readString(is: InputStream, encoding: String) = Source.fromInputStream(is, encoding).mkString
}