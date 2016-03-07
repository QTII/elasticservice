package test.elasticservice.util

import elasticservice.util.StringUtil

object StringUtilTest {
  def main(args: Array[String]) {
    test2()
  }
  
  def test2() {
    val str = "	abc	\n"
    val ret = StringUtil.trimBack(str)
    println(ret)
    assert(ret == "	abc")
  }
  
  def test1() {
    val str = "		\n"
    assert(StringUtil.indexOfNonSpace(str) == -1)
  }
}