package elasticservice.util.ep

import scala.collection.mutable.Buffer
import scala.language.existentials

import elasticservice.util.DataTypeUtil

case class ColumnInfo(id: String, text: String, typeClass: Class[_], size: Int)(implicit ms: MkString)
    extends Serializable {

  private var _userAttrs = Record[Any]()
  private val subColBuf = Buffer.empty[ColumnInfo]

  var inDynaText = false

  def typeName = DataTypeUtil.classToName(typeClass)

  /**
   * 기본 attributes(id, text, typeClass, size)가 아닌 사용자 정의 attributes
   */
  def userAttrs_=[T <: Any](m: Map[String, T]) { _userAttrs = Record[Any](m) }

  /**
   * 기본 attributes(id, text, typeClass, size)가 아닌 사용자 정의 attributes
   */
  def userAttrs = _userAttrs

  /**
   * 기본 attributes(id, text, typeClass, size)와  사용자 정의 attributes의 값을 구한다.
   */
  def attr(attrName: String): Option[Any] = {
    attrName match {
      case "id"   => Some(id)
      case "text" => Some(text)
      case "type" => Some(typeClass)
      case "size" => Some(size)
      case a      => _userAttrs.get(a)
    }
  }

  /**
   * 기본 attributes와 사용자 정의 attributes 존재 여부 확인
   */
  def containsAttr(attrName: String): Boolean = attr(attrName) != None

  def addSubColumnInfo(subCol: ColumnInfo) { subColBuf += subCol }

  def getSubColumnInfo(id: String): Option[ColumnInfo] = subColBuf.find(_.id == id)

  def isSubColumnInfosEmpty = subColBuf.isEmpty

  def getSubColumnInfos = subColBuf.toArray

  override def toString(): String = ms.mkString(this)
}

object ColumnInfo {
  val TYPE_PARAM = 1
  val TYPE_DATA_SET_COL = 2
  val TYPE_NONAME_DATA_SET_COL = 3

  def apply(id: String, text: String, typeName: String, size: Int)(implicit ms: MkString): ColumnInfo =
    DataTypeUtil.nameToClass(typeName).map(
      new ColumnInfo(id, text, _, size)(ms)).getOrElse(throw new Exception("unknown type '" + typeName + "'"))

  def apply(id: String, text: String, typeName: String)(implicit ms: MkString): ColumnInfo =
    ColumnInfo(id, text, typeName, 0)(ms)

  def apply(id: String, text: String, typeName: Class[_])(implicit ms: MkString): ColumnInfo =
    ColumnInfo(id, text, typeName, 0)(ms)

  def apply(allAttrs: Map[String, Any])(implicit ms: MkString): ColumnInfo = {
    val id = allAttrs.getOrElse("id", "id").toString
    val c = ColumnInfo(
      id,
      allAttrs.getOrElse("text", id).toString,
      allAttrs.getOrElse("type", "String").toString,
      allAttrs.getOrElse("size", 0) match {
        case n: Int    => n
        case s: String => s.toInt
      })(ms)
    c.userAttrs = allAttrs
    c
  }
}