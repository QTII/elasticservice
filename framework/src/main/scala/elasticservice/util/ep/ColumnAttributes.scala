package elasticservice.util.ep

/**
 * Attribute 종류에는 NOT_EMPTY와 OUT이 있다. NOT_EMPTY는 null과 빈문자열("")을 허용하지 않으며
 * OUT은 해당 column은 결과 값을 세팅하여 반환하는 용도로 쓰임을 의미한다.
 *
 * cf) Column 표현식: colName?attr1&attr2&attr3:String
 *
 * @param attrStr
 *            Attribute들을 '&'로 연결하여 만든다. 예) NOT_EMPTY이면서 OUT인 속성의 column을
 *            만들려면 NOT_EMPTY&OUT으로 만들면 된다.
 */
class ColumnAttributes(attrStr: String) {
  val attrs = attrStr.split("&")

  def toMap: Record[Boolean] = attrs.foldLeft(Record[Boolean]())((x, y) => x + (y -> true))

  override def toString(): String = attrs.mkString("[", ",", "]")
}

object ColumnAttributes {
  val NOT_EMPTY = "NOT_EMPTY"
  val OUT = "OUT"
  private val AttrName = Array(NOT_EMPTY, OUT)
}