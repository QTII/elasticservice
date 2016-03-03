package elasticservice.service

import java.sql.Connection

import scala.collection.mutable.ListBuffer
import scala.util.Try

import com.typesafe.scalalogging.LazyLogging

import elasticservice.ElasticService
import elasticservice.ElasticServiceUtil
import elasticservice.epMkString
import elasticservice.util.ep.Dataset
import elasticservice.util.ep.ElasticParams
import elasticservice.util.sqlrepo.SqlConn
import elasticservice.util.sqlrepo.SqlRepo
import elasticservice.util.sqlrepo.sql.Sql
import play.api.Play.current
import play.api.db.DB

class QueryService extends ElasticService with LazyLogging {
  def execute(req: ElasticParams): Try[ElasticParams] = {
    Try {
      ifNotAccessibleThrow.foreach(throw _)
      ifMissingThrow("sqlId", req).foreach(throw _)

      val fullSqlIds = req.get("sqlId").getOrElse("").toString
      val transaction = req.get("tx").getOrElse("false").toString.toBoolean

      val sqlsTry = toSqlList(fullSqlIds)
      ifFailThrow(sqlsTry).foreach(throw _)

      //      val helloConfiguration: Configuration =
      //      configuration.getConfig("hello").getOrElse(Configuration.empty)

      val conn: Connection = DB.getConnection("default", !transaction)

      val sqlConn = SqlConn(conn)

      try {
        val (errOpt, dsList) = query(sqlConn, sqlsTry.get, req.datasets.map(x => x._1 -> x._2.rows))

        if (transaction && errOpt == None)
          sqlConn.commit()

        val resEP = toElasticParams(dsList)
        for { e <- errOpt } yield ElasticServiceUtil.setCodeAndMsg(resEP, 999, e.getMessage)
        resEP
      } finally {
        sqlConn.close()
      }
    }
  }

  private def toSqlList(fullSqlIds: String): Try[List[Sql]] = {
    Try {
      fullSqlIds.split(',').toList.flatMap(fullSqlId => SqlRepo.getSql(fullSqlId) match {
        case None => throw new Exception("unknown sqlId: " + fullSqlId)
        case s    => s
      })
    }
  }

  private def toSql(fullSqlId: String): Try[Sql] = {
    Try {
      SqlRepo.getSql(fullSqlId) match {
        case None    => throw new Exception("unknown sqlId: " + fullSqlId)
        case Some(s) => s
      }
    }
  }

  private def toElasticParams(dsList: List[Dataset]): ElasticParams = {
    val ep = ElasticParams()
    var prevSqlId = ""
    var sqlIdx = 0
    var qryIdx = 0
    dsList.foreach { ds =>
      if (prevSqlId != ds.name) {
        sqlIdx += 1
        qryIdx = 1
      } else {
        qryIdx += 1
      }
      prevSqlId = ds.name
      ds.name = sqlIdx.toString + "." + qryIdx
      ep.setDataset(ds)
    }
    ep
  }

  private def query(sqlConn: SqlConn, sqls: List[Sql], recordsMap: Map[String, List[Map[String, Any]]]): (Option[Throwable], List[Dataset]) = {
    val buf = ListBuffer[Dataset]()
    @annotation.tailrec
    def go(sqlIdx: Int, size: Int): Option[Throwable] = {
      val sql = sqls(sqlIdx)
      val records = recordsMap.get((sqlIdx + 1).toString).getOrElse(List(Map[String, Any]()))
      sqlConn.query(sql, records) match {
        case (None, l) =>
          buf ++= l; if (sqlIdx < size - 1) go(sqlIdx + 1, size) else None
        case (e, l) => buf ++= l; e
      }
    }

    (go(0, sqls.size), buf.toList)
  }
}