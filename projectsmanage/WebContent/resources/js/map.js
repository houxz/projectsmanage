		/**
	     * 智图地图内容
	     */
	    var normalm1 = L.tileLayer.chinaProvider('Geoq.Normal.Map', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var normalm2 = L.tileLayer.chinaProvider('Geoq.Normal.Color', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var normalm3 = L.tileLayer.chinaProvider('Geoq.Normal.PurplishBlue', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var normalm4 = L.tileLayer.chinaProvider('Geoq.Normal.Gray', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var normalm5 = L.tileLayer.chinaProvider('Geoq.Normal.Warm', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var normalm6 = L.tileLayer.chinaProvider('Geoq.Normal.Cold', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    /**
	     * 天地图内容
	     */
	    var normalm = L.tileLayer.chinaProvider('TianDiTu.Normal.Map', {
	            maxZoom: 30,
	            minZoom: 5
	        }),
	        normala = L.tileLayer.chinaProvider('TianDiTu.Normal.Annotion', {
	            maxZoom: 30,
	            minZoom: 5
	        }),
	        imgm = L.tileLayer.chinaProvider('TianDiTu.Satellite.Map', {
	            maxZoom: 30,
	            minZoom: 5
	        }),
	        imga = L.tileLayer.chinaProvider('TianDiTu.Satellite.Annotion', {
	            maxZoom: 30,
	            minZoom: 5
	        });
	 
	    var normal = L.layerGroup([normalm, normala]),
	        image = L.layerGroup([imgm, imga]);
	    /**
	     * 谷歌
	     */
	    var normalMap = L.tileLayer.chinaProvider('Google.Normal.Map', {
	            maxZoom: 30,
	            minZoom: 1
	        }),
	        satelliteMap = L.tileLayer.chinaProvider('Google.Satellite.Map', {
	            maxZoom: 30,
	            minZoom: 1
	        });
	    /**
	     * 高德地图
	     */
	    var GaodeNormalWebrd7 = L.tileLayer.chinaProvider('GaoDe.NormalWebrd7.Map', {
	        maxZoom: 18,
	        minZoom: 1,
	        unlimited: true
	    });
	    
	    var GaodeNormalWebrd8 = L.tileLayer.chinaProvider('GaoDe.NormalWebrd8.Map', {
	        maxZoom: 18,
	        minZoom: 1
	    });
	    
	    var GaodeNormalWebst7 = L.tileLayer.chinaProvider('GaoDe.NormalWebst7.Map', {
	        maxZoom: 18,
	        minZoom: 1
	    });
	    
	    var GaodeNormalWebst8 = L.tileLayer.chinaProvider('GaoDe.NormalWebst8.Map', {
	        maxZoom: 18,
	        minZoom: 1
	    });
	    
	    var GaodeNormalWprd7 = L.tileLayer.chinaProvider('GaoDe.NormalWprd7.Map', {
	        maxZoom: 18,
	        minZoom: 1
	    });
	    
	    var GaodeNormalWprd8 = L.tileLayer.chinaProvider('GaoDe.NormalWprd8.Map', {
	        maxZoom: 18,
	        minZoom: 1
	    });
	    
	    var Gaodimgem = L.tileLayer.chinaProvider('GaoDe.Satellite.Map', {
	    	  maxZoom: 18,
		        minZoom: 1
	    });
	    var Gaodimga = L.tileLayer.chinaProvider('GaoDe.Satellite.Annotion', {
	    	  maxZoom: 18,
		      minZoom: 1
	    });
	    var Gaodimage = L.layerGroup([Gaodimgem, Gaodimga]);
	   
	    
	    /**
	     * 腾讯地图
	     */
	    var Tengxun = L.tileLayer.chinaProvider('TengXun.Normal.Map', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    
	    /**
	     * OSM地图
	     */
	    var OSM = L.tileLayer.chinaProvider('OSM.Normal.Map', {
	        maxZoom: 30,
	        minZoom: 5
	    });

	    /**
	     * EMG地图
	     */
	    var EMG = L.tileLayer.chinaProvider('EMG.Normal.Map', {
	        maxZoom: 30,
	        minZoom: 5
	    });

	    /**
	     *百度地图
	     */
/*	    var BaiDu = new L.tileLayer.baidu({ layer: 'custom',customid:'midnight'});
	    var BaiDu2 = L.tileLayer.chinaProvider('BaiDu.Normal.Map', {
	        maxZoom: 30,
	        minZoom: 5
	    });*/
	    
	    /**
	     * geoq地图
	     */
	    var Geoq = L.tileLayer.chinaProvider('Geoq.Normal.Map', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var GeoqPurplishBlue = L.tileLayer.chinaProvider('Geoq.Normal.PurplishBlue', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var GeoqGray = L.tileLayer.chinaProvider('Geoq.Normal.Gray', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    var GeoqWarm = L.tileLayer.chinaProvider('Geoq.Normal.Warm', {
	        maxZoom: 30,
	        minZoom: 5
	    });
	    
	    var baseLayers = {
	    		"高德简图WEBRD": GaodeNormalWebrd7,
	    		"高德详图WEBRD":GaodeNormalWebrd8,
	    		"高德简图WEBST": GaodeNormalWebst7,
	    		"高德详图WEBST":GaodeNormalWebst8,
	    		"高德简图WPRD": GaodeNormalWprd7,
	    		"高德详图WPRD":GaodeNormalWprd8,
		        "高德影像": Gaodimage,
		        "智图地图": normalm1,
		        "EMG地图":EMG,	      
		        "智图午夜蓝": normalm3,
		        "智图灰色": normalm4,
		        "智图暖色": normalm5,
		        "天地图": normal,
		        "天地图影像": image,
		        "谷歌地图": normalMap,
		        "谷歌影像": satelliteMap,	        
		        "腾讯地图": Tengxun,
		        "OSM地图":OSM,       
		        "GeoQ地图":Geoq,
		        "GeoqPurplishBlue":GeoqPurplishBlue,
		        "GeoqGray":GeoqGray,
		        "GeoqWarm":GeoqWarm
		    }
