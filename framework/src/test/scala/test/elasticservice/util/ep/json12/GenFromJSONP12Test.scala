package test.elasticservice.util.ep.json12

import elasticservice.util.ep.json12.GenFromJSONP12
import elasticservice.util.ep.json12.JSON12MkString

object GenFromJSONP12Test {
  def main(args: Array[String]): Unit = {
    test1()
  }

  def test1() {
    val inText = """_jsonpKey_=jQuery19109518310541752726_1437467540148&{"parameters":{"epType":"jsonp12","service.resType":"jsonp12","service":"EchoService"},"datasets":{"_NDS_":{"colInfos":[],"rows":[{"column_name1":"column 값","column_name2":"column 값"}]}}}&_=1437467540149"""
    val ep = GenFromJSONP12.gen(inText, Some("UTF-8"))
    println(ep.toString(JSON12MkString))
  }
}