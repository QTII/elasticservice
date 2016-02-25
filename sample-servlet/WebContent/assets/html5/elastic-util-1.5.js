/*! elastic-util 1.5.1 | QTI International, CO. LTD. */

//--- ElasticParams Beginning ---

function elastic_baseUrl() {
	return "/elastic";
}

function elastic_project_name() {
	return "elasticservice-0.1";
}

//--- AJAX functions Beginning ---
function elastic_ajax_jsonp(serverURL, ep, responseHandler, errorHandler) {
	elastic_ajax(serverURL, ep, responseHandler, errorHandler, true);
}

function elastic_ajax(serverURL, ep, responseHandler, errorHandler, isJSONP) {
	if (isJSONP) {
		ep.setParameter("epType", "jsonp13");
		ep.setParameter("service.resType", "jsonp13");
	} else {
		ep.setParameter("epType", "json13");
	}
	
	var reqDataStr = ep.toString();
	if (elastic_isTraceEnabled())
		console.log(elastic_timeTrace() + "request: " + reqDataStr);
	else if (elastic_isDebugEnabled())
		console.log(elastic_timeTrace() + "request: "
				+ elastic_getByteCountStr(reqDataStr) + "bytes");
			
	var ajaxParam = {
		url : !elastic_isEmpty(serverURL)? serverURL: elastic_baseUrl(), // 요청 주소
		type : isJSONP? "GET": "POST", // 요청 타입(post, get)
		data : reqDataStr, // 요청 데이터
		success : function(resData) { // 응답이 성공하면 호출되는 함수
			var resDataStr = null;
			if (elastic_isDebugEnabled() && resData.constructor == Object)
				resDataStr = JSON.stringify(resData);
			if (elastic_isTraceEnabled())
				console.log(elastic_timeTrace() + "response: " + resDataStr);
			else if (elastic_isDebugEnabled())
				console.log(elastic_timeTrace() + "response: " + elastic_getByteCountStr(resDataStr) + "bytes");
			if (!elastic_isEmpty(responseHandler)) {
				var ep = new ElasticParams(resData);
				if (ep.getCode() == 0) {
					responseHandler(ep);
				} else {
					if (!elastic_isEmpty(errorHandler)) errorHandler(ep);
					else alert("ERROR-" + ep.getCode() + ": " + ep.getMessage());
				}
			}
		},
		error: function(jqXHR, error, errorThrown) { // 응답이 실패하면 호출되는 함수
			if(jqXHR.status && jqXHR.status == 400) {
				try {
					console.log(elastic_timeTrace() + "response: " + jQuery.parseJSON(jqXHR.responseText).Message);
				} catch (e) {
					console.log(elastic_timeTrace() + "response: " + jqXHR.responseText); 
				}
          } else {
				try {
					console.log(elastic_timeTrace() + "response: " + jQuery.parseJSON(jqXHR.responseText).Message);
				} catch (e) {
					console.log(elastic_timeTrace() + "response: " + jqXHR.responseText); 
				}
          }
		}
	};
	if (isJSONP) {
		ajaxParam["dataType"] = "jsonp"; // 응답 데이터 타입(text, html, xml, json)
		ajaxParam["jsonp"] = "_jsonpKey_"; 
	} else {
		ajaxParam["contentType"] = "application/json"; // 요청 Content-Type
		var resType = ep.getParameter("service.resType");
		if (resType != undefined && resType.toLowerCase().startsWith("json")) {
			ajaxParam["dataType"] = "json"; // 응답 데이터 타입(text, html, xml, json)
		} else if (resType == "PlatformXml") {
			ajaxParam["dataType"] = "xml"; // 응답 데이터 타입(text, html, xml, json)
		}
	}
	if (elastic_isTraceEnabled())
		console.log(elastic_timeTrace() + "ajaxParam: " + JSON.stringify(ajaxParam));
	
	$.ajax(ajaxParam);
}
//--- AJAX functions End ---

