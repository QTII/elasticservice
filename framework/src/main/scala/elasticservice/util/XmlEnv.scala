package elasticservice.util

object XmlEnv {
  private var map = Map.empty[String, String]

  def +=(kv: (String, String)) { map += kv }

  def ++=(kvl: Seq[(String, String)]) { map ++= kvl }

  def apply(key: String): Option[String] = map.get(key)

  def iterator: Iterator[(String, String)] = map.iterator
}