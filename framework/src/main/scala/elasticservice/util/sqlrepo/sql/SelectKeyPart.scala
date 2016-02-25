package elasticservice.util.sqlrepo.sql

case class SelectKeyPart(keyProperty: String, resultClass: String) extends MultiPart {

  def visiable(record: Map[String, Any]): Boolean = true
}