function ElasticParams(datasrc) {
	if (datasrc == undefined || datasrc == null) {
		this.initJson();
	} else if (datasrc.constructor == Object || datasrc.constructor == XMLDocument) {
		this.datasrc = datasrc;
	} else if ($(datasrc).is("form")) {
		this.initJson();
		this.applyForm(datasrc);
	}
}

ElasticParams.prototype = {
	initJson: function() {
		this.datasrc = {
				"parameters" : {},
				"datasets" : []
		};
		this.datasrc.parameters["epType"] = "json13";
	},
	applyForm: function(form) {
		var formData = $(form).serializeArray()
		for (i = 0; i < formData.length; i++) {
			var name = formData[i]["name"];
			var value = formData[i]["value"];
			if(/^S[1-9]\d*COL_[a-zA-Z][0-9a-zA-Z]*$/.test(name)) {
				var dsName = name.match(/[1-9]\d*/)[0];
				var colName = name.substring(dsName.length + 5);
				this.setRowColumn(dsName, colName, value)
			} else {
				this.setParameter(name, value)
			}
		}
	},
	setParameters: function(parameters) {
		this.datasrc.parameters = parameters;
	},
	createDataset: function(dsName) {
		var dataset = undefined;
		if (this.datasrc.parameters.epType.toLowerCase() == "json12") {
			dataset = {colInfos:[], rows:[]}
			this.datasrc.datasets[dsName] = dataset
		} else if (this.datasrc.parameters.epType.toLowerCase() == "json13") {
			dataset = {name:dsName, colInfos:[], rows:[]}
			this.datasrc.datasets.push(dataset);
		}
		return dataset;
	},
	setCode: function(code) {
		this.setParameter("code", code);
	},
	setMessage: function(message) {
		this.setParameter("message", message);
	},
	getCode: function() {
		var code = this.getParameter("code");
		if (code == undefined || code == null)
			return 0;
		try {
			return parseInt(code);
		} catch (e) {
			return code;
		}
	},
	getMessage: function() {
		return this.getParameter("message");
	},
	setParameter: function(key, value) {
		this.datasrc.parameters[key] = value;
	},
	addParameters: function(parameters) {
		for (var key in parameters) {
			this.setParameter(key, parameters[key]);
		}
	},
	setRows: function(dsName, array) {
		var dataset = this.getDataset(dsName);
		if (dataset == undefined || dataset == null) {
			dataset = this.createDataset(dsName);
		}
		if (array == undefined || array == null) {
		} else if (array.constructor == Array) {
			dataset.rows = array;
		} else if (array.constructor == Object) {
			dataset.rows = [ array ];
		}
	},
	addRow: function(dsName, map) {
		var dataset = this.getDatasetOrCreate(dsName);
		dataset.rows.push(map);
	},
	setRowColumn: function(dsName, colName, value) {
		var dataset = this.getDatasetOrCreate(dsName);
		if (dataset.rows.length == 0) {
			dataset.rows.push({});
		}
		dataset.rows[0][colName] = value;
	},
	getParameter: function(paramName) {
		if (this.datasrc != undefined) 
			return this.datasrc.parameters[paramName];
		else 
			return undefined;
	},
	getDataset: function(dsName) {
		if (this.datasrc.parameters.epType.toLowerCase() == "json12") {
			return this.datasrc.datasets[dsName];
		} else if (this.datasrc.parameters.epType.toLowerCase() == "json13") {
			for (var i in this.datasrc.datasets) {
				if(this.datasrc.datasets[i].name == dsName)
					return this.datasrc.datasets[i];
			}
		}
		return undefined;
	},
	getDatasetOrCreate: function(dsName) {
		var dataset = this.getDataset(dsName);
		if (elastic_isEmpty(dataset))
			dataset = this.createDataset(dsName);
		return dataset;
	},
	getColumnInfosByName: function(dsName) {
		var ds = this.getDataset(dsName);
		if (elastic_isEmpty(ds))
			return undefined;
		return ds.colInfos;
	},
	getColumnInfos: function(dataset) {
		if (elastic_isEmpty(dataset))
			return this.getColumnInfosByName();
		return dataset.colInfos;
	},
	getColumnInfo: function(dsName, colIdx) {
		var colInfos = this.getColumnInfosByName(dsName);
		return colInfos[colIdx];
	},
	getColumnInfoByName: function(dsName, colId) {
		var colInfos = this.getColumnInfosByName(dsName);
		for (var c = 0; c < colInfos.length; c++) {
			if (colInfos[c].id == colId)
				return colInfos[c];
		}
		return undefined;
	},
	getColumnId: function(dsName, colIdx) {
		var colInfo = this.getColumnInfo(dsName, colIdx);
		return colInfo.id;
	},
	getRowsByName: function(dsName) {
		var ds = this.getDataset(dsName);
		if (elastic_isEmpty(ds))
			return undefined;
		return ds.rows;
	},
	getRows: function(dataset) {
		if (elastic_isEmpty(dataset))
			return this.getRowsByName();
		return dataset.rows;
	},
	getRow: function(dsName, srcRowId) {
		var rows = this.getRowsByName(dsName);
		return rows[srcRowId];
	},
	insertRow: function(dsName, map, idx) {
		var _idx = parseInt(idx);
		var rows = this.getRowsByName(dsName);
		var newArray = insertArrayEntry(rows, map, _idx);
		if (newArray == rows) {
			return false;
		} else {
			this.setRows(dsName, newArray);
			return true;
		}
	},
	appendRow: function(dsName, map, idx) {
		var _idx = parseInt(idx);
		var rows = this.getRowsByName(dsName);
		var newArray = appendArrayEntry(rows, map, _idx);
		if (newArray == rows) {
			return false;
		} else {
			this.setRows(dsName, newArray);
			return true;
		}
	},
	moveUpRow: function(dsName, idx) {
		var _idx = parseInt(idx);
		var rows = this.getRowsByName(dsName);
		var newArray = moveUpArrayEntry(rows, _idx);
		if (newArray == rows) {
			return false;
		} else {
			this.setRows(dsName, newArray);
			return true;
		}
	},
	moveDownRow: function(dsName, idx) {
		var _idx = parseInt(idx);
		var rows = this.getRowsByName(dsName);
		var newArray = moveDownArrayEntry(rows, _idx);
		if (newArray == rows) {
			return false;
		} else {
			this.setRows(dsName, newArray);
			return true;
		}
	},
	deleteRow: function(dsName, idx) {
		var _idx = parseInt(idx);
		var rows = this.getRowsByName(dsName);
		var newArray = deleteArrayEntry(rows, _idx);
		if (newArray == rows) {
			return false;
		} else {
			this.setRows(dsName, newArray);
			return true;
		}
	},
	setColumnValue: function(dsName, xpath, value) {
		var target = this.getRowsByName(dsName);
		var tokens = xpath.charAt(0) == '/' ? xpath.substring(1).split("/") : xpath
				.split("/");
		for (var i = 0; i < tokens.length; i++) {
			if (i < tokens.length - 1)
				target = target[tokens[i]];
			else {
				var id = undefined;
				var type = undefined;
				if (i == 1) {
					id = tokens[i];
					var colInfo = this.getColumnInfoByName(undefined, tokens[i]);
					if (!elastic_isEmpty(colInfo) &&  !elastic_isEmpty(colInfo.type))
						type = colInfo.type;
					else 
						type = "String";
				} else if (i == 3) {
					id = this.parseColumnId(tokens[i]);
					type = this.parseColumnType(tokens[i]);
				}
				if (elastic_isTraceEnabled())
					console.log("id:" + id + ", type:" + type);
				type = type.toUpperCase();
				if (type == "STRING") {
					target[id] = value;
				} else if (type == "INT" || type == "INTEGER"){
					target[id] = parseInt(value);
				} else if (type == "BOOLEAN"){
					target[id] = (value.toUpperCase() == "TRUE" || value.toUpperCase() == "YES");
				}
				return true;
			}
		}
		return false;
	},
	getColumnValue: function(dsName, xpath) {
		var target = this.getRowsByName(dsName);
		var tokens = xpath.charAt(0) == '/' ? xpath.substring(1).split("/") : xpath
				.split("/");
		for (var i = 0; i < tokens.length; i++) {
			if (i < tokens.length - 1)
				target = target[tokens[i]];
			else
				return target[tokens[i]];
		}
		return undefined;
	},
	parseColumnId: function(columnExpression) {
		var id = undefined;
		if (columnExpression.charAt(0) == '$') {
			columnExpression = columnExpression.substring(1);
		}
		var idx = columnExpression.indexOf(":");
		if (idx > 0) {
			id = columnExpression.substring(0, idx);
		} else {
			id = columnExpression;
		}
		return id;
	},
	parseColumnType: function(columnExpression) {
		var type = undefined;
		if (columnExpression.charAt(0) == '$') {
			columnExpression = columnExpression.substring(1);
		}
		var idx = columnExpression.indexOf(":");
		if (idx > 0) {
			type = columnExpression.substring(idx+1);
		} else {
			type = "String";
		}
		return type;
	},
	toString: function() {
		if (this.datasrc == undefined || this.datasrc == null) {
			return "";
		} else if (this.datasrc.constructor == Object) {
			return JSON.stringify(this.datasrc);
		} else if (this.datasrc.constructor == XMLDocument) {
			return xml2Str(this.datasrc)
		} else {
			return this.datasrc.toString();
		}
	}
};
//--- ElasticParams End ---

