

object RegexTest {
  def main(args: Array[String]) {
		  val str = "S123COL_"
		  println(toSqlIdx("S123COL_abc"))
		  println(toSqlIdx("S0COL_abcd"))
		  println(toSqlIdx("S1COL_abcde"))
		  println(toSqlIdx("S12COL_abcdef"))
		  println(toSqlIdx("S1234COL_abcdefg"))
		  println(toSqlIdx("S034COL_abcdefgh"))
		  println(toSqlIdx("SCOL_abcdefghi"))
  }
  
  def toSqlIdx(paramName: String): Option[(Int, String)] = { 
	  val pattern = "^S([0-9]|[1-9][0-9]|[1-9][0-9][0-9])COL_".r
	  for {
      s <- pattern.findFirstIn(paramName)
	  } yield {
	    (s.substring(1, s.indexOf("COL_")).toInt, paramName.substring(paramName.indexOf("COL_")+4))
	  }
  }
}