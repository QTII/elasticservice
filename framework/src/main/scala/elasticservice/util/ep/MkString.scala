package elasticservice.util.ep

trait MkString extends Serializable {
  def mkString(ep: ElasticParams): String
  
  def mkStringFromParameters(m: Map[String, Any]): String

  def mkString(d: Dataset): String

  def mkString(c: ColumnInfo): String

  def mkString(m: Map[String, Any]): String
  
  def EPTypeName: String
}
