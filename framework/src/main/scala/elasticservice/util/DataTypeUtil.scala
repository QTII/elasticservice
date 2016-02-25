package elasticservice.util

import java.math.BigInteger
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataTypeUtil {
  val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"

  def DefaultTypeClass: Class[_] = classOf[String]

  def DefaultTypeName: String = "String"

  def DefaultSqlType: Int = java.sql.Types.CHAR

  def classToName(c: Class[_]): String = DataTypeMap.classToName(c).getOrElse(c.getName)

  def nameToClass(typeName: String): Option[Class[_]] =
    nameToClass(typeName, DataTypeUtil.getClass().getClassLoader())

  def nameToClass(typeName: String, cl: ClassLoader): Option[Class[_]] =
    DataTypeMap.nameToClass(typeName).
      orElse(sqlTypeClass(typeName)).
      orElse(ClassLoaderUtil.forName(typeName, cl).toOption)

  def sqlTypeClass(sqlTypeName: String): Option[Class[_]] =
  	sqlTypeName match {
      case "CHAR" | "CHARACTER" => Some(classOf[String])
      case "INT"   							=> Some(classOf[Int])
      case "BIGINT" | "LONG"    => Some(classOf[Long])
      case "DOUBLE"    					=> Some(classOf[Double])
      case "FLOAT"     					=> Some(classOf[Float])
      case "BOOLEAN"   					=> Some(classOf[Boolean])
      case "VARCHAR" | "STRING"	=> Some(classOf[String])
      case "TIMESTAMP" 					=> Some(classOf[java.sql.Timestamp])
      case "TIME"      					=> Some(classOf[java.sql.Time])
      case "DATE"      					=> Some(classOf[java.sql.Date])
      case _                    => None
    }

  def sqlTypeClass(sqlTypeCode: Int): Option[Class[_]] =
    sqlTypeCode match {
      case java.sql.Types.CHAR      => Some(classOf[String])
      case java.sql.Types.INTEGER   => Some(classOf[Int])
      case java.sql.Types.BIGINT    => Some(classOf[Long])
      case java.sql.Types.DOUBLE    => Some(classOf[Double])
      case java.sql.Types.FLOAT     => Some(classOf[Float])
      case java.sql.Types.BOOLEAN   => Some(classOf[Boolean])
      case java.sql.Types.VARCHAR   => Some(classOf[String])
      case java.sql.Types.TIMESTAMP => Some(classOf[java.sql.Timestamp])
      case java.sql.Types.TIME      => Some(classOf[java.sql.Time])
      case java.sql.Types.DATE      => Some(classOf[java.sql.Date])
      case _                        => None
    }

  def sqlType(sqlTypeName: String): Option[Int] =
    sqlTypeName.toUpperCase() match {
      case "CHAR" | "CHARACTER" => Some(java.sql.Types.CHAR)
      case "INT"                => Some(java.sql.Types.INTEGER)
      case "BIGINT" | "LONG"    => Some(java.sql.Types.BIGINT)
      case "DOUBLE"             => Some(java.sql.Types.DOUBLE)
      case "FLOAT"              => Some(java.sql.Types.FLOAT)
      case "BOOLEAN"            => Some(java.sql.Types.BOOLEAN)
      case "VARCHAR" | "STRING" => Some(java.sql.Types.VARCHAR)
      case "TIMESTAMP"          => Some(java.sql.Types.TIMESTAMP)
      case "TIME"               => Some(java.sql.Types.TIME)
      case "DATE"               => Some(java.sql.Types.DATE)
      case _                    => None
    }

  def sqlType(javaClass: Class[_]): Option[Int] =
    if (javaClass == classOf[Char] || javaClass == classOf[Character])
      Some(java.sql.Types.CHAR)
    else if (javaClass == classOf[Int] || javaClass == classOf[Integer])
      Some(java.sql.Types.INTEGER)
    else if (javaClass == classOf[Long] || javaClass == classOf[BigDecimal] || javaClass == classOf[BigInteger])
      Some(java.sql.Types.BIGINT)
    else if (javaClass == classOf[Double])
      Some(java.sql.Types.DOUBLE)
    else if (javaClass == classOf[Float])
      Some(java.sql.Types.FLOAT)
    else if (javaClass == classOf[Boolean])
      Some(java.sql.Types.BOOLEAN)
    else if (javaClass == classOf[String])
      Some(java.sql.Types.VARCHAR)
    else if (javaClass == classOf[java.sql.Timestamp] || javaClass == classOf[java.util.Date])
      Some(java.sql.Types.TIMESTAMP)
    else if (javaClass == classOf[java.sql.Time])
      Some(java.sql.Types.TIME)
    else if (javaClass == classOf[java.sql.Date])
      Some(java.sql.Types.DATE)
    else
      None

  def changeValueType(value: Any, f: String => Any): Any =
    value match {
      case s: String => f(s)
      case a         => f(String.valueOf(a))
    }

  def valueByTypeName(valueExpression: Any, typeName: String): Option[Any] =
    nameToClass(typeName).map(valueByClass(valueExpression, _))

  def valueByClass(valueExpression: Any, typeClass: Class[_]): Any =
    if (valueExpression == null || typeClass == null || typeClass == valueExpression.getClass())
      valueExpression
    else if (valueExpression == "")
      ReflectionUtil.newInstance(typeClass, Nil)
    else if (typeClass == classOf[Char])
      valueExpression.toString().charAt(0)
    else if (typeClass == classOf[Int])
      changeValueType(valueExpression, _.toInt)
    else if (typeClass == classOf[Long])
      changeValueType(valueExpression, _.toLong)
    else if (typeClass == classOf[Double])
      changeValueType(valueExpression, _.toDouble)
    else if (typeClass == classOf[Float])
      changeValueType(valueExpression, _.toFloat)
    else if (typeClass == classOf[Boolean])
      changeValueType(valueExpression, _.toBoolean)
    else if (typeClass == classOf[String])
      String.valueOf(valueExpression)
    else if (typeClass == classOf[java.util.Date])
      new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.US).parse(valueExpression.toString, new ParsePosition(0))
    else if (typeClass == classOf[Timestamp])
      Timestamp.valueOf(valueExpression.toString())
    else if (typeClass == classOf[Time])
      Time.valueOf(valueExpression.toString)
    else if (typeClass == classOf[java.sql.Date])
      java.sql.Date.valueOf(valueExpression.toString)
    else
      ReflectionUtil.newInstance(typeClass, List(valueExpression.toString))
}