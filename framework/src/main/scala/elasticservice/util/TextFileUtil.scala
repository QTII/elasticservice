package elasticservice.util

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import elasticservice.DefaultVal

object TextFileUtil {
  val LineSeparator = sys.props("line.separator")

  val BOM_UTF_8 = Array[Byte](0xEF.toByte, 0xBB.toByte, 0xBF.toByte)
  val BOM_UTF_16BE = Array[Byte](0xFE.toByte, 0xFF.toByte)
  val BOM_UTF_16LE = Array[Byte](0xFF.toByte, 0xFE.toByte)
  val BOM_UTF_32BE = Array[Byte](0x00.toByte, 0x00.toByte, 0xFE.toByte, 0xFF.toByte)
  val BOM_UTF_32LE = Array[Byte](0xFF.toByte, 0xFE.toByte, 0x00.toByte, 0x00.toByte)

  def equalsBOM(bom: Array[Byte], firstLineBytes: Array[Byte]): Boolean = {
    (bom, firstLineBytes) match {
      case (null, b) => false
      case (a, null) => false
      case (a, b) =>
        if (a.length > b.length) false
        else {
          var i = 0
          for (x <- bom) {
            if (x != firstLineBytes(i))
              return false
            i += 1
          }
          true
        }
    }
  }

  def hasBOM(firstLine: String): Boolean = hasBOM(firstLine.getBytes())

  def hasBOM(firstLineBytes: Array[Byte]): Boolean = {
    equalsBOM(BOM_UTF_8, firstLineBytes) ||
      equalsBOM(BOM_UTF_16BE, firstLineBytes) ||
      equalsBOM(BOM_UTF_16LE, firstLineBytes) ||
      equalsBOM(BOM_UTF_32BE, firstLineBytes) ||
      equalsBOM(BOM_UTF_32LE, firstLineBytes)
  }

  def removeBOM(bytes: Array[Byte]): Array[Byte] = {
    var offset = if (equalsBOM(BOM_UTF_8, bytes))
      BOM_UTF_8.length
    else if (equalsBOM(BOM_UTF_16BE, bytes))
      BOM_UTF_16BE.length
    else if (equalsBOM(BOM_UTF_16LE, bytes))
      BOM_UTF_16LE.length
    else if (equalsBOM(BOM_UTF_32BE, bytes))
      BOM_UTF_32BE.length
    else if (equalsBOM(BOM_UTF_32LE, bytes))
      BOM_UTF_32LE.length
    else
      0

    if (offset > 0) {
      val newBytes = Array.fill[Byte](bytes.length - offset)(0)
      Array.copy(bytes, offset, newBytes, 0, newBytes.length)
      newBytes
    } else {
      bytes.clone
    }
  }

  def removeBOM(firstLine: String, encoding: String): String = {
    val bytes = firstLine.getBytes(encoding)
    if (!hasBOM(bytes)) {
      return firstLine
    } else {
      val offset = if ("UTF-8".equals(encoding))
        BOM_UTF_8.length
      else if ("UTF-16BE".equals(encoding))
        BOM_UTF_16BE.length
      else if ("UTF-16LE".equals(encoding))
        BOM_UTF_16LE.length
      else if ("UTF-32BE".equals(encoding))
        BOM_UTF_32BE.length
      else if ("UTF-32LE".equals(encoding))
        BOM_UTF_32LE.length
      else
        0

      if (offset > 0)
        new String(bytes, offset, bytes.length - offset, encoding)
      else
        firstLine
    }
  }

  def bytesFrom(file: File, max: Int): Array[Byte] = {
    val in = new BufferedInputStream(new FileInputStream(file))
    val t = Try {
      val baos = new ByteArrayOutputStream()
      var cnt = 0
      var b = -1
      var end = false
      while (!end) {
        b = in.read()
        if (b != -1) {
          baos.write(b)
          cnt += 1
          if (cnt == max) end = true
        } else {
          end = true
        }
      }
      baos.close()
      baos.toByteArray()
    }
    in.close()
    t match {
      case Success(a) => a
      case Failure(e) => Array.emptyByteArray
    }
  }

  def bytesFrom(file: File): Array[Byte] = bytesFrom(file, -1)

  def detectEncodingOfFile(file: File): Option[String] = detectEncoding(bytesFrom(file, 5))

  def textFrom(file: File, encodingOp: Option[String]): Try[String] = {
    val bytes = bytesFrom(file)
    Try {
      encodingOp match {
        case Some(e) => new String(bytes, e)
        case _ =>
          detectEncodingOfFile(file) match {
            case Some(e2) => new String(bytes, e2)
            case _        => new String(bytes)
          }
      }
    } match {
      case Success(s) => Success(s)
      case Failure(e) => Try { new String(bytes, DefaultVal.Charset) }
    }
  }

  def detectEncoding(firstLineBytes: Array[Byte]): Option[String] = {
    if (equalsBOM(BOM_UTF_8, firstLineBytes))
      Some("UTF-8")
    else if (equalsBOM(BOM_UTF_16BE, firstLineBytes))
      Some("UTF-16BE")
    else if (equalsBOM(BOM_UTF_16LE, firstLineBytes))
      Some("UTF-16LE")
    else if (equalsBOM(BOM_UTF_32BE, firstLineBytes))
      Some("UTF-32BE")
    else if (equalsBOM(BOM_UTF_32LE, firstLineBytes))
      Some("UTF-32LE")
    else
      None
  }

  def firstLineFromFile(fpath: String): String = firstLineFromFile(new File(fpath))

  def firstLineFromFile(file: File): String = {
    if (!file.exists() || file.isDirectory())
      return ""

    var br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
    try {
      br.readLine()
    } finally br.close()
  }

  def isUTF_8_BOM(firstLineBytes: Array[Byte]): Boolean = equalsBOM(BOM_UTF_8, firstLineBytes)
  def isBOM_UTF_16BE_BOM(firstLineBytes: Array[Byte]): Boolean = equalsBOM(BOM_UTF_16BE, firstLineBytes)
  def isBOM_UTF_16LE_BOM(firstLineBytes: Array[Byte]): Boolean = equalsBOM(BOM_UTF_16LE, firstLineBytes)
  def isBOM_UTF_32BE_BOM(firstLineBytes: Array[Byte]): Boolean = equalsBOM(BOM_UTF_32BE, firstLineBytes)
  def isBOM_UTF_32LE_BOM(firstLineBytes: Array[Byte]): Boolean = equalsBOM(BOM_UTF_32LE, firstLineBytes)

  def lengthOfBOM(firstLineBytes: Array[Byte]): Int = {
    if (equalsBOM(BOM_UTF_8, firstLineBytes))
      BOM_UTF_8.length
    else if (equalsBOM(BOM_UTF_16BE, firstLineBytes))
      BOM_UTF_16BE.length
    else if (equalsBOM(BOM_UTF_16LE, firstLineBytes))
      BOM_UTF_16LE.length
    else if (equalsBOM(BOM_UTF_32BE, firstLineBytes))
      BOM_UTF_32BE.length
    else if (equalsBOM(BOM_UTF_32LE, firstLineBytes))
      BOM_UTF_32LE.length
    else
      0
  }
}