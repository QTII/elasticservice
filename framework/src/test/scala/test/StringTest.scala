package test

object StringTest {
  def main(args: Array[String]) {
    val str = "abc.defg"
    val t = str.span { _ != '.' }
    println(t)
    
    
    str.split('.').foreach { println }
    
    val m = Map(1->2)
    println(m.get(2).orElse(m.get(1)).map("a" + _))
    println(m.get(1).orElse(m.get(2)).map("a" + _))
  }
}