/**
 * 地图基础操作
 */
	//百度
	var ZoomViewer = L.Control.extend({
		onAdd: function(){

			var container= L.DomUtil.create('div');
			var gauge = L.DomUtil.create('div');
			container.style.width = '200px';
			container.style.background = 'rgba(255,255,255,0.5)';
			container.style.textAlign = 'left';
			if (baidumap) {
				baidumap.on('zoomstart zoom zoomend', function(ev){
					gauge.innerHTML = 'Zoom level: ' + baidumap.getZoom();
				});
			}
			
			container.appendChild(gauge);
			return container;
		}
	});
	
	function centerMap (e) {
		baidumap.panTo(e.latlng);
	}
	function zoomIn (e) {
		baidumap.zoomIn();
	}
	function zoomOut (e) {
		baidumap.zoomOut();
	}
	
 	function onMapClick(e) {
		popup
			.setLatLng(e.latlng)
			.setContent("You clicked the map at " + e.latlng.toString())
			.openOn(baidumap);
		
	}
	
	function initBaidumap() {
		baidumap = L.map("baidumap", {
	    	crs: L.CRS.Baidu,
	        center: latlng,
	        zoom: m_oldzoom,
	        layers: [BaiDu],
	        zoomControl: true,
	    	minZoom: m_minzoom,
			maxZoom: m_maxzoom,
			zoomSnap: 0.5, //zoom的
			zoomDelta: 1, //该属性控制 缩放控件（L.Control.Zoom）及键盘+/-键  每次缩放地图的级别。
			contextmenu: true,
	     	contextmenuWidth: 140,
		    contextmenuItems: [{
			      text: 'Show coordinates',
			      callback: showCoordinates
		      }, {
			      text: 'Center map here',
			      callback: centerMap
		      }, '-', {
			      text: 'Zoom in',
			      icon: 'resources/images/logo.jpg',
			      callback: zoomIn
		      }, {
			      text: 'Zoom out',
			      icon: 'resources/images/logo.jpg',
			      callback: zoomOut
		  }]
	    });
		(new ZoomViewer).addTo(baidumap);
		baidumap.on('click', onMapClick);
		baidumap.on("zoom", function (e) {
			var zoom_val = e.target.getZoom();
			var zoom = parseInt(zoom_val);
			baidumap.setZoom(zoom+0.5);
		});
	}
	
	//高德
	var ZoomVieweremg = L.Control.extend({
		onAdd: function(){

			var container= L.DomUtil.create('div');
			var gauge = L.DomUtil.create('div');
			container.style.width = '200px';
			container.style.background = 'rgba(255,255,255,0.5)';
			container.style.textAlign = 'left';
			if (emgmap) {
				gauge.innerHTML = 'Zoom level: ' + emgmap.getZoom();
				emgmap.on('zoomstart zoom zoomend', function(ev){
					gauge.innerHTML = 'Zoom level: ' + emgmap.getZoom();
				})
			}
			
			container.appendChild(gauge);

			return container;
		}
	});
	
	
	function initEmgmap(point) {
		emgmap = L.map("emgmap", {
			center: point,
	        zoom: 18,
	        minZoom: 15,
			maxZoom: 25,
	        layers: [GaodeNormalWebrd7],
	        zoomControl: true,
	        doubleClickZoom: false,
			zoomSnap: 0.5,
			zoomDelta: 1, //该属性控制 缩放控件（L.Control.Zoom）及键盘+/-键  每次缩放地图的级别。
	     });
		emgmap.invalidateSize(true);
		(new ZoomVieweremg).addTo(emgmap);
		 L.control.layers(baseLayers, {}, {
	           position: "topright"
	     	}).addTo(emgmap);
		 //map_drag(emgmap);
			//拖动地图时发生
		
		 
		 emgmap.on("dragend", function (e) {
			 map_drag(emgmap);
			});
		 emgmap.on("zoom", function (e) {
			 map_drag(emgmap);
			});
		
		
		emgmap.on("dblclick", function(e) {
			insertPOI(e);
		});
	}    
	      
	function showBaiduMap() {
		if($('#tdbaidumap').is(':hidden')){
		     //如果隐藏时。。。
			$("#ashowbaidu").addClass("selecta");
			$("#tdbaidumap").attr("width","45%");
   	   		$("#tdemgmap").attr("width","55%");
   	   		$("#tdbaidumap").show();
   	   		if( baidumap == null){
   	   			initBaidumap();
   	   		}
   	   		baidumap.invalidateSize(true);
   	   		emgmap.invalidateSize(true);
		}else{
		     //如果显示时。。。
			$("#ashowbaidu").removeClass("selecta");
			$("#tdemgmap").attr("width","100%");
	   		$("#tdbaidumap").attr("width","0%");
	   		$("#tdbaidumap").hide();
	   		emgmap.invalidateSize(true);
		}
	}
	

	var MarkIcon = L.Icon.extend({
		options: {
			// shadowUrl: 'leaf-shadow.png',
			iconSize:     [24, 24],
			shadowSize:   [12, 12],
			iconAnchor:   [12, 24],
			// shadowAnchor: [4, 62],
			popupAnchor:  [-3, -76],
			className: 'myicon',
		}
	});
		
	function getIcon( zoom) {
		var myIcon;
		switch (zoom) {
		case "1":
		case "2":
		case "3":
		case "4":
		case "5":
		case "6":
		case "7":
		case "8":
		case "9":
		case "10":
		case "11":
            myIcon = new MarkIcon({iconUrl: 'resources/images/red.png'});
			return myIcon;
//		case "11":
//            myIcon = new MarkIcon({iconUrl: 'resources/images/purple.png'});
//			return myIcon;
		case "12":
            myIcon = new MarkIcon({iconUrl: 'resources/images/yellow.png'});
			return myIcon;
		case "13":
            myIcon = new MarkIcon({iconUrl: 'resources/images/green.png'});
			return myIcon;
		case "14":
            myIcon = new MarkIcon({iconUrl: 'resources/images/lightblue.png'});
			return myIcon;
		case "15":
            myIcon = new MarkIcon({iconUrl: 'resources/images/blue.png'});
			return myIcon;
		default:
			myIcon = new MarkIcon({iconUrl: 'resources/images/green2.png'});
			return myIcon;
		}
	}