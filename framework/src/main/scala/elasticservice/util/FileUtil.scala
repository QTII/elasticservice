package elasticservice.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.nio.channels.FileChannel

import scala.util.Sorting
import scala.util.Try
import scala.util.matching.Regex

import com.typesafe.scalalogging.LazyLogging

object FileUtil extends LazyLogging {
  def listFilesRecursively(f: File): Array[File] = listFilesRecursively(f, None: Option[Regex])

  def listFilesRecursively(f: File, extension: String): Array[File] =
    if (DataValid.isEmpty(extension)) listFilesRecursively(f, None: Option[Regex])
    else listFilesRecursively(f, Some((".*\\." + extension.toLowerCase + "$").r))

  def listFilesRecursively(f: File, regex: Option[Regex]): Array[File] = {
    val these = f.listFiles
    if (!these.isEmpty) Sorting.quickSort(these)
    val l = these ++ these.filter(_.isDirectory).flatMap(listFilesRecursively(_, regex))
    if (regex == None) l else l.filter(f => regex.get.findFirstIn(f.getName.toLowerCase).isDefined)
  }

  def listDirsRecursively(f: File): Array[File] = {
    val these = f.listFiles.filter(_.isDirectory)
    if (!these.isEmpty) Sorting.quickSort(these)
    these ++ these.flatMap(listDirsRecursively(_))
  }

  def append(to: File, text: String): Boolean = {
    makeDirs(new File(FilePathUtil.getBasePath(to)))

    val fw = new FileWriter(to, true)
    try {
      fw.write(text)
      true
    } catch {
      case e: Throwable => false
    } finally fw.close()
  }

  def write(to: File, text: String): Boolean = {
    makeDirs(new File(FilePathUtil.getBasePath(to)))

    val fw = new FileWriter(to, false)
    try {
      fw.write(text)
      true
    } catch {
      case e: Throwable => false
    } finally fw.close()
  }

  def appendln(file: File, str: String) { append(file, str + TextUtil.LineSeparator) }

  def makeEmptyFile(file: File): Boolean = {
    makeDirs(new File(FilePathUtil.getBasePath(file)))
    file.createNewFile()
  }

  def removeDirs(dir: File): Boolean = {
    listFilesRecursively(dir).foreach { file =>
      if (file.isDirectory) {
        if (!removeDirs(file)) return false
      } else {
        if (!removeFile(file)) return false
      }
    }
    removeFile(dir)
  }

  def removeFile(file: File): Boolean = {
    val suc = if (file.exists) file.getCanonicalFile.delete() else true
    def fileType = if (file.isDirectory()) "directory" else "file"
    if (suc)
      logger.trace("removed " + fileType + ": " + file.getCanonicalPath)
    else
      logger.trace("failed to remove " + fileType + ": " + file.getCanonicalPath)
    suc
  }

  def makeDirs(dir: File): Boolean = if (dir.exists()) true else dir.mkdirs()

  def renameDir(srcDir: File, dstDir: File): Boolean = {
    logger.trace("renameDir " + srcDir.getCanonicalPath + " to " + dstDir.getCanonicalPath)
    if (srcDir == dstDir) return true

    if (!srcDir.exists) {
      logger.warn("file not found: " + srcDir.getCanonicalPath)
      false
    } else if (srcDir.isFile) {
      logger.warn("not directory: " + srcDir.getCanonicalPath)
      false
    } else if (dstDir.exists) {
      logger.warn("directory already exists: " + dstDir.getCanonicalPath)
      false
    } else {
      if (!makeDirs(dstDir)) return false
      listFilesRecursively(srcDir).foreach { s =>
        if (!dstDir.getCanonicalPath.startsWith(s.getCanonicalPath))
          if (!(moveToUnderDir(s, dstDir)))
            return false
      }
      if (!dstDir.getCanonicalPath.startsWith(srcDir.getCanonicalPath))
        removeDirs(srcDir)
      else
        true
    }
  }

  def moveToUnderDir(src: File, dstDir: File): Boolean = {
    logger.trace("moveToUnderDir " + src.getCanonicalPath + " to " + dstDir.getCanonicalPath)

    if (!src.exists) {
      logger.warn("file not found: " + src.getCanonicalPath)
      false
    } else if (src.isFile) {
      renameFile(src, new File(dstDir.getCanonicalPath + File.separator + src.getName))
    } else {
      val newDir = new File(dstDir.getCanonicalPath + File.separator + src.getName)
      if (!makeDirs(newDir)) return false
      listFilesRecursively(src).foreach { s =>
        if (!dstDir.getCanonicalPath.startsWith(s.getCanonicalPath))
          if (!(moveToUnderDir(s, newDir))) return false
      }
      if (!dstDir.getCanonicalPath.startsWith(src.getCanonicalPath))
        removeDirs(src)
      else
        true
    }
  }

  def renameFile(srcFile: File, dstFile: File): Boolean = {
    logger.trace("moveFile " + srcFile.getCanonicalPath + " to " + dstFile.getCanonicalPath)
    if (srcFile == dstFile) return true

    if (dstFile.exists)
      removeFile(dstFile)

    if (!srcFile.getCanonicalFile.renameTo(dstFile.getCanonicalFile))
      if (!copyAndDeleteFile(srcFile, dstFile))
        return false

    true
  }

  def copyAndDeleteFile(srcFile: File, dstFile: File): Boolean = {
    logger.trace("copyAndDeleteFile " + srcFile.getCanonicalPath + " to " + dstFile.getCanonicalPath)
    if (srcFile == dstFile) return true

    try {
      if (!copyFile(srcFile, dstFile)) {
        return false
      } else {
        removeFile(srcFile)
      }
    } catch {
      case e: Exception =>
        logger.error(ExceptionDetail.getDetail(e))
        return false
    }
    true
  }

  def copyFile(srcFile: File, dstFile: File): Boolean = {
    logger.trace("copyFile " + srcFile.getCanonicalPath + " to " + dstFile.getCanonicalPath)
    if (srcFile == dstFile) return true

    makeDirs(new File(FilePathUtil.getBasePath(dstFile)))

    var inStream: FileInputStream = null
    var outStream: FileOutputStream = null
    var inCh: FileChannel = null
    var outCh: FileChannel = null

    try {
      val fileDir: File = dstFile.getParentFile()
      if (fileDir != null)
        fileDir.mkdirs()

      inStream = new FileInputStream(srcFile)
      outStream = new FileOutputStream(dstFile.getPath())
      inCh = inStream.getChannel()
      outCh = outStream.getChannel()

      inCh.transferTo(0, inCh.size(), outCh)
    } catch {
      case e: IOException =>
        logger.error(ExceptionDetail.getDetail(e))
        return false
    } finally {
      if (outCh != null)
        Try { outCh.close() }
      if (inCh != null)
        Try { inCh.close() }
      if (outStream != null)
        Try { outStream.close() }
      if (inStream != null)
        Try { inStream.close() }
    }
    true
  }
}