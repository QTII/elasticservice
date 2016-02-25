package elasticservice.util.sqlrepo.sql

import scala.xml.Node

import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Record
import elasticservice.util.sqlrepo.SqlRepo

/**
 *
 * o Statements
 *   - select
 *   - update
 *   - updateAndSelect
 *
 * o Parts
 *   - selectKey
 *   - dynamic
 *   - iterate
 *   - isEqual
 *   - isNotEqual
 *   - isGreaterThan
 *   - isGreaterEqual
 *   - isLessThan
 *   - isLessEqual
 *   - isNull
 *   - isNotNull
 *   - isEmpty
 *   - isNotEmpty
 */
class Sql {
  var sqlType = SqlType.Select

  private var pkgIdOpt: Option[String] = None
  private var sqlIdOpt: Option[String] = None

  private var root: MultiPart = MultiPartImpl()
  private var _colMapper: Option[ColMapper] = None
  private var _selectKeyPart: Option[SelectKeyPart] = None
  private var _lastModified = 0L
  private var _xmlStrOpt: Option[String] = None

  def id = sqlIdOpt.getOrElse("")

  def fullSqlId = SqlRepo.fullSqlId(pkgIdOpt.getOrElse(""), sqlIdOpt.getOrElse(""))

  def setId(pkgId: String, sqlId: String) {
    this.pkgIdOpt = Some(pkgId)
    this.sqlIdOpt = Some(sqlId)
  }

  def xmlString = _xmlStrOpt
  def xmlString_=(xmlStr: String) { this._xmlStrOpt = Some(xmlStr) }
  def xmlString_=(xmlStrOpt: Option[String]) { this._xmlStrOpt = xmlStrOpt }

  def lastModified = _lastModified
  def lastModified_=(lastModified: Long) { _lastModified = lastModified }

  def multiPart_=(p: MultiPart) { root = p }
  private def multiPart = root

  def selectKeyPart = _selectKeyPart
  def selectKeyPart_=(skp: SelectKeyPart) {
    _selectKeyPart = Some(skp)
    sqlType = SqlType.UpdateAndSelectKey
  }

  def colMapper = _colMapper
  def colMapper_=(cm: ColMapper) { _colMapper = Some(cm) }
  def colMapper_=(cm: Record[ColMap]) { _colMapper = Some(ColMapper(cm)) }

  def addColMap(cm: ColMap) { _colMapper.foreach { x => x += cm } }

  def getCols(record: Map[String, Any]): Array[ColumnInfo] = root.getCols(record)

  def toText(record: Map[String, Any], preparedStmt: Boolean): String = root.toText(record, preparedStmt)

  override def toString(): String = root.toString()
}

object SqlType extends Enumeration {
  type SqlType = Value
  val Select, SelectCallable, Update, UpdateAndSelect, UpdateAndSelectKey, UpdateCallable = Value

  private val StmtSelect = "select"
  private val StmtSelectCallable = "selectCallable"
  private val StmtUpdate = "update"
  private val StmtUpdateAndSelect = "updateAndSelect"
  private val StmtUpdateAndSelectKey = "updateAndSelectKey"
  private val StmtUpdateCallable = "updateCallable"

  def forName(stmt: String): SqlType = stmt match {
    case StmtSelect             => Select
    case StmtSelectCallable     => SelectCallable
    case StmtUpdate             => Update
    case StmtUpdateAndSelect    => UpdateAndSelect
    case StmtUpdateAndSelectKey => UpdateAndSelectKey
    case StmtUpdateCallable     => UpdateCallable
    case _                      => Value
  }

  def toCallable(sqlType: SqlType): SqlType = sqlType match {
    case Select             => SelectCallable
    case SelectCallable     => SelectCallable
    case Update             => UpdateCallable
    case UpdateAndSelect    => UpdateCallable
    case UpdateAndSelectKey => UpdateCallable
    case UpdateCallable     => UpdateCallable
    case _                  => Value
  }

  def isValidStmt(stmt: Node): Boolean = isValidStmt(stmt.label)

  def isValidStmt(stmt: String): Boolean =
    StmtSelect.equalsIgnoreCase(stmt) || StmtSelectCallable.equalsIgnoreCase(stmt) ||
      StmtUpdate.equalsIgnoreCase(stmt) || StmtUpdateAndSelect.equalsIgnoreCase(stmt) ||
      StmtUpdateAndSelectKey.equalsIgnoreCase(stmt) || StmtUpdateCallable.equalsIgnoreCase(stmt)

  def isValidPart(part: Node): Boolean = isValidPart(part.label)

  def isValidPart(part: String): Boolean =
    "selectKey".equalsIgnoreCase(part) ||
      "dynamic".equalsIgnoreCase(part) ||
      "iterate".equalsIgnoreCase(part) ||
      "isEqual".equalsIgnoreCase(part) ||
      "isNotEqual".equalsIgnoreCase(part) ||
      "isGreaterThan".equalsIgnoreCase(part) ||
      "isGreaterEqual".equalsIgnoreCase(part) ||
      "isLessThan".equalsIgnoreCase(part) ||
      "isLessEqual".equalsIgnoreCase(part) ||
      "isNull".equalsIgnoreCase(part) ||
      "isNotNull".equalsIgnoreCase(part) ||
      "isEmpty".equalsIgnoreCase(part) ||
      "isNotEmpty".equalsIgnoreCase(part)
}