package elasticservice.util.sqlrepo

import java.io.File
import java.util.TimerTask

import scala.util.Failure
import scala.util.Sorting
import scala.util.Success

import com.typesafe.scalalogging.LazyLogging

import elasticservice.epMkString
import elasticservice.util.DataValid
import elasticservice.util.FilePathUtil
import elasticservice.util.FileUtil
import elasticservice.util.GenXML
import elasticservice.util.ep.Record
import elasticservice.util.sqlrepo.sql.Sql

object SqlRepo extends LazyLogging {
  private val pkgMap = Record.empty[Package]
  private var baseDirOpt: Option[String] = None
  private val timer = new java.util.Timer()
  private var taskOpt: Option[TimerTask] = None
  private var errFiles = Set.empty[String]
  private val TemplatePrefix = "_Template_"

  setTimer()

  private def sync() { baseDirOpt.foreach(init) }

  def init(baseDir: String) {
    baseDirOpt = Some(baseDir)

    def error(t: Throwable, fpath: String) {
      if (!errFiles.contains(fpath)) {
        errFiles += fpath
        logger.error(t.getMessage + ": file=" + fpath)
      }
    }

    // logger.trace("scanning " + baseDir)
    
    val sqlIds = allIdsFromDisk()

    sqlIds.foreach {
      case ((pkgId, sqlIdSet)) =>
        val pkg = pkgMap.get(pkgId).getOrElse {
          val p = Package(pkgId)
          pkgMap += pkgId -> p
          p
        }

        sqlIdSet.foreach { sqlId =>
          val f = new File(filePath(pkgId, sqlId))
          getSql(pkgId, sqlId) match {
            case Some(s) if s.lastModified == f.lastModified =>
            case _ =>
              GenXML.toString(f, None) match {
                case Success(xmlStr) =>
                  new SqlXmlLoader().loadString(xmlStr) match {
                    case Success(s) =>
                      s.setId(pkg.id, FilePathUtil.getFileNameWithoutExt(f))
                      s.xmlString = xmlStr
                      s.lastModified = f.lastModified
                      pkg += s
                      logger.info("loaded: " + s.fullSqlId)
                    case Failure(e) => error(e, f.getAbsolutePath)
                  }
                case Failure(e) => error(e, f.getAbsolutePath)
              }
          }
        }
    }

    pkgMap.keySet.foreach { k =>
      if (!sqlIds.contains(k)) pkgMap -= k
      else {
        pkgMap.get(k).foreach { pkg =>
          pkg.idSet.foreach { id =>
            if (!sqlIds.get(k).get.contains(id)) pkg -= id
          }
        }
      }
    }
    
    // logger.trace("scanning is done")
  }

  def allPkgIdsFromDisk(): Seq[String] = {
    allPkgIdsFromDisk(pkgId => !pkgId.startsWith(TemplatePrefix))()
  }

  def templatePkgIds(): Seq[String] = {
    allPkgIdsFromDisk(pkgId => pkgId.startsWith(TemplatePrefix))()
  }

  private def getRelativePath(file: File) = {
    val baseDir = baseDirOpt.get
    file.getAbsolutePath.substring(baseDir.charAt(baseDir.size - 1) match {
      case '/' | '\\' => baseDir.size
      case _          => baseDir.size + 1
    })
  }

  private def allPkgIdsFromDisk(filter: String => Boolean)(): Seq[String] = {
    val pkgIds = scala.collection.mutable.ListBuffer.empty[String]

    FileUtil.listDirsRecursively(new File(baseDirOpt.get)).foreach { f =>
      val pkgId = getRelativePath(f).replace("/", ".").replace("\\", ".")
      if (filter(pkgId))
        pkgIds += pkgId
    }
    pkgIds.toSeq
  }

  def allIdsFromDisk(): Record[Seq[String]] = {
    allIdsFromDisk(pkgId => !pkgId.startsWith(TemplatePrefix))()
  }

  def templateSqlIds(): Record[Seq[String]] = {
    allIdsFromDisk(pkgId => pkgId.startsWith(TemplatePrefix))()
  }