function xml2Str(xmlNode) {
   try {
      // Gecko- and Webkit-based browsers (Firefox, Chrome), Opera.
      return (new XMLSerializer()).serializeToString(xmlNode);
  }
  catch (e) {
     try {
        // Internet Explorer.
        return xmlNode.xml;
     }
     catch (e) {  
        //Other browsers without XML Serializer
        alert('Xmlserializer not supported');
     }
   }
   return false;
}

//--- ElasticQueueMgr Beginning ---
function ElasticQueueMgr() {
	this._queues = {};
}
ElasticQueueMgr.prototype = {
	getQueue: function(queueName) {
		var queue = this._queues[queueName];
		if (elastic_isEmpty(queue)) {
			queue = [];
			this._queues[queueName] = queue;
		}
		return queue;
	},
	clearQueue: function(queueName) {
		this._queues[queueName] = [];
	}
};
//--- ElasticQueueMgr End ---

function insertArrayEntry(array, newEntry, idx) {
	if (array == undefined || array == null) {
		array = [];
	}
	if (idx == undefined || idx < 0)
		idx = 0;
	if (idx > array.length)
		idx = array.length;

	var _idx = parseInt(idx);
	var changed = false;
	var newArr = [];
	for (var i = 0; i < array.length; i++) {
		if (i == _idx) {
			newArr.push(newEntry);
			changed = true;
		}
		newArr.push(array[i]);
	}
	if (!changed)
		newArr.push(newEntry);
	return newArr;
}

