<%@ page import="com.emg.poiwebeditor.common.SrcTypeEnum"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>改错</title>
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

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
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
   

   
<script type="text/javascript">
	var $emgmap = null, $baidumap = null, $gaodemap = null, $tengxunmap = null;
	var $emgmarker = null, $baidumarker = null, $gaodemarker = null, $tengxunmarker = null;
	var srcType, srcInnerId, baiduSrcInnerId, baiduSrcType, gaodeSrcInnerId, gaodeSrcType, tengxunSrcInnerId, tengxunSrcInnerId, tengxunSrcType, emgSrcInnerId, emgSrcType, dianpingGeo;
	var emgDel, baiduDel, tengxunDel, gaodeDel;
	var keywordid = eval('(${keywordid})');
	var zoom = 17;
	var shapegeo;
	var poigeo;


	var loaderr = "<span class='red'>加载失败</span>";
// 	var redMarker = L.AwesomeMarkers.icon({
// 	    	icon: 'tag',
// 	    	markerColor: 'blue',
// 	    	iconColor:'white'
// 	  	});
// 	var editMarker = L.AwesomeMarkers.icon({
//     	icon: 'edit',
//     	markerColor: 'red',
//     	iconColor:'white'
//   	});
// 	var referMarker = L.AwesomeMarkers.icon({
//     	icon: 'eye-open',
//     	markerColor: 'blue',
//     	iconColor:'white'
//   	});
// 	var matchMarker = L.AwesomeMarkers.icon({
//     	icon: 'tag',
//     	markerColor: 'blue',
//     	iconColor:'white'
//   	});
	
	$(document).ready(function() {
		$.webeditor.getHead();
	
		if (keywordid && keywordid > 0) {
 			loadKeyword(keywordid);
			//改错不需要加载四方检索结果20190524
//   		loadReferdatas(keywordid);
			loadEditPOI( ${poiid});
			drawErrorList();
			
		}else {
			$.webeditor.showMsgLabel("alert", "没有获取到参考资料");
		}
	});
	
	
	function loadKeyword(keywordid) {
		jQuery.post("./modify.web", {
			"atn" : "getkeywordbyid",
			"keywordid" : keywordid
		}, function(json) {
			if (json && json.result == 1) {
				keyword = json.rows;
				srcType = keyword.srcType;
				srcInnerId = keyword.srcInnerId;
				
				dianpingGeo = keyword.geo;
				$("table#tbKeyword>tbody tr td.tdValue[data-key='name']").html(keyword.name);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='address']").html(keyword.address);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']").html(keyword.telephone);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='categoryName']").html(keyword.categoryName);						
				shapegeo = keyword.geo;
				//两个地方都绘制是因为jsp执行是线程级别，不能确定谁先执行完
				addMakerOnEMGMap($emgmap,true);
			} else {
				$("table#tbKeyword>tbody tr td.tdValue[data-key='name']").html(loaderr);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='address']").html(loaderr);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']").html(loaderr);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='categoryName']").html(loaderr);
			}
		}, "json");
	}
	
	function refercompare(a, b) {
		return a.sequence - b.sequence;
	}
	

		function addMakerOnEMGMap(map, isEmg ) {
		if (map == null || dianpingGeo == null) return;
		var img = new Image();
		// img.src = 'http://m.emapgo.cn/demo/electricize/img/poi_center.png';
		img.src = "resources/images/start.png";
		var geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
		if(isEmg) {
				// POINT (102.486719835069 24.9213802083333)
			
			var marker = new emapgo.Marker(img		
			)
			.setLngLat(geo)
			.addTo(map);
		}else{
			var myIcon = L.icon({
			    iconUrl: 'resources/images/start.png',
			    iconSize: [31, 40]
			});
			L.marker([geo[1], geo[0]], {icon: myIcon}).addTo(map);
		}
	}
	
	
	function drawEMGMap(lat, lng, zoom) {
		try{
			if ($emgmap) {
				$emgmap.setCenter([lng, lat]);
			} else {
				$("#emgmap").empty();
				$emgmap = new emapgo.Map({
			        container : 'emgmap',
			        style: 'http://tiles.emapgo.cn/styles/outdoor/style.json',
			        zoom: zoom-1,
			        center: [lng, lat],
			        localIdeographFontFamily : "'Noto Sans', 'Noto Sans CJK SC', sans-serif"
			    });
				$emgmap.on('styledata',function(){
					$emgmap.setPaintProperty('china-building', 'fill-extrusion-height', 0);
				})
				$emgmap.on('click', function(e) {
					if (e && $emgmarker) {
						$emgmarker.setLngLat([e.lngLat.lng,e.lngLat.lat]);
						dianpingGeo = 'MULTIPOINT (' + e.lngLat.lng + " " + e.lngLat.lat + "," +  + e.lngLat.lng + " " + e.lngLat.lat +")";//?
						poigeo = 'MULTIPOINT (' + e.lngLat.lng + " " + e.lngLat.lat + "," +  + e.lngLat.lng + " " + e.lngLat.lat +")";
					}
					console.log(e);
					});
			}
			
			if ($emgmarker) {
				$emgmarker.setLngLat([lng, lat ]);
			} else {
				$emgmarker = new emapgo.Marker()
				.setLngLat([lng, lat ])
				.addTo($emgmap);
			$emgmarker.on('click', function(e) {
				console.log("in the maker");
				console.log(e);
				});
			}
		} catch(e) {
			
		}
	}
	
	function loadErrorList(){
		jQuery.post("./modify.web", {
			"atn" : "geterrorlistbyid",
			"keywordid" : keywordid
		}, function(json) {
			if (json && json.result == 1) {
				var tbody = $("#errorlist tbody");
				tbody.empty();
				var html = new Array();
				
				var errorlist = json.errorlist;
				var count = errorlist.length;
				for(var i = 0 ;i < count ; i++){
					html.push("<tr>");
					html.push('<td class="tdKey">错误id</td>');
					html.push('<td class="tdValue" data-key="id">'+errorlist[i].id+'</td>');
					html.push("</tr>");
					html.push("<tr>");
					html.push('<td class="poiid">poiid</td>');
					html.push('<td class="poiidValue" data-key="featureid">'+errorlist[i].featureid+'</td>');
					html.push("</tr>");
					html.push("<tr>");
					html.push('<td class="remark">描述</td>');
					html.push('<td class="remarkValue" data-key="remark">'+errorlist[i].errorremark+'</td>');
					html.push("</tr>");
				}
				tbody.append(html.join(""));
			}
			
		}, "json");
	}
	
	function drawErrorList(){
		
		loadErrorList();
	
		
	}
	
	function loadEditPOI(oid) {
		if (!oid || oid <= 0) 	return;
		
		jQuery.post("./modify.web", {
			"atn" : "getpoibyoid",
			"oid" : oid
		}, function(json) {
			$("table#tbEdit>tbody td.tdValue>input:text").val("");
			if (json && json.result == 1 && json.poi != null) {
				var poi = json.poi;
				$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(poi.id);
				$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val(poi.namec);
				$("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val(poi.featcode);
				$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val(poi.sortcode);
				$("table#tbEdit>tbody td.tdValue[data-key='owner']>input:text").val(poi.owner);
				dianpingGeo = poi.geo;
				poi.poitags.forEach(function(tag, index) {
					$("table#tbEdit>tbody td.tdValue[data-key='" + tag.k +"']>input:text").val(tag.v);
				});
				
				//坐标异常
				if( poi.geo.indexOf("MULTIPOINT") <=-1){
					$("table#tbEdit>tbody td.tdValue>input:text").val("加载失败:坐标异常");
				}
				else{
				
	 			var geo = poi.geo.replace("MULTIPOINT ((","").replace("),","").replace("(","").replace(")", "").split(" ");
	 			poigeo = geo;
	 			drawEMGMap(geo[1], geo[0], zoom);
	 			addMakerOnEMGMap($emgmap,true);
				}
			} else {
				$("table#tbEdit>tbody td.tdValue>input:text").val("加载失败");
			}
		}, "json");
	}
	
	function textCopy(obj) {
		var $this = $(obj);
		var value = $this.parent().prev().html();
		var key = $this.parent().prev().data("key");
		
		key = (key == "telephone" ? "tel" : key);
		
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>textarea").val(value);
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>input:text").val(value);
	}
	
	function submitEditTask() {
		var oid = null;
		try {
			oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		} catch(e) {
			return;
		}
		if (!oid || oid <= 0) 	return;
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode = $("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val();
		var sortcode = $("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val();
		var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val();
		var namep 	 = $("table#tbEdit>tbody td.tdValue[data-key='namep']>input:text").val();
		var names 	 = $("table#tbEdit>tbody td.tdValue[data-key='names']>input:text").val();
		var namee 	 = $("table#tbEdit>tbody td.tdValue[data-key='namee']>input:text").val();
		var namesp 	 = $("table#tbEdit>tbody td.tdValue[data-key='namesp']>input:text").val();
		var address1 = $("table#tbEdit>tbody td.tdValue[data-key='address1']>input:text").val();
		var address1p= $("table#tbEdit>tbody td.tdValue[data-key='address1p']>input:text").val();
		var address1e= $("table#tbEdit>tbody td.tdValue[data-key='address1e']>input:text").val();
		var address2 = $("table#tbEdit>tbody td.tdValue[data-key='address2']>input:text").val();
		var address2p= $("table#tbEdit>tbody td.tdValue[data-key='address2p']>input:text").val();
		var address2e= $("table#tbEdit>tbody td.tdValue[data-key='address2e']>input:text").val();
		var address3 = $("table#tbEdit>tbody td.tdValue[data-key='address3']>input:text").val();
		var address3p= $("table#tbEdit>tbody td.tdValue[data-key='address3p']>input:text").val();
		var address3e= $("table#tbEdit>tbody td.tdValue[data-key='address3e']>input:text").val();
		var owner 	 = $("table#tbEdit>tbody td.tdValue[data-key='owner']>input:text").val();
		var postalcode= $("table#tbEdit>tbody td.tdValue[data-key='postalcode']>input:text").val();
		var projectId = $("#curProjectID").val();
		$.webeditor.showMsgBox("info", "数据保存中...");
		jQuery.post("./modify.web", {
			"atn" : "submitmodifytask",
			"taskid" : $("#curTaskID").html(),
			"getnext" : true,
			"srcType":srcType,
			"srcInnerId": srcInnerId,
			"emgSrcInnerId": emgSrcInnerId,
			"emgSrcType": emgSrcType,
			"namec": namec,
			"oid": oid,
			"tel": tel,
			"featcode" : featcode,
			"sortcode" : sortcode,
			"address4" : address4,
			"address5" : address5,
			"address6" : address6,
			"address7" : address7,
			"address8" : address8,
			"namep"	   : namep,
			"names"    : names,
			"namee"    : namee,
			"namesp"   : namesp,
			"address1" : address1,
			"address1p": address1p,
			"address1e": address1e,
			"address2" : address2,
			"address2p": address2p,
			"address2e": address2e,
			"address3" : address3,
			"address3p": address3p,
			"address3e": address3e,
			"owner"	   : owner,
			"projectId": projectId,
			"postalcode": postalcode,
			"dianpingGeo" : dianpingGeo,
			"poigeo":poigeo
		}, function(json) {
			if (json && json.result == 1) {
				var task = json.task;
				if (task && task.id) {
					var process = json.process;
					var project = json.project;
					var poiid = json.poiid;
					$("#curProcessID").text(process.id);
					$("#curProcessName").text(process.name);
					$("#curProjectOwner").text(project.owner == 1 ? '私有' : '公有');
					$("#curTaskID").text(task.id);
					$("#curProjectID").text(task.projectid);
					keywordid = json.keywordid;
					if (keywordid && keywordid > 0) {
						loadKeyword(keywordid);
						loadEditPOI(poiid);
						drawErrorList();
					}
				} else {
					$.webeditor.showMsgBox("close");
					$.webeditor.showConfirmBox("info","没有任务了");
					window.location.reload();
				}
				
			} else {
				console.log("submitEditTask error");
			}
		});
			$.webeditor.showMsgBox("close");
	}
	
	function deletePOI(obj) {
		var oid = null;
		try {
			oid = $(obj).parent().prev().children()[0].value;
		} catch(e) {
			return;
		}
		if (!oid || oid <= 0) 	return;
		
		$.webeditor.showConfirmBox("alert","确定要删除这个POI吗？", function(){
			$.webeditor.showMsgBox("info", "数据保存中...");
			jQuery.post("./edit.web", {
				"atn" : "deletepoibyoid",
				"oid" : oid
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
		if (!oid || oid <= 0) 	return;
	
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode = $("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val();
		var sortcode = $("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val();
		var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val();
		var namep 	 = $("table#tbEdit>tbody td.tdValue[data-key='namep']>input:text").val();
		var names 	 = $("table#tbEdit>tbody td.tdValue[data-key='names']>input:text").val();
		var namee 	 = $("table#tbEdit>tbody td.tdValue[data-key='namee']>input:text").val();
		var namesp 	 = $("table#tbEdit>tbody td.tdValue[data-key='namesp']>input:text").val();
		var address1 = $("table#tbEdit>tbody td.tdValue[data-key='address1']>input:text").val();
		var address1p= $("table#tbEdit>tbody td.tdValue[data-key='address1p']>input:text").val();
		var address1e= $("table#tbEdit>tbody td.tdValue[data-key='address1e']>input:text").val();
		var address2 = $("table#tbEdit>tbody td.tdValue[data-key='address2']>input:text").val();
		var address2p= $("table#tbEdit>tbody td.tdValue[data-key='address2p']>input:text").val();
		var address2e= $("table#tbEdit>tbody td.tdValue[data-key='address2e']>input:text").val();
		var address3 = $("table#tbEdit>tbody td.tdValue[data-key='address3']>input:text").val();
		var address3p= $("table#tbEdit>tbody td.tdValue[data-key='address3p']>input:text").val();
		var address3e= $("table#tbEdit>tbody td.tdValue[data-key='address3e']>input:text").val();
		var owner 	 = $("table#tbEdit>tbody td.tdValue[data-key='owner']>input:text").val();
		var postalcode= $("table#tbEdit>tbody td.tdValue[data-key='postalcode']>input:text").val();
		var projectId = $("#curProjectID").val();
		$.webeditor.showMsgBox("info", "数据保存中...");
		jQuery.post("./modify.web", {
			"atn" : "updatepoibyoid",
			"taskid" : $("#curTaskID").html(),
			"getnext" : false,
			"srcType":srcType,
			"srcInnerId": srcInnerId,
			"emgSrcInnerId": emgSrcInnerId,
			"emgSrcType": emgSrcType,
			"namec": namec,
			"oid": oid,
			"tel": tel,
			"featcode" : featcode,
			"sortcode" : sortcode,
			"address4" : address4,
			"address5" : address5,
			"address6" : address6,
			"address7" : address7,
			"address8" : address8,
			"namep"	   : namep,
			"names"    : names,
			"namee"    : namee,
			"namesp"   : namesp,
			"address1" : address1,
			"address1p": address1p,
			"address1e": address1e,
			"address2" : address2,
			"address2p": address2p,
			"address2e": address2e,
			"address3" : address3,
			"address3p": address3p,
			"address3e": address3e,
			"owner"	   : owner,
			"projectId": projectId,
			"postalcode": postalcode,
			"dianpingGeo" : dianpingGeo,
			"poigeo":poigeo
		}, function(json) {
			if (json && json.result > 0) {
				$.webeditor.showMsgLabel("success", "保存成功");
			} else {
				$.webeditor.showMsgLabel("alert", "保存失败");
			}
			$.webeditor.showMsgBox("close");
		}, "json");
	}
</script>
</head>
<body>
	<div id="headdiv"></div>
	<c:choose>
		<c:when test="${task != null and task.id != null}">
		<div class="containerdiv">
			<div class="row-fluid fullHeight">
<!-- 			暂时分三块：左中右 ： 资料，错误，地图  -->
				<div class="col-md-2 fullHeight">
					<div style="position: absolute; top: 3%; left: 0; right: 0; height: 200px;">
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
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr>
								<tr>
									<td class="tdKey">地址</td>
									<td class="tdValue" data-key="address">加载中...</td>
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr>
								<tr>
									<td class="tdKey">电话</td>
									<td class="tdValue" data-key="telephone">加载中...</td>
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr>
								<tr>
									<td class="tdKey">分类</td>
									<td class="tdValue" data-key="categoryName">加载中...</td>
									<td class="tbTool"><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div style="position: absolute; top: 205px; left: 0; right: 0; bottom: 60px;">
					<div id="divpois" style="overflow-y: auto; width: 100%; height: 100%;">
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
									<td class="tdValue" data-key="name"><textarea class="form-control input-sm"></textarea></td>
								</tr>
								<tr>
									<td class="tdKey">电话</td>
									<td class="tdValue" data-key="tel"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">类型</td>
									<td class="tdValue" data-key="featcode"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">系列</td>
									<td class="tdValue" data-key="sortcode"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
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
									<td class="tdValue" data-key="address8"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">拼音名称</td>
									<td class="tdValue" data-key="namep"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">简称</td>
									<td class="tdValue" data-key="names"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">英文正式名称</td>
									<td class="tdValue" data-key="namee"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">拼音简称</td>
									<td class="tdValue" data-key="namesp"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址（省级）</td>
									<td class="tdValue" data-key="address1"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址拼音（省级）</td>
									<td class="tdValue" data-key="address1p"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址英文（省级）</td>
									<td class="tdValue" data-key="address1e"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址（地级）</td>
									<td class="tdValue" data-key="address2"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址拼音（地级）</td>
									<td class="tdValue" data-key="address2p"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址英文（地级）</td>
									<td class="tdValue" data-key="address2e"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址（区县级）</td>
									<td class="tdValue" data-key="address3"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址拼音（区县级）</td>
									<td class="tdValue" data-key="address3p"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">详细地址英文（区县级）</td>
									<td class="tdValue" data-key="address3e"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">所属行政区域代码</td>
									<td class="tdValue" data-key="owner"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey">邮政代码</td>
									<td class="tdValue" data-key="postalcode"><input class="form-control input-sm" type="text"></td>
								</tr>
							</tbody>
						</table>
					</div>
					</div>
					<div style="position: absolute; left: 0; right: 0; bottom: 12px; height: 38px; text-align: center;">
<!-- 						<button class="btn btn-default">稍后修改</button> -->
						<button class="btn btn-default" onClick="updatePOI();">保存</button>
						<button class="btn btn-default" onClick="submitEditTask();">提交</button>
					</div>
				</div>
				<div class="col-md-2 fullHeight" >
					<div style="position:absolute;top:25px;left:0:right:200px;height:250px">
						<div class="errorpanel" style="position:absolute;top:0;left:0;width:290px;height:100%">
							<table id="errorlist" class="table table-bordered table-condensed">
								<thead>
									<tr>
										<th><span class="glyphicon glyphicon-eye-open"></span></th>
										<th>质检错误列表</th>
									</tr>
								</thead>
									<tbody>
									</tbody>
								</table>
						</div>
					</div>
				</div>
				<div class="col-md-8 fullHeight">
					<div style="position: absolute; top: 0; left: 0; right: 0; height: 100%;">
				    	<div class="mappanel" style="position: absolute; top: 0; left: 0; width: 99.2%; height: 100%;">
				    		<div class="panel panel-default">
							    <div class="panel-heading"><strong>EMG地图</strong></div>
							    <div class="panel-body">
							    	<div id="emgmap" style="height: 100%; z-index: 10;">加载中...</div>
							    </div>
							</div>
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