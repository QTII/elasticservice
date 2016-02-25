package test.elasticservice.util

import elasticservice.util.ReplaceableText

object ReplaceableTextTest {
  def main(args: Array[String]) {
    test1()
  }

  def test2() {
    val str = "abcd&efgh"
    println(str)
    println(str.replace("&", "&amp;"))
    assert(str == "")
  }

  def test1() {
    val src = """${c/*urrent.dir}  <h*/tml>\n<a     111${ABC}a 
222$ABC$  333#ABC#  444#{ABC}>\n     ${AB_C}\n<LOOP${loopA}>${aaa},
${bbb} 하하  </LOOP>\n </html>"""
    val text = ReplaceableText(src)
    text.varMark = '$'
    println("src=" + text.srcText + "\n")

    val map = Map("AbC" -> "하하호호히히히", "bbb" -> 1234)
    text.setValues(map)
    text.setValue("AB_C", "ho");
    text.setValue("ABc", "가나다라마바사");

    println("vars=" + text.variables + "\n")
    println("dst=" + text + "\n")
  }
}