package elasticservice.util.ep.urlparams

import scala.annotation.migration

import com.typesafe.scalalogging.LazyLogging

import elasticservice.epMkString
import elasticservice.util.URLParamsUtil
import elasticservice.util.ep.DatasetUtil
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenFrom

object GenFromURLParams extends GenFrom with LazyLogging {

  /**
   * @param paramName URL parameter naming:
   *    - S + Number + COL_ + columnExpression <br>
   *       - Number: 1 ~ <br>
   *       - columnExpression: see DatasetUtil.parseColumnExpression() <br>
   *
   *    - Examples:
   *       - S1COL_colExpr
   *
   * @return Option of a tuple consisting of number (as the first element) and column expression (as the second element).
   * The number here means name of dataset.
   *
   * @see DatasetUtil.parseColumnExpression()
   */
  private def parseParamName(paramName: String): Option[(Int, String)] = {
    val filter = """^S(\d+)COL_(.+)""".r
    paramName match {
      case filter(index, colExpr) => Some(index.toInt, colExpr)
      case _ => None
    }
  }

  def gen(text: String, encodingOpt: Option[String]): ElasticParams = {
    val ep = ElasticParams()
    val urlParams = URLParamsUtil.queryStringToMap(text, encodingOpt)

    urlParams.keys.foreach { name =>
      urlParams.get(name).foreach { value =>
        parseParamName(name) match {
          case None => value.foreach { setEpParam(ep, name, _) }
          case Some(e) => value.foreach { setEpDataset(ep, e._1, e._2, _) }
        }
      }
    }

    ep
  }

  def setEpParam(ep: ElasticParams, colExpr: String, v: String) {
    val col = DatasetUtil.parseColumnExpression(colExpr)
    ep += col.id -> v
  }

  def setEpDataset(ep: ElasticParams, dsName: Int, colExpr: String, v: String) {
    val col = DatasetUtil.parseColumnExpression(colExpr)
    ep.addDatasetColumn(dsName.toString, col, v)
  }
}