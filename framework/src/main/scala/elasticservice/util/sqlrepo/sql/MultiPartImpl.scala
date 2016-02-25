package elasticservice.util.sqlrepo.sql

case class MultiPartImpl() extends MultiPart {
	def visiable(record: Map[String, Any]): Boolean = true
}
