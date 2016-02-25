package elasticservice.util.ep

import java.sql.ResultSetMetaData

import scala.collection.mutable.ListBuffer

import elasticservice.util.DataTypeUtil
import elasticservice.util.StringUtil

object DatasetUtil {
  private val LIST_ENTRY_MARK = "[]."
  private val LIST_MARK = "[]"
  private val LIST_VALUE_SEPERATOR = ","
  private val VAL_NULL = "@NULL@"

  /**
   * 칼럼명 뒷부분에 []가 있으면 해당 칼럼은 하부 칼럼들로 구성된 칼럼이다.
   */
  def isListTypeColumn(colName: String) = colName.indexOf(LIST_MARK) >= 0

  def getListTypeColumnName(colName: String) = colName.substring(0, colName.indexOf(LIST_MARK) + 2)

  /**
   * Parses a column's name part into an actual column name, data type and user-defined attributes
   * and then return ColumnInfo object which has those informations.
   *
   * o Column expression
   *    - columnName?attribute1&attribute2:dataType
   *    - Examples: <br>
   *       colName                     -> column name: colName, data type: String (default) <br>
   *       colName:String              -> column name: colName, data type: String <br>
   *       colName?attr1&attr2:String  -> column name: colName, data type: String, user attributes: attr1 and attr2 <br>
   *
   * @param attrStr
   */
  def parseColumnExpression(colExpr: String): ColumnInfo = {
    val tup: Tuple2[String, Class[_]] = colExpr.indexOf(":") match {
      case -1 => Tuple2(colExpr, DataTypeUtil.DefaultTypeClass)
      case m =>
        val c = DataTypeUtil.nameToClass(colExpr.substring(m + 1)).getOrElse(DataTypeUtil.DefaultTypeClass)
        Tuple2(colExpr.substring(0, m), c)
    }

    tup match {
      case (colName, typeClass) if colName.indexOf("?") == -1 => ColumnInfo(colName, colName, typeClass)
      case (colName, typeClass) =>
        val (c, a) = colName.span { _ == '?' }
        val col = ColumnInfo(c, c, typeClass)
        col.userAttrs = new ColumnAttributes(a.drop(1)).toMap
        col
    }
  }

  def setColTo(map: Record[Any], colName: String, typeName: String, value: Any, nullKeyword: Any): Unit =
    setColTo(map, colName, DataTypeUtil.nameToClass(typeName).getOrElse(DataTypeUtil.DefaultTypeClass), value, nullKeyword)

  def setColTo(map: Record[Any], colName: String, typeClass: Class[_], value: Any, nullKeyword: Any) {
    value match {
      case str: String =>
        // colName[].subCol
        if (colName.indexOf(LIST_ENTRY_MARK) >= 0) {
          val listValue = StringUtil.splitWithoutEmpty(str, LIST_VALUE_SEPERATOR)
          val listTypeColName = getListTypeColumnName(colName)

          val list = map.getOrElse(listTypeColName, {
            val l = ListBuffer.empty[Record[Any]]
            map += listTypeColName -> l
            l
          }).asInstanceOf[ListBuffer[Record[Any]]]
          //						map.get(listTypeColName) match {
          //							case None => 
          //								val l = ListBuffer.empty[Record[Any]]
          //								map += listTypeColName -> l
          //								l
          //							case Some(e) => e.asInstanceOf[ListBuffer[Record[Any]]]
          //						}

          listValue.zipWithIndex foreach {
            case (lv, lIdx) =>
              var record = Record.empty[Any]
              if (lIdx < list.size)
                record = list(lIdx)
              else
                list :+ record
              record += colName -> convert(lv, typeClass, nullKeyword)
          }
        } else if (colName.indexOf(LIST_MARK) >= 0) { // colName[]
          val listValue = StringUtil.splitWithoutEmpty(str, LIST_VALUE_SEPERATOR)
          val list = ListBuffer.empty[Any]
          listValue.foreach { x =>
            list :+ convert(x, typeClass, nullKeyword)
          }
          map += colName -> list
        } else {
          val v = convert(str, typeClass, nullKeyword)
          addListTypeValue(map, colName, v)
        }
      case _ => addListTypeValue(map, colName, value)
    }
  }

  def convert(value: Any, classType: Class[_], nullKeyword: Any): Any = {
    if (value == null || value == nullKeyword)
      null
    else
      DataTypeUtil.valueByClass(value, classType)
  }

  def addListTypeValue(map: Record[Any], colName: String, obj: Any) {
    map.get(colName) match {
      case None => map += colName -> obj
      case Some(e) =>
        e match {
          case l: ListBuffer[_] => l :+ obj
          case _                => map += colName -> ListBuffer(e, obj)
        }
    }
  }

  def createColumnInfoList(m: Map[String, Any]): List[ColumnInfo] = {
    val colInfoList = new ListBuffer[ColumnInfo]
    val it = m.iterator
    for (kv <- it) {
      colInfoList += ColumnInfo(kv._1, kv._1, if (kv._2 == null) DataTypeUtil.DefaultTypeClass else kv._2.getClass())
    }
    colInfoList.toList
  }

  def createColumnInfoList(md: ResultSetMetaData): List[ColumnInfo] = {
    val colInfoList = new ListBuffer[ColumnInfo]
    val colCnt = md.getColumnCount()
    for (sqlColIndex <- 1 to colCnt) {
      val sqlTypeCode = md.getColumnType(sqlColIndex);
      val sqlColName = md.getColumnName(sqlColIndex);
      colInfoList += DataTypeUtil.sqlTypeClass(sqlTypeCode).
        map(ColumnInfo(sqlColName, sqlColName, _)).
        getOrElse(ColumnInfo(sqlColName, sqlColName, DataTypeUtil.DefaultTypeName))
    }
    colInfoList.toList
  }
}