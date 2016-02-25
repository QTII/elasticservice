package elasticservice.util.sqlrepo

import java.sql.CallableStatement
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Statement

import scala.collection.mutable.ListBuffer
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.epMkString
import elasticservice.util.DataTypeUtil
import elasticservice.util.DataValid
import elasticservice.util.ExceptionDetail
import elasticservice.util.ep.Record
import elasticservice.util.TextUtil
import elasticservice.util.ep.ColumnAttributes
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.DatasetUtil
import elasticservice.util.sqlrepo.sql.Sql
import elasticservice.util.sqlrepo.sql.SqlType

case class SqlConn(conn: Connection) extends LazyLogging {

  def query(sqlId: String): Try[Dataset] = query(sqlId, Map.empty[String, Any])

  def query(sql: Sql): Try[Dataset] = query(sql, Map.empty[String, Any])

  def query(sqlId: String, records: List[Map[String, Any]]): (Option[Throwable], List[Dataset]) =
    (SqlRepo.getSql(sqlId).map(query(_, records))).
      getOrElse((Some(new Exception("unknown sqlId: " + sqlId)), List.empty[Dataset]))

  def query(sqlId: String, record: Map[String, Any]): Try[Dataset] =
    (SqlRepo.getSql(sqlId).map(query(_, record))).
      getOrElse(Failure(new Exception("unknown sqlId: " + sqlId)))

  def query(sql: Sql, records: List[Map[String, Any]]): (Option[Throwable], List[Dataset]) = {
    sql.sqlType match {
      case SqlType.Select             => select(sql, records)
      case SqlType.SelectCallable     => queryCallable(sql, records)
      case SqlType.Update             => update(sql, records)
      case SqlType.UpdateAndSelect    => select(sql, records)
      case SqlType.UpdateAndSelectKey => updateAndSelectKey(sql, records)
      case SqlType.UpdateCallable     => queryCallable(sql, records)
      case _                          => select(sql, records)
    }
  }

  def query(sql: Sql, record: Map[String, Any]): Try[Dataset] =
    sql.sqlType match {
      case SqlType.Select             => $select(sql, record)
      case SqlType.SelectCallable     => $queryCallable(sql, record)
      case SqlType.Update             => $update(sql, record)
      case SqlType.UpdateAndSelect    => $select(sql, record)
      case SqlType.UpdateAndSelectKey => $updateAndSelectKey(sql, record)
      case SqlType.UpdateCallable     => $queryCallable(sql, record)
      case _                          => $select(sql, record)
    }

  private def log(sql: Sql, record: Map[String, Any]) {
    sql.sqlType match {
      case SqlType.Select             => logSelect(sql, record)
      case SqlType.SelectCallable     => logSelect(sql, record)
      case SqlType.Update             => logUpdate(sql, record)
      case SqlType.UpdateAndSelect    => logUpdate(sql, record)
      case SqlType.UpdateAndSelectKey => logUpdate(sql, record)
      case SqlType.UpdateCallable     => logUpdate(sql, record)
      case _                          => logSelect(sql, record)
    }
  }

  private def log(sql: Sql, t: Try[Dataset], elapsed: Long) {
    sql.sqlType match {
      case SqlType.Select             => logSelect(sql.fullSqlId, t, elapsed)
      case SqlType.SelectCallable     => logSelect(sql.fullSqlId, t, elapsed)
      case SqlType.Update             => logUpdate(sql.fullSqlId, t, elapsed)
      case SqlType.UpdateAndSelect    => logUpdate(sql.fullSqlId, t, elapsed)
      case SqlType.UpdateAndSelectKey => logUpdate(sql.fullSqlId, t, elapsed)
      case SqlType.UpdateCallable     => logUpdate(sql.fullSqlId, t, elapsed)
      case _                          => logSelect(sql.fullSqlId, t, elapsed)
    }
  }

  private def logSelect(sql: Sql, record: Map[String, Any]) {
    if (logger.underlying.isTraceEnabled)
      logger.trace(s"executes ${sql.fullSqlId}:${TextUtil.LineSeparator}${sql.toText(record, false)}")
    else
      logger.info(s"executes ${sql.fullSqlId}")
  }

  private def logUpdate(sql: Sql, record: Map[String, Any]) {
    if (logger.underlying.isDebugEnabled)
      logger.debug(s"executes ${sql.fullSqlId}:${TextUtil.LineSeparator}${sql.toText(record, false)}")
    else
      logger.info(s"executes ${sql.fullSqlId}")
  }

  private def logSelect(sqlId: String, t: Try[Dataset], elapsed: Long) {
    t match {
      case Success(ds) =>
        if (logger.underlying.isTraceEnabled) logger.trace(_msg(ds)) else logger.info(_msg(ds))
      case Failure(e) =>
        e match {
          case e: SQLException if e.getErrorCode == 0 =>
            if (logger.underlying.isTraceEnabled) logger.trace(_msg(Dataset(sqlId))) else logger.info(_msg(Dataset(sqlId)))
          case e => logger.error(msg(sqlId, e, elapsed))
        }
    }
    def _msg(ds: Dataset): String = {
      val sb = new StringBuilder()
      sb.append("resultOf ").append(sqlId).append(": ")
      sb.append("(").append(ds.rows.size).append("rows/").append(commaNumber(elapsed)).append("ms)")
      if (logger.underlying.isTraceEnabled) {
        ds.rows.foreach { m =>
          sb.append(TextUtil.LineSeparator).append(m.mkString("{", ", ", "}")).append(",")
        }
      }
      sb.append(TextUtil.LineSeparator).toString
    }
  }

  private def logUpdate(sqlId: String, t: Try[Dataset], elapsed: Long) {
    t match {
      case Success(ds) =>
        if (logger.underlying.isDebugEnabled) logger.debug(_msg(ds)) else logger.info(_msg(ds))
      case Failure(e) =>
        e match {
          case e: SQLException if e.getErrorCode == 0 =>
            if (logger.underlying.isDebugEnabled) logger.debug(_msg(Dataset(sqlId))) else logger.info(_msg(Dataset(sqlId)))
          case e => logger.error(msg(sqlId, e, elapsed))
        }
    }
    def _msg(ds: Dataset): String = {
      val sb = new StringBuilder()
      sb.append("resultOf ").append(sqlId).append(": ")
      sb.append("(").append(ds.rows.size).append("rows/").append(commaNumber(elapsed)).append("ms)")
      if (logger.underlying.isDebugEnabled) {
        ds.rows.foreach { m =>
          sb.append(TextUtil.LineSeparator).append(m.mkString("{", ", ", "}")).append(",")
        }
      }
      sb.append(TextUtil.LineSeparator).toString
    }
  }

  private def msg(sqlId: String, e: SQLException, elapsed: Long): String = {
    val sb = new StringBuilder()
    sb.append("resultOf ").append(sqlId).append(": ")
    sb.append("(0rows/").append(commaNumber(elapsed)).append("ms) ").append(e.getErrorCode).append("-").append(e.getMessage)
    sb.append(TextUtil.LineSeparator).toString
  }

  private def msg(sqlId: String, e: Throwable, elapsed: Long): String = {
    val sb = new StringBuilder()
    sb.append("resultOf ").append(sqlId).append(": ")
    sb.append("(0rows/").append(commaNumber(elapsed)).append("ms) ").append(ExceptionDetail.getDetail(e))
    sb.append(TextUtil.LineSeparator).toString
  }

  /**
   * for select, updateSelect
   */
  private def $select(sql: Sql, record: Map[String, Any]): Try[Dataset] = {
    val r = if (DataValid.isNotEmpty(record)) record else Map.empty[String, Any]
    val query = sql.toText(r, true)
    var stmt = conn.prepareStatement(query)
    var rs: ResultSet = null

    var sTime = 0L
    val t = Try {
      val cols = sql.getCols(r)
      if (DataValid.isNotEmpty(cols))
        SqlConn.setColumns(stmt, cols, r)

      log(sql, record)
      sTime = System.currentTimeMillis()
      rs = stmt.executeQuery()

      SqlConn.toDataset(sql, rs)
    }
    SqlConn.close(rs)
    SqlConn.close(stmt)

    log(sql, t, System.currentTimeMillis() - sTime)

    t match {
      case Success(ds) => t
      case Failure(e) =>
        e match {
          case e: SQLException if e.getErrorCode == 0 => Success(Dataset(sql.id))
          case e                                      => Failure(e)
        }
    }
  }

  /**
   * for update
   */
  private def $update(sql: Sql, record: Map[String, Any]): Try[Dataset] = {
    val r = if (DataValid.isNotEmpty(record)) record else Map.empty[String, Any]
    val queryStr = sql.toText(r, true)
    val stmt = conn.prepareStatement(queryStr)

    var sTime = 0L
    val t = Try {
      val cols = sql.getCols(r)
      if (DataValid.isNotEmpty(cols))
        SqlConn.setColumns(stmt, cols, r)

      log(sql, record)
      sTime = System.currentTimeMillis()
      val rowCount = stmt.executeUpdate()

      val colName = "_row_count"
      val ds = Dataset(sql.id)
      ds.addColInfo(ColumnInfo(colName, colName, classOf[Int]))
      ds += Record[Any](colName -> rowCount)
      ds
    }
    SqlConn.close(stmt)

    log(sql, t, System.currentTimeMillis() - sTime)
    t
  }

  /**
   * for updateAndSelectKey
   */
  private def $updateAndSelectKey(sql: Sql, record: Map[String, Any]): Try[Dataset] = {
    val r = if (DataValid.isNotEmpty(record)) record else Map.empty[String, Any]
    val stmt = conn.createStatement()
    var rs: ResultSet = null

    var sTime = 0L
    val t = Try {
      val staticQStr = sql.toText(r, false)

      log(sql, record)
      sTime = System.currentTimeMillis()
      val rowCount = stmt.executeUpdate(staticQStr, Statement.RETURN_GENERATED_KEYS)

      sql.selectKeyPart match {
        case Some(skPart) =>
          val skQStr = skPart.toText(record, false)
          rs = if (DataValid.isNotEmpty(skQStr)) stmt.executeQuery(skQStr) else stmt.getGeneratedKeys()
          SqlConn.toDataset(sql, rs)
        case None =>
          val ds = Dataset(sql.id)
          val colName = "_row_count"
          ds.addColInfo(ColumnInfo(colName, colName, classOf[Int]))
          ds += Record[Any](colName -> rowCount)
          ds
      }
    }
    SqlConn.close(rs)
    SqlConn.close(stmt)

    log(sql, t, System.currentTimeMillis() - sTime)
    t
  }

  /**
   * for selectCallable, updateCallable
   */
  private def $queryCallable(sql: Sql, record: Map[String, Any]): Try[Dataset] = {
    val query = sql.toText(record, true)
    val stmt = conn.prepareCall(query)
    var sTime = 0L

    val t = Try {
      val cols = sql.getCols(record)
      if (DataValid.isNotEmpty(cols))
        SqlConn.setColumns(stmt, cols, record)

      log(sql, record)
      sTime = System.currentTimeMillis()

      if (sql.sqlType == SqlType.SelectCallable)
        stmt.executeQuery()
      else
        stmt.executeUpdate()

      val oRecord = Record.empty[Any]
      for (i <- 0 until cols.length) {
        val col = cols(i)
        if (col.containsAttr(ColumnAttributes.OUT))
          oRecord += col.id -> stmt.getObject(i + 1)
        else
          record.get(col.id).foreach { oRecord += col.id -> _ }
      }

      val ds = Dataset(sql.id)
      ds.colInfos = DatasetUtil.createColumnInfoList(oRecord)
      ds += oRecord
      ds
    }
    SqlConn.close(stmt)

    log(sql, t, System.currentTimeMillis() - sTime)
    t
  }

  private def select(sql: Sql, records: List[Map[String, Any]]): (Option[Throwable], List[Dataset]) =
    xquery($select)(sql, records)

  private def update(sql: Sql, records: List[Map[String, Any]]): (Option[Throwable], List[Dataset]) =
    xquery($update)(sql, records)

  private def updateAndSelectKey(sql: Sql, records: List[Map[String, Any]]): (Option[Throwable], List[Dataset]) =
    xquery($updateAndSelectKey)(sql, records)

  private def queryCallable(sql: Sql, records: List[Map[String, Any]]): (Option[Throwable], List[Dataset]) =
    xquery($queryCallable)(sql, records)

  private def xquery(f: (Sql, Map[String, Any]) => Try[Dataset])(sql: Sql, records: List[Map[String, Any]]): (Option[Throwable], List[Dataset]) = {
    val buf = ListBuffer[Dataset]()
    @annotation.tailrec
    def go(l: List[Map[String, Any]]): Option[Throwable] = {
      l match {
        case Nil => None
        case record :: t =>
          f(sql, record) match {
            case Success(ds) =>
              buf += ds; go(t)
            case Failure(e) => Some(e)
          }
      }
    }

    val l = if (DataValid.isEmpty(records)) List(Map.empty[String, Any]) else records
    (go(l), buf.toList)
  }

  def close() {
    Try {
      rollback()
      if (conn != null)
        conn.close()
    }
  }

  def isClosed: Boolean = conn.isClosed

  def startTransaction() { conn.setAutoCommit(false) }

  def commit() {
    if (conn != null && !conn.getAutoCommit) {
      conn.commit()
      conn.setAutoCommit(true)
    }
  }

  def rollback() {
    if (conn != null && !conn.getAutoCommit) {
      conn.rollback()
      conn.setAutoCommit(true)
    }
  }

  private def commaNumber(num: Long): String = commaNumber(num, 1)

  private def commaNumber(num: Long, depth: Int): String = {
    if (depth == 1) {
      if (1000L > num) {
        String.valueOf(num % 1000L)
      } else {
        commaNumber(num, depth + 1) + "," + String.valueOf(threeDigit(num % 1000L))
      }
    } else {
      if (mmm(depth) > num) {
        String.valueOf((num % mmm(depth)) / mmm(depth - 1))
      } else {
        commaNumber(num, depth + 1) + "," + String.valueOf(threeDigit((num % mmm(depth)) / mmm(depth - 1)))
      }
    }
  }
  private def mmm(depth: Int): Long =
    if (depth <= 0) 1L
    else 1000L * mmm(depth - 1)

  private def threeDigit(num: Long): String =
    if (0 <= num && num < 10)
      "00" + num
    else if (10 <= num && num < 100)
      "0" + num
    else
      String.valueOf(num)
}

