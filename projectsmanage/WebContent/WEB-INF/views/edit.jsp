<%@ page import="com.emg.poiwebeditor.common.SrcTypeEnum"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>制作</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css" rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href='http://static.emapgo.cn/webjs-sdk/css/emapgo-1.0.0.css' rel='stylesheet' />
<link href="resources/js/leaflet/leaflet.css" rel="stylesheet" />
<link rel="stylesheet" href="http://code.ionicframework.com/ionicons/1.5.2/css/ionicons.min.css">
<link href="resources/leaflet.awesome-markers-2.0/leaflet.awesome-markers.css" rel="stylesheet">
<link href="resources/mapbox/mapbox-gl.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/js/enumJS.js"></script>
<script src="resources/js/coordtransform.js"></script>
<script src="resources/js/common.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src='http://static.emapgo.cn/webjs-sdk/js/emapgo-1.0.0.js'></script>
<script src="resources/js/leaflet/leaflet.js"></script>
<script src="resources/js/leaflet.ChineseTmsProviders.js"></script>
<script src="resources/js/proj4-compressed.js"></script>
<script src="resources/js/proj4leaflet.js"></script>
<script src="resources/js/tileLayer.baidu.js" ></script >
<script src="resources/leaflet.awesome-markers-2.0/leaflet.awesome-markers.min.js"></script>
<script src="https://unpkg.com/leaflet.vectorgrid@latest/dist/Leaflet.VectorGrid.js"></script>
<script src="resources/mapbox/mapbox-gl.js" ></script >
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=C1D7k5in8hXWy6njGuPbDXKEksGzUro1"></script>
<script src='resources/dtree/dtree.js'></script>
<script src='resources/js/featcodeget.js'></script>
<script src="resources/js/featcoderegex.js"></script>
<script type="text/javascript">
	var $emgmap = null, $baidumap = null, $gaodemap = null, $tengxunmap = null;
	var $emgmarker = null, $baidumarker = null, $gaodemarker = null, $tengxunmarker = null;
	var $emgmarkerBase = null, $baidumarkerBase = null, $gaodemarkerBase = null, $tengxunmarkerBase = null;
	var dianpingGeo;
	var keywordid = eval('(${keywordid})');
	var keyword = null;
	var systemPoi = null;
	var systemOid = -1; // 当前编辑器左侧有OID
	// databaseSaveRelation: 用来存储数据库中保存着的relation 关系, originalCheckRelation: 数据库中存在emg和点评的关系，但该点在现在EMG中没有，单独记录，用来做提示框条件， currentCheckRelation： 数据库中有，且已经被选中的
	var databaseSaveRelation = [], originalCheckRelation = [], currentCheckRelation = [];
	var source = [];
	var zoom = 17;
	
	var loaderr = "<span class='red'>加载失败</span>";
	var redMarker = L.AwesomeMarkers.icon({
	    	icon: 'tag',
	    	markerColor: 'blue',
	    	iconColor:'white'
	  	});
	var editMarker = L.AwesomeMarkers.icon({
    	icon: 'edit',
    	markerColor: 'red',
    	iconColor:'white'
  	});
	var referMarker = L.AwesomeMarkers.icon({
    	icon: 'eye-open',
    	markerColor: 'blue',
    	iconColor:'white'
  	});
	var matchMarker = L.AwesomeMarkers.icon({
    	icon: 'tag',
    	markerColor: 'blue',
    	iconColor:'white'
  	});
	
	
	$(document).ready(function() {
		$.webeditor.getHead();
		
		if (keywordid && keywordid > 0) {
			//console.log("开始加载数据: " + Date.now());
			$.when( 
				loadKeyword(keywordid)
			).done(function() {
				$.when(loadReferdatas(keywordid)
				).done(function() {
					if (keyword) {
						loadRelation(keyword.srcInnerId, keyword.srcType);
					}
				});
			});
			/* $.when( 
				loadKeyword(keywordid),loadReferdatas(keywordid)
			).done(function() {
				if (keyword) {
					loadRelation(keyword.srcInnerId, keyword.srcType);
				}
			}); */
			//console.log("结束加载数据: " + Date.now());
			
		}else {
			$.webeditor.showMsgLabel("alert", "没有获取到参考资料");
		}
		$('#featcode').change(function(){
			setfeatcodename();
			
			$('#sortcode').val("");
			$('#sortcodename').val("");
			
			var featcode = $('#featcode').val();
			var sortcodes = getsortcode(featcode);
			if (sortcodes && sortcodes.length > 0) {
				$('#sortcode').removeAttr("disabled");
			} else {
				$('#sortcode').attr("disabled", "disabled");
			}
		});
		
		$('sortcode').change(function(){
			setsortcodename();
		});
	});
	
	function setfeatcodename(){
		$('#featcodename').val("");
		var featcode = $('#featcode').val();
		var featcodename = getfeatcodename(featcode);
		$('#featcodename').val(featcodename);
	}
	
	function setsortcodename(){
		$('#sortcodename').val("");
		var sortcode = $('#sortcode').val();
		var sortcodename = getsortcodename(sortcode);
		$('#sortcodename').val(sortcodename);
	}
	
	function setFeatcode(){
		var band2 = $("#featcodePOI").data("band2");
		if (!band2) return;
		
		var nulStr = "";
		var name = d.aNodes[d.selectedNode].name;
		var featcode = getfeatcodebyname(name);
		if (band2 < 0) {
			$('#featcode').val(featcode);
			$('#featcodename').val(name);
			
			$('#sortcode').val(nulStr);
			$('#sortcodename').val(nulStr);
		} else {
			$("tr.trIndex" + band2 + ":eq(3) td input").val(featcode);
			$("tr.trIndex" + band2 + ":eq(4) td input").val(name);
			
			$("tr.trIndex" + band2 + ":eq(5) td input").val(nulStr);
			$("tr.trIndex" + band2 + ":eq(6) td input").val(nulStr);
		}
		
		$("#featcodePOI").dialog("close");
		
		var sortcodes = getsortcode(featcode);
		if (sortcodes && sortcodes.length > 0) {
			$('#sortcode').removeAttr("disabled");
		} else {
			$('#sortcode').attr("disabled", "disabled");
		}
	}

	function setSortcode(){
		var band2 = $("#sortcodePOI").data("band2");
		if (!band2) return;
		
		var sname = "";
		var ssort = "";
		var sorts = $("#sortcodePOI tbody>tr>td>div>label>input");
		for( var i = 0 ;i < sorts.length;i++){
			if(sorts[i].checked == true){
				var s1 = sorts[i].name;
				var s2 = sorts[i].value;
				if( sname == ""){
					sname = s1;
					ssort = s2;
				}
				else{
					sname +=";";
					sname +=s1;
					ssort +=";";
					ssort +=s2;
				}
			}
		}
		
		if (band2 < 0) {
			$('#sortcode').val(ssort);
			$('#sortcodename').val(sname);
		} else {
			$("tr.trIndex" + band2 + ":eq(5) td input").val(ssort);
			$("tr.trIndex" + band2 + ":eq(6) td input").val(sname);
		}
		
		$("#sortcodePOI").dialog("close");
	}
	
	function dlgFeatcodePOIConfig(band2) {
		$("#featcodePOI").data("band2", band2);
		$("#featcodePOI").dialog({
			modal : true,
			closeOnEscape : false,
			resizable : false,
			width : 320,
			heigth: 500,
			position: { at: "right" },
			title : "设置featecode",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function() {
			},
			buttons : [ {
				text : "确定",
				'class' : "btn btn-default",
				click : setFeatcode
			}, {
				text : "取消",
				'class' : "btn btn-default",
				click : function() {
					$(this).dialog("close");
				}
			} ]
		});
	}
	
	function drawsortcheck(featcode){
		var sortcodes = getsortcode(featcode);
		var tbody = $("#sortcodePOI tbody");
		tbody.empty();
		var html = new Array();
		var len = sortcodes.length;
		if (len > 0) {
			for(var i = 0 ; i < len; i++){
				var sortcodename = sortcodes[i][0];
				var sortcode = sortcodes[i][1];
				html.push('<tr><td><div class="checkbox" style="margin-top: 2px; margin-bottom: 2px;"><label><input type="checkbox" name="'+sortcodename+'" value="'+sortcode+'">'+ sortcodename+'</label></div></td></tr>');
			}
		} else {
			html.push('无数据');
		}
		tbody.append(html.join(""));
	}
	
	function dlgSortcodeConfig(band2) {
		$("#sortcodePOI").data("band2", band2);
		
		if (band2 < 0) {
			var featcode=  $('#featcode').val();	
			drawsortcheck(featcode);
		} else {
			var featcode=  $("tr.trIndex" + band2 + ":eq(3) td input").val();
			drawsortcheck(featcode);
		}
		
		$("#sortcodePOI").dialog({
			modal : true,
			closeOnEscape : false,
			resizable : false,
			width : 320,
			heigth: 500,
			overflow: scroll,
			position: { at: "right" },
			title : "设置sortcode",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function() {
			},
			buttons : [ {
				text : "确定",
				'class' : "btn btn-default",
				click : setSortcode
			}, {
				text : "取消",
				'class' : "btn btn-default",
				click : function() {
					$(this).dialog("close");
				}
			} ]
		});
	}
	
	// 记录数据来源
	// txtValue:要复制的值
	// datasource: 要记录的数据源
	function changeName(txtValue, datasource, key) {
		var value = "";
		if ("namec" == key) {
			value = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
			$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val(txtValue);
			
		}else if("tel" == key) {
			value = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
			$("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val(txtValue);
			
		}
		var oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		var length = source.length;
		for (var i = 0; i < datasource.length; i++) {
			// source[length + i] = {srcType: datasource[i].srcType, srcInnerId: datasource[i].srcInnerId, oid: oid, k: key, oldValue: value, newValue: txtValue, flag: oid > 0 ? 1 : 2, keywordId:keyword.id};
			source[length + i] = {srcType: datasource[i].srcType, srcInnerId: datasource[i].srcInnerId, oid: oid, k: key,  newValue: txtValue, flag: oid > 0 ? 1 : 2, keywordId:keyword.id};
		}
		
	}
	//4个一样#7D7DFF， 
	function changeColor(viewArray, color) {
		for (var i = 0; i < viewArray.length; i++) {
			if (viewArray[i] == null )	continue;
			viewArray[i].style.backgroundColor = color;
		}
	}
	
	function getSRC(viewArray){
		var datasource = [];
		for (var i = 0; i < viewArray.length; i++) {
			if (viewArray[i] == null) continue;
			var obj = new Object();
			// var id = $(viewArray[i]).parents("table")[0].id;
			var id = $($(viewArray[i]).parents("table")[0]).attr("id");
			if (id != null && id.indexOf("Keyword") < 0 ) {
				var value = $($(viewArray[i]).parents("table")[0]).find("input:checkbox").val();
				obj.srcType = value.split(",")[2];
				obj.srcInnerId = value.split(",")[1];
				obj.newValue = value;
				datasource.push(obj);
				
			}else {
				datasource.push(keyword);
			}
			
		}
		return datasource;
	}
	
	Array.prototype.pushNoRepeat = function(){
	    for(var i=0; i<arguments.length; i++){
	      var ele = arguments[i];
	      if(this.indexOf(ele) == -1){
	          this.push(ele);
	      }
	  }
	};
	
	//比较名称
	function markNameColor(baiduName,gaodeName, tengxunName, dianpingName ) {
		var nameArray = [];
		if (baiduName != null && baiduName.length > 0) {
			nameArray.push(baiduName);
		}
		if (gaodeName != null && gaodeName.length >0) {
			nameArray.push(gaodeName);
		}
		if(tengxunName != null &&  tengxunName.length > 0) {
			nameArray.push(tengxunName);
		}
		if(dianpingName != null &&  dianpingName.length > 0) {
			nameArray.push(dianpingName);
		}
		var mark = [];
		for (var j = 0; j < nameArray.length - 1; j++) {
			var bname = nameArray[j][0].innerText;
			var i1, i2 ;
			for (var k = j + 1; k < nameArray.length; k++) {
				var gname = nameArray[k][0].innerText;
				if (bname == gname ) {
					i1 = j; i2 = k;
					mark.pushNoRepeat(nameArray[j][0]);
					mark.pushNoRepeat(nameArray[k][0]);
					// break;
				}
			}
			//为了避免出现百度和腾讯一样，高德和点评一样，却被误标为四个一样的情况
			if (mark.length == 2) {
				if (i1 == 0 && i2 == 3 && nameArray.length == 4 && nameArray[1][0].innerText ==  nameArray[2][0].innerText ) {
					mark = [];
					mark.pushNoRepeat(nameArray[1][0]);
					mark.pushNoRepeat(nameArray[2][0]);
				}
			}
			if (mark.length >= 2) break;
		}
		if (2 == mark.length) {
			changeColor(mark, "#66FF00");
		}else if(3 == mark.length) {
			changeColor(mark, "cyan");
		}else if(4 == mark.length) {
			changeColor(mark, "#7D7DFF");
		}
		return mark;
		
	}
	
	//比较电话
	function markTelColor(baiduTelArray,gaodeTelArray, tengxunTelArray, dianpingTelArray ) {
		var telArray = [];
		if (baiduTelArray != null && baiduTelArray.length > 0) {
			telArray.push(baiduTelArray);
		}
		if (gaodeTelArray != null && gaodeTelArray.length >0) {
			telArray.push(gaodeTelArray);
		}
		if(tengxunTelArray != null &&  tengxunTelArray.length > 0) {
			telArray.push(tengxunTelArray);
		}
		if(dianpingTelArray != null &&  dianpingTelArray.length > 0) {
			telArray.push(dianpingTelArray);
		}
		if (telArray == null || telArray.length == 0) return null;
		for(var i = 0; i< telArray.length - 1; i++) {
			for (var j = i + 1; j < telArray.length; j++) {
				if (telArray[i].length < telArray[j].length) {
					var temp = telArray[i];
					telArray[i] = telArray[j];
					telArray[j] = temp;
				}
			}
		}
		
		var telMarks = [];
		for (var i = 0 ; i < telArray.length - 1; i++) {
			for (var j = 0; j < telArray[i].length; j++) {
				var btel = getTel(telArray[i][j].innerText);
				btel = btel.replace(";","");
				for (var k = 0; k < telArray[i+1].length; k++) {
					var gtel = getTel(telArray[i+1][k].innerText);
					gtel = gtel.replace(";", "");
					if (btel != gtel)continue;
					
					if (telMarks == null || telMarks.length == 0) {
						var telMark = new Object();
						telMark.tel = gtel;
						telMark.array = [telArray[i][j], telArray[i+1][k]];
						telMarks.push(telMark);
					}else {
						var flag = false;
						for (var z = 0; z< telMarks.length; z++) {
							if (telMarks[z].tel == btel) {
								telMarks[z].array.pushNoRepeat(telArray[i][j]);
								telMarks[z].array.pushNoRepeat(telArray[i+1][k]);
								flag = true;
							}
						}
						if (!flag) {
							var telMark = new Object();
							telMark.tel = gtel;
							telMark.array = [telArray[i][j], telArray[i+1][k]];
							telMarks.push(telMark);
						}
					}
					//break;
				}
				
			}
		}
		
		
		for (var z = 0; z< telMarks.length; z++) {
			if (telMarks[z].array.length == 2) {
				changeColor(telMarks[z].array, "#66FF00");
			}else if (telMarks[z].array.length == 3) {
				changeColor(telMarks[z].array, "cyan");
			} else if (telMarks[z].array.length == 4) {
				changeColor(telMarks[z].array, "#7D7DFF");
			} 
		}
		
		
		return telMarks;
	}
	
	
	/* function initArray() {
		console.log("开始初始化颜色开始");
		console.log(Date.now());
		originalCheckRelation = [];
		currentCheckRelation = [];
		var tbtables = [ "tbbaidu", "tbtengxun", "tbgaode"];
		var baidu =null;
		var tengxun = null;
		var gaode = null;	
		if ( $("#tbbaidu input:checkbox") != null  || $("#tbbaidu input:checkbox").length > 0) {
			baidu = $("#tbbaidu input:checked")[0];			
		}
		if ( $("#tbtengxun input:checkbox") != null  || $("#tbtengxun input:checkbox").length > 0) {
			tengxun = $("#tbtengxun input:checked")[0];			
		}
		if ( $("#tbgaode input:checkbox") != null  || $("#tbgaode input:checkbox").length > 0) {
			gaode = $("#tbgaode input:checked")[0];			
		}
		
		var dianpingName = null, dianpingTel = null,baiduName = null,baiduTel = null,tengxunName = null, tengxunTel = null, gaodeName = null, gaodeTel = null;
		if (baidu != null ) {
			var baidutable = $("#tbbaidu" +baidu.value.split(",")[0]);
			baiduName = $(baidutable.find("tbody tr td[data-key=name]")[0]);
			baiduTel = $(baidutable.find("tbody tr td[data-key=telephone]")[0]);
		}
		
		if (tengxun != null ) {
			var tengxuntable = $("#tbtengxun" +tengxun.value.split(",")[0]);
			tengxunName = $(tengxuntable.find("tbody tr td[data-key=name]")[0]);
			tengxunTel = $(tengxuntable.find("tbody tr td[data-key=telephone]")[0]);
		}
		
		if (gaode != null ) {
			var gaodetable = $("#tbgaode" +gaode.value.split(",")[0]);
			gaodeName = $(gaodetable.find("tbody tr td[data-key=name]")[0]);
			gaodeTel = $(gaodetable.find("tbody tr td[data-key=telephone]")[0]);
		}
		
		if (keyword != null) {
			dianpingName = $("table#tbKeyword>tbody tr td.tdValue[data-key='name']");
			dianpingTel = $("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']");
		}
		var markName = markNameColor(baiduName,gaodeName, tengxunName, dianpingName);
		if (markName != null && markName.length > 1){
			var datasource = getSRC(markName);
			changeName(markName[0].innerText, datasource, "namec");
		}
		var baiduTelArray = [], gaodeTelArray = [], tengxunTelArray = [], dianpingTelArray = [];
		if(baiduTel != null) {
			baiduTelArray = baiduTel.find("span");
		}
		if(tengxunTel != null) {
			tengxunTelArray = tengxunTel.find("span");
		}
		if(gaodeTel != null) {
			gaodeTelArray = gaodeTel.find("span");
		}
		if(dianpingTel != null) {
			dianpingTelArray = dianpingTel.find("span");
		}
		
		var telMarks = markTelColor(baiduTelArray, gaodeTelArray, tengxunTelArray, dianpingTelArray);
		
		var tbtables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
		var btel = $($("table#tbbaidu>tbody td.tdValue[data-key='hideTel']")[0]).text();
		var ttel = $($("table#tbtengxun>tbody td.tdValue[data-key='hideTel']")[0]).text();
		var gtel = $($("table#tbgaode>tbody td.tdValue[data-key='hideTel']")[0]).text();
		var dtel = $("table#tbKeyword>tbody td.tdValue[data-key='hideTel']").text();
		if (btel != null && btel.trim() != "" && btel == ttel && ttel == gtel && gtel == dtel) {
			var datasource = getSRC([baidu,gaode, tengxun, keyword]);
			
			changeName(btel, datasource, "tel");
		}else if (btel != null && btel.trim() != "" && btel == ttel && ttel == gtel ) {
			var datasource = getSRC([baidu,gaode, tengxun, null]);
			changeName(btel, datasource, "tel");
		}
		console.log("结束初始化颜色");
		console.log(Date.now());
	} */
	
	function initArray() {
		//console.log("开始初始化颜色开始");
		//console.log(Date.now());
		originalCheckRelation = [];
		currentCheckRelation = [];
		var tbtables = [ "tbbaidu", "tbtengxun", "tbgaode"];
		var baidu =null;
		var tengxun = null;
		var gaode = null;	
		if ( $("#tbbaidu input:checkbox") != null  || $("#tbbaidu input:checkbox").length > 0) {
			baidu = $("#tbbaidu input:checked")[0];			
		}
		if ( $("#tbtengxun input:checkbox") != null  || $("#tbtengxun input:checkbox").length > 0) {
			tengxun = $("#tbtengxun input:checked")[0];			
		}
		if ( $("#tbgaode input:checkbox") != null  || $("#tbgaode input:checkbox").length > 0) {
			gaode = $("#tbgaode input:checked")[0];			
		}
		
		var dianpingName = null,baiduName = null,tengxunName = null,  gaodeName = null;
		if (baidu != null ) {
			var baidutable = $("#tbbaidu" +baidu.value.split(",")[0]);
			baiduName = $(baidutable.find("tbody tr td[data-key=name]")[0]);
		}
		
		if (tengxun != null ) {
			var tengxuntable = $("#tbtengxun" +tengxun.value.split(",")[0]);
			tengxunName = $(tengxuntable.find("tbody tr td[data-key=name]")[0]);
		}
		
		if (gaode != null ) {
			var gaodetable = $("#tbgaode" +gaode.value.split(",")[0]);
			gaodeName = $(gaodetable.find("tbody tr td[data-key=name]")[0]);
		}
		
		if (keyword != null) {
			dianpingName = $("table#tbKeyword>tbody tr td.tdValue[data-key='name']");
		}
		var markName = markNameColor(baiduName,gaodeName, tengxunName, dianpingName);
		if (markName != null && markName.length > 1){
			var datasource = getSRC(markName);
			changeName(markName[0].innerText, datasource, "namec");
		}
		
		//console.log("结束初始化颜色");
		//console.log(Date.now());
	}
	
	function getTel(tempTel) {
		//var tempTel = tel.innerText;
		var gtel = ""
		if (tempTel.indexOf("-") > -1) {
			gtel = tempTel.split("-")[1].trim();
		}else {
			// gtel = tempTel.innerText;
			gtel = tempTel.trim();
		}
		return gtel;
	}
	
	
	function loadKeyword(keywordid) {
		//console.log("加载keyword开始");
		//console.log(Date.now());
		var dtd = $.Deferred(); 
		jQuery.post("./edit.web", {
			"atn" : "getkeywordbyid",
			"keywordid" : keywordid
		}, function(json) {
			if (json && json.result == 1 && json.rows != null) {
				keyword = json.rows;
				/* srcType = keyword.srcType;
				srcInnerId = keyword.srcInnerId; */
				
				dianpingGeo = keyword.geo;
				$("table#tbKeyword>tbody tr td.tdValue[data-key='name']").html(keyword.name);
				/* $("table#tbKeyword>tbody tr td.tdValue[data-key='address']").html(keyword.address);
				var keywordTel =  $($("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']")[0]); */
				/* if ( keyword.telephone != null && keyword.telephone.trim() != "") {
					keywordTel.html("");
			    	var tel = keyword.telephone.split(",");
			    	
			    	// html.push("<td class='tdValue' data-key='telephone'>");
			    	for (var i = 0; i < tel.length; i++) {
			    		if(i != tel.length - 1) {
			    			if (i == 0) {
				    			keywordTel.append("<span>" + tel[i] + ";</span>");
				    		}else {
				    			if (tel[i].indexOf("-") < 0) {
				    				keywordTel.append("<span>" + tel[i] + ";</span>");
				    			}else {
				    				keywordTel.append("<span>" + tel[i].split("-")[1] + ";</span>");
				    			}
				    		}
			    		}else {
			    			if (tel[i].indexOf("-") < 0) {
			    				keywordTel.append("<span>" + tel[i] + "</span>");
			    			}else {
			    				keywordTel.append("<span>" + tel[i].split("-")[1] + "</span>");
			    			}
			    		}
			    		
			    	}
			    }else {
			    	$("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']").html(keyword.telephone);
			    } */
				$("table#tbKeyword>tbody tr td.tdValue[data-key='categoryName']").html(keyword.categoryName);
			} else {
				loaderr = "没有资料了";
				$("table#tbKeyword>tbody tr td.tdValue[data-key='name']").html(loaderr);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='address']").html(loaderr);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']").html(loaderr);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='categoryName']").html(loaderr);
			}
			dtd.resolve();
		}, "json");
		//console.log("加载keyword结束");
		// console.log(Date.now());
		return dtd;
	}
	
	function loadRelation(srcInnerId, srcType) {
		var dtd = $.Deferred(); 
		//console.log("加载relation开始");
		//console.log(Date.now());
		jQuery.post("./edit.web", {
			"atn" : "getRelationByOid",
			"srcInnerId" : srcInnerId,
			"srcType": srcType
		}, function(json) {
			var tables = ["rdtbemg", "rdtbbaidu", "rdtbtengxun", "rdtbgaode"];
			var tbtables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
			var flags = [false, false, false, false];
			if (json && json.result == 1 && json.rows.length > 0) {
				databaseSaveRelation = json.rows;
				for (var i = 0; i < tables.length; i++) {
					$.each($("input[name='" +tables[i] +"']:checkbox"), function(){
						var srcInnerId = $(this).val().split(",")[1];
						var srcType = $(this).val().split(",")[2];
						for (var j = 0; j < databaseSaveRelation.length; j++) {
							if (i == 0) {
								if (keyword.srcInnerId == databaseSaveRelation[j].srcInnerId && keyword.srcType == databaseSaveRelation[j].srcType && srcInnerId == databaseSaveRelation[j].oid) {
									// 当为EMG数据时，srcinnerid则为oid
									$(this).prop("checked", true);
									// loadEditPOI(srcInnerId);
									var emgFeatcode = $($(this).parents("table")[0]).find("td.tdValue[data-key='class']");
									$.when( 
											loadEditPOI(srcInnerId, emgFeatcode)
									).done(function() {
										source = [];
										initArray();
									});
									flags[i] = true;
									currentCheckRelation.push(databaseSaveRelation[j]);
								} 
							} else {
								if (srcInnerId == databaseSaveRelation[j].srcInnerId && srcType == databaseSaveRelation[j].srcType && systemOid == databaseSaveRelation[j].oid) {
									$(this).prop("checked", true);
									
									flags[i] = true;
									currentCheckRelation.push(databaseSaveRelation[j]);
								}
							}
						}
						
					}); 
				}
				
			} 
			if (!flags[0]) {
				for (var j = 0; j < databaseSaveRelation.length; j++) {
					if (keyword.srcInnerId == databaseSaveRelation[j].srcInnerId && keyword.srcType == databaseSaveRelation[j].srcType) {
						// 在数据库里存在点评和EMG的关系，但是在界面上加载的时候，EMG中却没有指定oid的数据
						originalCheckRelation.push(databaseSaveRelation[j]);
					}
				}
			}
			
			
			for (var i = 0; i < tbtables.length; i++) {
				if ( $("#" + tbtables[i] + " input:checkbox") && $("#" + tbtables[i] + " input:checkbox").length > 0 && !flags[i]) {
					$("#" + tbtables[i] + " input:checkbox")[0].checked = true;
					flags[i] = true;
					if (i == 0 ) {
						var srcInnerId = $("#" + tbtables[i] + " input:checkbox")[0].value.split(",")[1];
						var emgFeatcode = $($($("#" + tbtables[i] + " input:checkbox")[0]).parents("table")[0]).find("td.tdValue[data-key='class']");
						$.when( 
								loadEditPOI(srcInnerId, emgFeatcode)
						).done(function() {
							source = [];	
							initArray();
							
						});
					}
				}
			}
			if(!flags[0]) {
				var oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
				if (oid == null || oid.trim() == "") {
					source = [];
					initArray();
				}
			}
			
			dtd.resolve();
		}, "json");
		//console.log(Date.now());
		//console.log("加载relation结束");
		return dtd;
	}
	
	function refercompare(a, b) {
		return a.sequence - b.sequence;
	}
	
	
	function drawEMGMap(lat, lng, zoom) {
		//console.log("开始绘制EMG地图: " + Date.now());
		var geo = null;
		if(keyword != null && keyword.geo != null ){
			geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
		}
		try{
			if ($emgmap != null && geo != null) {
				$emgmap.setCenter(geo);
			} else if($emgmap == null && geo != null) {
				$("#emgmap").empty();
				$emgmap = new emapgo.Map({
			        container : 'emgmap',
			        style: 'http://tiles.emapgo.cn/styles/outdoor/style.json',
			        zoom: zoom-1,
			        center: geo,
			        localIdeographFontFamily : "'Noto Sans', 'Noto Sans CJK SC', sans-serif"
			    });
				$emgmap.on('styledata',function(){
					$emgmap.setPaintProperty('china-building', 'fill-extrusion-height', 0);
				})
				$emgmap.on('click', function(e) {
					if (e && $emgmarker) {
						$emgmarker.setLngLat([e.lngLat.lng,e.lngLat.lat]);
						$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val('MULTIPOINT (' + e.lngLat.lng + " " + e.lngLat.lat + "," +   e.lngLat.lng + " " + e.lngLat.lat +")");
					}
					console.log(e);
					});
			}
			if(keyword != null && keyword.geo != null ) {
				var img = new Image();
				img.src = "resources/images/start.png";
				if ($emgmarkerBase) {
					$emgmarkerBase.setLngLat(geo);
				} else {
					$emgmarkerBase = new emapgo.Marker(img)
						.setLngLat(geo)
						.addTo($emgmap);
					
				}
			}
			if ($emgmarker) {
				$emgmarker.setLngLat([lng, lat ]);
			} else {
				$emgmarker = new emapgo.Marker()
					.setLngLat([lng, lat ])
					.addTo($emgmap);
				
			}
			
		} catch(e) {
			
		}
		//console.log("结束绘制EMG地图: " + Date.now());
	}
	
	 //double x = double.Parse(lat1) - 0.0065, y = double.Parse(lon1) - 0.006;
    //double z = Math.Sqrt(x * x + y * y) - 0.00002 * Math.Sin(y * Math.PI);
    //double theta = Math.Atan2(y, x) - 0.000003 * Math.Cos(x * Math.PI);
    //lat = (z * Math.Cos(theta)).ToString();
    //lon = (z * Math.Sin(theta)).ToString();
	
	function drawBaiDuMap(lat, lng, zoom) {
		//console.log("开始绘制百度地图: " + Date.now());
		var geo = null;
		try{
			$("#baidumap").empty();
			var convertor = new BMap.Convertor();
			var pointArr = [];
			if(keyword != null && keyword.geo != null) {
				geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
				pointArr.push(new BMap.Point(geo[0], geo[1]));
			}
			/* geo = [lng, lat];
	        pointArr.push(new BMap.Point(geo[0], geo[1])); */
	        geo = coordtransform.bd09togcj02(lng, lat);
	       
	        pointArr.push(new BMap.Point(geo[0], geo[1]));
	        convertor.translate(pointArr, 3, 5, function(data){
	        	if(data.status === 0) {
	        		$baidumap = new BMap.Map("baidumap");
	        		$baidumap.centerAndZoom(data.points[0], zoom+1);
	    			$baidumap.enableScrollWheelZoom(true);
	    			
		    	  	$baidumarker = new BMap.Marker(data.points[1]);
					$baidumarker.disableDragging();
					$baidumap.addOverlay($baidumarker);
					if (keyword != null && keyword.geo != null) {
						var myIcon = new BMap.Icon("resources/images/start.png",new BMap.Size(40,40));
						$baidumarkerBase = new BMap.Marker(data.points[0],{icon:myIcon});
						$baidumap.addOverlay($baidumarkerBase);
					}
		      	}
	        });
		} catch(e) {
		}
		//console.log("结束绘制百度地图: " + Date.now());
	}
	
	function drawGaoDeMap(lat, lng, zoom) {
		//console.log("开始绘制高德地图: " + Date.now());
		var geo = null;
		if(keyword != null && keyword.geo != null ){
			geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
		}
		console.log("高德地图keyword geo: " + geo);
		console.log("高德地图keyword geo: " + lng+ "," + lat);
		try{
			if ($gaodemap != null && geo != null) {
				$gaodemap.setCenter(geo);
			} else if ($gaodemap == null && geo != null){
				$("#gaodemap").empty();
				$gaodemap = new mapboxgl.Map({
					container: 'gaodemap',
					crossDomain: true,
					style: {
						"version": 8,
				        "sprite": "http://tiles.emapgo.cn/styles/outdoor/sprite",
				        "glyphs": "http://static.emapgo.cn/{fontstack}/{range}.pbf",
				        "sources": {
				          	"osm-tiles": {
				            	"type": "raster",
				            	"tileSize": 256,
				            	'tiles': [
				                  	"https://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=11",
				            		"https://webrd02.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=11",
				            		"https://webrd03.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=11",
				            		"https://webrd04.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=7&x={x}&y={y}&z={z}&scl=1&ltype=11"
				              	],
				              	transformRequest: (url, resourceType)=> {
			              	      	return {
			              	       		url: url,
			              	       		headers: { 'Access-Control-Allow-Origin' : '*' }
			              	     	}
				              	}
				          	}
				        },
				        "layers": [{
				          	"id": "simple-tiles",
				          	"type": "raster",
				          	"source": "osm-tiles",
				        }]
					},
					center: geo,
					zoom: zoom-1,
				});
			}
			
			if ($gaodemarker) {
				$gaodemarker.setLngLat([lng, lat]);
			} else {
				$gaodemarker = new mapboxgl.Marker()
							  .setLngLat([lng, lat])
							  .addTo($gaodemap);
			}
			
			if(keyword != null && keyword.geo != null && dianpingGeo != null) {
				var img = new Image();
				img.src = "resources/images/start.png";
				if ($gaodemarkerBase) {
					$gaodemarkerBase.setLngLat(geo);
				} else {
					$gaodemarkerBase = new mapboxgl.Marker(img)
						.setLngLat(geo)
						.addTo($gaodemap);
					
				}
			}
		} catch(e) {
			
		}
		//console.log("结束绘制高德地图: " + Date.now());
	}
	
	function drawTengXunMap(lat, lng, zoom) {
		//console.log("开始绘制腾讯地图: " + Date.now());
		var geo = null;
		if(dianpingGeo != null ){
			geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
		}else {
			geo = [lng, lat];
		}
		try{
			if ($tengxunmap) {
				$tengxunmap.setCenter(geo);
			} else {
				$("#tengxunmap").empty();
				$tengxunmap = new mapboxgl.Map({
					container: 'tengxunmap',
					style: {
						"version": 8,
				        "sprite": "http://tiles.emapgo.cn/styles/outdoor/sprite",
				        "glyphs": "http://static.emapgo.cn/{fontstack}/{range}.pbf",
				        "sources": {
				          "osm-tiles": {
				            "type": "raster",
				            "scheme": "tms",
				            "tileSize": 256,
				            'tiles': [
				                  "http://rt1.map.gtimg.com/tile?z={z}&x={x}&y={y}&styleid=0"
				              ]
				        
				          }
				        },
				        "layers": [{
				          "id": "simple-tiles",
				          "type": "raster",
				          "source": "osm-tiles",
				        }]
					},
					center:  geo,
					zoom: zoom-1
				});
			}
			
			if ($tengxunmarker) {
				$tengxunmarker.setLngLat([lng, lat]);
			} else {
				$tengxunmarker = new mapboxgl.Marker()
							  .setLngLat([lng, lat])
							  .addTo($tengxunmap);
			}
			if(keyword != null && keyword.geo != null) {
				var img = new Image();
				img.src = "resources/images/start.png";
				if ($tengxunmarkerBase) {
					$tengxunmarkerBase.setLngLat(geo);
				} else {
					$tengxunmarkerBase = new mapboxgl.Marker(img)
						.setLngLat(geo)
						.addTo($tengxunmap);
					
				}
			}
		} catch(e) {
			
		}
		//console.log("结束绘制腾讯地图: " + Date.now());
	}
	
	function loadEditPOI(oid, emgFeatcode) {
		if (!oid || oid <= 0) 	return;
		var dtd = $.Deferred(); 
		systemOid = oid;
		jQuery.post("./edit.web", {
			"atn" : "getpoibyoid",
			"oid" : oid
		}, function(json) {
			$("table#tbEdit>tbody td.tdValue>input:text").val("");
			if (json && json.result == 1 && json.poi != null) {
				systemPoi = json.poi;
				var poi = json.poi;
				$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(poi.id);
				$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val(poi.namec);
				$("#featcode").val(poi.featcode);
				$("#sortcode").val(poi.sortcode);
				$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val(poi.geo);
				if (emgFeatcode != null) {
					var featcode = getfeatcodename(poi.featcode);
					emgFeatcode.text(featcode);
				}
				poi.poitags.forEach(function(tag, index) {
					$("table#tbEdit>tbody td.tdValue[data-key='" + tag.k +"']>input:text").val(tag.v);
				});
			} else {
				$("table#tbEdit>tbody td.tdValue>input:text").val("加载失败");
			}
			dtd.resolve();
		}, "json");
		return dtd;
	}
	
	function rdChange(ck, srcType, lat, lng, srcInnerId) {
		if (ck.checked == false && srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("-1");
			$("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val("");
			emgSrcInnerId = "";
			emgSrcType = 0;
			
		}else if (ck.checked == true && srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			// loadEditPOI(srcInnerId);
			var emgFeatcode = $($(ck).parents("table")[0]).find("td.tdValue[data-key='class']");
			loadEditPOI(srcInnerId, emgFeatcode);
			$emgmarker.setLngLat([lng, lat ]);
			$emgmap.setCenter([lng, lat]);
			emgSrcInnerId = srcInnerId;
			emgSrcType = srcType;
			 $("input[name='"+ ck.name +"']:checkbox").prop("checked", false);
			ck.checked = true; 
		}
		if (srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			source = [];
		} else if (srcType == <%=SrcTypeEnum.BAIDU.getValue() %>) {
			drawBaiDuMap(lat, lng, zoom);
		} else if (srcType == <%=SrcTypeEnum.TENGXUN.getValue() %>) {
			$tengxunmarker.setLngLat([lng, lat]);
			$tengxunmap.setCenter([lng, lat]);	
		} else if (srcType == <%=SrcTypeEnum.GAODE.getValue() %>) {
			$gaodemarker.setLngLat([lng, lat]);
			$gaodemap.setCenter([lng, lat]);
		} else {
			console.log("Error on srcType: " + srcType);
			return;
		}
	
	}
	
	function textCopy(obj) {
		var $this = $(obj);
		var value = $this.parent().prev().html();
		var key = $this.parent().prev().data("key");
		key = (key == "telephone" ? "tel" : key);
		// var oldObj = null;
		var oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		if (source != null && source.length > 0) {
			var flag = false;
			var ktemp = key == "name" ? "namec" : key;
			for (var i = source.length - 1; i > -1; i--) {
				
				if(source[i].k == ktemp && value != null && value.trim() != "") {
					var t = source.splice(i,1);
				}
			}
		}
		if ((key == "name" || key == "geo" || key == "address") && value != null && $this.parents("table")[0].id.indexOf("tbemg") < 0) {
		// if ((key == "name" || key == "geo" || key == "address") && value != null && $this.parents("table")[0].id.indexOf("tbemg") < 0) {
			var obj = new Object();
			var z = $($this.parents("table")[0]).find("input:checkbox").val();
			obj.srcType = z.split(",")[2];
			obj.srcInnerId = z.split(",")[1];
			obj.newValue = value;
			obj.oid = oid;
			obj.keywordId = keyword.id;
			if (key == "name") {
				obj.k = "namec";
			}else if (key == "address") {
				obj.k = "address8";
			}else {
				obj.k = "geo";
			}
			
			source.push(obj);
		}
		
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>textarea").val(value);
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>input:text").val(value);
		if (key == "address") {
			$("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val(value);
			
		}else if (key == "class") {
			var parent = $($this.parents("table")[0]).attr("id");
			
			if (parent.indexOf("tbbaidu") != -1) {
				var valueArray = value.split(";");
				getCode(baiduCode, valueArray,  $this);
			}else if(parent.indexOf("tbtengxun") != -1) {
				var valueArray = value.split(":");
				getCode(tengxunCode, valueArray,  $this);
			}else if(parent.indexOf("tbgaode") != -1) {
				var valueArray = value.split(";");
				getCode(gaodeCode, valueArray,  $this);
			}else if(parent.indexOf("tbemg") != -1) {
				//当选中为EMG的分类时要去库里查询当前编辑库中的featcode, sortcode
				var v = $($this.parents("table")[0]).find("input[type=checkbox]").val();
				var oid = v.split(",")[1];
				jQuery.post("./edit.web", {
					"atn" : "getpoibyoid",
					"oid" : oid
				}, function(json) {
					if (json && json.result == 1 && json.poi != null) {
						var poi = json.poi;
						$("#featcode").val(poi.featcode);
						$("#sortcode").val(poi.sortcode);
						
					}
				}, "json");
			}
		}else if (key == "geo" && value != null) {
			var geo = value.replace("MULTIPOINT (","").replace(")", "").split(",")[0].split(" ");
			$emgmarker.setLngLat(geo);
		}else if (key == "tel" && value != null){
			var value = $this.parent().prev().html();
			var tel = ""
			for(var i = 0; i < $(value).length; i++) {
				tel = tel + $(value)[i].innerText;
			}
			$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>input:text").val(tel);
			if ( $this.parents("table")[0].id.indexOf("tbemg") < 0) {
				var obj = new Object();
				var z = $($this.parents("table")[0]).find("input:checkbox").val();
				obj.srcType = z.split(",")[2];
				obj.srcInnerId = z.split(",")[1];
				obj.newValue = tel;
				obj.k = "tel";
				obj.oid = oid;
				obj.keywordId = keyword.id;
				source.push(obj);
			}
			
		}
		
	} 
	
	/* function textCopy(obj) {
		var $this = $(obj);
		var value = $this.parent().prev().html();
		var key = $this.parent().prev().data("key");
		key = (key == "telephone" ? "tel" : key);
		// var oldObj = null;
		var oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		if (source != null && source.length > 0) {
			var flag = false;
			var ktemp = key == "name" ? "namec" : key;
			for (var i = source.length - 1; i > -1; i--) {
				if(ktemp == "class" && (source[i].k == "featcode" || source[i].k == "sortcode") && value != null && value.trim() != "") {
					var t = source.splice(i,1);
				}else if(source[i].k == ktemp && value != null && value.trim() != "") {
					var t = source.splice(i,1);
				}
			}
		}
		if ((key == "name" || key == "geo" ) && value != null && $this.parents("table")[0].id.indexOf("tbemg") < 0) {
		// if ((key == "name" || key == "geo" || key == "address") && value != null && $this.parents("table")[0].id.indexOf("tbemg") < 0) {
			var obj = new Object();
			var z = $($this.parents("table")[0]).find("input:checkbox").val();
			obj.srcType = z.split(",")[2];
			obj.srcInnerId = z.split(",")[1];
			obj.newValue = value;
			obj.oid = oid;
			obj.keywordId = keyword.id;
			if (key == "name") {
				obj.k = "namec";
			}else {
				obj.k = "geo";
			}
			
			source.push(obj);
		}
		
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>textarea").val(value);
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>input:text").val(value);
		if (key == "class") {
			var parent = $($this.parents("table")[0]).attr("id");
			
			if (parent.indexOf("tbbaidu") != -1) {
				var valueArray = value.split(";");
				getCode(baiduCode, valueArray,  $this);
			}else if(parent.indexOf("tbtengxun") != -1) {
				var valueArray = value.split(":");
				getCode(tengxunCode, valueArray,  $this);
			}else if(parent.indexOf("tbgaode") != -1) {
				var valueArray = value.split(";");
				getCode(gaodeCode, valueArray,  $this);
			}else if(parent.indexOf("tbemg") != -1) {
				//当选中为EMG的分类时要去库里查询当前编辑库中的featcode, sortcode
				var v = $($this.parents("table")[0]).find("input[type=checkbox]").val();
				var oid = v.split(",")[1];
				jQuery.post("./edit.web", {
					"atn" : "getpoibyoid",
					"oid" : oid
				}, function(json) {
					if (json && json.result == 1 && json.poi != null) {
						var poi = json.poi;
						$("#featcode").val(poi.featcode);
						$("#sortcode").val(poi.sortcode);
						
					}
				}, "json");
			}
		}else if (key == "geo" && value != null) {
			var geo = value.replace("MULTIPOINT (","").replace(")", "").split(",")[0].split(" ");
			$emgmarker.setLngLat(geo);
		}
		
	} */
	//function getCode(objCodes, values, oldObj, ele) {
	function getCode(objCodes, values,  ele) {
		if (objCodes == null || values == null) return;
		var flag = false;
		var oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		for (var j =  values.length -1 ; j > -1; j--) {
			for (let key in objCodes){
				if(objCodes[key].name != values[j]) continue;
				if (objCodes[key].featcode != null && objCodes[key].featcode != "" && ele.parents("table")[0].id.indexOf("tbemg") < 0) {
					var obj = new Object();
					var z = $(ele.parents("table")[0]).find("input:checkbox").val();
					obj.srcType = z.split(",")[2];
					obj.srcInnerId = z.split(",")[1];
					obj.newValue = objCodes[key].featcode;
					obj.k = "featcode";
					obj.oid = oid;
					obj.keywordId = keyword.id;
					source.push(obj);
				}
				if (objCodes[key].sortcode != null && objCodes[key].sortcode != "" && ele.parents("table")[0].id.indexOf("tbemg") < 0) {
					var obj = new Object();
					var z = $(ele.parents("table")[0]).find("input:checkbox").val();
					obj.srcType = z.split(",")[2];
					obj.srcInnerId = z.split(",")[1];
					obj.newValue = objCodes[key].sortcode;
					obj.k = "sortcode";
					obj.oid = oid;
					obj.keywordId = keyword.id;
					source.push(obj);
				}
				$("#featcode").val(objCodes[key].featcode);
				$("#sortcode").val(objCodes[key].sortcode);
				flag = true;
				return;
			}
			
		}
	}
	
	function drawReferdatas(tbid, referdatas) {
		var $tbody = $("table#" + tbid + ">tbody");
		$tbody.empty();
		referdatas.forEach(function(referdata, index) {
			var html = new Array();
			html.push("<table id ='"+ tbid + referdata.id + "' class=\"table table-bordered table-condensed tableWith\"><tbody>");
			html.push("<tr class='trIndex'><td class='tdIndex' rowspan='6'>");
		    html.push("<input type='checkbox' name='rd" + tbid + "' onChange='rdChange(this, " + referdata.srcType + "," + referdata.srcLat + "," + referdata.srcLon + ",\"" + referdata.srcInnerId + "\");' value='" + referdata.id + "," + referdata.srcInnerId + "," + referdata.srcType + "' >");
		    html.push("</td></tr>");
		    
		    html.push("<tr><td class='tdKey'>名称</td>");
		    html.push("<td class='tdValue' data-key='name'>" + referdata.name + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		   /*  html.push("<tr style='display:none'><td  class='tdKey'>电话</td>");
		    html.push("<td class='tdValue' data-key='hideTel'>" + referdata.tel + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>"); */
		    
		    /* html.push("<tr><td class='tdKey'>电话</td>");
		    if ( referdata.tel != null && referdata.tel.trim() != "") {
		    	var tel = [];
		    	if (tbid == "tbbaidu") {
		    		var str = referdata.tel.replace(/\(/g,"").replace(/\)/g, "-");
		    	}
		    		tel = referdata.tel.split(";");
		    	
		    	html.push("<td class='tdValue' data-key='telephone'>");
		    	for (var i = 0; i < tel.length; i++) {
		    		if(i != tel.length - 1) {
		    			if (i == 0) {
			    			html.push("<span>" + tel[i].trim() + ";</span>");
			    		}else {
			    			if (tel[i].indexOf("-") < 0) {
			    				html.push("<span>" + tel[i].trim() + ";</span>");
			    			}else {
			    				html.push("<span>" + tel[i].split("-")[1].trim() + ";</span>");
			    			}
			    		}
		    		}else {
		    			if (tel[i].indexOf("-") < 0) {
		    				html.push("<span>" + tel[i].trim() + "</span>");
		    			}else {
		    				html.push("<span>" + tel[i].split("-")[1].trim() + "</span>");
		    			}
		    		}
		    		
		    	}
		    	html.push("</td><td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    }else {
		    	html.push("<td class='tdValue' data-key='telephone'>" + referdata.tel + "</td>");
		    	html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    }
		     */
		    html.push("<tr><td class='tdKey'>分类</td>");
		    html.push("<td class='tdValue' data-key='class'>" + referdata.orgCategoryName + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr><td class='tdKey'>坐标</td>");
		    if(referdata.srcType == 45) {
		    	// 百度坐标需要特殊处理
		    	var baidugeo = coordtransform.bd09togcj02(referdata.srcLon, referdata.srcLat);
		    	html.push("<td class='tdValue' data-key='geo'>MULTIPOINT (" + baidugeo[0] + " " + baidugeo[1] + "," +  baidugeo[0]  + " " + baidugeo[1] +")</td>");
		    } else {
		    	html.push("<td class='tdValue' data-key='geo'>MULTIPOINT (" + referdata.srcLon + " " + referdata.srcLat + "," +   referdata.srcLon + " " + referdata.srcLat +")</td>");
		    }
		    
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    /* html.push("<tr class='trEnd'><td class='tdKey'>地址</td>");
		    html.push("<td class='tdValue' data-key='address'>" + referdata.address + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    */
		    html.push("</tbody></table>");
		    $tbody.append(html.join(''));
		});
	}
	
	function loadReferdatas(keywordid) {
		var dtd = $.Deferred();
		//console.log("加载referdata开始");
		//console.log(Date.now());
		jQuery.post("./edit.web", {
			"atn" : "getreferdatabykeywordid",
			"keywordid" : keywordid
		}, function(json) {
			if (json && json.result == 1) {
				var referdatas = json.rows;
				var emgrefers = new Array();
				var baidurefers = new Array();
				var gaoderefers = new Array();
				var tengxunrefers = new Array();
				if (referdatas && referdatas.length > 0) {
					referdatas.forEach(function(referdata, index) {
						if (referdata.srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
							emgrefers.push(referdata);
						} else if (referdata.srcType == <%=SrcTypeEnum.BAIDU.getValue() %>) {
							baidurefers.push(referdata);
						} else if (referdata.srcType == <%=SrcTypeEnum.TENGXUN.getValue() %>) {
							tengxunrefers.push(referdata);					
						} else if (referdata.srcType == <%=SrcTypeEnum.GAODE.getValue() %>) {
							gaoderefers.push(referdata);
						}
					});
					
					if (emgrefers && emgrefers.length > 0) {
						emgrefers.sort(refercompare);
						drawEMGMap(emgrefers[0].srcLat, emgrefers[0].srcLon, zoom);
						drawReferdatas("tbemg", emgrefers);
						emgSrcInnerId = emgrefers[0].srcInnerId;
						emgSrcType = emgrefers[0].srcType;
					} else {
						$("#emgmap").html("无数据");
						$emgmap = null;
						$emgmarker = null;
						$emgmarkerBase = null;
						if (keyword && keyword.geo) {
							var geo = keyword.geo.replace("POINT (","").replace(")", "").split(" ");
							drawEMGMap(geo[1], geo[0], zoom);
							$("table#tbemg>tbody").html("<tr><td>无数据</td></tr>");
						}
						
					}
					
					if (baidurefers && baidurefers.length > 0) {
						baidurefers.sort(refercompare);
						drawBaiDuMap(baidurefers[0].srcLat, baidurefers[0].srcLon, zoom);
						drawReferdatas("tbbaidu", baidurefers);
					} else {
						$("#baidumap").html("无数据");
						$("table#tbbaidu>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					if (gaoderefers && gaoderefers.length > 0) {
						gaoderefers.sort(refercompare);
						drawGaoDeMap(gaoderefers[0].srcLat, gaoderefers[0].srcLon, zoom);
						drawReferdatas("tbgaode", gaoderefers);
					} else {
						$("#gaodemap").html("无数据");
						$gaodemap = null;
						$gaodemarkerBase = null;
						$gaodemarker = null;
						$("table#tbgaode>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					if (tengxunrefers && tengxunrefers.length > 0) {
						tengxunrefers.sort(refercompare);
						drawTengXunMap(tengxunrefers[0].srcLat, tengxunrefers[0].srcLon, zoom);
						drawReferdatas("tbtengxun", tengxunrefers);
					} else {
						$tengxunmap = null;
						$tengxunmarker = null;
						$tengxunmarkerBase = null;
						$("#tengxunmap").html("无数据");
						$("table#tbtengxun>tbody").html("<tr><td>无数据</td></tr>");
					}
				}
			} else {
				keywordError();
			}
			dtd.resolve();
		}, "json");
		//console.log("加载referdata结束");
		//console.log(Date.now());
		return dtd;
	}
	
	//标识资料错误
	function keywordError(){
		var isLoadNextTask = $("table#tbEdit>tbody td.tdKey[data-key='isLoadNextTask']>input:checkbox").prop("checked");
	
			$.webeditor.showConfirmBox("alert","你确定把当前资料打上错误标识吗？", function(){
				$.webeditor.showMsgBox("info", "数据保存中...");
				jQuery.post("./edit.web", {
					"atn" : "keywordError",
					"taskid" : $("#curTaskID").html(),
					"getnext" : isLoadNextTask,
					"keywordid": keyword.id
				}, function(json) {
					if (json && json.result == 1) {
						var task = json.task;
						if (task && task.id) {
							var process = json.process;
							var project = json.project;
							$("#curProcessID").text(process.id);
							$("#curProcessName").text(process.name);
							$("#curProjectOwner").text(project.owner == 1 ? '私有' : '公有');
							$("#curTaskID").text(task.id);
							$("#curProjectID").text(task.projectid);
							$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val("");
							//$("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
							$("#featcode").val("");
							$("#sortcode").val("");
							/* $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val(""); */
							$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val("");
							keywordid = json.keywordid;
							initKeywordColor();
							if (keywordid && keywordid > 0) {
								$.when( 
										loadKeyword(keywordid)
									).done(function() {
										$.when(loadReferdatas(keywordid)
										).done(function() {
											if (keyword) {
												loadRelation(keyword.srcInnerId, keyword.srcType);
											}
										});
									});
								/* $.when( 
									loadKeyword(keywordid),loadReferdatas(keywordid)
								).done(function() {
									if (keyword) {
										loadRelation(keyword.srcInnerId, keyword.srcType);
									}
								}); */
							}
						} else {
							
						}
						
					} else {
						console.log("submitEditTask error");
					}
					$.webeditor.showMsgBox("close");
				}, "json");
			});
			
	
	}
	
	function submitEditTask() {
		console.log("提交前预处理开始: " + Date.now());
		var oid = null;
		try {
			oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
			oid = oid == "" || oid == null ? -1 : oid;
		} catch(e) {
			return;
		}
		
		var tables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
		var relations = [];
		var emgChecked = false;
		console.log("遍历relation开始: " + Date.now());
		for (var i = 0; i < tables.length; i++) {
			// $("#" + tables[i] + " table tbody tr :checkbox")
			$("#" + tables[i] + " input:checked").each(function(){
				// relations.push();
				var srcInnerId = $(this).val().split(",")[1];
				var srcType = $(this).val().split(",")[2];
				if (i == 0 && oid > 0 && (keyword.srcType != null && keyword.srcType > 0 ) && oid > 0) {
					//EMG 选中保存的是EMG和poi的
					emgChecked = true;
					var relation = new Object();
					relation.srcInnerId = keyword.srcInnerId;
					relation.srcType = keyword.srcType;
					relation.oid = oid;
					relation.qid = keyword.qid;
					relation.errorType = keyword.errorType;
					relations.push(relation);
				}else if (i > 0 && oid > 0){
					
					var relation = new Object();
					relation.srcInnerId = srcInnerId;
					relation.srcType = srcType;
					relation.oid = oid;
					relation.qid = keyword.qid;
					relation.errorType = keyword.errorType;
					relations.push(relation);
				}
			});
			
			
		}
		console.log("遍历relation结束: " + Date.now());
		if (!emgChecked && oid > -1 && oid.trim() != "" && keyword != null && keyword.srcInnerId != null) {
			var relation = new Object();
			relation.srcInnerId = keyword.srcInnerId;
			relation.srcType = keyword.srcType;
			relation.oid = oid;
			relation.qid = keyword.qid;
			relation.errorType = keyword.errorType;
			relations.push(relation);
		}
		console.log("2: " + Date.now());
		var delFlag = false;
		//提示删除标识
		if (databaseSaveRelation != null && relations != null) {
			for (var j = 0; j < databaseSaveRelation.length; j++) {
				var flag = false;
				for (var i = 0; i < relations.length; i++) {
					if(relations[i].srcInnerId == databaseSaveRelation[j].srcInnerId && relations[i].srcType == databaseSaveRelation[j].srcType && relations[i].oid != databaseSaveRelation[j].oid && databaseSaveRelation[j].importTime != null) {
						// 此种情况需要人工确认，表明数据库中的关系存在且已经确认过，但现在提交的与库里保存的不一致
						delFlag = true;
						break;
						
					}
					if (delFlag) break;
					
				}
				
			}
		}
		// 在库里保存了关系，现在取消掉，需要删除relation,且没有新增POI点，即oid为-1
		if (currentCheckRelation != null && (relations == null || relations.length == 0) && oid !="") {
			for (var i = 0; i < currentCheckRelation.length; i++) {
				currentCheckRelation[i].isDel = true;
				relations.push(currentCheckRelation[i]);
			}
		}
		console.log("4: " + Date.now());
		if (originalCheckRelation != null && originalCheckRelation.length > 0 && oid > 0) {
			$.webeditor.showCheckBox("alert", "与EMG的关系在数据库中已经存在，但EMG检索未查询到该记录，请连接开发人员查明此问题");
			return;
		} 
		var projectId = $("#curProjectID").val();
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		// var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		
		var featcode =$("#featcode").val();
		var sortcode =$("#sortcode").val();
		/* var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val(); */
		var remark = $("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val();
		var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val();
		var isLoadNextTask = $("table#tbEdit>tbody td.tdKey[data-key='isLoadNextTask']>input:checkbox").prop("checked");
		if((namec == null || namec.trim() == "" || featcode == null || featcode.trim() == "" || geo == null || geo.trim() == "") && oid != -1) {
			$.webeditor.showMsgLabel("alert", "名称、分类、坐标不能为空");
			return;
		}
		$.webeditor.showMsgBox("info", "数据保存中...");
		console.log("提交前预处理结束: " + Date.now());
		if(delFlag) {
			$.webeditor.showConfirmBox("alert","保存的关系已经在数据库中存在，是否删除原关系保存新的关系", function(){
				
				jQuery.post("./edit.web", {
					"atn" : "submitedittask",
					"taskid" : $("#curTaskID").html(),
					"getnext" : isLoadNextTask,
					"relations": JSON.stringify(relations),
					"namec": namec,
					"oid": oid,
					//"tel": tel,
					"featcode" : featcode,
					"sortcode" : sortcode,
					/* "address4" : address4,
					"address5" : address5,
					"address6" : address6,
					"address7" : address7,
					"address8" : address8, */
					"geo" : geo,
					"remark": remark == "" || remark == null ? " " : remark,
					"projectId": projectId,
					"source": JSON.stringify(source)
					// "isLoadNextTask": isLoadNextTask
				}, function(json) {
					if (json && json.result == 1) {
						if (!isLoadNextTask) {
							$("#submitTask").prop("disabled", true); 
						}
						var task = json.task;
						initTask(task, json);
						
					} else {
						if(json.error != null && json.error.length > 0) {
							$.webeditor.showMsgLabel("alert", json.error);
						}else {
							$.webeditor.showMsgLabel("alert", "提交失败");
						}
					}
				}, "json");
			});
		}else {
			
			$.webeditor.showConfirmBox("alert","确实要提交当前资料？", function(){
				jQuery.post("./edit.web", {
					"atn" : "submitedittask",
					"taskid" : $("#curTaskID").html(),
					"getnext" : isLoadNextTask,
					"relations": JSON.stringify(relations),
					"namec": namec,
					"oid": oid,
					//"tel": tel,
					"featcode" : featcode,
					"sortcode" : sortcode,
					/* "address4" : address4,
					"address5" : address5,
					"address6" : address6,
					"address7" : address7,
					"address8" : address8, */
					"geo" : geo,
					"remark": remark == "" || remark == null ? " " : remark,
					"projectId": projectId,
					"source": JSON.stringify(source)
				}, function(json) {
					if (json && json.result == 1) {
						if (!isLoadNextTask) {
							$("#submitTask").prop("disabled", true); 
						}
						var task = json.task;
						initTask(task, json);
						
					} else {
						if(json.error != null && json.error.length > 0) {
							$.webeditor.showMsgLabel("alert", json.error);
						}else {
							$.webeditor.showMsgLabel("alert", "提交失败");
						}
					}
				});
			});
			
		}
		
		
		$.webeditor.showMsgBox("close");
		
	}
	
	function initTask(task, json) {
		console.log("接收到返回数据开始初始化控件: " + Date.now());
	
		if (task && task.id) {
			var process = json.process;
			var project = json.project;
			$("#curProcessID").text(process.id);
			$("#curProcessName").text(process.name);
			$("#curProjectOwner").text(project.owner == 1 ? '私有' : '公有');
			$("#curTaskID").text(task.id);
			$("#curProjectID").text(task.projectid);
			$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val("");
			// $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
			$("#featcode").val("");
			$("#sortcode").val("");
			/* $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val(""); */
			$("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val("");
			
			initKeywordColor();
			console.log("接收到返回数据结束初始化控件: " + Date.now());
			keywordid = json.keywordid;
			if (keywordid && keywordid > 0) {
				
				/* $.when( 
					loadKeyword(keywordid),loadReferdatas(keywordid)
				).done(function() {
					if (keyword) {
						loadRelation(keyword.srcInnerId, keyword.srcType);
					}
				}); */
				$.when( 
						loadKeyword(keywordid)
					).done(function() {
						$.when(loadReferdatas(keywordid)
						).done(function() {
							if (keyword) {
								loadRelation(keyword.srcInnerId, keyword.srcType);
							}
						});
					});
			}
		} 
	}
	
	function initKeywordColor() {
		var keyEle = $("table#tbKeyword>tbody td.tdValue");
		for(var i = 0; i < keyEle.length; i++) {
			keyEle[i].style.backgroundColor = "white";
		}
	}
	
	function deletePOI(obj) {
		var oid = null;
		try {
			oid = $(obj).parent().prev().children()[0].value;
		} catch(e) {
			return;
		}
		source = [];
		if (!oid || oid <= 0) 	return;
		var projectId = $("#curProjectID").val();
		$.webeditor.showConfirmBox("alert","确定要删除这个POI吗？", function(){
			$.webeditor.showMsgBox("info", "数据保存中...");
			jQuery.post("./edit.web", {
				"atn" : "deletepoibyoid",
				"oid" : oid,
				"projectId": projectId
			}, function(json) {
				if (json && json.result > 0) {
					$("table#tbEdit>tbody td.tdValue>input:text").val("");
					$.webeditor.showMsgLabel("success", "POI删除成功");
				} else {
					$.webeditor.showMsgLabel("alert", "POI删除失败");
				}
				$.webeditor.showMsgBox("close");
			}, "json");
		});
	}
	
	function updatePOI() {
		var oid = null;
		try {
			oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		} catch(e) {
			return;
		}
		if (oid > 0) {
			$.webeditor.showMsgLabel("alert", "当前已有oid,无需再重新获取oid");
			return;
		}
		
		$.webeditor.showMsgBox("info", "获取oid");
		jQuery.post("./edit.web", {
			"atn" : "updatepoibyoid",
			
		}, function(json) {
			if (json && json.result > 0) {
				$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(json.result);
				$.webeditor.showMsgLabel("success", "获取OID成功");
			} else {
				
				if(json.error != null && json.error.length > 0) {
					$.webeditor.showMsgLabel("alert", json.error);
				}else {
					$.webeditor.showMsgLabel("alert", "获取OID失败");
				}
				
			}
			$.webeditor.showMsgBox("close");
		}, "json");
	}
	
	/* function updatePOI() {
		var oid = null;
		try {
			oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		} catch(e) {
			return;
		}
		var projectId = $("#curProjectID").val();
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		// var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode =$("#featcode").val();
		var sortcode =$("#sortcode").val();
		/* var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val(); */
		//var remark = $("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val();
		/* var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val();
		if(namec == null || namec.trim() == "" || featcode == null || featcode.trim() == "" || geo == null || geo.trim() == "") {
			$.webeditor.showMsgLabel("alert", "名称、分类、坐标不能为空");
			return;
		}
		$.webeditor.showMsgBox("info", "数据保存中...");
		jQuery.post("./edit.web", {
			"atn" : "updatepoibyoid",
			"oid" : oid,
			"namec" : namec,
			// "tel" : tel,
			"featcode" : featcode,
			"sortcode" : sortcode,
			/* "address4" : address4,
			"address5" : address5,
			"address6" : address6,
			"address7" : address7,
			"address8" : address8, 
			"geo" : geo,
			// "remark": remark,
			"projectId": projectId
		}, function(json) {
			if (json && json.result > 0) {
				$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(json.result);
				$.webeditor.showMsgLabel("success", "保存成功");
			} else {
				
				if(json.error != null && json.error.length > 0) {
					$.webeditor.showMsgLabel("alert", json.error);
				}else {
					$.webeditor.showMsgLabel("alert", "保存失败");
				}
				
			}
			$.webeditor.showMsgBox("close");
		}, "json");
	} */
	
	function valueChange(obj) {
		var $this = $(obj);
		var key = $this.parent().data("key");
		
		if (source != null && source.length > 0) {
			var flag = false;
			var ktemp = key == "name" ? "namec" : key;
			for (var i = source.length - 1; i > -1; i--) {
				
				if(source[i].k == ktemp ) {
					oldObj = source.splice(i,1);
				}
			}
		}
	}
	
	function getNextTask() {
		jQuery.post("./edit.web", {
			"atn" : "getNextTask",
			"taskid" : $("#curTaskID").html(),
			"getnext" : true,
		}, function(json) {
			if (json && json.result == 1) {
				var task = json.task;
				if (task && task.id) {
					var process = json.process;
					var project = json.project;
					$("#curProcessID").text(process.id);
					$("#curProcessName").text(process.name);
					$("#curProjectOwner").text(project.owner == 1 ? '私有' : '公有');
					$("#curTaskID").text(task.id);
					$("#curProjectID").text(task.projectid);
					$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("");
					$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val("");
					// $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
					$("#featcode").val("");
					$("#sortcode").val("");
					/* $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
					$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
					$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
					$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
					$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val(""); */
					keywordid = json.keywordid;
					initKeywordColor();
					if (keywordid && keywordid > 0) {
						/* $.when( 
							loadKeyword(keywordid),loadReferdatas(keywordid)
						).done(function() {
							if (keyword) {
								loadRelation(keyword.srcInnerId, keyword.srcType);
							}
						}); */
						$.when( 
								loadKeyword(keywordid)
							).done(function() {
								$.when(loadReferdatas(keywordid)
								).done(function() {
									if (keyword) {
										loadRelation(keyword.srcInnerId, keyword.srcType);
									}
								});
							});
					}
				}
			}
		});
	}
</script>
</head>
<body>
	<div id="headdiv"></div>
	<c:choose>
		<c:when test="${task != null and task.id != null}">
		<div class="containerdiv">
			<div class="row-fluid fullHeight">
				<div class="col-md-2 fullHeight">
					<div style="position: absolute; top: 0; left: 0; height: 100%; overflow-y: scroll;">
						<table id="tbKeyword" class="table table-bordered table-condensed">
							<thead>
						    	<tr>
						      		<th><span class="glyphicon glyphicon-eye-open"></span></th>
						      		<th>参考数据</th>
						    	</tr>
						  	</thead>
							<tbody>
								<tr>
									<td class="tdKey">名称</td>
									<td class="tdValue" data-key="name">加载中...</td>
									<td class="tbTool" ><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr>
								<!-- <tr>
									<td class="tdKey">地址</td>
									<td class="tdValue" data-key="address">加载中...</td>
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr> -->
								<!-- <tr>
									<td class="tdKey">电话</td>
									<td class="tdValue" data-key="telephone">加载中...</td>
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr> -->
								<!-- <tr style="display:none">
									<td class="tdKey">电话</td>
									<td class="tdValue" data-key="hideTel">加载中...</td>
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" ></span></td>
								</tr> -->
								<tr>
									<td class="tdKey">分类</td>
									<td class="tdValue" data-key="categoryName">加载中...</td>
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr>
							</tbody>
						</table>
				
						<table id="tbEdit" class="table table-bordered table-condensed">
							<thead>
						    	<tr>
						      		<th><span class="glyphicon glyphicon-edit"></span></th>
						      		<th>当前编辑数据</th>
						    	</tr>
						  	</thead>
							<tbody>
								<tr>
									<td class="tdKey">OID</td>
									<td class="tdValue" data-key="oid"><input class="form-control input-sm" type="text" disabled></td>
									<td class="tbTool"><span class="glyphicon glyphicon-remove cursorable" onClick="deletePOI(this);"></span></td>
								</tr>
								<tr>
									<td class="tdKey">名称</td>
									<td class="tdValue" data-key="name"><textarea onchange="valueChange(this)" class="form-control input-sm"></textarea></td>
								</tr>
								<!-- <tr>
									<td class="tdKey">电话</td>
									onchange="value=value.replace(/[^\0-9\-\;]/g,'')" 
									<td class="tdValue" data-key="tel">
										<input onkeyup="value=value.replace(/[^\d;-]/g,'')" 
										onpaste="value=value.replace(/[^\d;-]/g,'')" 
										onchange="value=value.replace(/[^\d;-]/g,'');valueChange(this)"
										oncontextmenu = "return false;"
										onblur = ""
									class="form-control input-sm" type="text"></td>
								</tr> -->
								<!-- <tr style="display: none;"> -->
								<tr>
									<td class="tdKey">类型</td>
									<td class="tdValue" data-key="featcode">
									<!-- <input id="featcode" class="form-control input-sm" type="text"> -->
									<div class="input-group">
										<input id="featcode" onchange="valueChange(this)" class="form-control input-sm" type="text">
										<span class="input-group-addon" style="cursor: pointer;" onClick="dlgFeatcodePOIConfig(-1);" title="选择类型代码">选择</span>
									</div>
									
									</td>
								</tr>
								<!-- <tr style="display: none;" > -->
								<tr  >
									<td class="tdKey">系列</td>
									<td class="tdValue" data-key="sortcode">
										<!-- <input class="form-control input-sm" type="text"> -->
										<div class="input-group">
											<input type="text" onchange="valueChange(this)" class="form-control" id="sortcode" placeholder="请输入系列代码">
											<span class="input-group-addon" style="cursor: pointer;" onClick="dlgSortcodeConfig(-1);" title="选择系列代码">选择</span>
										</div>
									</td>
								</tr>
								<!-- <tr>
									<td class="tdKey">四级地址</td>
									<td class="tdValue" data-key="address4"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">五级地址</td>
									<td class="tdValue" data-key="address5"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">六级地址</td>
									<td class="tdValue" data-key="address6"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">七级地址</td>
									<td class="tdValue" data-key="address7"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">八级地址</td>
									<td class="tdValue" data-key="address8"><input onchange="valueChange(this)" class="form-control input-sm" type="text"></td>
								</tr> -->
								<tr>
									<td class="tdKey">坐标</td>
									<td class="tdValue" data-key="geo"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey" data-key="isLoadNextTask"><input  type="checkbox" checked="true"></td>
									<td class="tdValue" >是否获取下一条任务</td>
								</tr>
								
								<tr style="display:none">
									<td class="tdKey">备注</td>
									<td class="tdValue" data-key="remark"><input class="form-control input-sm" type="text"></td>
								</tr>
							</tbody>
						</table>
						<div >
							
							<button class="btn btn-default" style="padding: 5px 5px 5px 5px;" onClick="getNextTask();">稍后修改</button>
							<button class="btn btn-default" style="padding: 5px 5px 5px 5px;" onClick="keywordError();">资料错误</button>
							<button class="btn btn-default" style="padding: 5px 5px 5px 5px;" onClick="updatePOI();">新增</button>
							<button id="submitTask" class="btn btn-default" style="padding: 5px 5px 5px 5px;" onClick="submitEditTask();">提交</button>
							
						</div>
					</div>
				</div>
				<div class="col-md-10 fullHeight">
					<div style="position: absolute; top: 0; left: 0; right: 0; height: 50%;">
				    	<div class="mappanel" style="position: absolute; top: 0; left: 0; width: 24.8%; height: 100%;">
				    		<div class="panel panel-default">
							    <div class="panel-heading"><strong>EMG地图</strong></div>
							    <div class="panel-body">
							    	<div id="emgmap" style="height: 100%; z-index: 10;">加载中...</div>
							    </div>
							</div>
				    	</div>
				    	<div class="mappanel" style="position: absolute; top: 0; left: 25%; width: 24.8%; height: 100%;">
				    		<div class="panel panel-default">
							    <div class="panel-heading"><strong>百度地图</strong></div>
							    <div class="panel-body">
							    	<div id="baidumap" style="height: 100%; z-index: 10;">加载中...</div>
							    </div>
							</div>
				    	</div>
				    	<div class="mappanel" style="position: absolute; top: 0; left: 50%; width: 24.8%; height: 100%;">
				    		<div class="panel panel-default">
							    <div class="panel-heading"><strong>高德地图</strong></div>
							    <div class="panel-body">
							    	<div id="gaodemap" style="height: 100%; z-index: 10;">加载中...</div>
							    </div>
							</div>
				    	</div>
				    	<div class="mappanel" style="position: absolute; top: 0; left: 75%; width: 24.8%; height: 100%;">
				    		<div class="panel panel-default">
							    <div class="panel-heading"><strong>腾讯地图</strong></div>
							    <div class="panel-body">
							    	<div id="tengxunmap" style="height: 100%; z-index: 10;">加载中...</div>
							    </div>
							</div>
				    	</div>
					</div>
					<div style="position: absolute; left: 0; right: 0; bottom: 0; height: 50%;">
						<div style="position: absolute; top: 0; left: 0; width: 24.8%; height: 100%; overflow-y: scroll;">
							<table id="tbemg" class="table table-bordered table-condensed">
								<tbody><tr><td>加载中...</td></tr></tbody>
							</table>
				    	</div>
				    	<div style="position: absolute; top: 0; left: 25%; width: 24.8%; height: 100%; overflow-y: scroll;">
							<table  id="tbbaidu" class="table table-bordered table-condensed">
								<tbody><tr><td>加载中...</td></tr></tbody>
							</table>
				    	</div>
				    	<div style="position: absolute; top: 0; left: 50%; width: 24.8%; height: 100%; overflow-y: scroll;">
							<table id="tbgaode" class="table table-bordered table-condensed">
								<tbody><tr><td>加载中...</td></tr></tbody>
							</table>
				    	</div>
				    	<div style="position: absolute; top: 0; left: 75%; width: 24.8%; height: 100%; overflow-y: scroll;">
							<table id="tbtengxun" class="table table-bordered table-condensed">
								<tbody><tr><td>加载中...</td></tr></tbody>
							</table>
				    	</div>
					</div>
				</div>
			</div>
		</div>
		</c:when>
		<c:otherwise>
			<div class="well" style="width: 30em; margin: auto; margin-top: 150px;">
				<br>
				<img src="/poiwebeditor/resources/images/hasnotask.png" class="center-block img-rounded">
				<h2 class="text-center">没有任务了</h2>
			</div>
		</c:otherwise>
	</c:choose>
	<div id="featcodePOI" style="display: none;">
		<div class="dtree" style="height: 400px;">
			<p><a href="javascript: d.openAll();">全部展开</a> | <a href="javascript: d.closeAll();">全部折叠</a></p>
			<script type="text/javascript">
				var d = featcodetree();
				document.write(d);
			</script>
		</div>
	</div>
	<div id="sortcodePOI" style="display: none;">
		<div class="sortcheck" style="height: 400px;">
			<table>
				<tbody>
				</tbody>
			</table>
		</div>
	</div>
	<div class="footline">
		<div><span>当前项目编号：</span><span id="curProcessID">${process.id}</span></div>
		<div><span>当前项目：</span><span id="curProcessName">${process.name}</span></div>
		<div><span>项目公有/私有：</span><span id="curProjectOwner">
			<c:set var="owner" value="${project.owner == 1 ? '私有' : '公有' }"/>
			<c:out value="${owner }"></c:out>
		</span></div>
		<div><span>当前任务编号：</span><span id="curTaskID">${task.id}</span></div>
		<div><input type="hidden" id="curProjectID" value="${task.projectid}"></div>
	</div>
</body>
</html>