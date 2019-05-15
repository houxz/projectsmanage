<%@page import="com.emg.poiwebeditor.common.SrcTypeEnum"%>
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
<script type="text/javascript"  src="https://unpkg.com/leaflet.vectorgrid@1.2.0"></script>
   
<script type="text/javascript">
	var $emgmap = null, $baidumap = null, $gaodemap = null, $tengxunmap = null;
	var $emgmarker = null, $baidumarker = null, $gaodemarker = null, $tengxunmarker = null;
	var keywordid = eval('(${keywordid})');
	var zoom = 17;
	
	var loaderr = "<span class='red'>加载失败</span>";
	var redMarker = L.AwesomeMarkers.icon({
	    	icon: 'coffee',
	    	markerColor: 'red'
	  	});
	
	$(document).ready(function() {
		$.webeditor.getHead();
		
		loadKeyword(keywordid);
		
		loadReferdatas(keywordid);
		
	});
	
	function loadKeyword(keywordid) {
		jQuery.post("./edit.web", {
			"atn" : "getkeywordbyid",
			"keywordid" : keywordid
		}, function(json) {
			if (json && json.result == 1) {
				var keyword = json.rows;
				$("table#tbKeyword>tbody tr td.tdValue[data-key='name']").html(keyword.name);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='address']").html(keyword.address);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']").html(keyword.telephone);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='categoryName']").html(keyword.categoryName);
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
	
	function drawEMGMap(lat, lng, zoom) {
			if ($emgmap) {
				$emgmap.setCenter([lng, lat]);
			} else {
				$emgmap = new emapgo.Map({
			        container : 'emgmap',
			        style: 'http://tiles.emapgo.cn/styles/outdoor/style.json',
			        zoom: zoom-1,
			        center: [lng, lat]
			    });
			}
			
			if ($emgmarker) {
				$emgmarker.setLngLat([lng, lat ]);
			} else {
				$emgmarker = new emapgo.Marker()
					.setLngLat([lng, lat ])
					.addTo($emgmap);
			}
	}
	
	function drawBaiDuMap(lat, lng, zoom) {
		try{
			if ($baidumap) {
				$baidumap.setView([lat, lng]);
			} else {
				$baidumap = L.map('baidumap', {
					zoomControl : false,
					center:  [lat, lng],
					zoom: zoom+1,
					crs: L.CRS.Baidu,
					layers : [new L.tileLayer.baidu({ layer: 'custom'})]
				});
			}
			
			if ($baidumarker) {
				$baidumarker.setLatLng([lat, lng]);
			} else {
				$baidumarker = L.marker([lat, lng], {icon: redMarker}).addTo($baidumap);
			}
		} catch(e) {
			
		}
	}
	
	function drawGaoDeMap(lat, lng, zoom) {
		try{
			if ($gaodemap) {
				$gaodemap.setView([lat, lng]);
			} else {
				$gaodemap = L.map('gaodemap', {
					zoomControl : false,
					center:  [lat, lng],
					zoom: zoom,
					layers : [L.tileLayer.chinaProvider('GaoDe.Normal.Map', {})]
				});
			}
			
			if ($gaodemarker) {
				$gaodemarker.setLatLng([lat, lng]);
			} else {
				$gaodemarker = L.marker([lat, lng], {icon: redMarker}).addTo($gaodemap);
			}
		} catch(e) {
			
		}
	}
	
	function drawTengXunMap(lat, lng, zoom) {
		try{
			if ($tengxunmap) {
				$tengxunmap.setView([lat, lng]);
			} else {
				$tengxunmap = L.map('tengxunmap', {
					zoomControl : false,
					center:  [lat, lng],
					zoom: zoom,
					layers : [L.tileLayer.chinaProvider('TengXun.Normal.Map', {
						subdomains: '0123',
						tms:true
					})]
				});
			}
			
			if ($tengxunmarker) {
				$tengxunmarker.setLatLng([lat, lng]);
			} else {
				$tengxunmarker = L.marker([lat, lng], {icon: redMarker}).addTo($tengxunmap);
			}
		} catch(e) {
			
		}
	}
	
	function loadEditPOI(oid) {
		jQuery.post("./edit.web", {
			"atn" : "getpoibyoid",
			"oid" : oid
		}, function(json) {
			$("table#tbEdit>tbody td.tdValue>input:text").val("");
			if (json && json.result == 1) {
				var poi = json.poi;
				$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(poi.id);
				$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val(poi.namec);
				$("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val(poi.featcode);
				$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val(poi.sortcode);
				
				poi.poitags.forEach(function(tag, index) {
					$("table#tbEdit>tbody td.tdValue[data-key='" + tag.k +"']>input:text").val(tag.v);
				});
			} else {
				$("table#tbEdit>tbody td.tdValue>input:text").val("加载失败");
			}
		}, "json");
	}
	
	function rdChange(srcType, lat, lng, srcInnerId) {
		if (srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			loadEditPOI(srcInnerId);
			$emgmarker.setLngLat([lng, lat ]);
			$emgmap.setCenter([lng, lat]);
		} else if (srcType == <%=SrcTypeEnum.BAIDU.getValue() %>) {
			$baidumarker.setLatLng([lat, lng]);
			$baidumap.setView([lat, lng]);
		} else if (srcType == <%=SrcTypeEnum.TENGXUN.getValue() %>) {
			$tengxunmarker.setLatLng([lat, lng]);
			$tengxunmap.setView([lat, lng]);					
		} else if (srcType == <%=SrcTypeEnum.GAODE.getValue() %>) {
			$gaodemarker.setLatLng([lat, lng]);
			$gaodemap.setView([lat, lng]);
		} else {
			console.log("Error on srcType: " + srcType);
			return;
		}
	}
	
	function textCopy(obj) {
		var $this = $(obj);
		var value = $this.parent().prev().html();
		var key = $this.parent().prev().data("key");
		
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>input:text").val(value);
	}
	
	function drawReferdatas(tbid, referdatas) {
		var $tbody = $("table#" + tbid + ">tbody");
		$tbody.empty();
		referdatas.forEach(function(referdata, index) {
			var html = new Array();
			html.push("<tr><td class='tdIndex' rowspan='5'>");
		    html.push("<input type='radio' name='rd" + tbid + "' onChange='rdChange(" + referdata.srcType + "," + referdata.srcLat + "," + referdata.srcLon + ",\"" + referdata.srcInnerId + "\");' value='" + referdata.id + "' " + (index == 0 ? 'checked':'') + ">");
		    html.push("<span class='glyphicon glyphicon-share cursorable'></span></td></tr>");
		    
		    html.push("<tr><td class='tdKey'>名称</td>");
		    html.push("<td class='tdValue' data-key='name'>" + referdata.name + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr><td class='tdKey'>电话</td>");
		    html.push("<td class='tdValue' data-key='telephone'>" + referdata.tel + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr><td class='tdKey'>分类</td>");
		    html.push("<td class='tdValue' data-key='class'>" + referdata.orgCategoryName + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr><td class='tdKey'>地址</td>");
		    html.push("<td class='tdValue' data-key='address'>" + referdata.address + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    $tbody.append(html.join(''));
		});
	}
	
	function loadReferdatas(keywordid) {
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
					
					emgrefers.sort(refercompare);
					baidurefers.sort(refercompare);
					gaoderefers.sort(refercompare);
					tengxunrefers.sort(refercompare);
					
					loadEditPOI(emgrefers[0].srcInnerId);
					
					drawEMGMap(emgrefers[0].srcLat, emgrefers[0].srcLon, zoom);
					drawBaiDuMap(baidurefers[0].srcLat, baidurefers[0].srcLon, zoom);
					drawGaoDeMap(gaoderefers[0].srcLat, gaoderefers[0].srcLon, zoom);
					drawTengXunMap(tengxunrefers[0].srcLat, tengxunrefers[0].srcLon, zoom);
					
					drawReferdatas("tbemg", emgrefers);
					drawReferdatas("tbbaidu", baidurefers);
					drawReferdatas("tbgaode", gaoderefers);
					drawReferdatas("tbtengxun", tengxunrefers);
				}
			} else {
				
			}
		}, "json");
	}
	
	function getNextEditTask(taskid) {
		jQuery.post("./edit.web", {
			"atn" : "getnextedittask",
			"taskid" : taskid
		}, function(json) {
			if (json && json.result == 1) {
				var task = json.task;
				var process = json.process;
				var project = json.project;
				$("#curProcessID").text(process.id);
				$("#curProcessName").text(process.name);
				$("#curProjectOwner").text(project.owner == 1 ? '私有' : '公有');
				$("#curTaskID").text(task.id);
				$("#curProjectID").text(task.projectid);
				keywordid = json.keywordid;
				loadKeyword(keywordid);
				loadReferdatas(keywordid);
			} else {
				console.log("getNextEditTask error");
			}
		}, "json");
	}
</script>
</head>
<body>
	<div id="headdiv"></div>
	<div class="containerdiv">
		<div class="row-fluid fullHeight">
			<div class="col-md-2 fullHeight">
				<div style="position: absolute; top: 3%; left: 0; right: 0; height: 180px;">
					<table id="tbKeyword" class="table table-bordered table-condensed">
						<thead>
					    	<tr>
					      		<th></th>
					      		<th>参考数据</th>
					      		<th></th>
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
				<div style="position: absolute; top: 185px; left: 0; right: 0; bottom: 60px;">
					<table id="tbEdit" class="table table-bordered table-condensed">
						<thead>
					    	<tr>
					      		<th></th>
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
								<td class="tdValue" data-key="telephone"><input class="form-control input-sm" type="text"></td>
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
						</tbody>
					</table>
				</div>
				<div style="position: absolute; left: 0; right: 0; bottom: 12px; height: 38px; text-align: center;">
					<button class="btn btn-default">稍后修改</button>
					<button class="btn btn-default" onClick="getNextEditTask();">保存</button>
					<button class="btn btn-default">提交</button>
				</div>
			</div>
			<div class="col-md-10 fullHeight">
				<div style="position: absolute; top: 0; left: 0; right: 0; height: 50%;">
			    	<div class="mappanel" style="position: absolute; top: 0; left: 0; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>EMG地图</strong></div>
						    <div class="panel-body">
						    	<div id="emgmap" style="height: 100%;"></div>
						    </div>
						</div>
			    	</div>
			    	<div class="mappanel" style="position: absolute; top: 0; left: 25%; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>百度地图</strong></div>
						    <div class="panel-body">
						    	<div id="baidumap" style="height: 100%;"></div>
						    </div>
						</div>
			    	</div>
			    	<div class="mappanel" style="position: absolute; top: 0; left: 50%; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>高德地图</strong></div>
						    <div class="panel-body">
						    	<div id="gaodemap" style="height: 100%;"></div>
						    </div>
						</div>
			    	</div>
			    	<div class="mappanel" style="position: absolute; top: 0; left: 75%; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>腾讯地图</strong></div>
						    <div class="panel-body">
						    	<div id="tengxunmap" style="height: 100%;"></div>
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