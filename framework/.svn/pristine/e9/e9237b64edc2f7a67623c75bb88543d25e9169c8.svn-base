package test.elasticservice.util

object BytesTest {
  def main(args: Array[String]) {
    val b = equalsBOM(Array[Byte](1.toByte, 2.toByte), Array[Byte](1.toByte, 2.toByte, 3.toByte))
    println(b)
  }

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

}