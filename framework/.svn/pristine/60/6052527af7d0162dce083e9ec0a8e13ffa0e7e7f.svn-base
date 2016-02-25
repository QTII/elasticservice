package test.elasticservice.util.sqlrepo

object PkgAndIdFromFullSqlIdTest {
    def main (args: Array[String]) {
    println(pkgAndIdFromFullSqlId(".d"))
    println(pkgAndIdFromFullSqlId("d"))
    println(pkgAndIdFromFullSqlId(".a.d"))
    println(pkgAndIdFromFullSqlId(".a.b.c.d"))
    println(pkgAndIdFromFullSqlId("a.d"))
    println(pkgAndIdFromFullSqlId("a.b.c.d"))
  }
  
  def pkgAndIdFromFullSqlId(fullSqlId: String): (String, String) = {
    fullSqlId.lastIndexOf('.') match {
      case -1  => ("", fullSqlId)
      case 0   => (fullSqlId, "")
      case idx => (fullSqlId.substring(0, idx), fullSqlId.substring(idx + 1))
    }
  }
}