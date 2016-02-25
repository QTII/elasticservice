package elasticservice.util

object DataValid {
  def isEmpty(obj: Any): Boolean = {
    obj match {
      case a: String         => a.isEmpty || a.forall(Character.isWhitespace)
      case a: Traversable[_] => a.isEmpty
      case a: Array[_]       => a.isEmpty
      case null              => true
      case None              => true
      case _                 => false
    }
  }

  def isNotEmpty(obj: Any): Boolean = !isEmpty(obj)
}