function appendArrayEntry(array, newEntry, idx) {
	if (array == undefined || array == null) {
		array = [];
	}
	if (idx < 0)
		idx = 0;
	if (idx == undefined || idx > array.length)
		idx = array.length;

	var _idx = parseInt(idx);
	var changed = false;
	var newArr = [];
	for (var i = 0; i < array.length; i++) {
		if (i == _idx + 1) {
			newArr.push(newEntry);
			changed = true;
		}
		newArr.push(array[i]);
	}
	if (!changed)
		newArr.push(newEntry);
	return newArr;
}

function moveUpArrayEntry(array, idx) {
	if (array == undefined || array == null || idx == undefined)
		return array;
	if (idx <= 0)
		return array;
	if (idx >= array.length)
		return array;

	var _idx = parseInt(idx);
	var newArray = [];
	for (var i = 0; i < array.length; i++) {
		if (i == (_idx - 1)) {
			newArray.push(array[_idx]);
			newArray.push(array[i]);
			i++;
		} else {
			newArray.push(array[i]);
		}
	}
	return newArray;
}

function moveDownArrayEntry(array, idx) {
	if (array == undefined || array == null || idx == undefined)
		return array;
	if (idx < 0)
		return array;
	if (idx >= array.length - 1)
		return array;

	var _idx = parseInt(idx);
	var newArray = [];
	for (var i = 0; i < array.length; i++) {
		if (i == _idx) {
			var nextIdx = _idx + 1;
			newArray.push(array[nextIdx]);
			newArray.push(array[_idx]);
			i++;
		} else {
			newArray.push(array[i]);
		}
	}
	return newArray;
}

