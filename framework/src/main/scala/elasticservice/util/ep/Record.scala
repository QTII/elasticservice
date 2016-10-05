package elasticservice.util.ep

import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.MapLike
import scala.collection.immutable.TreeMap
import scala.collection.mutable.Builder
import scala.collection.mutable.MapBuilder

import elasticservice.epMkString

object IgnoreCaseOrdering extends Ordering[String] {
  def compare(x: String, y: String): Int = x.compareToIgnoreCase(y)
}

class Record[T] private (m: TreeMap[String, T])
    extends Map[String, T]
    with MapLike[String, T, Record[T]] with Serializable {

  private implicit val ord: Ordering[String] = IgnoreCaseOrdering
  private implicit var ms: MkString = elasticservice.epMkString
  private var tm: TreeMap[String, T] = if (m == null) TreeMap[String, T]() else m

  def this() = this(null)

  def clear() = tm = TreeMap[String, T]()

  override def empty = Record.empty[T](ms)

  def +=(kv: (String, T)) { tm += kv }

  def ++=(m: Map[String, T]) { tm ++= m }

  def -=(k: String) { tm -= k }

  // Members declared in scala.collection.immutable.Map
  def +[B1 >: T](kv: (String, B1)): Record[B1] = new Record(tm + kv).setMkString(ms)

  // Members declared in scala.collection.MapLike
  def -(key: String): Record[T] = new Record(tm - key).setMkString(ms)
  def get(key: String): Option[T] = tm.get(key)
  def iterator: Iterator[(String, T)] = tm.iterator

  def setMkString(ms: MkString): Record[T] = {
    this.ms = ms
    this
  }

  override def toString(): String = ms.mkString(this)
}

object Record {
  def empty[T](implicit ms: MkString) = new Record[T]().setMkString(ms)

  def apply[T](kvs: (String, T)*)(implicit ms: MkString): Record[T] = {
    kvs.foldLeft(empty[T](ms))((x, kv) => x + kv)
  }

  def apply[T](map: Map[String, T])(implicit ms: MkString): Record[T] = map match {
    case null         => new Record[T]().setMkString(ms)
    case m: Record[T] => m.setMkString(ms)
    case m: Map[_, _] => m.foldLeft(empty[T](ms))((x, kv) => x + kv)
  }

  def newBuilder[T]: Builder[(String, T), Record[T]] =
    new MapBuilder[String, T, Record[T]](empty)

  implicit def canBuildFrom[T]: CanBuildFrom[Record[_], (String, T), Record[T]] = {
    new CanBuildFrom[Record[_], (String, T), Record[T]] {
      def apply(from: Record[_]) = newBuilder[T]
      def apply() = newBuilder[T]
    }
  }
}
