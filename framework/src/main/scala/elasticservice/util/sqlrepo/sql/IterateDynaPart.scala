package elasticservice.util.sqlrepo.sql

import elasticservice.util.DataValid._
import scala.collection.mutable.{ Map => MMap }
import elasticservice.util.ep.DatasetUtil
import elasticservice.util.ep.ColumnInfo

case class IterateDynaPart(open: String, close: String, conjunction: String) extends MultiPart with DynaText {

  def visiable(record: Map[String, Any]): Boolean = true

  override def getCols(record: Map[String, Any]): Array[ColumnInfo] = {
    def isListTypeColumn(colId: String): Boolean = { colId.contains("[]") }

    filterSubPart { _.isInstanceOf[TextPart] }.foldLeft(Array[ColumnInfo]()) {
      (arr, tp) =>
        tp.asInstanceOf[TextPart].cols.flatMap { col =>
          if (DatasetUtil.isListTypeColumn(col.id)) {
            val setName = DatasetUtil.getListTypeColumnName(col.id)

            arr.find { x => x.id == setName } match {
              case someCol: Some[ColumnInfo] =>
                someCol.get.addSubColumnInfo(col)
                None
              case _ =>
                val c = ColumnInfo(setName, setName, "String", 0)
                c.addSubColumnInfo(c)
                Some(c)
            }
          } else {
            Some(col)
          }
        }
    }
  }

  override def toText(pRecord: Map[String, Any], preparedStmt: Boolean): String = {
    val record = if (isNotEmpty(pRecord)) pRecord else Map[String, Any]()
    val sb = new StringBuilder()

    if (isNotEmpty(getPrepend))
      sb ++= getPrepend + " "

    if (isNotEmpty(open))
      sb ++= open

    val cols = getCols(record)

    val filteredRecord = cols.filter { c => /*isEmpty(c.getSubColumnInfos())*/ true }.
      foldLeft(Map[String, Any]())((b, a) => record.get(a.id) match {
        case s: Some[Any] => Map[String, Any](a.id -> s)
        case _ => Map[String, Any]()
      })

    cols.find { c => /* isNotEmpty(c.getSubColumnInfos()) */ true }.foreach {
      c =>
        record.get(c.id).foreach {
          l =>
            {
              var first = true
              l.asInstanceOf[List[Any]].foreach {
                v =>
                  {
                    val map = MMap[String, Any]() ++ filteredRecord
                    v match {
                      case m: Map[_, _]  => map ++= m.asInstanceOf[Map[String, Any]]
                      case _ => map += (c.id -> v)
                    }
                    if (first)
                      first = false
                    else if (isNotEmpty(conjunction))
                      sb ++= " " + conjunction + " "

                    getSubParts.foreach { p =>
                      p match {
                        case s: String => sb ++= s
                        case t: TextPart => t.text(map.toMap, preparedStmt) match {
                          case s: String => sb ++= s
                          case _ =>
                        }
                        case _ =>
                      }
                    }
                  }
              }
            }
        }
    }

    if (isNotEmpty(close))
      sb ++= close

    sb.toString
  }
}