  private def allIdsFromDisk(filter: String => Boolean)(): Record[Seq[String]] = {
    val sqlIds = Record.empty[scala.collection.mutable.ListBuffer[String]]
    val pkgFiles = FileUtil.listDirsRecursively(new File(baseDirOpt.get))
    val sqlFiles = FileUtil.listFilesRecursively(new File(baseDirOpt.get), "xml")
    val allFiles = pkgFiles ++ sqlFiles
    Sorting.quickSort(allFiles)

    allFiles.foreach { f =>
      val (pkgId, id) = pkgAndIdFromPath(getRelativePath(f))
      if (filter(pkgId)) {
        val idList = sqlIds.get(pkgId).getOrElse {
          val set = scala.collection.mutable.ListBuffer.empty[String]
          sqlIds += pkgId -> set
          set
        }
        if (DataValid.isNotEmpty(id))
          idList += id
      }
    }
    sqlIds.map { case ((pkgId, idList)) => pkgId -> idList.toSeq }
  }

  def get(pkgId: String): Option[Package] = pkgMap.get(pkgId)

  def getSql(fullSqlId: String): Option[Sql] = {
    val (pkgId, id) = pkgAndIdFromFullSqlId(fullSqlId)
    getSql(pkgId, id)
  }

  def getSql(pkgId: String, id: String): Option[Sql] = pkgMap.get(pkgId).flatMap(_.get(id))

  def fullSqlId(pkgId: String, id: String): String = pkgId match {
    case null | "" if DataValid.isEmpty(id) => ""
    case null | ""                          => id
    case p if DataValid.isEmpty(id)         => p
    case p                                  => p + "." + id
  }

  def pkgAndIdFromFullSqlId(fullSqlId: String): (String, String) = {
    fullSqlId.lastIndexOf('.') match {
      case -1  => ("", fullSqlId)
      case 0   => (fullSqlId, "")
      case idx => (fullSqlId.substring(0, idx), fullSqlId.substring(idx + 1))
    }
  }

  def pkgAndIdFromPath(relativePath: String): (String, String) = {
    relativePath.toLowerCase.lastIndexOf(".xml") match {
      case -1 =>
        (relativePath.replace("/", ".").replace("\\", "."), "")
      case i =>
        pkgAndIdFromFullSqlId(relativePath.substring(0, i).replace("/", ".").replace("\\", "."))
    }
  }

  def file(pkgAndId: (String, String)): File = new File(filePath(pkgAndId))

  def filePath(pkgAndId: (String, String)): String = filePath(pkgAndId._1, pkgAndId._2)

  def filePath(pkg: String, sqlId: String): String =
    baseDirOpt.get + File.separator +
      (pkg.takeWhile(_ == '.') + pkg.dropWhile(_ == '.').replace(".", File.separator)) +
      (if (DataValid.isEmpty(sqlId)) "" else File.separator + sqlId + ".xml")

  def renamePackage(src: Option[String], dst: Option[String]): Boolean = {
    val suc = (src, dst) match {
      case (None, None)       => false
      case (None, Some(d))    => FileUtil.makeDirs(file(d, ""))
      case (Some(s), None)    => FileUtil.removeDirs(file(s, ""))
      case (Some(s), Some(d)) => FileUtil.renameDir(file(s, ""), file(d, ""))
    }
    if (suc) sync()
    suc
  }

  def saveSql(fullSqlId: String, xml: String): Option[Throwable] = {
    new SqlXmlLoader().loadString(xml) match {
      case Success(s) =>
        FileUtil.write(file(pkgAndIdFromFullSqlId(fullSqlId)), xml)
        sync()
        None
      case Failure(e) => Some(e)
    }
  }

  def removeSql(fullSqlId: String): Boolean = {
    val suc = FileUtil.removeFile(file(pkgAndIdFromFullSqlId(fullSqlId)))
    if (suc) sync()
    suc
  }

  private def setTimer() {
    taskOpt.foreach(_.cancel)
    taskOpt = Some(new java.util.TimerTask { def run() = sync })
    taskOpt.foreach { timer.schedule(_, 10000L, 10000L) }
  }
}