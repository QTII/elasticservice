package elasticservice.util.sqlrepo.sql

import elasticservice.util.DataValid
import elasticservice.util.sqlrepo.VisibleCondition._

case class ChkValDynaPart(col: String, vc: VisibleCondition) extends MultiPart with DynaText {

  def visiable(record: Map[String, Any]): Boolean = {
    if (record == null || record == None)
      return true

    val obj = record.get(col)

    vc match {
      case IS_NULL => obj == None
      case IS_NOT_NULL => obj != None
      case IS_EMPTY => DataValid.isEmpty(obj)
      case IS_NOT_EMPTY => DataValid.isNotEmpty(obj)
      case _ => false
    }
  }
}