function deleteArrayEntry(array, idx) {
	if (array == undefined || array == null || idx == undefined)
		return array;
	if (idx < 0 || idx >= array.length)
		return array;

	var newArray = [];
	for (var a = 0; a < array.length; a++) {
		if (a != idx) {
			newArray.push(array[a]);
		}
	}
	return newArray;
}

//loadScript("../html5/elastic-init-1.4.3.jsp");
function loadScript(url, callback) {
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    if (callback != undefined) {
    	script.onreadystatechange = callback;
    	script.onload = callback;
    }

    head.appendChild(script);
}

function HBox(tableStyle, tdStyle) {
	this._defaultTableStyle = "border-collapse: collapse; box-shadow: none; border: 0px; background: none;";
	this.tableStyle = elastic_isEmpty(tableStyle) ? this._defaultTableStyle
			: tableStyle;

	this._defaultTdStyle = "box-shadow: none; border: 0px; background: none;";
	this.tdStyle = elastic_isEmpty(tdStyle) ? this._defaultTdStyle : tdStyle;

	this.table = document.createElement("table");
	this.table.setAttribute("style", this.tableStyle);

	this.tr = document.createElement("tr");
	this.table.appendChild(this.tr);
}

HBox.prototype.add = function(domElement) {
	if (domElement == undefined || domElement == null)
		return;
	var td = document.createElement("td");
	td.setAttribute("style", this.tdStyle);

	if (domElement.constructor == HBox
			|| domElement.constructor == VBox) {
		td.appendChild(domElement.table);
	} else if (domElement.constructor == String
			|| domElement.constructor == Boolean
			|| domElement.constructor == Number) {
		td.innerHTML = domElement;
	} else {
		try {
			td.appendChild(domElement);
		} catch (e) {
			if (domElement.outerHTML) {
				td.innerHTML += domElement.outerHTML;
			} else {
				console.log(e);
			}
		}
	}
	this.tr.appendChild(td);
};

function VBox(tableStyle, tdStyle) {
	this._defaultTableStyle = "border-collapse: collapse; box-shadow: none; border: 0px; background: none;";
	this.tableStyle = elastic_isEmpty(tableStyle) ? this._defaultTableStyle
			: tableStyle;

	this._defaultTdStyle = "box-shadow: none; border: 0px; background: none;";
	this.tdStyle = elastic_isEmpty(tdStyle) ? this._defaultTdStyle : tdStyle;

	this.table = document.createElement("table");
	this.table.setAttribute("style", this.tableStyle);
}

VBox.prototype.add = function(domElement) {
	if (domElement == undefined || domElement == null)
		return;
	var tr = document.createElement("tr");
	this.table.appendChild(tr);

	var td = document.createElement("td");
	td.setAttribute("style", this.tdStyle);

	if (domElement.constructor == HBox
			|| domElement.constructor == VBox) {
		td.appendChild(domElement.table);
	} else if (domElement.constructor == String
			|| domElement.constructor == Boolean
			|| domElement.constructor == Number) {
		td.innerHTML = domElement;
	} else {
		try {
			td.appendChild(domElement);
		} catch (e) {
			if (domElement.outerHTML) {
				td.innerHTML += domElement.outerHTML;
			} else {
				console.log(e);
			}
		}
	}
	tr.appendChild(td);
};

function elastic_htmlToString(node) {
	clone = node.cloneNode(true);
	var tmp = document.createElement("div");
	tmp.appendChild(clone);
	return tmp.innerHTML;
}

// 0: None, 1: Debug, 2: Trace
var LOG_NONE = 0;
var LOG_DEBUG = 1;
var LOG_TRACE = 2;
var logLevel = LOG_TRACE;
var beginTime = 0;

