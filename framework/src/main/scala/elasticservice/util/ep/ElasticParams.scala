package elasticservice.util.ep

import elasticservice.util.DataValid

case class ElasticParams(implicit ms: MkString) {
  private var _params: Record[Any] = Record.empty[Any].setMkString(ms)
  private var _datasets: Record[Dataset] = Record.empty[Dataset].setMkString(ms)

  /**
   * 다수의 파라미터들을 추가
   */
  def ++=(map: Map[String, Any]) { _params ++= Record[Any](map)(ms) }

  /**
   * 하나의 파라미터를 추가
   */
  def +=(param: (String, Any)) { _params += param }

  /**
   * 파라미터 삭제
   */
  def -=(paramName: String) { _params -= paramName }

  /**
   * 파라미터 값 구하기
   */
  def get(paramName: String): Option[Any] = _params.get(paramName)

  /**
   * 주어진 파라미터가 있는지 검사
   */
  def contains(paramName: String) = _params.contains(paramName)

  def isEmpty(paramName: String) = _params.get(paramName) match {
    case None    => true
    case Some(v) => DataValid.isEmpty(v)
  }

  def isNotEmpty(paramName: String) = !isEmpty(paramName)

  /**
   * 파라미터들을 Iterator에 담아서 리턴
   */
  def paramIterator: Iterator[(String, Any)] = _params.iterator

  /**
   * 파라미터들을 담은 Map을 리턴
   */
  def parameters = _params

  /**
   * Dataset들을 담은 List를 리턴
   */
  def datasetsToList = _datasets.foldLeft(List.empty[Dataset])((l, kv) => l ::: List(kv._2))

  /**
   * Dataset들을 담은 Map을 리턴
   */
  def datasets = _datasets

  /**
   * 주어진 Dataset의 List로 모든 Dataset들을 재설정한다. 즉, 기존 Dataset들은 삭제된다.
   */
  def datasets_=(datasets: List[Dataset]) {
    datasets.foreach { ds =>
      _datasets += ds.name -> ds
    }
  }

  /**
   * 주어진 Dataset의 Map으로 모든 Dataset들을 재설정한다. 즉, 기존 Dataset들은 삭제된다.
   */
  def datasets_=(datasets: Map[String, Dataset]) {
    _datasets.clear()
    _datasets ++= datasets
  }

  /**
   * 주어진 Dataset을 설정한다. 동일한 이름의 Dataset이 있을 경우에는 overriding 한다.
   */
  def setDataset(ds: Dataset) { _datasets += ds.name -> ds }

  /**
   * 주어진 이름의 Dataset이 있을 경우에는 해당 Dataset의 값을 삭제 후 주어진 rows로 값을 재설정한다.
   * 주어진 이름의 Dataset이 없을 경우에는 새로운 Dataset을 생성하여 rows를 설정한다.
   */
  def setDatasetRows(dsName: String, rows: List[Map[String, Any]]) {
    val ds = getDatasetOrCreate(dsName)
    ds.rows = rows
  }

  /**
   * 주어진 이름의 Dataset이 있을 경우에는 해당 Dataset에 주어진 rows를 추가한다.
   * 주어진 이름의 Dataset이 없을 경우에는 새로운 Dataset을 생성하여 rows를 설정한다.
   */
  def addDatasetRows(dsName: String, rows: List[Map[String, Any]]) {
    val ds = getDatasetOrCreate(dsName)
    ds ++= rows
  }

  def addDatasetRow(dsName: String, row: Map[String, Any]) {
    val ds = getDatasetOrCreate(dsName)
    ds += row
  }

  /**
   * Dataset을 구한다.
   */
  def getDataset(dsName: String): Option[Dataset] = _datasets.get(dsName)

  /**
   * 주어진 이름의 Dataset이 없을 경우 새로운 Dataset을 생성한다.
   */
  def getDatasetOrCreate(dsName: String): Dataset = {
    getDataset(dsName).getOrElse {
      val d = Dataset(dsName)
      setDataset(d)
      d
    }
  }

  /**
   * Dataset을 삭제한다.
   */
  def delDataset(dsName: String) { _datasets -= dsName }

  /**
   * Dataset들을 Iterator에 담아서 리턴한다.
   */
  def datasetIterator: Iterator[Dataset] = _datasets.iterator.map { case ((dsName, ds)) => ds }

  /**
   * 주어진 이름의 Dataset의 칼럼을 생성(존재할 경우에는 overriding)하고 해당 칼럼에 값이 설정되지 않은 row를 찾아서 값을 설정한다.
   * 모든 row에 값이 설정되어 있을 경우에는 새로운 row를 추가하고 값을 설정한다.
   */
  def addDatasetColumn(dsName: String, colInfo: ColumnInfo, value: Any) {
    val ds = getDatasetOrCreate(dsName)
    ds.addColInfo(colInfo)
    ds += (colInfo, value)
  }

  override def toString(): String = ms.mkString(this)

  def toString(ms: MkString): String = ms.mkString(this)
}

//object ElasticParams {
//  def apply(parameters: Map[String, Any])(implicit ms: MkString): ElasticParams = {
//    val ep = ElasticParams()(ms)
//    ep ++= parameters
//    ep
//  }
//
//  def apply(parameters: Map[String, Any], datasets: List[Dataset])(implicit ms: MkString): ElasticParams = {
//    val ep = ElasticParams()(ms)
//    ep ++= parameters
//    ep.datasets = datasets
//    ep
//  }
//}