object SqlConn {

  def setColumns(stmt: PreparedStatement, cols: Array[ColumnInfo], record: Map[String, Any]) {
    var pIdx = 1
    for (col <- cols) {
      if (col.containsAttr(ColumnAttributes.OUT) && stmt.isInstanceOf[CallableStatement]) {
        val sqlType = DataTypeUtil.sqlType(col.typeClass).getOrElse(-1)
        stmt.asInstanceOf[CallableStatement].registerOutParameter(pIdx, sqlType)
        pIdx += 1
      } else if (record == null) {
        stmt.setObject(pIdx, DataTypeUtil.valueByClass(null, col.typeClass))
        pIdx += 1
      } else if (col.isSubColumnInfosEmpty) {
        stmt.setObject(pIdx, DataTypeUtil.valueByClass(record.getOrElse(col.id, null), col.typeClass))
        pIdx += 1
      } else {
        record.get(col.id).foreach {
          v =>
            v.asInstanceOf[List[Any]].foreach {
              _ match {
                case x: Map[String, _] @unchecked => 
                  if (!col.isSubColumnInfosEmpty) {
                    val subCols = col.getSubColumnInfos
                    for (subCol <- subCols) {
                      val subColValue = x.get(subCol.id).getOrElse(null)
                      stmt.setObject(pIdx, DataTypeUtil.valueByClass(subColValue, subCol.typeClass))
                      pIdx += 1
                    }
                  }
                case x =>
                  stmt.setObject(pIdx, DataTypeUtil.valueByClass(x, col.typeClass))
                  pIdx += 1
              }
            }
        }
      }
    }
  }

  def close(conn: Any) {
    Try {
      conn match {
        case c: Statement  => c.asInstanceOf[Statement].close()
        case c: ResultSet  => c.asInstanceOf[ResultSet].close()
        // TODO
        //        case c: SqlResultSet => c.asInstanceOf[SqlResultSet].close()
        case c: SqlConn    => c.asInstanceOf[SqlConn].close()
        case c: Connection => c.asInstanceOf[Connection].close()
        case null          =>
        case _             =>
      }
    }
  }

  def toDataset(sql: Sql, rs: ResultSet): Dataset = {
    val ds = Dataset(sql.id)
    if (rs == null) return ds

    val md: ResultSetMetaData = rs.getMetaData()

    while (rs.next())
      ds += JDBCUtil.toMap(rs, md, sql.colMapper).getOrElse(Record.empty[Any])

    if (ds.isEmpty)
      ds.colInfos = DatasetUtil.createColumnInfoList(md)

    ds
  }
}