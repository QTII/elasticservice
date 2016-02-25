package elasticservice.util

import java.text.SimpleDateFormat

object DateUtil {
	val MS_MINUTE = 60000L
	val MS_HOUR = 3600000L
	val MS_DAY = 86400000L
	val MS_WEEK = 604800000L
	val MS_YEAR = 31536000000L
	
  val DEFAULT_DATE_FORMAT: String = "yyyy-MM-dd HH:mm:ss.SSS"
  
  def toString(date: Any): String = toString(date, null)
  
  def toString(date:Any, fmt: String): String = {
		val format = 
		  if (fmt == null || fmt == "")
        DEFAULT_DATE_FORMAT
      else
        fmt
			  
		date match {
      case o: java.sql.Date => {
        val formatter = new SimpleDateFormat(format)
        formatter.format(o)
      }
      case o: java.util.Date => {
        val formatter = new SimpleDateFormat(format)
        formatter.format(o)
      }
//      case o: java.sql.Timestamp => {
//        val formatter = new SimpleDateFormat(format)
//        formatter.format(o)
//      }
      case o: Long => toString(new java.util.Date(o.longValue()), format)
      case _ => date.toString
		}
	}
	
	private def twoDigit(num: Int): String = 
		if (0 <= num && num < 10)
			"0" + num
		else
			String.valueOf(num)
			
	private def toMiMilli(time: Long): Int =  ((time % MS_HOUR) / MS_MINUTE).toInt
	
	def commaNumber(num: Long): String = commaNumber(num, 1)
	
	def commaNumber(num: Long, depth: Int): String = {
		if (depth == 1) {
			if (1000L > num) {
				String.valueOf(num % 1000L)
			} else {
				commaNumber(num, depth + 1) + "," + String.valueOf(threeDigit(num % 1000L))
			}
		} else {
			if (mmm(depth) > num) {
				String.valueOf((num % mmm(depth)) / mmm(depth - 1))
			} else {
				commaNumber(num, depth + 1) + "," + String.valueOf(threeDigit((num % mmm(depth)) / mmm(depth - 1)))
			}
		}
	}
	
	private def mmm(depth: Int): Long =
		if (depth <= 0) 1L
		else 1000L * mmm(depth - 1) 
	
	private def threeDigit(num: Long): String =
		if (0 <= num && num < 10)
			"00" + num
		else if (10 <= num && num < 100)
			"0" + num
		else
			String.valueOf(num)
			
	private def toHrMilli(time: Long): Int = ((time % MS_DAY) / MS_HOUR).toInt
	
	private def toSeMilli(time: Long): Int = ((time % MS_MINUTE) / 1000).toInt
	
	private def toMsMilli(time: Long): Int = (time % 1000).toInt
	
	private def toDyMilli(time: Long): Int = (time / MS_DAY).toInt
  
  def toElapsedTimeString(time: Long): String =
		if (time < MS_DAY)
			twoDigit(toHrMilli(time)) + ":" + twoDigit(toMiMilli(time)) + ":" + 
			twoDigit(toSeMilli(time)) + "." + threeDigit(toMsMilli(time)) + 
			"(" + commaNumber(time) + "ms)"
		else
			toDyMilli(time) + "day " + 
			twoDigit(toHrMilli(time)) + ":" + twoDigit(toMiMilli(time)) + ":" + 
			twoDigit(toSeMilli(time)) + "." + threeDigit(toMsMilli(time)) + 
			"(" + commaNumber(time) + "ms)"
}