package elasticservice.util.ep

import scala.collection.mutable.Buffer

import com.typesafe.scalalogging.LazyLogging

/**
 * 칼럼에 관한 메타정보(ColumnInfo)의 목록 부분과 실제 데이터가 담기는 레코드 목록 부분으로 구성되어 있다.
 */
case class Dataset(var name: String, var _colInfos: List[ColumnInfo])(implicit var ms: MkString)
    extends LazyLogging {

  private val rowsBuf: Buffer[Record[Any]] = Buffer[Record[Any]]()

  /**
   * 칼럼에 관한 메타정보(ColumnInfo)의 목록
   */
  def colInfos = _colInfos

  /**
   * 칼럼에 관한 메타정보(ColumnInfo)의 목록 설정
   */
  def colInfos_=(colInfos: List[ColumnInfo]) { _colInfos = colInfos }

  /**
   * 목록(l)의 뒤에 항목(e)을 추가한다.
   */
  private def appendElemToList[T <: Any](l: List[T], e: T): List[T] = {
    val b = l.toBuffer
    b += e
    b.toList
  }

  /**
   * 칼럼에 관한 메타정보(ColumnInfo)를 추가한다.
   */
  def addColInfo(colInfo: ColumnInfo) {
    if (colInfos.exists { c => c.id == colInfo.id })
      logger.warn("duplicated ColumnInfo: " + colInfo.id)
    else
      _colInfos = appendElemToList(_colInfos, colInfo)
  }

  /**
   * 칼럼에 관한 메타정보(ColumnInfo) 목록의 Iterator
   */
  def colInfosIterator: Iterator[ColumnInfo] = _colInfos.iterator

  /**
   * 주어진 인덱스(idx)에 해당하는 칼럼의 메타정보(ColumnInfo)
   */
  def colInfo(idx: Int): ColumnInfo = _colInfos(idx)

  /**
   * 칼럼의 갯수
   */
  def nrOfCols: Int = _colInfos.length

  /**
   * 레코드 목록이 비어 있는지 검사
   */
  def isEmpty = rowsBuf.isEmpty

  /**
   * 레코드 목록
   */
  def rows = rowsBuf.toList

  /**
   * 레코드 목록을 새로 설정. 기존 레코드는 삭제된다.
   */
  def rows_=(rows: List[Map[String, Any]]) {
    rowsBuf.clear();
    ++=(rows)
  }

  /**
   * 레코드 목록을 추가 (Mutable)
   */
  def ++=(rows: List[Map[String, Any]]) {
    rowsBuf ++= rows.map {
      _ match {
        case m: Record[Any]      => m.setMkString(ms)
        case m: Map[String, Any] => Record(m).setMkString(ms)
      }
    }
  }

  /**
   * 레코드를 추가 (Mutable)
   */
  def +=(row: Map[String, Any]) {
    rowsBuf += (row match {
      case m: Record[Any]      => m.setMkString(ms)
      case m: Map[String, Any] => Record(m).setMkString(ms)
    })
  }

  /**
   * 주어진 칼럼(colInfo)이 없는 레코드를 찾아서 칼럼을 추가한다.
   * 레코드가 없거나(nrOfRowss가 0인 경우)나 모든 레코드에 해당 칼럼이 있을 경우 새로운 레코드를 추가한다.
   * (Mutable)
   */
  def +=(colInfo: ColumnInfo, value: Any) { +=(colInfo.id, value) }

  /**
   * 주어진 칼럼이 없는 레코드를 찾아서 칼럼을 추가한다.
   * 레코드가 없거나(nrOfRowss가 0인 경우)나 모든 레코드에 해당 칼럼이 있을 경우 새로운 레코드를 추가한다.
   * (Mutable)
   */
  def +=(col: (ColumnInfo, Any)) { +=(col._1, col._2) }

  /**
   * 주어진 칼럼 아이디(colId)이 없는 레코드를 찾아서 칼럼을 추가한다.
   * 레코드가 없거나(nrOfRowss가 0인 경우)나 모든 레코드에 해당 칼럼이 있을 경우 새로운 레코드를 추가한다.
   * (Mutable)
   */
  def +=(colId: String, value: Any) {
    val rowOp = rowsBuf.find { r => r.get(colId) == None }
    rowOp match {
      case None    => +=(Record(colId -> value).setMkString(ms))
      case Some(r) => r += colId -> value
    }
  }

  /**
   * Iterator of 레코드
   */
  def rowsIterator: Iterator[Record[Any]] = rows.iterator

  /**
   * 레코드 갯수
   */
  def nrOfRowss: Int = rowsBuf.size

  def setMkString(ms: MkString): Dataset = {
    this.ms = ms
    this
  }

  override def toString(): String = ms.mkString(this)
}

object Dataset {
  def apply(name: String)(implicit ms: MkString): Dataset =
    Dataset(name, List[ColumnInfo]())(ms)
}