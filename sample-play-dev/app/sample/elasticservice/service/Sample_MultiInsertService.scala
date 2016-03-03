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
import elasticservice.util.ep.Record

class Sample_MultiInsertService extends ElasticService {
  def execute(ep: ElasticParams): Try[ElasticParams] = {
    Try {
      val loop = ep.get("loop").getOrElse("1").toString.toInt
      val transaction = ep.get("t").orElse(ep.get("transaction")).getOrElse("false").toString.toBoolean

      val conn: Connection = DB.getConnection("default", !transaction)
      val sqlConn = SqlConn(conn)

      val dataset: Dataset = ep.getDataset("1").get
      val record: Record[Any] = dataset.rows(0)
      record += "emp_no" -> 1
      
      for (emp_no <- 2 to loop) {
        val r = new Record[Any]
        r ++= record
        r += "emp_no" -> emp_no
        r += "emp_name" -> (r.get("emp_name").getOrElse("kang") + emp_no.toString)
        dataset += r
      }

      try {
        val (errOpt, dsList) = sqlConn.query("sample.update_insert", dataset.rows)

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