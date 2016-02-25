package elasticservice.util.ep.xml

import scala.util.Failure
import scala.util.Success
import scala.xml.Elem

import com.typesafe.scalalogging.LazyLogging

import elasticservice.ElasticServiceUtil
import elasticservice.epMkString
import elasticservice.util.ExceptionDetail
import elasticservice.util.GenXML
import elasticservice.util.ep.ColumnInfo
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.DatasetUtil
import elasticservice.util.ep.ElasticParams
import elasticservice.util.ep.GenFrom
import elasticservice.util.ep.Record

object GenFromXML extends GenFrom with LazyLogging {

  val VAL_DEFAULT_NULL = "${NULL}"

  def gen(xml: Elem): ElasticParams = {
    val ep = ElasticParams()
    val paramList = xml \ "Root" \ "Parameters" \ "Parameter"
    paramList foreach { x =>
      val id = x \@ "id"
      val value = x.text
      ep += id -> value
    }

    val dsList = xml \ "Root" \ "Dataset"
    dsList.foreach { dsNode =>
      val dataSetId = dsNode \@ "id"

      val webDS = Dataset(dataSetId)
      ep.setDataset(webDS)

      // Parses ColumnInfo node and declares ColumnInfos
      var colMap = Map[String, ColumnInfo]()

      dsNode \ "ColumnInfo" \ "Column" foreach { cNode =>
        val id = cNode \@ "id"
        val col = ColumnInfo(id, id, cNode \@ "type", (cNode \@ "size").toInt)
        webDS.addColInfo(col)
        colMap += id -> col
      }

      // Parses Rows node
      dsNode \ "Rows" \ "Row" foreach { rNode =>
        // Adds a record to the Dataset.
        val wRow = Record.empty[Any]
        webDS += wRow

        rNode \ "Col" foreach { x =>
          for {
            ci <- colMap.get(x \@ "id")
          } yield DatasetUtil.setColTo(wRow, ci.id, ci.typeClass, x.text, VAL_DEFAULT_NULL)
        }
      }
    }
    ep
  }

  def gen(text: String, encodingOpt: Option[String]): ElasticParams = {
    GenXML.loadString(text) match {
      case Success(xml) => gen(xml)
      case Failure(e) =>
        logger.error("xml: " + text + "\n" + ExceptionDetail.getDetail(e))
        ElasticServiceUtil.epWithCodeMessage(999, e.getMessage)
    }
  }
}