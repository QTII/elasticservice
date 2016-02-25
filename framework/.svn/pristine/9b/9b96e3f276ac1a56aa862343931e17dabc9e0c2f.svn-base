package elasticservice.util.sqlrepo

import elasticservice.epMkString
import elasticservice.util.ep.Record
import elasticservice.util.sqlrepo.sql.ColMapper
import elasticservice.util.sqlrepo.sql.Sql

case class Package(id: String) {
  val sqls = Record[Sql]()
  val colMappers = Record[ColMapper]()

  def isRoot = id == "."

  def +=(sql: Sql) { sqls += sql.id -> sql }

  def -=(sqlId: String) { sqls -= sqlId }

  def get(sqlId: String): Option[Sql] = { sqls.get(sqlId) }

  def idSet = sqls.keySet

  def addColMapper(kv: (String, ColMapper)) { colMappers += kv }

  def delColMapper(colMapId: String) { colMappers -= colMapId }

  def getColMapper(colMapId: String): Option[ColMapper] = colMappers.get(colMapId)

  override def toString() = id + " : " + sqls.toString()
}