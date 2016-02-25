package test.elasticservice.web

import elasticservice.util.ep.urlparams.GenFromURLParams

object GenFromURLParamsTest {
  def main(args: Array[String]) {
    val b = genTest("COL_aa=aaaa&COL_aa=111111&COL_bb=bbbbbbb&", "UTF-8")
    println(b)
  }
  
  def genTest(in: String, encoding: String) =
  	GenFromURLParams.gen(in, Some(encoding))
}