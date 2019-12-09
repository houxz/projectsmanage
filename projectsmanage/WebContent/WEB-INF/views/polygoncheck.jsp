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
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css" rel="stylesheet">
<link href="https://unpkg.com/jquery-resizable-columns@0.2.3/dist/jquery.resizableColumns.css" rel="stylesheet">
<link href="resources/js/leaflet/leaflet.css" rel="stylesheet" />
<link href="http://code.ionicframework.com/ionicons/1.5.2/css/ionicons.min.css" rel="stylesheet">
<link href="resources/leaflet.awesome-markers-2.0/leaflet.awesome-markers.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>

<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap-table-resizable.js"></script>
<script src="https://unpkg.com/jquery-resizable-columns@0.2.3/dist/jquery.resizableColumns.min.js"></script>
	
<link href='https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/String/Trim' />
<script src="resources/js/leaflet/leaflet.js"></script>
<script src="resources/js/leaflet/leaflet-src.js"></script>
<script src="resources/js/leaflet.ChineseTmsProviders.js"></script>
<script src="resources/js/proj4-compressed.js"></script>
<script src="resources/js/proj4leaflet.js"></script>

<script src="resources/leaflet.awesome-markers-2.0/leaflet.awesome-markers.min.js"></script>
<script src='resources/dtree/dtree.js'></script>

