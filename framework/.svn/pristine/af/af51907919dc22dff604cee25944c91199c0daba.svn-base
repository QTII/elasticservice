package elasticservice.util.sqlrepo.sql

import java.sql.{Date => SDate}
import java.sql.{Time => STime}
import java.sql.{Timestamp => STimestamp}
import java.text.SimpleDateFormat
import java.util.Date
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import elasticservice.util.DataValid
import elasticservice.util.ReflectionUtil

object TypeConvertor {
  def convert[T](v: Any, t: Class[T]): Option[Any] = {
    if (DataValid.isEmpty(v) || DataValid.isEmpty(t))
      None
    else {
    	val C = classOf[Char]
    	val I = classOf[Int]
    	val L = classOf[Long]
    	val D = classOf[Double]
    	val F = classOf[Float]
    	val B = classOf[Boolean]
    	val S = classOf[String]
    	val Da = classOf[Date]
    	val Sd = classOf[SDate]
    	val Sts = classOf[STimestamp]
    	val St = classOf[STime]
      t match {
        case C => Some(v.toString.charAt(0))
        case I => Some(v.toString.toInt)
        case L => Some(v.toString.toLong)
        case D => Some(v.toString.toDouble)
        case F => Some(v.toString.toFloat)
        case B => Some(v.toString.toBoolean)
        case S => Some(v.toString)
        case Da => toDate(v.toString)
        case Sd => toSqlDate(v.toString)
        case Sts => toSqlTimestamp(v.toString)
        case St => toSqlTime(v.toString)
        case _ => Some(v) 
      }
    }
  }

  def toDate(s: String, formatStr: String = "yyyy-MM-dd HH:mm:ss.SSS"): Option[Date] = s match {
    case "" => None
    case _ => {
      val format = new SimpleDateFormat(formatStr)
      Try(format.parse(s)) match {
        case Success(d) => Some(d)
        case Failure(_) => None
      }
    }
  }

  def toSqlTimestamp(s: String, formatStr: String = "yyyy-MM-dd HH:mm:ss.SSS"): Option[STimestamp] = s match {
    case "" => None
    case _ => {
      val format = new SimpleDateFormat(formatStr)
      Try(new STimestamp(format.parse(s).getTime)) match {
        case Success(t) => Some(t)
        case Failure(_) => None
      }
    }
  }

  def toSqlTime(s: String, formatStr: String = "HH:mm:ss.SSS"): Option[STime] = s match {
    case "" => None
    case _ => {
      val format = new SimpleDateFormat(formatStr)
      Try(new STime(format.parse(s).getTime)) match {
        case Success(t) => Some(t)
        case Failure(_) => None
      }
    }
  }

  def toSqlDate(s: String, formatStr: String = "yyyy-MM-dd"): Option[SDate] = s match {
    case "" => None
    case _ => {
      val format = new SimpleDateFormat(formatStr)
      Try(new SDate(format.parse(s).getTime)) match {
        case Success(t) => Some(t)
        case Failure(_) => None
      }
    }
  }
  
  def toImmutable(m: scala.collection.mutable.Map[Any, Any]): scala.collection.immutable.Map[Any, Any] = {
    m.toMap
  }
  
  def toImmutable(l: scala.collection.mutable.MutableList[Any]): scala.collection.immutable.List[Any] = {
    l.toList
  }
  
  def toMutable(m: scala.collection.immutable.Map[Any, Any]): scala.collection.mutable.Map[Any, Any] = {
    scala.collection.mutable.Map[Any, Any]() ++ m
  }
  
  def toMutable(l: scala.collection.immutable.List[Any]): scala.collection.mutable.MutableList[Any] = {
    scala.collection.mutable.MutableList[Any](l:_*)
  }
}