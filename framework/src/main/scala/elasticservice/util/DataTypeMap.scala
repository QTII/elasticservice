package elasticservice.util

import java.io.File
import elasticservice.ElasticConfigurator
import java.util.Date
import com.typesafe.scalalogging.LazyLogging

object DataTypeMap extends LazyLogging {
  private val xml = ElasticConfigurator.getXml
  private val list = xml \ "Config" \ "dataTypeMap"

  private var NameToClass = Map[String, Class[_]](
    "Char" -> classOf[Char],
    "Character" -> classOf[Char],
    "Int" -> classOf[Int],
    "Integer" -> classOf[Int],
    "Long" -> classOf[Long],
    "BigDecimal" -> classOf[BigDecimal],
    "Double" -> classOf[Double],
    "Float" -> classOf[Float],
    "Boolean" -> classOf[Boolean],
    "String" -> classOf[String],
    "VARCHAR" -> classOf[String],
    "Date" -> classOf[Date],
    "File" -> classOf[File],
    "Map" -> classOf[Map[_, _]],
    "List" -> classOf[List[_]])

  private var ClassToName = Map.empty[Class[_], String]

  list.filter { _.label == "type" }.foreach { sub =>
    val typeName = sub \@ "name"
    val className = sub \@ "class"

    val classOpt: Option[Class[_]] = className.toUpperCase match {
      case "CHAR"    => Some(classOf[Char])
      case "STRING"  => Some(classOf[String])
      case "INT"     => Some(classOf[Int])
      case "LONG"    => Some(classOf[Long])
      case "DOUBLE"  => Some(classOf[Double])
      case "FLOAT"   => Some(classOf[Float])
      case "BOOLEAN" => Some(classOf[Boolean])
      case cn        => ClassLoaderUtil.forName(cn).toOption
    }

    for {
      c <- classOpt
    } yield NameToClass += (typeName -> c)
  }

  NameToClass.foreach { case (key, clazz) => ClassToName += (clazz -> key) }

  def nameToClass(typeName: String): Option[Class[_]] = NameToClass.get(typeName)

  def classToName(clazz: Class[_]): Option[String] = ClassToName.get(clazz)
}