package elasticservice.util.sqlrepo.sql

object ColMap {
  def apply(javaColName: String, javaType: String, sqlColName: String, sqlType: Int): ColMap =
    new ColMap(javaColName, javaType, sqlColName, -1, sqlType)

  def apply(javaColName: String, javaType: String, sqlColIndex: Int, sqlType: Int): ColMap =
    new ColMap(javaColName, javaType, "", sqlColIndex, sqlType)

//  def main(args: Array[String]) {
//    val cm = new ColMap("a", "String", "col1", 2, 4)
//    println(cm)
//
//    cm.javaColName = "b"
//    println(cm)
//  }
}

class ColMap(var _javaColName: String,
             var _javaTypeName: String,
             var _sqlColName: String = "",
             var _sqlColIndex: Int = 1,
             var _sqlType: Int) {

  javaColName_=(_javaColName)
  javaTypeName_=(_javaTypeName)
  sqlColName_=(_sqlColName)
  sqlColIndex_=(_sqlColIndex)
  sqlType_=(_sqlType)

  override def toString(): String = {
    s"javaColName:$javaColName, javaType:$javaTypeName, sqlColName:$sqlColName, sqlColIndex:$sqlColIndex, sqlType:$sqlType"
  }

  def javaColName = _javaColName
  def javaColName_=(v: String) {
    _javaColName = v
  }

  def javaTypeName = _javaTypeName
  def javaTypeName_=(v: String) {
    _javaTypeName = v
  }
  def sqlColName = _sqlColName
  def sqlColName_=(v: String) {
    _sqlColName = v
  }

  def sqlColIndex = _sqlColIndex
  def sqlColIndex_=(v: Int) {
    _sqlColIndex = v
  }

  def sqlType = _sqlType
  def sqlType_=(v: Int) {
    _sqlType = v
  }
}