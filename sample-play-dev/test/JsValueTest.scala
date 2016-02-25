import play.api.libs.json.JsLookupResult.jsLookupResultToJsLookup
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json
import play.api.libs.json.JsSuccess

object JsValueTest {
  def main(args: Array[String]) {
    val json = Json.parse("""
        {
          "parameters": {
              "epType":"json13",
              "newSql":"false",
              "sqlId":"a.bbdc",
              "xml":"<select>\r\n-- execute dbo.UP_COM_CMN_9091R #{AUTH_CODE} -- 한글 --\r\nSELECT *\r\nFROM T_ORD_ORD_H\r\nWHERE BIZ_DD = #{BIZ_DD}\r\n   \t<dynamic>\r\n   \t\t<iterate prepend=\"AND ORD_SEQNO IN\" open=\"(\" close=\")\" conjunction=\",\">\r\n\t#{seqno[].aa}, #{seqno[].bb}\r\n   \t\t</iterate>\r\n   \t</dynamic>\r\n</select>",
              "service":"elasticservice.service.SqlRepoService",
              "service.resType":"json13",
              "cmd":"saveSql"
            },
          "datasets":[]
        }
      """)
    println((json \ "parameters" \ "epType").validate[String].getOrElse(""))
  }
}