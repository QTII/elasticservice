package test.elasticservice.util.ep.json12

import elasticservice.util.ep.json12.GenFromJSON12
import elasticservice.util.ep.json12.JSON12MkString

object GenFromJSON12Test {
  def main(args: Array[String]): Unit = {
    test1()
  }

  def test1() {
    val json12Str = """{
	"parameters":{"save":true, "epName":"macro_01", "code":0, "epType":"jsonp12", "message":"", "title":"マクロ 01 : EBS在庫組織変換処理", "page":1, "service.resType":"jsonp12", "service":"epService", "encoding":"UTF-8", "maxRows":100, "ErrorMsg":"", "ErrorCode":0, "resType":"json12"}, 
	"datasets":{
		 "1":{
			"colInfos":[
				{"id":"_srcRowId"},
				{"id":"id", "text":"ID"},
				{"id":"$file", "text":"File Type"},
				{"id":"inCols", "text":"Input Columns", "dataset":["idx:Integer", "id", "text", "text2"]},
				{"id":"outColsLen", "text":"", "size":6},
				{"id":"outCols", "text":"Columns", "dataset":["idx:Integer", "id", "text", "text2", "fixedValue"]},
				{"id":"inFolder", "text":"Input Folder Path", "size":"50"},
				{"id":"inFile", "text":"Input File Name", "size":"30"},
				{"id":"outFolder", "text":"Output Folder Path", "size":"50"},
				{"id":"outFile", "text":"Output File Name", "size":"30"}
			],
			"rows":[
				{
					"id":"PLANT_STK_ORG_TBL"
					, "inCols":[{"id":"PLANT", "text":"ERP PLANT", "idx":0, "text2":"ERP PLANT"}, {"id":"PDM_LOCATION", "text":"PDM 拠点情報", "idx":1, "text2":"PDM 거점정보"}, {"id":"STK_ORG", "text":"在庫組織コード", "idx":2, "text2":"재고조직"}]
					, "_rowIdx":0
					, "inFile":""
					, "file":"PLANT_STK_ORG_TBL"
					, "inFolder":""
					, "_srcRowId":0
				}
				,{
					"id":"PLANT_ITEM_TBL"
					, "inCols":[{"id":"ITEM", "text":"品目", "idx":0, "text2":"품목"}, {"id":"PLANT_LOCATION", "text":"生産拠点", "idx":1, "text2":"생산거점"}]
					, "_rowIdx":1
					, "inFile":""
					, "file":"PLANT_ITEM_TBL"
					, "inFolder":""
					, "_srcRowId":1
				}
				,{
					"id":"PLANT_LOCATION"
					, "inCols":[{"id":"ITEM_TYPE1", "text":"品目1桁目 コード値", "idx":0, "text2":"품목 첫번째 자릿수 코드 값"}, {"id":"ITEM_TYPE2", "text":"品目コード 判断対象桁数", "idx":1, "text2":"품목코드 판별대상 자릿수 "}, {"id":"ITEM_TYPE3", "text":"コード値", "idx":2, "text2":"코드 값"}, {"id":"LOCATION_TYPE", "text":"生産拠点位置", "idx":3, "text2":"생산거점위치"}]
					, "_rowIdx":2
					, "inFile":""
					, "file":"PLANT_LOCATION"
					, "inFolder":""
					, "_srcRowId":2
				}
				,{
					"id":"PLANT_TARGET"
					, "outFolder":""
					, "inCols":[]
					, "_rowIdx":3
					, "inFile":""
					, "file":"PLANT_TARGET"
					, "inFolder":""
					, "_srcRowId":3
					, "outFile":""
				}

			]
		}
	}
}"""

    val ep = GenFromJSON12.gen(json12Str, Some("UTF-8"))
    val parameters = ep.parameters
    val dataset = ep.getDataset("1")

    println(dataset.map { ds => ds.setMkString(JSON12MkString) })
    println(ep.toString(JSON12MkString))
  }
}