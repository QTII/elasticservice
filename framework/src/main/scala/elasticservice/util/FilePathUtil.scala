package elasticservice.util

import java.io.File

object FilePathUtil {
  def getAbsolutePath(currPath: String, relativePath: String): String = {
    if (currPath == null)
      return relativePath

    if (DataValid.isEmpty(relativePath))
      return currPath

    if (relativePath.startsWith("/")
      || (relativePath.length() > 2 && relativePath.charAt(1) == ':'))
      return relativePath

    val sb = new StringBuilder()
    sb.append(currPath)

    val c = currPath.charAt(currPath.length() - 1)

    var i = 0
    if (relativePath.charAt(0) != '/' && relativePath.charAt(0) != '\\'
      && c != '/' && c != '\\')
      sb.append(File.separatorChar)
    else if ((relativePath.charAt(0) == '/' || relativePath.charAt(0) == '\\')
      && (c == '/' || c == '\\'))
      i = 1

    val pathLen = relativePath.length()
    for (j <- i until pathLen) {
      val ch = relativePath.charAt(j)
      if (ch == '\\' || ch == '/')
        sb.append(File.separatorChar)
      else
        sb.append(ch)
    }
    sb.toString()
  }

  /**
   * 플랫폼에 맞는 path명으로 변환한다.
   *
   * @param path
   *            OS Independent path
   * @return OS dependent path
   */
  def adaptPath(path: String): String = {
    if (DataValid.isEmpty(path))
      return File.separator

    val pathLen = path.length()
    val value = Array.fill(pathLen)(' ')
    for (i <- 0 until pathLen) {
      value(i) = path.charAt(i)
      if (value(i) == '\\' || value(i) == '/')
        value(i) = File.separatorChar
    }
    new String(value)
  }

  def getExtention(fpath: String): String = {
    val idx = fpath.lastIndexOf('.')
    if (idx < 0) fpath else fpath.substring(idx + 1)
  }

  def getExtention(file: File): String = getExtention(file.getPath())

  /**
   * @deprecated
   * @param fpath
   * @return
   */
  def getBasePath(fpath: String): String = fpath.substring(0, getNameIndex(fpath) - 1)

  def getBasePath(file: File): String = {
    val fpath = file.getCanonicalPath()
    val fnameIdx = getFileNameIndex(file)
    if (fnameIdx == -1) fpath else fpath.substring(0, fnameIdx - 1)
  }

  def getDirectory(file: File): String = getBasePath(file)

  def getEntryDirectoryName(file: File): String = {
    val fnameIdx = getFileNameIndex(file)
    val fpath = file.getCanonicalPath()
    var end = if (fnameIdx == -1) fpath.length() else fnameIdx - 1

    var i = end - 1
    while (i >= 0) {
      if (fpath.charAt(i) == '\\' || fpath.charAt(i) == '/') {
        return fpath.substring(i + 1, end)
      }
      i -= 1
    }
    "";
  }

  def getFileNameIndex(file: File): Int = {
    if (file.isDirectory())
      return -1
    val fpath = file.getCanonicalPath()
    val len = fpath.length()
    var pos = 0
    var i = len - 1
    while (i >= 0) {
      if (fpath.charAt(i) == '\\' || fpath.charAt(i) == '/') {
        pos = i + 1
        i = -1
      } else {
        i -= 1
      }
    }
    i = pos
    while (i < len && fpath.charAt(i) == '.') i += 1
    if (len == i) i else pos
  }

  /**
   * @deprecated
   * @param fpath
   * @return
   */
  def getNameIndex(fpath: String): Int = {
    val len = fpath.length()
    var pos = 0
    var i = len - 1
    while (i >= 0) {
      if (fpath.charAt(i) == '\\' || fpath.charAt(i) == '/') {
        pos = i + 1
        i = -1
      } else {
        i -= 1
      }
    }
    i = pos
    while (i < len && fpath.charAt(i) == '.') i += 1
    if (len == i) i else pos
  }

  def toDirPath(fpath: String): String = {
    val ch = fpath.charAt(fpath.length() - 1)
    if ((ch != '/') && (ch != '\\'))
      fpath + File.separatorChar
    else fpath
  }

  /**
   * 파일 경로에서 파일명을 구한다. 이때 확장자는 제외한다.
   *
   * @param fpath
   * @return
   */
  def getFileNameWithoutExt(fpath: String): String = getFileNameWithoutExt(new File(fpath))

  def getFileNameWithoutExt(file: File): String = {
    val fname = file.getName
    val idx = fname.lastIndexOf('.')
    if (idx > 0) fname.substring(0, idx) else fname
  }

  def getFileName(fpath: String): String = new File(fpath).getName
}