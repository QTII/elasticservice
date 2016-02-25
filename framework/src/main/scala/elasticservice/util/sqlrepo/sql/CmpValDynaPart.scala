package elasticservice.util.sqlrepo.sql

import elasticservice.util.sqlrepo.VisibleCondition._

case class CmpValDynaPart(colName: String, visibleCondition: VisibleCondition, criterion: String) extends MultiPart with DynaText {

  def visiable(record: Map[String, Any]): Boolean = {
    if (record == null || record == None) return true

    val v = record.get(colName)
    val c = TypeConvertor.convert(criterion, v.getClass)

    visibleCondition match {
      case IS_EQUAL        => v == c
      case IS_NOT_EQUAL    => v != c
      case IS_GREATER_THAN => v.asInstanceOf[Ordered[Any]] > c
      case IS_GRATER_EQUAL => v.asInstanceOf[Ordered[Any]] >= c
      case IS_LESS_THAN    => v.asInstanceOf[Ordered[Any]] < c
      case IS_LESS_EQUAL   => v.asInstanceOf[Ordered[Any]] <= c
      case _               => false
    }
  }
}