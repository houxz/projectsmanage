function compare(value1, value2) {
	if (value1 < value2) {
		return -1;
	} else if (value1 > value2) {
		return 1;
	} else {
		return 0;
	}
}

function isIE() {
	if (!!window.ActiveXObject || "ActiveXObject" in window)
		return true;
	else
		return false;
}

var Sys = {};
var ua = navigator.userAgent.toLowerCase();
var s;
(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;

var imgoffset, imgwidth, imgheight;

function imgZoom(obj, center, scale) {
    var that = $(obj).children("img");
    var h0 = that.height();
    var w0 = that.width();
    var tarWidth = w0 * scale;
    var tarHeight = h0 * scale;
    that.height(parseInt(tarHeight));
    that.width(parseInt(tarWidth));
    var offset = that.offset();
    that.offset({
        top: (center.y-that.height()*(center.y-offset.top)/h0),
        left: (center.x-that.width()*(center.x-offset.left)/w0)
    });
}

function renewImg() {
	var img = $("#curPic");
	if (!img)	return;
	
	img.offset(imgoffset);
	img.width(imgwidth);
	img.height(imgheight);
}

function initImg() {
	var img = $("#curPic");
	if (!img)	return;
	
	img.draggable();
	imgoffset = img.parent().offset();
	imgwidth = img.parent().width();
	img.width(img.parent().width());
	imgheight = img.parent().height();
	img.height(img.parent().height());
	
	img.show();
}

function isDislogOpen() {
	var dialogIsOpen = false;
	try{
		dialogIsOpen = $("#dlgPOI").dialog('isOpen');
	}catch(e){
		dialogIsOpen = false;
	}
	return dialogIsOpen;
}

function drawTaskLine(shapeGeoJsonData) {
	if (map.getLayer("lines"))
		map.removeLayer("lines");
	if (map.getSource("taskline"))
		map.removeSource("taskline");
	
	map.addSource("taskline", {
		'type': 'geojson',
		'data': shapeGeoJsonData
	});
	
	map.addLayer({
	   "id": "lines", 
	   "type": "line", 
	   "source": 'taskline', 
	   "paint": {
    		"line-width": 4,
    		"line-color": "blue"
		},
		"layout" : {
    		"line-cap" : "round",
    		"line-join" : "round",
		}
   	});
}

function drawMap(centerX, centerY, shapeGeoJsonData, attachesJson) {
	$("#map").show();
	
	map = new emapgo.Map({
        container : 'map',
        style: 'http://tiles.emapgo.cn/styles/outdoor/style.json',
        zoom: 16,
        localIdeographFontFamily: "'Noto Sans', 'Noto Sans CJK SC', sans-serif",
        center:  [centerX, centerY],
        hash :true
    });
    map.on('load',function(){
    	drawTaskLine(shapeGeoJsonData);
    	drawAttachPoint(attachesJson);
    });
}

function removeMap() {
	$("#map").hide();
	
	var points = $("div.point");
	if(points && points.length > 0) {
		points.remove();
	}
	if (map != undefined) {
		map.remove();
		map = null;
	}
}

function reloadMap(centerX, centerY, shapeGeoJsonData, attachesJson) {
	removeMap();
	drawMap(centerX, centerY, shapeGeoJsonData, attachesJson);
}

function toggleMap() {
	var mapIsShow = PWEConfig.getLocalConfig("mapIsShow");
	if (mapIsShow) {
		$("div#toggleMapIcon").css("margin-bottom", 0);
		removeMap();
		$("div#divpois").height($("div#divpois").height() + 320);
	} else {
		$("div#toggleMapIcon").css("margin-bottom", "-20px");
		drawMap(centerX, centerY, shapeGeoJsonData, attachesJson);
		$("div#divpois").height($("div#divpois").height() - 320);
	}
	PWEConfig.setLocalConfig("mapIsShow", !mapIsShow);
}

function initMap() {
	var mapIsShow = PWEConfig.getLocalConfig("mapIsShow");
	if (mapIsShow) {
		$("div#toggleMapIcon").css("margin-bottom", "-20px");
		drawMap(centerX, centerY, shapeGeoJsonData, attachesJson);
	} else {
		$("div#toggleMapIcon").css("margin-bottom", 0);
		removeMap();
		$("div#divpois").height($("div#divpois").height() + 320);
	}
}

function thumbnailTo(index) {
	$("#thumbnails a").removeClass("active");
	$("#thumbnails a:eq(" + index + ")").focus();
	$("#thumbnails a:eq(" + index + ")").addClass("active");
}
function thumbnailDone(index) {
	$("#thumbnails a:eq(" + index + ")").removeClass("red");
	$("#thumbnails a:eq(" + index + ")").addClass("green");
}
function thumbnailError(index) {
	$("#thumbnails a:eq(" + index + ")").removeClass("green");
	$("#thumbnails a:eq(" + index + ")").addClass("red");
}
function mapPointTo(index) {
	$("#map div.point").removeClass("active");
	$("#map div.point:eq(" + index + ")").addClass("active");
}
function mapPointDone(index) {
	$("#map div.point:eq(" + index + ")").removeClass("red");
	$("#map div.point:eq(" + index + ")").addClass("green");
}
function mapPointError(index) {
	$("#map div.point:eq(" + index + ")").removeClass("green");
	$("#map div.point:eq(" + index + ")").addClass("red");
}

function setAttachPstate(attaches, attachid, pstate) {
	if (!attaches || attaches.length <= 0)
		return;
	
	for(var i = 0, len = attaches.length; i < len; i++) {
		if (attaches[i].id == attachid) {
			attaches[i].pstate = pstate;
			return;
		}
	}
}
function deletePOI(attaches, attachid, poiid) {
	if (!attaches || attaches.length <= 0)
		return;
	
	for(var i = 0, len = attaches.length; i < len; i++) {
		if (attaches[i].id == attachid) {
			for (var j = 0, lenj = attaches[i].pois.length; j < lenj; j++) {
				if (attaches[i].pois[j].id == poiid) {
					attaches[i].pois.splice(j, 1);
					return;
				}
			}
		}
	}
}
function setErrorPstate(attaches, attachid, poiid, errorid, pstate) {
	if (!attaches || attaches.length <= 0)
		return;
	
	for(var i = 0, len = attaches.length; i < len; i++) {
		if (attaches[i].id == attachid) {
			for (var j = 0, lenj = attaches[i].pois.length; j < lenj; j++) {
				if (attaches[i].pois[j].id == poiid) {
					for (var k = 0, lenk = attaches[i].pois[j].errors.length; k < lenk; k++) {
						if (attaches[i].pois[j].errors[k].id == errorid) {
							attaches[i].pois[j].errors[k].pstate = pstate;
							return;
						}
					}
				}
			}
		}
	}
}
function attachIsEditing(attach) {
	var pstate = attach.pstate;
	return pstate == 1;
}
function attachNeedEdit(attach) {
	try {
		if (attach && attach.pstate != undefined) {
			var pstate = attach.pstate;
			return pstate == 0 || pstate == 1;
		} else {
			return false;
		}
	} catch(e) {
		return false;
	}
}
function attachIsChecking(attach) {
	var pstate = attach.pstate;
	return pstate == 3;
}
function attachNeedCheck(attach) {
	try {
		if (attach && attach.pstate) {
			var pstate = attach.pstate;
			return pstate == 2 || pstate == 3;
		} else {
			return false;
		}
	} catch(e) {
		return false;
	}
}
function attachIsModifying(attach) {
	var pstate = attach.pstate;
	return pstate == 5;
}
function attachNeedModify(attach) {
	try {
		if (attach && attach.pstate) {
			var pstate = attach.pstate;
			return pstate == 4 || pstate == 5;
		} else {
			return false;
		}
	} catch(e) {
		return false;
	}
}
function attachHasUnModifyError(attach) {
	try {
		if (attach && attach.pois && attach.pois.length > 0) {
			for (var i = 0, len = attach.pois.length; i < len; i++) {
				var poi = attach.pois[i];
				if (poi.errors && poi.errors.length > 0) {
					for (var j = 0, lenj = poi.errors.length; j < lenj; j++) {
						var error = poi.errors[j];
						if (error && error.pstate != undefined && (error.pstate == 0 || error.pstate == 41))
							return true;
					}
				}
			}
			return false;
		} else {
			return false;
		}
	} catch(e) {
		return false;
	}
}
function attachHasUnCheckError(attach) {
	try {
		if (attach && attach.pois && attach.pois.length > 0) {
			for (var i = 0, len = attach.pois.length; i < len; i++) {
				var poi = attach.pois[i];
				if (poi.errors && poi.errors.length > 0) {
					for (var j = 0, lenj = poi.errors.length; j < lenj; j++) {
						var error = poi.errors[j];
						if (error && error.pstate != undefined && error.pstate == 61)
							return true;
					}
				}
			}
			return false;
		} else {
			return false;
		}
	} catch(e) {
		return false;
	}
}

function emptyPOIList() {
	var tbody = $("#pois tbody");
	tbody.empty();
}

function lazyloadImagesUp(up) {
	var index = curAttachIndex + up;
	var _img = $("div#thumbnails>a>img[data-index=" + index + "]");
	if (!_img || _img.length <= 0)
		return;
	
	var img = _img[0];
	var src = _img.data("lazysrc")
	
	img.onload = function(){
        img.onload = null;
        lazyloadImagesUp(++up);
	}
	
	img.src = src;
}
function lazyloadImagesDown(down) {
	var index = curAttachIndex + down;
	var _img = $("div#thumbnails>a>img[data-index=" + index + "]");
	if (!_img || _img.length <= 0)
		return;
	
	var img = _img[0];
	var src = _img.data("lazysrc")
	
	img.onload = function(){
        img.onload = null;
        lazyloadImagesDown(--down);
	}
	
	img.src = src;
}