function elastic_msToTime(s) {
	function addZ(n) {
		return (n < 10 ? '0' : '') + n;
	}

	var ms = s % 1000;
	s = (s - ms) / 1000;
	var secs = s % 60;
	s = (s - secs) / 60;
	var mins = s % 60;
	var hrs = (s - mins) / 60;

	if (ms < 10) {
		ms = "00" + ms;
	} else if (ms < 100) {
		ms = "0" + ms;
	}

	return addZ(hrs) + ':' + addZ(mins) + ':' + addZ(secs) + ',' + ms;
}

function elastic_timeTrace() {
	return elastic_getTimeStr() + ": elapsed="
			+ elastic_getElapsedTimeAndSetBeginTime() + ": ";
}

function elastic_getElapsedTimeAndSetBeginTime() {
	var curr = new Date().getTime();
	if (beginTime == 0)
		beginTime = curr;
	var elapsed = curr - beginTime;
	beginTime = curr;
	return elastic_msToTime(elapsed);
}

function elastic_getTimeStr() {
	var currentTime = new Date();
	var hours = currentTime.getHours();
	var minutes = currentTime.getMinutes();
	var seconds = currentTime.getSeconds();
	var ms = currentTime.getMilliseconds();
	if (minutes < 10) {
		minutes = "0" + minutes;
	}
	if (seconds < 10) {
		seconds = "0" + seconds;
	}
	if (ms < 10) {
		ms = "00" + ms;
	} else if (ms < 100) {
		ms = "0" + ms;
	}
	var v = hours + ":" + minutes + ":" + seconds + "," + ms;
	if (hours > 11) {
		v += " PM";
	} else {
		v += " AM";
	}
	return v;
}

function elastic_getDateStr(timeMillis) {
	var time = new Date(timeMillis);
	var yyyy = time.getFullYear();
	var mm = time.getMonth()+1;
	var dd = time.getDate();
	time.get
	
	var hours = time.getHours();
	var minutes = time.getMinutes();
	var seconds = time.getSeconds();
	var ms = time.getMilliseconds();
	if (minutes < 10) {
		minutes = "0" + minutes;
	}
	if (seconds < 10) {
		seconds = "0" + seconds;
	}
	if (ms < 10) {
		ms = "00" + ms;
	} else if (ms < 100) {
		ms = "0" + ms;
	}
	var v = yyyy + "/" + mm + "/" + dd;
	v += " " + hours + ":" + minutes;
	return v;
}

function elastic_getDateStr_MMDDHHMM(timeMillis) {
	var time = new Date(timeMillis);
	var mm = time.getMonth()+1;
	var dd = time.getDate();
	
	var hours = time.getHours();
	var minutes = time.getMinutes();
	var seconds = time.getSeconds();
	var ms = time.getMilliseconds();
	if (minutes < 10) {
		minutes = "0" + minutes;
	}
	if (seconds < 10) {
		seconds = "0" + seconds;
	}
	if (ms < 10) {
		ms = "00" + ms;
	} else if (ms < 100) {
		ms = "0" + ms;
	}
	var v = mm + "/" + dd;
	v += " " + hours + ":" + minutes;
	return v;
}

function elastic_setDebug() {
	logLevel = LOG_DEBUG;
}

function elastic_setTrace() {
	logLevel = LOG_TRACE;
}

function elastic_isDebugEnabled() {
	return logLevel >= LOG_DEBUG;
}

function elastic_isTraceEnabled() {
	return logLevel >= LOG_TRACE;
}

/**
 * Count bytes in a string's UTF-8 representation.
 * 
 * @param string
 * @return Number
 */
function elastic_getByteCount(normal_val) {
	// Force string type
	normal_val = String(normal_val);

	var byteLen = 0;
	for (var i = 0; i < normal_val.length; i++) {
		var c = normal_val.charCodeAt(i);
		byteLen += c < (1 << 7) ? 1 : c < (1 << 11) ? 2 : c < (1 << 16) ? 3
				: c < (1 << 21) ? 4 : c < (1 << 26) ? 5 : c < (1 << 31) ? 6
						: Number.NaN;
	}
	return byteLen;
}

