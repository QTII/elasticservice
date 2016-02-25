package test.elasticservice.util

import java.io.File
import elasticservice.util.DataValid
import elasticservice.util.FilePathUtil

object FileTest {
  def listFilesRecursively(f: File): Array[File] = {
    listFilesRecursively(f, null)
  }

  def listFilesRecursively(f: File, extension: String): Array[File] = {
    val these = f.listFiles
    val l = these ++ these.filter(_.isDirectory).flatMap(listFilesRecursively(_, extension))
    if (!DataValid.isEmpty(extension))
      l.filter(f => (".*\\." + extension + "$").r.findFirstIn(f.getName).isDefined)
    else
      l
  }

  def main(args: Array[String]) {
    val l = listFilesRecursively(new File("D:\\Temp\\util"))

    println(l.mkString("[", ", ", "]"))
    
    l.foreach { f => println(f.getName + " -> " + FilePathUtil.getEntryDirectoryName(f)) }
  }
}