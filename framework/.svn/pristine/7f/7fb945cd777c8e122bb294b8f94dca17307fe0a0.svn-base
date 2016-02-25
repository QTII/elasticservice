package elasticservice.util

import scala.util.Try

object Hangul {
  def fromJVM(str: String): String = Try { new String(str.getBytes("8859_1"), "KSC5601") }.getOrElse(str)
  def toJVM(str: String): String = Try { new String(str.getBytes("KSC5601"), "8859_1") }.getOrElse(str)
}