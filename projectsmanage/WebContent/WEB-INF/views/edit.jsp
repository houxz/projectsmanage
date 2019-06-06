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
<script type="text/javascript">
	var $emgmap = null, $baidumap = null, $gaodemap = null, $tengxunmap = null;
	var $emgmarker = null, $baidumarker = null, $gaodemarker = null, $tengxunmarker = null;
	var $emgmarkerBase = null, $baidumarkerBase = null, $gaodemarkerBase = null, $tengxunmarkerBase = null;
	var srcType, srcInnerId, dianpingGeo;
	var emgDel, baiduDel, tengxunDel, gaodeDel;
	var keywordid = eval('(${keywordid})');
	var keyword = null;
	var systemOid = -1; // 当前编辑器左侧有OID
	// 用来存储数据库中保存着的relation 关系
	var databaseSaveRelation = [], originalCheckRelation = [];
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
			
			$.when( 
				loadKeyword(keywordid),loadReferdatas(keywordid)
			).done(function() {
				if (keyword) {
					loadRelation(keyword.srcInnerId, keyword.srcType);
				}
			});
			
			
		}else {
			$.webeditor.showMsgLabel("alert", "没有获取到参考资料");
		}
	});
	
	
	
	function loadKeyword(keywordid) {
		var dtd = $.Deferred(); 
		jQuery.post("./edit.web", {
			"atn" : "getkeywordbyid",
			"keywordid" : keywordid
		}, function(json) {
			if (json && json.result == 1 && json.rows != null) {
				keyword = json.rows;
				srcType = keyword.srcType;
				srcInnerId = keyword.srcInnerId;
				
				dianpingGeo = keyword.geo;
				$("table#tbKeyword>tbody tr td.tdValue[data-key='name']").html(keyword.name);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='address']").html(keyword.address);
				$("table#tbKeyword>tbody tr td.tdValue[data-key='telephone']").html(keyword.telephone);
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
		return dtd;
	}
	
	function loadRelation(srcInnerId, srcType) {
		jQuery.post("./edit.web", {
			"atn" : "getRelationByOid",
			"srcInnerId" : srcInnerId,
			"srcType": srcType
		}, function(json) {
			var tables = ["rdtbemg", "rdtbbaidu", "rdtbtengxun", "rdtbgaode"];
			var tbtables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
			var flags = [false, false, false, false];
			if (json && json.result == 1 && json.rows.length > 0) {
				// var relations = json.rows;
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
									loadEditPOI(srcInnerId);
									var relation = new Object();
									relation.srcInnerId = keyword.srcInnerId;
									relation.srcType = keyword.srcType;
									relation.oid = srcInnerId;
									relation.id = databaseSaveRelation[j].id;
									originalCheckRelation.push(relation);
									flags[i] = true;
								}
							}else {
								if (srcInnerId == databaseSaveRelation[j].srcInnerId && srcType == databaseSaveRelation[j].srcType && systemOid == databaseSaveRelation[j].oid) {
									$(this).prop("checked", true);
									var relation = new Object();
									relation.srcInnerId = srcInnerId;
									relation.srcType = srcType;
									relation.oid = systemOid;
									relation.id = databaseSaveRelation[j].id;
									originalCheckRelation.push(relation);
									flags[i] = true;
								}
							}
						}
						
					}); 
				}
				
			} 
			// else {
				for (var i = 0; i < tbtables.length; i++) {
					if ( $("#" + tbtables[i] + " input:checkbox") && $("#" + tbtables[i] + " input:checkbox").length > 0 && !flags[i]) {
						$("#" + tbtables[i] + " input:checkbox")[0].checked = true;
						if (i == 0 ) {
							var srcInnerId = $("#" + tbtables[i] + " input:checkbox")[0].value.split(",")[1];
							loadEditPOI(srcInnerId);
						}
						
					}
					
					
				}
			// }
		}, "json");
	}
	
	function refercompare(a, b) {
		return a.sequence - b.sequence;
	}
	
	
	function drawEMGMap(lat, lng, zoom) {
		var geo = null;
		if(dianpingGeo != null ){
			geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
		}else {
			geo = [lng, lat];
		}
		try{
			if ($emgmap != null && geo != null) {
				$emgmap.setCenter(geo);
			} else  {
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
						// dianpingGeo = 'MULTIPOINT (' + e.lngLat.lng + " " + e.lngLat.lat + "," +   e.lngLat.lng + " " + e.lngLat.lat +")";
					}
					console.log(e);
					});
			}
			if(keyword != null && keyword.geo != null && dianpingGeo != null) {
				var img = new Image();
				img.src = "resources/images/start.png";
				
				
				// var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val().replace("MULTIPOINT (","").replace(")", "").split(" ");
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
				$emgmarker.on('click', function(e) {
					console.log("in the maker");
					console.log(e);
				});
			}
			
		} catch(e) {
			
		}
	}
	
	function drawBaiDuMap(lat, lng, zoom) {
		var geo = null;
		if(dianpingGeo != null ){
			geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
		}else {
			geo = [lng, lat];
		}
		try{
			$("#baidumap").empty();
			var convertor = new BMap.Convertor();
	        var pointArr = [];
	        pointArr.push(new BMap.Point(geo[0], geo[1]));
	        var geo = null;
	        if(keyword != null && keyword.geo != null) {
				
				geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
				// var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val().replace("MULTIPOINT (","").replace(")", "").split(" ");
				pointArr.push(new BMap.Point(geo[0], geo[1]));
			}
	        convertor.translate(pointArr, 3, 5, function(data){
	        	if(data.status === 0) {
	        		$baidumap = new BMap.Map("baidumap");
	        		$baidumap.centerAndZoom(data.points[0], zoom+1);
	    			$baidumap.enableScrollWheelZoom(true);
	    			
		    	  	$baidumarker = new BMap.Marker(data.points[0]);
					$baidumarker.disableDragging();
					$baidumap.addOverlay($baidumarker);
					if (geo != null) {
						var myIcon = new BMap.Icon("resources/images/start.png",new BMap.Size(40,40));
						$baidumarkerBase = new BMap.Marker(data.points[1],{icon:myIcon});
						$baidumap.addOverlay($baidumarkerBase);
					}
		      	}
	        });
		} catch(e) {
		}
	}
	
	function drawGaoDeMap(lat, lng, zoom) {
		var geo = null;
		if(dianpingGeo != null ){
			geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
		}else {
			geo = [lng, lat];
		}
		try{
			if ($gaodemap) {
				$gaodemap.setCenter(geo);
			} else {
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
				// var geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
				// var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val().replace("MULTIPOINT (","").replace(")", "").split(" ");
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
	}
	
	function drawTengXunMap(lat, lng, zoom) {
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
				// var geo = dianpingGeo.replace("POINT (","").replace(")", "").split(" ");
				// var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val().replace("MULTIPOINT (","").replace(")", "").split(" ");
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
	}
	
	function loadEditPOI(oid) {
		if (!oid || oid <= 0) 	return;
		systemOid = oid;
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
				$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val(poi.geo);
				// dianpingGeo = poi.geo;
				poi.poitags.forEach(function(tag, index) {
					$("table#tbEdit>tbody td.tdValue[data-key='" + tag.k +"']>input:text").val(tag.v);
				});
			} else {
				$("table#tbEdit>tbody td.tdValue>input:text").val("加载失败");
			}
		}, "json");
	}
	
	

	function rdChange(ck, srcType, lat, lng, srcInnerId) {
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
			
		}else if (key == "class") {
			var parent = $($this.parents("table")[0]).attr("id");
			
			if (parent.indexOf("tbbaidu") != -1) {
				var valueArray = value.split(";");
				getCode(baiduCode, valueArray);
			}else if(parent.indexOf("tbtengxun") != -1) {
				var valueArray = value.split(":");
				getCode(tengxunCode, valueArray);
			}else if(parent.indexOf("tbgaode") != -1) {
				var valueArray = value.split(";");
				getCode(gaodeCode, valueArray);
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
						$("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val(poi.featcode);
						$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val(poi.sortcode);
						
					}
				}, "json");
			}
		}else if (key == "geo" && value != null) {
			var geo = value.replace("MULTIPOINT (","").replace(")", "").split(",")[0].split(" ");
			$emgmarker.setLngLat(geo);
		}
	}
	
	function getCode(objCodes, values) {
		if (objCodes == null || values == null) return;
		var flag = false;
		for (var j =  values.length -1 ; j > -1; j--) {
			for (let key in objCodes){
			// for (var i = 0; i < Object.keys(objCodes).length; i++) {
				if(objCodes[key].name == values[j]) {
					$("table#tbEdit>tbody td.tdValue[data-key='featcode']>textarea").val(objCodes[key].featcode);
					$("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val(objCodes[key].featcode);
					$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>textarea").val(objCodes[key].sortcode);
					$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val(objCodes[key].sortcode);
					flag = true;
					break;
				}
			}
			if (flag) break;
		}
	}
	
	function drawReferdatas(tbid, referdatas) {
		var $tbody = $("table#" + tbid + ">tbody");
		$tbody.empty();
		referdatas.forEach(function(referdata, index) {
			var html = new Array();
			html.push("<table id ='"+ tbid + referdata.id + "' class=\"table table-bordered table-condensed\"><tbody>");
			html.push("<tr class='trIndex'><td class='tdIndex' rowspan='6'>");
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
		    
		    html.push("<tr><td class='tdKey'>坐标</td>");
		    html.push("<td class='tdValue' data-key='geo'>MULTIPOINT (" + referdata.srcLon + " " + referdata.srcLat + "," +   referdata.srcLon + " " + referdata.srcLat +")</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		    
		    html.push("<tr class='trEnd'><td class='tdKey'>地址</td>");
		    html.push("<td class='tdValue' data-key='address'>" + referdata.address + "</td>");
		    html.push("<td class='tbTool'><span class='glyphicon glyphicon-share cursorable' onClick='textCopy(this);'></span></td></tr>");
		    
		   
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
						// loadEditPOI(emgrefers[0].srcInnerId);
						drawEMGMap(emgrefers[0].srcLat, emgrefers[0].srcLon, zoom);
						drawReferdatas("tbemg", emgrefers);
						emgSrcInnerId = emgrefers[0].srcInnerId;
						emgSrcType = emgrefers[0].srcType;
						//addMakerOnEMGMap($emgmap, true, $emgmarkerBase);
					} else {
						$("#emgmap").html("无数据");
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
						$("table#tbgaode>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					if (tengxunrefers && tengxunrefers.length > 0) {
						tengxunrefers.sort(refercompare);
						drawTengXunMap(tengxunrefers[0].srcLat, tengxunrefers[0].srcLon, zoom);
						drawReferdatas("tbtengxun", tengxunrefers);
					} else {
						$("#tengxunmap").html("无数据");
						$("table#tbtengxun>tbody").html("<tr><td>无数据</td></tr>");
					}
					
					/* if (keyword) {
						loadRelation(keyword.srcInnerId, keyword.srcType);
					} */
				}
			} else {
				
			}
			dtd.resolve();
		}, "json");
		return dtd;
	}
	
	//标识资料错误
	function keywordError(){
		var isLoadNextTask = $("table#tbEdit>tbody td.tdKey[data-key='isLoadNextTask']>input:checkbox").prop("checked");
	
			$.webeditor.showMsgBox("info", "数据保存中...");
			jQuery.post("./edit.web", {
				"atn" : "keywordError",
				"taskid" : $("#curTaskID").html(),
				"getnext" : isLoadNextTask,
				"keywordid": keyword.id
				
				// "isLoadNextTask": isLoadNextTask
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
						$("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val("");
						keywordid = json.keywordid;
						
						if (keywordid && keywordid > 0) {
							
							$.when( 
								loadKeyword(keywordid),loadReferdatas(keywordid)
							).done(function() {
								if (keyword) {
									loadRelation(keyword.srcInnerId, keyword.srcType);
								}
							});
						}
					} else {
						
					}
					
				} else {
					console.log("submitEditTask error");
				}
				$.webeditor.showMsgBox("close");
			}, "json");
	
	}
	
	function submitEditTask() {
		var oid = null;
		try {
			oid = $("table#tbEdit>tbody td.tdValue[data-key='oid']>input:text").val();
		} catch(e) {
			return;
		}
		// if (!oid || oid <= 0) 	return;
		// var $tbemg = $("#tbbaidu");
		var tables = ["tbemg", "tbbaidu", "tbtengxun", "tbgaode"];
		var relations = [];
		var emgChecked = false;
		for (var i = 0; i < tables.length; i++) {
			// $("#" + tables[i] + " table tbody tr :checkbox")
			$("#" + tables[i] + " input:checked").each(function(){
				relations.push();
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
					// relations.push({"srcInnerId"： keyword.srcInnerId, "srcType"： keyword.srcType, "oid": oid});
					relations.push(relation);
				}else if (i > 0 && oid > 0){
					
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
			
			
		}
		if (!emgChecked && oid > -1 && keyword != null && keyword.srcInnerId != null) {
			var relation = new Object();
			relation.srcInnerId = keyword.srcInnerId;
			relation.srcType = keyword.srcType;
			relation.oid = oid;
			relation.qid = keyword.qid;
			relation.errorType = keyword.errorType;
			// relations.push({"srcInnerId"： keyword.srcInnerId, "srcType"： keyword.srcType, "oid": oid});
			relations.push(relation);
		}
		
		var delFlag = false;
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
					/* if (relations[i].srcInnerId == databaseSaveRelation[j].srcInnerId && relations[i].srcType == databaseSaveRelation[j].srcType && relations[i].oid == databaseSaveRelation[j].oid && databaseSaveRelation[j].importTime == null) {
						// 此种情况为该relation已经存在且为正确关系，只需要修改qid和errortype
						flag = true;
					}else if(relations[i].srcInnerId == databaseSaveRelation[j].srcInnerId && relations[i].srcType == databaseSaveRelation[j].srcType && relations[i].oid != databaseSaveRelation[j].oid && databaseSaveRelation[j].importTime != null) {
						// 此种情况需要人工确认，表明数据库中的关系存在且已经确认过，但现在提交的与库里保存的不一致
						delFlag = true;
						databaseSaveRelation[j].isDel = true;
						relations.push(databaseSaveRelation[j]);
					}else if(relations[i].srcInnerId == databaseSaveRelation[j].srcInnerId && relations[i].srcType == databaseSaveRelation[j].srcType && relations[i].oid != databaseSaveRelation[j].oid && databaseSaveRelation[j].importTime == null) {
						//这种情况说明该relation在库里存在，但不正确且没有人工确认过可以直接修改
						// flag = true;
						databaseSaveRelation[j].isDel = true;
						relations.push(databaseSaveRelation[j]);
					} */
				}
				
			}
		}
		
		var projectId = $("#curProjectID").val();
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode = $("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val();
		var sortcode = $("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val();
		var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val();
		var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val();
		var isLoadNextTask = $("table#tbEdit>tbody td.tdKey[data-key='isLoadNextTask']>input:checkbox").prop("checked");
		$.webeditor.showMsgBox("info", "数据保存中...");
		if(delFlag) {
			$.webeditor.showConfirmBox("alert","保存的关系已经在数据库中存在，是否删除原关系保存新的关系", function(){
				$.webeditor.showMsgBox("info", "数据保存中...");
				jQuery.post("./edit.web", {
					"atn" : "submitedittask",
					"taskid" : $("#curTaskID").html(),
					"getnext" : isLoadNextTask,
					"relations": JSON.stringify(relations),
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
					"geo" : geo,
					"projectId": projectId
					// "isLoadNextTask": isLoadNextTask
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
							$("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val("");
							$("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val("");
							keywordid = json.keywordid;
							/* if (keywordid && keywordid > 0) {
								loadKeyword(keywordid);
								loadReferdatas(keywordid);
								
							} */
							if (keywordid && keywordid > 0) {
								
								$.when( 
									loadKeyword(keywordid),loadReferdatas(keywordid)
								).done(function() {
									if (keyword) {
										loadRelation(keyword.srcInnerId, keyword.srcType);
									}
								});
							}
						} else {
							
						}
						
					} else {
						console.log("submitEditTask error");
					}
				}, "json");
			});
		}else {
			jQuery.post("./edit.web", {
				"atn" : "submitedittask",
				"taskid" : $("#curTaskID").html(),
				"getnext" : isLoadNextTask,
				"relations": JSON.stringify(relations),
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
				"geo" : geo
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
						$("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val("");
						$("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val("");
						keywordid = json.keywordid;
						/* if (keywordid && keywordid > 0) {
							loadKeyword(keywordid);
							loadReferdatas(keywordid);
						} */
						if (keywordid && keywordid > 0) {
							
							$.when( 
								loadKeyword(keywordid),loadReferdatas(keywordid)
							).done(function() {
								if (keyword) {
									loadRelation(keyword.srcInnerId, keyword.srcType);
								}
							});
						}
					} else {
						
					}
					
				} else {
					console.log("submitEditTask error");
				}
			});
		}
		
		/*  */
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
		// if (!oid || oid <= 0) 	return;
		var projectId = $("#curProjectID").val();
		var namec = $("table#tbEdit>tbody td.tdValue[data-key='name']>textarea").val();
		var tel = $("table#tbEdit>tbody td.tdValue[data-key='tel']>input:text").val();
		var featcode = $("table#tbEdit>tbody td.tdValue[data-key='featcode']>input:text").val();
		var sortcode = $("table#tbEdit>tbody td.tdValue[data-key='sortcode']>input:text").val();
		var address4 = $("table#tbEdit>tbody td.tdValue[data-key='address4']>input:text").val();
		var address5 = $("table#tbEdit>tbody td.tdValue[data-key='address5']>input:text").val();
		var address6 = $("table#tbEdit>tbody td.tdValue[data-key='address6']>input:text").val();
		var address7 = $("table#tbEdit>tbody td.tdValue[data-key='address7']>input:text").val();
		var address8 = $("table#tbEdit>tbody td.tdValue[data-key='address8']>input:text").val();
		var geo = $("table#tbEdit>tbody td.tdValue[data-key='geo']>input:text").val();
		if(namec == null || namec.trim() == "" || featcode == null || featcode.trim() == "" || geo == null || geo.trim() == "") {
			$.webeditor.showMsgLabel("alert", "名称、分类、坐标不能为空");
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
			"geo" : geo,
			"projectId": projectId
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
								<tr>
									<td class="tdKey">坐标</td>
									<td class="tdValue" data-key="geo"><input class="form-control input-sm" type="text"></td>
								</tr>
								<tr>
									<td class="tdKey" data-key="isLoadNextTask"><input  type="checkbox" checked="true"></td>
									<td class="tdValue" >是否获取下一条任务</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div style="position: absolute; left: 0; right: 0; bottom: 12px; height: 38px; text-align: center;">
						<button class="btn btn-default">稍后修改</button>
						<button class="btn btn-default" onClick="updatePOI();">保存</button>
						<button class="btn btn-default" onClick="submitEditTask();">提交</button>
						<button class="btn btn-default" onClick="keywordError();">资料错误</button>
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