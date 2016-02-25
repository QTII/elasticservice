package elasticservice.util

object StringUtil {
  def splitWithoutEmpty(str: String, regex: String): Array[String] = str.split(regex).filter { x => x.trim() != "" }
}