package sample.elasticservice.service

import java.sql.Connection

import scala.util.Try

import elasticservice.ElasticService
import elasticservice.ElasticServiceUtil
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.sqlrepo.SqlConn
import play.api.Play.current
import play.api.db.DB

class Sample031_MultiInsertService extends ElasticService {
  def execute(ep: ElasticParams): Try[ElasticParams] = {
    Try {
      val loop = ep.get("loop").getOrElse("1").toString.toInt
      val transaction = ep.get("t").orElse(ep.get("transaction")).getOrElse("false").toString.toBoolean

      val conn: Connection = DB.getConnection("default", !transaction)
      val sqlConn = SqlConn(conn)

      val r = ep.getDataset("1").get.rows(0)
      ep.getDataset("1").get ++= List.fill(loop - 1)(r)

      try {
        val (errOpt, dsList) = sqlConn.query("sample.mysql_insert1", ep.getDataset("1").getOrElse(Dataset("")).rows)

        if (transaction && errOpt == None)
          sqlConn.commit()

        val resEP = ElasticParams()
        errOpt.foreach(e => ElasticServiceUtil.setCodeAndMsg(resEP, 999, e.getMessage))

        var i = 1
        dsList.foreach { ds =>
          ds.name = i.toString
          resEP.setDataset(ds)
          i += 1
        }
        resEP
      } finally {
        sqlConn.close()
      }
    }
  }
}