function elastic_getByteCountStr(normal_val) {
	var n = elastic_getByteCount(normal_val);
	return n.toLocaleString();
}

// function elastic_getByteCount2(s) {
// var count = 0, stringLength = s.length, i;
// s = String( s || "" );
// for( i = 0 ; i < stringLength ; i++ )
// {
// var partCount = encodeURI( s[i] ).split("%").length;
// count += partCount==1?1:partCount-1;
// }
// return count;
// }

function elastic_go(urlParams) {
	document.location.href = elastic_baseUrl() + "?" + urlParams;
}

function elastic_isEmpty(obj) {
	return obj == undefined || obj == null
			|| (String == obj.constructor && obj == '');
}

function elastic_size(obj) {
	var size = 0;
	for ( var key in obj) {
		if (obj.hasOwnProperty(key))
			size++;
	}
	return size;
}

function elastic_getCurrentFileName() {
	var url = window.location.pathname;
	return url.substring(url.lastIndexOf('/') + 1);
}

function elastic_setAllDivsVisible(visible) {
	var divs = document.getElementsByTagName("div");
	for (var i = 0; i < divs.length; i++) {
		if (visible)
			divs[i].style.visibility = 'visible';
		else
			divs[i].style.visibility = 'hidden';
	}
}

function loadScript(url, callback) {
	// Adding the script tag to the head as suggested before
	var head = document.getElementsByTagName('head')[0];
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = url;

	if (!elastic_isEmpty(callback)) {
		// Then bind the event to the callback function.
		// There are several events for cross browser compatibility.
		script.onreadystatechange = callback;
		script.onload = callback;
	}

	// Fire the loading
	head.appendChild(script);
}

function elastic_startsWith(str, prefix) {
    return str.indexOf(prefix) === 0;
}

function elastic_endsWith(str, suffix) {
    return str.match(suffix+"$")==suffix;
}

function type_test() {
	var obj = [ "a", "b", "c" ];
	// Array, Object, Number, Boolean
	console.log(getTypeName(obj));
}

var HashMap = function(){
    this.map = new Object();
};
 
HashMap.prototype = {
    /* key, value 값으로 구성된 데이터를 추가 */
    put: function (key, value) {
        this.map[key] = value;
    },
    /* 지정한 key값의 value값 반환 */
    get: function (key) {
        return this.map[key];
    },
    /* 구성된 key 값 존재여부 반환 */
    containsKey: function (key) {
        return key in this.map;
    },
    /* 구성된 value 값 존재여부 반환 */
    containsValue: function (value) {
        for (var prop in this.map) {
            if (this.map[prop] == value) {
                return true;
            }
        }
        return false;
    },
    /* 구성된 데이터 초기화 */
    clear: function () {
        for (var prop in this.map) {
            delete this.map[prop];
        }
    },
    /*  key에 해당하는 데이터 삭제 */
    remove: function (key) {
        delete this.map[key];
    },
    /* 배열로 key 반환 */
    keys: function () {
        var arKey = new Array();
        for (var prop in this.map) {
            arKey.push(prop);
        }
        return arKey;
    },
    /* 배열로 value 반환 */
    values: function () {
        var arVal = new Array();
        for (var prop in this.map) {
            arVal.push(this.map[prop]);
        }
        return arVal;
    },
    /* 순번 value 반환 */
    getPos: function (num) {
        var arPos = new Array();
        for (var prop in this.map) {
        	arPos.push(this.map[prop]);
        }
        return arPos[num];
    },    
    /* Map에 구성된 개수 반환 */
    size: function () {
        var count = 0;
        for (var prop in this.map) {
            count++;
        }
        return count;
    }
};

function multiLineHtmlEncode(value) {
    var lines = value.split(/\r\n|\r|\n/);
    for (var i = 0; i < lines.length; i++) {
        lines[i] = htmlEncode(lines[i]);
    }
    return lines.join('\r\n');
}

function htmlEncode(value) {
    return $('<div/>').text(value).html();
} 

function replaceAll(str, find, replace) {
	  return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

function escapeRegExp(str) {
    return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}
