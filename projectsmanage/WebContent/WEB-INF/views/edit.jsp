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
	var keyword = null;
	// 用来存储数据库中保存着的relation 关系
	var databaseSaveRelation = [];
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
			loadReferdatas(keywordid);
			loadKeyword(keywordid);			
		}
	});
	
	
	
	function loadKeyword(keywordid) {
		jQuery.post("./edit.web", {
			"atn" : "getkeywordbyid",
			"keywordid" : keywordid
		}, function(json) {
			if (json && json.result == 1) {
				keyword = json.rows;
				srcType = keyword.srcType;
				srcInnerId = keyword.srcInnerId;
				console.log(keyword);
				
				loadRelation(keyword.srcInnerId);
				
				dianpingGeo = keyword.geo;
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
	
	function loadRelation(oid) {
		jQuery.post("./edit.web", {
			"atn" : "getRelationByOid",
			"oid" : oid
		}, function(json) {
			var tables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
			if (json && json.result == 1 && json.rows.length > 0) {
				var relations = json.rows;
				
				for (var i = 0; i < tables.length; i++) {
					$.each($("input[name='" +tables[i] +"']:checkbox"), function(){
						var srcInnerId = $(this).val().split(",")[1];
						var srcType = $(this).val().split(",")[2];
						for (var j = 0; j < relations.length; j++) {
							if (i == 0) {
								if (keyword.srcInnerId == relations[j].srcInnerId && keyword.srcType == relations[j].srcType && oid == relations[j].oid) {
									$(this).prop("checked", true);
									var relation = new Object();
									relation.srcInnerId = keyword.srcInnerId;
									relation.srcType = keyword.srcType;
									relation.oid = oid;
									relation.id = relations[j].id;
									// relations.push({"srcInnerId"： keyword.srcInnerId, "srcType"： keyword.srcType, "oid": oid});
									databaseSaveRelation.push(relation);
								}
							}else {
								if (srcInnerId == relations[j].srcInnerId && srcType == relations[j].srcType && oid == relations[j].oid) {
									$(this).prop("checked", true);
									var relation = new Object();
									relation.srcInnerId = srcInnerId;
									relation.srcType = srcType;
									relation.oid = oid;
									relation.id = relations[j].id;
									databaseSaveRelation.push(relation);
									// databaseSaveRelation.push({"srcInnerId":srcInnerId, "srcType"： srcType, "oid": oid});
								}
							}
						}
						
					}); 
				}
				
			} else {
				for (var i = 0; i < tables.length; i++) {
					if ( $("#" + tables[i] + " input:checkbox") && $("#" + tables[i] + " input:checkbox").length > 0) {
						$("#" + tables[i] + " input:checkbox")[0].checked = true;
					}
					
				}
			}
		}, "json");
	}
	
	function refercompare(a, b) {
		return a.sequence - b.sequence;
	}
	
	function addMakerOnEMGMap(map ) {
		if (map) {
			var img = new Image();
			// img.src = 'http://m.emapgo.cn/demo/electricize/img/poi_center.png';
			img.src = "resources/images/start.png";
			if (dianpingGeo) {
				// POINT (102.486719835069 24.9213802083333)
				var geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
				var marker = new emapgo.Marker(img		
				)
				.setLngLat(geo)
				.addTo(map);
			}
			
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
						dianpingGeo = 'MULTIPOINT (' + e.lngLat.lng + " " + e.lngLat.lat + "," +  + e.lngLat.lng + " " + e.lngLat.lat +")";
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
					layers : [new L.tileLayer.baidu({layer: 'custom'})]
				});
			}
			
			if ($baidumarker) {
				$baidumarker.setLatLng([lat, lng]);
			} else {
				$baidumarker = L.marker([lat, lng], {icon: matchMarker}).addTo($baidumap);
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
				$gaodemarker = L.marker([lat, lng], {icon: matchMarker}).addTo($gaodemap);
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
				$tengxunmarker = L.marker([lat, lng], {icon: matchMarker}).addTo($tengxunmap);
			}
		} catch(e) {
			
		}
	}
	
	function loadEditPOI(oid) {
		if (!oid || oid <= 0) 	return;
		
		jQuery.post("./edit.web", {
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
				dianpingGeo = poi.geo;
				poi.poitags.forEach(function(tag, index) {
					$("table#tbEdit>tbody td.tdValue[data-key='" + tag.k +"']>input:text").val(tag.v);
				});
			} else {
				$("table#tbEdit>tbody td.tdValue>input:text").val("加载失败");
			}
		}, "json");
	}
	
	

	function rdChange(ck, srcType, lat, lng, srcInnerId) {
		 // $("input[name='"+ ck.name +"']:checkbox").prop("checked", false);
		// 	ck.checked = true; 
		if (ck.checked == false && srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val("-1");
			emgSrcInnerId = "";
			emgSrcType = 0;
		}else if (ck.checked == true && srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			loadEditPOI(srcInnerId);
			$emgmarker.setLngLat([lng, lat ]);
			$emgmap.setCenter([lng, lat]);
			emgSrcInnerId = srcInnerId;
			emgSrcType = srcType;
			 $("input[name='"+ ck.name +"']:checkbox").prop("checked", false);
			ck.checked = true; 
		}
		if (srcType == <%=SrcTypeEnum.EMG.getValue() %>) {
			/* loadEditPOI(srcInnerId);
			$emgmarker.setLngLat([lng, lat ]);
			$emgmap.setCenter([lng, lat]);
			emgSrcInnerId = srcInnerId;
			emgSrcType = srcType; */
		} else if (srcType == <%=SrcTypeEnum.BAIDU.getValue() %>) {
			$baidumarker.setLatLng([lat, lng]);
			$baidumap.setView([lat, lng]);
			baiduSrcInnerId = srcInnerId;
			baiduSrcType = srcType;
		} else if (srcType == <%=SrcTypeEnum.TENGXUN.getValue() %>) {
			$tengxunmarker.setLatLng([lat, lng]);
			$tengxunmap.setView([lat, lng]);	
			tengxunSrcInnerId = srcInnerId;
			tengxunSrcType = srcType;
		} else if (srcType == <%=SrcTypeEnum.GAODE.getValue() %>) {
			$gaodemarker.setLatLng([lat, lng]);
			$gaodemap.setView([lat, lng]);
			gaodeSrcInnerId = srcInnerId;
			gaodeSrcType = srcType;
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
		
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>textarea").val(value);
		$("table#tbEdit>tbody td.tdValue[data-key='" + key + "']>input:text").val(value);
		if (key == "address") {
			$("table#tbEdit>tbody td.tdValue[data-key='address4']>textarea").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address5']>textarea").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address6']>textarea").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address7']>textarea").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
			$("table#tbEdit>tbody td.tdValue[data-key='address8']>textarea").val(value);
			$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val(value);
			
		}
	}
	
	function drawReferdatas(tbid, referdatas) {
		var $tbody = $("table#" + tbid + ">tbody");
		$tbody.empty();
		referdatas.forEach(function(referdata, index) {
			var html = new Array();
			html.push("<table id ='"+ tbid + referdata.id + "' class=\"table table-bordered table-condensed\"><tbody>");
			html.push("<tr class='trIndex'><td class='tdIndex' rowspan='5'>");
		    //html.push("<input type='checkbox' name='rd" + tbid + "' onChange='rdChange(this, " + referdata.srcType + "," + referdata.srcLat + "," + referdata.srcLon + ",\"" + referdata.srcInnerId + "\");' value='" + referdata.id + "' " + (index == 0 ? 'checked':'') + ">");
		    //html.push("<span class='glyphicon glyphicon-share cursorable'></span></td></tr>");
		    html.push("<input type='checkbox' name='rd" + tbid + "' onChange='rdChange(this, " + referdata.srcType + "," + referdata.srcLat + "," + referdata.srcLon + ",\"" + referdata.srcInnerId + "\");' value='" + referdata.id + "," + referdata.srcInnerId + "," + referdata.srcType + "' >");
		    html.push("</td></tr>");
		    
		    html.push("<tr><td class='tdKey'>名称</td>");
		    html.push("<td class='tdValue' data-key='name'>" + referdata.name + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr><td class='tdKey'>电话</td>");
		    html.push("<td class='tdValue' data-key='telephone'>" + referdata.tel + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr><td class='tdKey'>分类</td>");
		    html.push("<td class='tdValue' data-key='class'>" + referdata.orgCategoryName + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    html.push("<tr class='trEnd'><td class='tdKey'>地址</td>");
		    html.push("<td class='tdValue' data-key='address'>" + referdata.address + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		   /*  html.push("<tr  style='display:none'><td class='tdKey'>srcType,srcInnerId</td>");
		    html.push("<td class='tdValue' data-key='srcInnerId'>" + referdata.srcInnerId + "</td>");
		    html.push("<td class='tdValue' data-key='srcType'>" + referdata.srcType + "</td></tr>"); */
		    html.push("</tbody></table>");
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
					
					if (emgrefers && emgrefers.length > 0) {
						emgrefers.sort(refercompare);
						loadEditPOI(emgrefers[0].srcInnerId);
						drawEMGMap(emgrefers[0].srcLat, emgrefers[0].srcLon, zoom);
						drawReferdatas("tbemg", emgrefers);
						emgSrcInnerId = emgrefers[0].srcInnerId;
						emgSrcType = emgrefers[0].srcType;
						addMakerOnEMGMap($emgmap);
					} else {
						$("#emgmap").html("无数据");
						if (keyword && keyword.geo) {
							var geo = keyword.geo.replace("POINT (","").replace(")", "").split(" ");
							drawEMGMap(geo[1], geo[0], zoom);
							// drawReferdatas("tbemg", emgrefers);
							/* emgSrcInnerId = emgrefers[0].srcInnerId;
							emgSrcType = emgrefers[0].srcType; */
							addMakerOnEMGMap($emgmap);
							$("table#tbemg>tbody").html("<tr><td>无数据</td></tr>");
						}
						
					}
					
					if (baidurefers && baidurefers.length > 0) {
						baidurefers.sort(refercompare);
						drawBaiDuMap(baidurefers[0].srcLat, baidurefers[0].srcLon, zoom);
						drawReferdatas("tbbaidu", baidurefers);
						baiduSrcInnerId = baidurefers[0].srcInnerId;
						baiduSrcType = baidurefers[0].srcType;
					} else {
						$("#baidumap").html("无数据");
						$("table#tbbaidu>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					if (gaoderefers && gaoderefers.length > 0) {
						gaoderefers.sort(refercompare);
						drawGaoDeMap(gaoderefers[0].srcLat, gaoderefers[0].srcLon, zoom);
						drawReferdatas("tbgaode", gaoderefers);
						gaodeSrcInnerId = gaoderefers[0].srcInnerId;
						gaodeSrcType = gaoderefers[0].srcType;
					} else {
						$("#gaodemap").html("无数据");
						$("table#tbgaode>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					if (tengxunrefers && tengxunrefers.length > 0) {
						tengxunrefers.sort(refercompare);
						drawTengXunMap(tengxunrefers[0].srcLat, tengxunrefers[0].srcLon, zoom);
						drawReferdatas("tbtengxun", tengxunrefers);
						tengxunSrcInnerId = tengxunrefers[0].srcInnerId;
						tengxunSrcType = tengxunrefers[0].srcType;
					} else {
						$("#tengxunmap").html("无数据");
						$("table#tbtengxun>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					
				}
			} else {
				
			}
			
		}, "json");
	}
	
	function submitEditTask() {
		var oid = null;
		try {
			oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		} catch(e) {
			return;
		}
		if (!oid || oid <= 0) 	return;
		var $tbemg = $("#tbbaidu");
		var tables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
		var relations = [];
		for (var i = 0; i < tables.length; i++) {
			// $("#" + tables[i] + " table tbody tr :checkbox")
			$("#" + tables[i] + " input:checked").each(function(){
				relations.push();
				var srcInnerId = $(this).val().split(",")[1];
				var srcType = $(this).val().split(",")[2];
				if (i == 0 && (keyword.srcType != null && keyword.srcType > 0 )) {
					
					var relation = new Object();
					relation.srcInnerId = keyword.srcInnerId;
					relation.srcType = keyword.srcType;
					relation.oid = oid;
					relation.qid = keyword.qid;
					relation.errorType = keyword.errorType;
					// relations.push({"srcInnerId"： keyword.srcInnerId, "srcType"： keyword.srcType, "oid": oid});
					relations.push(relation);
				}else {
					
					// $(this).prop("checked", true);
					var relation = new Object();
					relation.srcInnerId = srcInnerId;
					relation.srcType = srcType;
					relation.oid = oid;
					relation.qid = keyword.qid;
					relation.errorType = keyword.errorType;
					relations.push(relation);
					// relations.push({"srcInnerId":srcInnerId, "srcType"： srcType, "oid": oid});
				}
			});
			// var relation = {"srcInnerId" : checkTable.find("tbody td.tdValue[data-key='srcInnerId']>input:text").val()};
			
		}
		if (databaseSaveRelation && relations) {
			for (var j = 0; j < databaseSaveRelation.length; j++) {
				var flag = false;
				for (var i = 0; i < relations.length; i++) {
					if (relations[i].srcInnerId == databaseSaveRelation[j].srcInnerId && relations[i].srcType == databaseSaveRelation[j].srcType && relations[i].oid == databaseSaveRelation[j].oid) {
						flag = true;
					}
				}
				if (flag == false) {
					databaseSaveRelation[j].isDel = true;
					relations.push(databaseSaveRelation[j]);
				}
			};
		}
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode = $("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val();
		var sortcode = $("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val();
		var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val();
		
		$.webeditor.showMsgBox("info", "数据保存中...");
		jQuery.post("./edit.web", {
			"atn" : "submitedittask",
			"taskid" : $("#curTaskID").html(),
			"getnext" : true,
			"relations": JSON.stringify(relations),
			/* "srcType":srcType,
			"srcInnerId": srcInnerId,
			"baiduSrcInnerId": baiduSrcInnerId,
			"baiduSrcType": baiduSrcType,
			"gaodeSrcInnerId": gaodeSrcInnerId,
			"gaodeSrcType": gaodeSrcType,
			"tengxunSrcInnerId": tengxunSrcInnerId,
			"tengxunSrcType": tengxunSrcType,
			"emgSrcInnerId": emgSrcInnerId,
			"emgSrcType": emgSrcType, */
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
			"dianpingGeo" : dianpingGeo
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
					keywordid = json.keywordid;
					if (keywordid && keywordid > 0) {
						loadKeyword(keywordid);
						loadReferdatas(keywordid);
					}
				} else {
					
				}
				
			} else {
				console.log("submitEditTask error");
			}
		});
			$.webeditor.showMsgBox("close");
		
		/* $.webeditor.showConfirmBox("alert","确定要提交并获取下一个资料吗？", function(){
			$.webeditor.showMsgBox("info", "数据保存中...");
			jQuery.post("./edit.web", {
				"atn" : "submitedittask",
				"taskid" : $("#curTaskID").html(),
				"getnext" : true,
				"srcType":srcType,
				"baiduSrcInnerId": baiduSrcInnerId,
				"baiduSrcType": baiduSrcType,
				"gaodeSrcInnerId": gaodeSrcInnerId,
				"gaodeSrcType": gaodeSrcType,
				"tengxunSrcInnerId": tengxunSrcInnerId,
				"tengxunSrcType": tengxunSrcType,
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
				"address8" : address8
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
						keywordid = json.keywordid;
						if (keywordid && keywordid > 0) {
							loadKeyword(keywordid);
							loadReferdatas(keywordid);
						}
					} else {
						
					}
					
				} else {
					console.log("submitEditTask error");
				}
				$.webeditor.showMsgBox("close");
			}, "json");
		}); */
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
		// if (!oid || oid <= 0) 	return;
		
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode = $("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val();
		var sortcode = $("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val();
		var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val();
		if(namec == null || namec.trim() == "" || featcode == null || featcode.trim()) {
			$.webeditor.showMsgLabel("alert", "名称和分类不能为空");
			return;
		}
		$.webeditor.showMsgBox("info", "数据保存中...");
		jQuery.post("./edit.web", {
			"atn" : "updatepoibyoid",
			"oid" : oid,
			"namec" : namec,
			"tel" : tel,
			"featcode" : featcode,
			"sortcode" : sortcode,
			"address4" : address4,
			"address5" : address5,
			"address6" : address6,
			"address7" : address7,
			"address8" : address8,
			"dianpingGeo" : dianpingGeo
		}, function(json) {
			if (json && json.result > 0) {
				$("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val(json.result);
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
							</tbody>
						</table>
					</div>
					<div style="position: absolute; left: 0; right: 0; bottom: 12px; height: 38px; text-align: center;">
						<button class="btn btn-default">稍后修改</button>
						<button class="btn btn-default" onClick="updatePOI();">保存</button>
						<button class="btn btn-default" onClick="submitEditTask();">提交</button>
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