<script src="resources/js/leaflet/lib/leaflet.toolbar.js"></script>
<script src="resources/js/leaflet/lib/leaflet.label.js"></script>
<script src="resources/js/leaflet/lib/leaflet.markercluster.js"></script>
<link href="resources/js/leaflet/lib/MarkerCluster.css" rel="stylesheet" />
<link href="resources/js/leaflet/lib/MarkerCluster.Default.css" rel="stylesheet" />
<script src="resources/js/leaflet/editableMarkercluster/leaflet.markercluster.editablemarker.js"></script>
<link rel="stylesheet" href="resources/js/leaflet/leaflet.contextmenu.css" />
<script src="resources/js/leaflet/leaflet.contextmenu.js"></script>
<script src="resources/js/map.js"></script>
<link href="resources/css/edit.css?v=<%=System.currentTimeMillis() %>" rel="stylesheet" />
<script src="resources/js/basemap.js?v=<%=System.currentTimeMillis() %>"></script>
<script src='resources/js/featcodeget.js?v=<%=System.currentTimeMillis() %>'></script>
<script src="resources/js/featcoderegex.js?v=<%=System.currentTimeMillis() %>"></script>
<script src="resources/js/coordtransform.js?v=<%=System.currentTimeMillis() %>"></script>
</head>
<body>
	<div id="headdiv"></div>
	<c:choose>
		<c:when test="${task != null and task.id != null}">
		<div class="containerdiv">
		<div class="row-fluid fullHeight">
			
			<div class="col-md-2 fullHeight" id="divpoilist">
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
									<td class="tdKey">ID</td>
									<td class="tdValue" data-key="id"><input class="form-control input-sm" type="text" disabled></td>
								</tr>
								<tr>
									<td class="tdKey">名称</td>
									<td class="tdValue" data-key="name">加载中...</td>
									<td class="tbTool" ><span class="glyphicon glyphicon-share cursorable" onClick="textCopy(this);"></span></td>
								</tr>
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
								
								<tr>
									<td class="tdKey">类型</td>
									<td class="tdValue" data-key="featcode">
									<!-- <input id="featcode" class="form-control input-sm" type="text"> -->
									<div class="input-group">
										<input id="featcode" onchange="valueChange(this)" class="form-control input-sm" type="text" >
										<span id="featcodeSpan" class="input-group-addon" style="cursor: pointer;" onClick="dlgFeatcodePOIConfig(-1, this);" title="选择类型代码" disabled >选择</span>
									</div>
									
									</td>
								</tr>
								<tr>
											<td class="tdKey">中文</td>
											<td class='tdValue'><input type="text" 
											class="form-control input-sm" id="featcodename"  disabled="disabled"
											placeholder="类型代码中文"></td>
										</tr>
								<!-- <tr style="display: none;" > -->
								<tr  >
									<td class="tdKey">系列</td>
									<td class="tdValue" data-key="sortcode">
										<!-- <input class="form-control input-sm" type="text"> -->
										<div class="input-group">
											<input type="text" onchange="valueChange(this)" class="form-control" id="sortcode" placeholder="请输入系列代码" >
											<span id="sortcodeSpan" class="input-group-addon" style="cursor: pointer;" onClick="dlgSortcodeConfig(-1, this);" title="选择系列代码" disabled>选择</span>
										</div>
									</td>
								</tr>
								<tr>
											<td class="tdKey">中文</td>
											<td class='tdValue'><input type="text" 
											class="form-control input-sm" id="sortcodename"  disabled="disabled"
											placeholder="系列代码中文"></td>
										</tr>
								
								<tr>
									<td class="tdKey">坐标</td>
									<td class="tdValue" data-key="geo"><input onchange="valueChange(this)" class="form-control input-sm" type="text"></td>
								</tr>
								<tr >
									<td class="tdKey">状态</td>
									<td class="tdValue" data-key="poistate">
										<select id="poistate" >
							                <option value= "0">正常</option>
							                <option value= "1">在建</option>
							                <option value= "2">不开放</option>
							                <option value= "3">维修</option>
							                <option value= "4">未调查</option>
						            	</select>
									</td>
								</tr>
								<tr style="display:none">
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
			<div class="col-md-10 fullHeight" id="divmap">
				<table id="tb_pano" border="0" width="100%" height='100%' >
					<div id="wrap1" class="dropdown">		
						  <span>展开功能</span>
						  <div class="dropdown-content">
						  	<!-- <a id="ashowbaidu" onclick=showBaiduMap()>显示百度</a> -->
						  	<a id="ashowpoianno" onclick=showPoiAnno() >显示道路(B)</a>
						  	<a id="ashowpoilist" class="selecta" onclick=showPoiList() >显示背景</a>
						  	<!-- <a id="ashowadmin" onclick=showAdmin() >显示区划</a>
						  	<a id="ashowpoi" onclick=showPoi() >显示POI</a> -->
						    <!-- <a id="asubmittask" onclick=submitTask() >提交任务(Ctrl+Enter)</a>		 -->			    
						  </div>
					</div>		
					<tr id="tr_pano" >
						<!-- <td id="tdbaidumap" width='0%' height='100%' style="display:none">
							<div id='baidumap' height='100%'>百度</div>
						</td> -->
						<td id="tdemgmap" style="position: absolute; top: 0; left: 0; right: 0; height: 70%;">
							<div id='emgmap'>.</div>			
						</td>	
						<td id="tdemgmap" style="position: absolute; left: 0; right: 0; bottom: 0; height: 30%;">
							<div style="position: absolute; left: 0; right: 0; bottom: 0; height: 100%;">
								<div style="position: absolute; top: 0; left: 0; width: 24.8%; height: 100%; overflow-y: scroll;">
								<div class="panel-heading"><strong>EMG</strong></div>
									<table id="tbemg" class="table table-bordered table-condensed">
										<tbody><tr><td>加载中...</td></tr></tbody>
									</table>
						    	</div>
						    	<div style="position: absolute; top: 0; left: 25%; width: 24.8%; height: 100%; overflow-y: scroll;">
						    	<div class="panel-heading"><strong>百度地图</strong></div>
									<table  id="tbbaidu" class="table table-bordered table-condensed">
										<tbody><tr><td>加载中...</td></tr></tbody>
									</table>
						    	</div>
						    	<div style="position: absolute; top: 0; left: 50%; width: 24.8%; height: 100%; overflow-y: scroll;">
						    	<div class="panel-heading"><strong>高德地图</strong></div>
									<table id="tbgaode" class="table table-bordered table-condensed">
										<tbody><tr><td>加载中...</td></tr></tbody>
									</table>
						    	</div>
						    	<div style="position: absolute; top: 0; left: 75%; width: 24.8%; height: 100%; overflow-y: scroll;">
						    	<div class="panel-heading"><strong>腾讯地图</strong></div>
									<table id="tbtengxun" class="table table-bordered table-condensed">
										<tbody><tr><td>加载中...</td></tr></tbody>
									</table>
						    	</div>
							</div>	
						</td>
					</tr>
				</table>
			</div>
		</div>
		</div>
		<div id="configDlg" style="display: none;">
		<table id="tbEdit" class="table table-bordered table-condensed">
			<thead>
		    	<tr>
		      		<th><span class="glyphicon glyphicon-edit"></span></th>
		      		<th>poi minzoom修改</th>
		    	</tr>
		  	</thead>
			<tbody>
				<tr>
					<td class="tdKey">OID</td>
					<td class="tdValue" data-key="oid"><input id="poi_oid" class="form-control input-sm" type="text" disabled></input></td>
				</tr>
				<tr>
					<td class="tdKey">名称</td>
					<td class="tdValue" data-key="name"><textarea id="poi_namec" class="form-control input-sm" disabled></textarea></td>
				</tr>
				<tr>
					<td class="tdKey">minzoom</td>
					<td class="tdValue" data-key="minzoom">
					<div class="input-group">
					     <span class="input-group-btn">
					       <button class="btn btn-default" type="button" id="buttonminusminzoom" data-toggle="tooltip" title="D" onclick="minusMinzoom()">-</button>
					     </span>
					     <input id = "poi_minzoom" class="form-control" type="text" data-toggle="tooltip" title="F:当前,C:0" 
					     		maxlength="2" 
					     	 	onkeyup="value=value.replace(/[^0-9]/g,'')" onpaste="value=value.replace(/[^0-9]/g,'')" 
					     	 	oncontextmenu = "value=value.replace(/[^0-9]/g,'')" >	
					     <span class="input-group-btn">
					       <button class="btn btn-default" type="button" id="buttonplusminzoom" data-toggle="tooltip" title="E" onclick="plusMinzoom()">+</button>
					     </span>
					</div>		 
					</td>
				</tr>
			</tbody>
		</table>
		<div>			
		<button id="btcanclepoi" class="btn btn-default"  data-toggle="tooltip" title="Q" onClick="canclePoi();">取消</button>
		<button id="btsavepoi" class="btn btn-warning" data-toggle="tooltip" title="S/Enter" onClick="savePoi();">保存</button>
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
			<c:set var="owner" value="${project.owner == 1 ? '私有' : (project.owner == 0 ? '公有' : '') }"/>
			<c:out value="${owner }"></c:out>
		</span></div>
		<div><span>当前任务编号：</span><span id="curTaskID">${task.id}</span></div>
		<div><span>模式:</span><span id="curModelName">
		<c:set var="modeltext" value='${model==1?"编辑":"浏览"}'/>
		<c:out value="${modeltext}"></c:out>
		</span></div>
		<div><input type="hidden" id="curProjectID" value="${task.projectid}"></div>
		<div><input type="hidden" id="taskname" value="${task.name}"></div>
		<div><input type="hidden" id="curModel" value="${model}"></div>	
	</div>
	
	<script>
	var $emgmap = null, $baidumap = null, $gaodemap = null, $tengxunmap = null;
	var $emgmarker = null, $baidumarker = null, $gaodemarker = null, $tengxunmarker = null;
	var $emgmarkerBase = null, $baidumarkerBase = null, $gaodemarkerBase = null, $tengxunmarkerBase = null;
	var dianpingGeo;
	var keywordid = eval('(${keywordid})');
	var keyword = null;
	var systemPoi = null;
	// var delFeatcoe = true;
	var systemOid = -1; // 当前编辑器左侧有OID
	// databaseSaveRelation: 用来存储数据库中保存着的relation 关系, originalCheckRelation: 数据库中存在emg和点评的关系，但该点在现在EMG中没有，单独记录，用来做提示框条件， currentCheckRelation： 数据库中有，且已经被选中的
	var databaseSaveRelation = [], originalCheckRelation = [], currentCheckRelation = [];
	
	var source = [];
	var zoom = 17;
	var submitflag = 0;
	var brandcode = [2040105,2070201,2070301,2060308,2040203,2080501,2080701,2080702,2080703,2100105,2100201,2110201,2110202,2110205];
	// 不允许编辑的类型
	var notEditCode = [2080101,2080102,2080103,2010101,2010102,2010103,2010104,2010105,2010106, 2010108];
	var loaderr = "<span class='red'>加载失败</span>";
	//emgCurrentMarker emg当前选中的项
	var emgCurrentMarker = null,emgMarker=L.layerGroup(), baiduMarker = L.layerGroup(), gaodeMarker = L.layerGroup(), tengxunMarker = L.layerGroup();
	// 当前选中的POI点是否修改了
	var poiEditFlag = false;
	// 是不是第一次保存POI点，只是保存POI点，而非提交任务
	var isFirstSavePoi = false;
	L.Marker.mergeOptions({
	    poi: null,
	    srcType: -1,
	    srcInnerId: ""
	  });
	
	var yellowIcon = L.icon({
	    iconUrl: 'resources/images/yellow.png'
	});
	
	var redIcon = L.icon({
	    iconUrl: 'resources/images/red.png'
	});
	var greenIcon = L.icon({
	    iconUrl: 'resources/images/green.png'
	});
	
	var lightblueIcon = L.icon({
	    iconUrl: 'resources/images/lightblue.png'
	});
	
	var redemgIcon = L.icon({
	    iconUrl: 'resources/images/red_emg.png'
	});
	var blueemgIcon = L.icon({
	    iconUrl: 'resources/images/blue_emg.png'
	});
	
	$(document).ready(function() {
		$.webeditor.getHead();
		
		console.log("hello edit: 111 " + Date.now());
		if (keywordid && keywordid > 0) {
			//console.log("开始加载数据: " + Date.now());
			$.when( 
				loadKeyword(keywordid)
			).done(function() {
				var geo = getGEO(dianpingGeo);
				
				initEmgmap([parseFloat(geo[1]), parseFloat(geo[0])]);
				tengxunMarker.addTo(emgmap);
				baiduMarker.addTo(emgmap);
				gaodeMarker.addTo(emgmap);
				emgMarker.addTo(emgmap);
				$.when(loadReferdatas(keywordid)
				).done(function() {
					if (keyword) {
						loadRelation(keyword.srcInnerId, keyword.srcType);
					}
				});
			});
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
		 $(document).keydown(function (event){
			 if (event.altKey &&  event.keyCode==86) {   
				 submitEditTask();
             }else if(event.altKey &&  event.keyCode==67) {   
				updatePOI();
             }
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
	
	function dlgFeatcodePOIConfig(band2, ele) {
		if($(ele).attr("disabled")) return;
		// delFeatcoe = false;
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
	
	function dlgSortcodeConfig(band2, ele) {
		if($(ele).attr("disabled")) return;
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
	
	function initArray() {
		originalCheckRelation = [];
		currentCheckRelation = [];
		emgCurrentMarker = null;
		// 判断点是否被编辑了
		poiEditFlag = false;
		isFirstSavePoi = false;
	}
	
	
	/* function initArray() {
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
	} */
	
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
				dianpingGeo = keyword.geo;
				$("table#tbKeyword>tbody tr td.tdValue[data-key='name']").html(keyword.name);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='id']").html(keyword.id);
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
			initArray();
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
									var k = j;
									if (emgCurrentMarker == null) {
										var geo = null;
										var geoTemp =$($(this).parents("tbody")[0]).find("td.tdValue[data-key='geo']")[0].innerText;
										if ($($(this).parents("tbody")[0]).find("td.tdValue[data-key='geo']")[0].innerText.indexOf("MULTIPOIN") >= 0) {
											var t = geoTemp.split(",")[1];
											geo = t.substring(0, t.length-1).split(" ");
										}else {
											geo = geoTemp.replace("POINT (","").replace(")", "").split(" ");
										}
										initEmgMarker(geo,databaseSaveRelation[j].srcType, databaseSaveRelation[j].srcInnerId);
									}
									$.when( 
											loadEditPOI(srcInnerId, emgFeatcode)
									).done(function() {
										source = [];
										
										currentCheckRelation.push(databaseSaveRelation[k]);
									});
									flags[i] = true;
									
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
			
			
			/* for (var i = 0; i < tbtables.length; i++) {
				if ( $("#" + tbtables[i] + " input:checkbox") && $("#" + tbtables[i] + " input:checkbox").length > 0 && !flags[i]) {
					// $("#" + tbtables[i] + " input:checkbox")[0].checked = true;
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
			} */
			if(!flags[0]) {
				var oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
				if (oid == null || oid.trim() == "") {
					source = [];
					// initArray();
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
	
	
	function drawEMGMap() {
		//console.log("开始绘制EMG地图: " + Date.now());
		var geo = getGEO(dianpingGeo);
		
		try{
			var latlngpoi = L.latLng(parseFloat(geo[1]), parseFloat(geo[0]));
			emgmap.setView(latlngpoi);
			
			if(geo != null ) {
				if ($emgmarkerBase) {
					$emgmarkerBase.setLatLng(latlngpoi);
				} else {
					$emgmarkerBase = L.marker([geo[1], geo[0]]).addTo(emgmap);
					
				}
			}	
			return true;
			
		} catch(e) {
			return false;
		}
		//console.log("结束绘制EMG地图: " + Date.now());
	}
	
	function loadEditPOI(oid, emgFeatcode) {
		if (!oid || oid <= 0) 	return;
		var dtd = $.Deferred(); 
		systemOid = oid;
		jQuery.post("./polygonedit.web", {
			"atn" : "getpoibyoid",
			"oid" : oid
		}, function(json) {
			$("table#tbEdit>tbody td.tdValue>input:text").val("");
			if (json && json.result == 1 && json.poi != null) {
				systemPoi = json.poi;
				var poi = json.poi;
				if(poi.del) {
					$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(-1);
				}else {
					 $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(poi.id);
				}
				$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val(poi.namec);
				$("#featcode").val(poi.featcode);
				$("#sortcode").val(poi.sortcode);
				setfeatcodename();
				setsortcodename();
				$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val(poi.geo);
				if (emgFeatcode != null) {
					var featcode = getfeatcodename(poi.featcode);
					emgFeatcode.text(featcode);
					// 如果POI已经被删除，则把颜色置为红色
					if (poi.del) {
						$($($(emgFeatcode).parents("table")[0]).find("td.tdIndex")[0]).css('background-color', 'red');
					}
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
		var latlngpoi = L.latLng(parseFloat(lat), parseFloat(lng));
		if (ck.checked == false && srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			if (emgCurrentMarker != null) {
		        emgCurrentMarker.remove();
		        emgCurrentMarker = null;
			}
			$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("-1");
			$("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val("");
			emgSrcInnerId = "";
			emgSrcType = 0;
			$('#sortcode').val("");
			$('#featcode').val("");
			$('#sortcodename').val("");
			$('#featcodename').val("");
			
		}else if (ck.checked == true && srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			// loadEditPOI(srcInnerId);
			var emgFeatcode = $($(ck).parents("table")[0]).find("td.tdValue[data-key='class']");
			loadEditPOI(srcInnerId, emgFeatcode);
			
			emgmap.setView(latlngpoi);
			emgSrcInnerId = srcInnerId;
			emgSrcType = srcType;
			 $("input[name='"+ ck.name +"']:checkbox").prop("checked", false);
			ck.checked = true; 
			if (emgCurrentMarker == null) {
				initEmgMarker([lng,lat], srcType, srcInnerId);
			}else {
				emgCurrentMarker.setLatLng(L.latLng(lat,lng));
			}
		}else if (ck.checked == true && srcType == <%=SrcTypeEnum.BAIDU.getValue() %>) {
			addBaiduMarker(lat,lng,  srcType, srcInnerId);
		} else if (ck.checked == false && srcType == <%=SrcTypeEnum.BAIDU.getValue() %>) {
			removeMarker(baiduMarker, srcType, srcInnerId);
		} else if (ck.checked == true && srcType == <%=SrcTypeEnum.TENGXUN.getValue() %>) {
			addTengxunMarker(lat,lng,  srcType, srcInnerId);
		}else if (ck.checked == false && srcType == <%=SrcTypeEnum.TENGXUN.getValue() %>) {
			removeMarker(tengxunMarker, srcType, srcInnerId);
		} else if (ck.checked == true && srcType == <%=SrcTypeEnum.GAODE.getValue() %>) {
			addGaodeMarker(lat,lng,  srcType, srcInnerId);
		} else if (ck.checked == false && srcType == <%=SrcTypeEnum.GAODE.getValue() %>) {
			removeMarker(gaodeMarker, srcType, srcInnerId);
		} else {
			console.log("Error on srcType: " + srcType);
			return;
		}
	
	}
	
	function removeMarker(layerGroup, srcType, srcInnerId){
		var markers = layerGroup.getLayers();
		if (markers == null || markers.length == 0) return;
		for(var i = 0; i < markers.length; i++) {
			var marker = markers[i];
			if (marker.options.srcType == srcType && marker.options.srcInnerId == srcInnerId) {
				// 从数组中删除元素
				
				layerGroup.removeLayer(marker);
			}
		}
	}
	
	// 添加高德标注
	function addGaodeMarker( lat, lng, srcType, srcInnerId) {
		
		var pointFeature = new L.marker([lat,lng],
        		{ icon: yellowIcon,title:name, srcType: srcType, srcInnerId: srcInnerId});
		gaodeMarker.addLayer(pointFeature);
        pointFeature.on("click",function(){
            alert(name)
        });
	}
	
	// 添加百度标注
	function addBaiduMarker( lat, lng,  srcType, srcInnerId) {
		
		 var geo = coordtransform.bd09togcj02(lng, lat);
		var pointFeature = new L.marker([geo[1], geo[0]],
        		{ icon: greenIcon,title:name, srcType: srcType, srcInnerId: srcInnerId});
		baiduMarker.addLayer(pointFeature);
        pointFeature.on("click",function(){
            alert(name)
        });
	}
	
	// 添加腾讯标注
	function addTengxunMarker( lat, lng,  srcType, srcInnerId) {
		
		var pointFeature = new L.marker([lat,lng],
        		{ icon:lightblueIcon,title:name, srcType: srcType, srcInnerId: srcInnerId});
		tengxunMarker.addLayer(pointFeature);
        pointFeature.on("click",function(){
            alert(name)
        });
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
		    
		    html.push("<tr><td class='tdKey'>分类</td>");
		    html.push("<td class='tdValue' data-key='class'>" + referdata.orgCategoryName + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr style='display:none'><td class='tdKey'>坐标</td>");
		    if(referdata.srcType == 45) {
		    	// 百度坐标需要特殊处理
		    	var baidugeo = coordtransform.bd09togcj02(referdata.srcLon, referdata.srcLat);
		    	html.push("<td class='tdValue'  data-key='geo'>MULTIPOINT (" + baidugeo[0] + " " + baidugeo[1] + "," +  baidugeo[0]  + " " + baidugeo[1] +")</td>");
		    } else {
		    	html.push("<td class='tdValue' data-key='geo'>MULTIPOINT (" + referdata.srcLon + " " + referdata.srcLat + "," +   referdata.srcLon + " " + referdata.srcLat +")</td>");
		    }
		    
		    html.push("</tr>");
		    html.push("</tbody></table>");
		    $tbody.append(html.join(''));
		});
	}
	
	function loadReferdatas(keywordid) {
		var dtd = $.Deferred();
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
						var flag = drawEMGMap(emgrefers[0].srcLat, emgrefers[0].srcLon, zoom);
						// L.marker([emgrefers[0].srcLat, emgrefers[0].srcLon]).addTo(emgmap);
						if (flag) {
							//加载方圆500米范围的点
							jQuery.post("./polygonedit.web", {
								"atn" : "getpoibydistance",
								"processid" : $("#curProcessID").text(),
								"distance" : keyword.distance,
								"location": keyword.geo
							}, function(json) {
								if (json && json.result == 1) {
									var pois = json.rows;
									for(var i = 0; i  < pois.length; i++) {
										(function (index){ //封闭空间开始
						                    var row=pois[index];
											var geo = getGEO(row.geo);
						                    
						                    var pointFeature = new L.marker([geo[1], geo[0]],
						                    		{ icon: redIcon,title:row.namec, poi: row} );
						                    pointFeature.on("click",function(){
						                    	updatePOIInMap();
						                    	console.log(row);
						                    	loadEditPOI(row.id, null);
						                    	
						                    });
						                    emgMarker.addLayer(pointFeature);
						                    // pointFeature.addTo(emgmap);
						                })(i) 
									}
								} 
							});
							// 成功显示地图
							for(var i = 0; i  < emgrefers.length; i++) {
								(function (index){ //封闭空间开始
				                    var row=emgrefers[index];
				                    var pointFeature = new L.marker([row.srcLat,row.srcLon],
				                    		{ icon: redIcon,title:row.name,srcType: row.srcType, srcInnerId: row.srcInnerId});
				                    pointFeature.on("click",function(){
										updatePOIInMap();
				                    	loadEditPOI(row.srcInnerId, null);
				                    	
				                    });
				                    emgMarker.addLayer(pointFeature);
				                    // pointFeature.addTo(emgmap);
				                })(i) 
							}
							
						}
						drawReferdatas("tbemg", emgrefers);
						emgSrcInnerId = emgrefers[0].srcInnerId;
						emgSrcType = emgrefers[0].srcType;
					} else {
						$("#emgmap").html("无数据");
						$emgmap = null;
						$emgmarker = null;
						$emgmarkerBase = null;
						$("table#tbemg>tbody").html("<tr><td>无数据</td></tr>");
						if (keyword && keyword.geo) {
							var geo = null;
							drawEMGMap(null,null, zoom);
							
						}
					}
					if (baidurefers && baidurefers.length > 0) {
						baidurefers.sort(refercompare);
						// drawBaiDuMap(baidurefers[0].srcLat, baidurefers[0].srcLon, zoom);
						drawReferdatas("tbbaidu", baidurefers);
					} else {
						$("#baidumap").html("无数据");
						$("table#tbbaidu>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					if (gaoderefers && gaoderefers.length > 0) {
						gaoderefers.sort(refercompare);
						//drawGaoDeMap(gaoderefers[0].srcLat, gaoderefers[0].srcLon, zoom);
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
						// drawTengXunMap(tengxunrefers[0].srcLat, tengxunrefers[0].srcLon, zoom);
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
		var oid = oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		var relation = null;
			// $("#" + tables[i] + " table tbody tr :checkbox")
		$("#tbemg input:checked").each(function(){
			// relations.push();
			var srcInnerId = $(this).val().split(",")[1];
			var srcType = $(this).val().split(",")[2];
			if ((oid == 0 || oid.trim() == "" || oid == -1) && (keyword.srcType != null && keyword.srcType > 0 ) ) {
				//EMG 选中保存的是EMG和poi的
				relation = new Object();
				relation.srcInnerId = keyword.srcInnerId;
				relation.srcType = keyword.srcType;
				relation.oid = srcInnerId;
				relation.qid = keyword.qid;
				relation.errorType = keyword.errorType;
			}
		});
		
		//提示删除标识
		var saveRelation = null;
		// 在库里保存了关系，现在取消掉，需要删除relation,且没有新增POI点，即oid为-1
		if (currentCheckRelation != null && relation != null ) {
			for (var i = 0; i < currentCheckRelation.length; i++) {
				if (currentCheckRelation[i].srcType == relation.srcType && currentCheckRelation[i].srcInnerId == relation.srcInnerId && currentCheckRelation[i].oid == relation.oid) {
					saveRelation = relation;			
				}
			}
		}
		
		$.webeditor.showConfirmBox("alert","你确定把当前资料打上错误标识吗？", function(){
			$.webeditor.showMsgBox("info", "数据保存中...");
			jQuery.post("./edit.web", {
				"atn" : "keywordError",
				"taskid" : $("#curTaskID").text(),
				"getnext" : isLoadNextTask,
				"relation": JSON.stringify(saveRelation),
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
						$("#curProjectID").val(task.projectid);
						$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val("");
						//$("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
						$("#featcode").val("");
						$("#sortcode").val("");
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
						}
					} 
					
				}else {
					if(json.error != null && json.error.length > 0) {
						$.webeditor.showMsgLabel("alert", json.error);
					}else {
						$.webeditor.showMsgLabel("alert", "标记失败");
					}
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
			if (oid == "" || oid == null || oid == -1) {
				$.webeditor.showCheckBox("alert", "未新增点，且EMG中没有勾选");
				return;
			}
			
		} catch(e) {
			return;
		}
		
		var tempfeatcode = $("#featcode").val();
		if (isCanSubmit(tempfeatcode, notEditCode)) {
			$.webeditor.showMsgLabel("alert", "当前POI点的类型不允许操作");
			return;
		}
		if (submitflag == 1) {
			$.webeditor.showCheckBox("alert", "当前任务已经提交过，请刷新页面获取下一任务");
			return;
		}
		submitflag = submitflag + 1;
		var tables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
		var relations = [];
		var emgChecked = false;
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
		if (!emgChecked && oid > -1 && oid.trim() != "" && keyword != null && keyword.srcInnerId != null) {
			var relation = new Object();
			relation.srcInnerId = keyword.srcInnerId;
			relation.srcType = keyword.srcType;
			relation.oid = oid;
			relation.qid = keyword.qid;
			relation.errorType = keyword.errorType;
			relations.push(relation);
		}
		var delFlag = false;
		//提示删除标识
		/* if (databaseSaveRelation != null && relations != null) {
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
		} */
		// 在库里保存了关系，现在取消掉，需要删除relation,且没有新增POI点，即oid为-1
		if (currentCheckRelation != null && (relations == null || relations.length == 0) && oid !="" && oid != -1) {
			for (var i = 0; i < currentCheckRelation.length; i++) {
				currentCheckRelation[i].isDel = true;
				relations.push(currentCheckRelation[i]);
			}
		}
		// 当聚合成功，在数据库里有relation,但EMG中未检索到聚合成功的点，则弹窗提示，未考虑新增点的情况
		if (originalCheckRelation != null && originalCheckRelation.length > 0 && oid > 0) {
			$.webeditor.showCheckBox("alert", "与EMG的关系在数据库中已经存在，但EMG检索未查询到该记录，请连接开发人员查明此问题");
			return;
		} 
		var projectId = $("#curProjectID").val();
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		// var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		//提交的时候把featcode和sortcode的值清掉，用提交处理规则来赋值
		var existBrand = false;
		for (var i = 0; i < brandcode.length; i++) {
			if ($("#featcode").val() == brandcode[i]) existBrand = true;
		}
		/* if (emgChecked && existBrand) {
			
		}else  {
			if (delFeatcoe) {
				$("#featcode").val("");
				$("#sortcode").val("");
				$("#featcodename").val("");
				$("#sortcodename").val(""); 
			} */
			
			// $("#tbbaidu").find("td input:checked")
			/* if ( $("#tbgaode input:checkbox") && $("#tbgaode input:checked").length > 0){
				var ele = $("#tbgaode input:checked");
			
				// var ele = $($($("#tbgaode input:checked")[0]).parents("table")[0]).find("td.tdValue[data-key='class']");	
				for (var i = 0; i < ele.length; i++) {
					var g = $($(ele[i]).parents("table")[0]).find("td.tdValue[data-key='class']");	
					var valueArray = g.text().split(";");
					getCode(gaodeCode, valueArray, g);
					if ($("#featcode").val() != null && $("#featcode").val().trim() != "") break;
				}
				
			} */
			
			/* if (($("#featcode").val() == null || $("#featcode").val().trim() == "") && $("#tbbaidu input:checkbox") && $("#tbbaidu input:checked").length > 0) {
				// var ele = $($($("#tbgaode input:checked")[0]).parents("table")[0]).find("td.tdValue[data-key='class']");		
				var ele = $("#tbbaidu input:checked");
				for (var i = 0; i < ele.length; i++) {
					var g = $($(ele[i]).parents("table")[0]).find("td.tdValue[data-key='class']");	
					var valueArray = g.text().split(";");
					getCode(baiduCode, valueArray, g);
					if ($("#featcode").val() != null && $("#featcode").val().trim() != "") break;
				}
			}
			
			if (($("#featcode").val() == null || $("#featcode").val().trim() == "") && $("#tbtengxun input:checkbox") && $("#tbtengxun input:checked").length > 0) {
				// var ele = $($($("#tbgaode input:checked")[0]).parents("table")[0]).find("td.tdValue[data-key='class']");
				var ele = $("#tbtengxun input:checked");
				for (var i = 0; i < ele.length; i++) {
					var g = $($(ele[i]).parents("table")[0]).find("td.tdValue[data-key='class']");	
					var valueArray = g.text().split(":");
					getCode(tengxunCode, valueArray, g);
					if ($("#featcode").val() != null && $("#featcode").val().trim() != "") break;
				}
			}
			
			if (($("#featcode").val() == null || $("#featcode").val().trim() == "") && $("#tbemg input:checkbox") && $("#tbemg input:checked").length > 0) {
				// var ele = $($($("#tbgaode input:checked")[0]).parents("table")[0]).find("td.tdValue[data-key='class']");
				var ele = $("#tbemg input:checked");
				for (var i = 0; i < ele.length; i++) {
					var g = $($(ele[i]).parents("table")[0]).find("td.tdValue[data-key='class']");	
					var featcode =$("#featcode").val(systemPoi.featcode);
					var sortcode =$("#sortcode").val(systemPoi.sortcode);
				}
			} */
		// }
		
		var featcode =$("#featcode").val();
		var sortcode =$("#sortcode").val();
		var remark = $("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val();
		var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val();
		var isLoadNextTask = $("table#tbEdit>tbody td.tdKey[data-key='isLoadNextTask']>input:checkbox").prop("checked");
		if((namec == null || namec.trim() == "" || featcode == null || featcode.trim() == "" || geo == null || geo.trim() == "") && oid != -1) {
			submitflag = 0;
			$.webeditor.showMsgLabel("alert", "名称、分类、坐标不能为空");
			if (featcode == null || featcode.trim() == "" ){
				$("#featcode").removeAttr("disabled");
				$("#sortcode").removeAttr("disabled");
				$("#featcodeSpan").removeAttr("disabled");
				$("#sortcodeSpan").removeAttr("disabled");
			}
			return;
		}
		$.webeditor.showMsgBox("info", "数据保存中...");
		console.log("提交前预处理结束: " + Date.now());
		if(delFlag) {
			$.webeditor.showConfirmBox("alert","保存的关系已经在数据库中存在，是否删除原关系保存新的关系", function(){
				
				jQuery.post("./edit.web", {
					"atn" : "submitedittask",
					"taskid" : $("#curTaskID").text(),
					"getnext" : isLoadNextTask,
					"relations": JSON.stringify(relations),
					"namec": namec,
					"oid": oid,
					//"tel": tel,
					"featcode" : featcode,
					"sortcode" : sortcode,
					"geo" : geo,
					"remark": remark == "" || remark == null ? " " : remark,
					"projectId": projectId,
					"source": JSON.stringify(source),
					"poistate": $("#poistate").val()
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
			
			//$.webeditor.showConfirmBox("alert","确实要提交当前资料？", function(){
				jQuery.post("./edit.web", {
					"atn" : "submitedittask",
					"taskid" : $("#curTaskID").text(),
					"getnext" : isLoadNextTask,
					"relations": JSON.stringify(relations),
					"namec": namec,
					"oid": oid,
					//"tel": tel,
					"featcode" : featcode,
					"sortcode" : sortcode,
					"geo" : geo,
					"remark": remark == "" || remark == null ? " " : remark,
					"projectId": projectId,
					"source": JSON.stringify(source),
					"poistate": $("#poistate").val()
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
		}
		
		
		$.webeditor.showMsgBox("close");
		
	}
	
		//在地图中添加EMG选中行的特殊marker
	function initEmgMarker(geo, srcType, srcInnerId) {
		emgCurrentMarker = new L.marker([geo[1],geo[0]],
        		{ icon: blueemgIcon,title:name, srcType: srcType, srcInnerId: srcInnerId});
		
		emgCurrentMarker.on("click",function(){
			loadEditPOI(srcInnerId, emgFeatcode);
        });
        emgCurrentMarker.addTo(emgmap);
	}
		
	function getGEO(point) {
		var geo = null;
		if (point.indexOf("MULTIPOIN") >= 0) {
		var t = point.split(", (")[1];
		geo = t.substring(0, t.length-3).split(" ");
			// geo = dianpingGeo.replace("MULTIPOIN (","").replace(")", "").split(" ");
		}else {
			geo = point.replace("POINT (","").replace(")", "").split(" ");
		}
		return geo;
	}
		
	function initTask(task, json) {
		console.log("接收到返回数据开始初始化控件: " + Date.now());
		// delFeatcoe = true;
		if (task && task.id) {
			submitflag = 0;
			
			var process = json.process;
			var project = json.project;
			$("#curProcessID").text(process.id);
			$("#curProcessName").text(process.name);
			$("#curProjectOwner").text(project.owner == 1 ? '私有' : '公有');
			$("#curTaskID").text(task.id);
			$("#curProjectID").val(task.projectid);
			$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val("");
			// $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
			$("#featcode").val("");
			$("#sortcode").val("");
			$("#featcodename").val("");
			$("#sortcodename").val("");
			$("#featcode").attr("disabled", true);
			$("#sortcode").attr("disabled", true);
			$("#featcodeSpan").attr("disabled", true);
			$("#sortcodeSpan").attr("disabled", true);
			$("table#tbEdit>tbody td.tdValue[data-key='remark']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val("");
			$("#poistate").val(0)
			initKeywordColor();
			console.log("接收到返回数据结束初始化控件: " + Date.now());
			keywordid = json.keywordid;
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
			}
		} 
	}
	
	function initKeywordColor() {
		var keyEle = $("table#tbKeyword>tbody td.tdValue");
		for(var i = 0; i < keyEle.length; i++) {
			keyEle[i].style.backgroundColor = "white";
		}
	}
	
	// 当为指定的对象类型代码时不能进行提交， 修改，删除
	function isCanSubmit(featcode, canEditFeatcode) {
		if (featcode == null || featcode < 0) return true;
		var flag = false;
		for (var i = 0; i < canEditFeatcode.length; i++) {
			if (featcode == canEditFeatcode[i]) {
				flag = true;
				break;
			}
		}
		return flag;
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
		var tempfeatcode = $("#featcode").val();
		if (isCanSubmit(tempfeatcode, notEditCode)) {
			$.webeditor.showMsgLabel("alert", "当前POI点的类型不允许操作");
			return;
		}
		var projectId = $("#curProjectID").val();
		$.webeditor.showConfirmBox("alert","确定要删除这个POI吗？", function(){
			$.webeditor.showMsgBox("info", "数据保存中...");
			jQuery.post("./edit.web", {
				"atn" : "deletepoibyoid",
				"oid" : oid,
				"taskid" : $("#curTaskID").text(),
				"projectId": projectId
			}, function(json) {
				if (json && json.result > 0) {
					$("table#tbEdit>tbody td.tdValue>input:text").val("");
					$("#tbemg  input:checked").parent().css('background-color', 'red');
					$.webeditor.showMsgLabel("success", "POI删除成功");
					var markers = emgMarker.getLayers();
					for(var i = 0; i < markers.length; i++) {
						var marker = markers[i];
						if(marker.options.poi != null && marker.options.poi.id == oid) {
							emgMarker.removeLayer(marker);
						}
					}
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
	
	//切换点的时候，保存上一个修改的POI
	function updatePOIInMap() {
		var oid = null;
		try {
			oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		} catch(e) {
			return;
		}
		//判断点是否被编辑
		if (!poiEditFlag ) {
			return;
		}
		
		var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val();
		var projectId = $("#curProjectID").val();
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		// var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode =$("#featcode").val();
		var sortcode =$("#sortcode").val();
		var poistate = $("#poistate").val();
		if(namec == null || namec.trim() == "" || featcode == null || featcode.trim() == "" || geo == null || geo.trim() == "") {
			$.webeditor.showMsgLabel("alert", "名称、分类、坐标不能为空");
			return;
		}
		$.webeditor.showMsgBox("info", "数据保存中...");
		jQuery.post("./polygonedit.web", {
			"atn" : "updatepoi",
			"oid" : oid,
			"namec" : namec,
			"featcode" : featcode,
			"sortcode" : sortcode,
			"geo" : geo,
			"poistate": poistate,
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
		if(!isFirstSavePoi) {
			isFirstSavePoi = true;
			// 当一个任务中需要修改多个POI点时，在修改完第一个点后，把任务状态修改为6，5
			jQuery.post("./polygonedit.web", {
				"atn" : "updatetaskstate",
				"taskid" : $("#curTaskID").text(),
				"state" : 6,
				"process" : 5
			}, function(json) {
				if (json && json.result > 0) {
					
					$.webeditor.showMsgLabel("success", "修改任务状态成功");
				} else {
					
					if(json.error != null && json.error.length > 0) {
						$.webeditor.showMsgLabel("alert", json.error);
					}else {
						$.webeditor.showMsgLabel("alert", "修改任务状态失败");
					}
					
				}
				// $.webeditor.showMsgBox("close");
			}, "json");
		}
	}
	
	function valueChange(obj) {
		poiEditFlag = true;
	
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
			"taskid" : $("#curTaskID").text(),
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
					$("#curProjectID").val(task.projectid);
					$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("");
					$("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val("");
					// $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
					$("#featcode").val("");
					$("#sortcode").val("");
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
					}
				}
			}else {
				if(json.error != null && json.error.length > 0) {
					$.webeditor.showMsgLabel("alert", json.error);
				}else {
					$.webeditor.showMsgLabel("alert", "标记失败");
				}
			}
		});
	}
	function map_drag(map) {	
		var zoom = emgmap.getZoom();
		var minzoom = parseFloat(zoom);
		
		var oldzoom = m_oldzoom;
		//卸载minzoom大于本级别的poi
		if (m_oldzoom != minzoom) {
			canclePoi();
			var i;
			for (i in emgmap._layers) {
				var f = emgmap._layers[i].feature;
				if (f != null) {
					if(f.geometry.type != 'Point'){
						continue;
					}
					var zoomtemp;
					if (f.properties.minzoom == null || f.properties.minzoom == '') {
						zoomtemp = 0;
					} else {
						zoomtemp = parseInt(f.properties.minzoom);
					}				 
					if (zoomtemp > parseInt(zoom)+1 || zoomtemp == 0) {
						emgmap._layers[i].remove();
						
						var bounds = m_mapBound.get(zoomtemp);
						if (bounds != null) {	
							m_mapBound.delete(zoomtemp);
						}
					}
				}
			}
			m_oldzoom = minzoom;
		}

		var minx = map.getBounds().getSouthWest().lng;
		var miny = map.getBounds().getSouthEast().lat;
		var maxx = map.getBounds().getSouthEast().lng;
		var maxy = map.getBounds().getNorthWest().lat;		

	//  获取拆分的格网
		jQuery.post("./edit.web", {
				"atn" : "getbounds",
				"minx" : minx,
				"miny" : miny,
				"maxx" : maxx,
				"maxy" : maxy
			}, function(json) {
				if (json != null && json.bounds != null) {				
					for (var i = 0; i < json.bounds.length; i++) {
						var bound = json.bounds[i];		

						var bExist = addBound(parseInt(zoom)+1,bound);
						if (!bExist) { //没加载过的
							var pt = bound.split(',');
							movemap(pt[0],pt[1],pt[2],pt[3],oldzoom);							
						}
					}
				}	
			}, "json");
	}
    </script>
    
</body>
</html>