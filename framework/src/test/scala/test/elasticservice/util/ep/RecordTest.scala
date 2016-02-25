package test.elasticservice.util.ep

import elasticservice.epMkString
import elasticservice.util.ep.Record

object RecordTest {
  def main(args: Array[String]) = {
    //var map = Record("bb" -> "BBB", "aa" -> "aa1", "aA" -> "aa2")
    var map = Record(Map("bb2" -> "BBB", "aa" -> "aa1", "aA" -> "aa2"))
    log("map", map)

    log("aa", map.get("aa"))
    log("aA", map.get("aA"))
    log("Aa", map.get("Aa"))
    log("AA", map.get("AA"))
    log("BBB", map.get("BBB"))

    var m = map + ("bb" -> "BB1")

    log("map", map)
    log("m", m)
  }

  def log(t: String, m: Any) {
    println(t + ":" + m.getClass.getSimpleName + " = " + m.toString())
  }
}