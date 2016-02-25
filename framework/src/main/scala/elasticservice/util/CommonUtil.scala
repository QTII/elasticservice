package elasticservice.util

object CommonUtil {
  val NEW_LINE = System.getProperty("line.separator") match {
    case null => "\n"
    case a => a 
  }
}