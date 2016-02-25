package elasticservice.util.sqlrepo

object VisibleCondition extends Enumeration {
  type VisibleCondition = Value
  val IS_EQUAL, IS_NOT_EQUAL, IS_GREATER_THAN, IS_GRATER_EQUAL, IS_LESS_THAN, IS_LESS_EQUAL, IS_NULL, IS_NOT_NULL, IS_EMPTY, IS_NOT_EMPTY = Value

  def forName(name: String): VisibleCondition = name match {
    case "isEqual"        => IS_EQUAL
    case "isNotEqual"     => IS_NOT_EQUAL
    case "isGreaterThan"  => IS_GREATER_THAN
    case "isGreaterEqual" => IS_GRATER_EQUAL
    case "isLessThan"     => IS_LESS_THAN
    case "isLessEqual"    => IS_LESS_EQUAL
    case "isNull"         => IS_NULL
    case "isNotNull"      => IS_NOT_NULL
    case "isEmpty"        => IS_EMPTY
    case "isNotEmpty"     => IS_NOT_EMPTY
    case _                => Value
  }
}
