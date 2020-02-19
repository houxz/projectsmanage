		/**
	     * 智图地图内容
	     */
	    var normalm1 = L.tileLayer.chinaProvider('Geoq.Normal.Map', {
	        maxZoom: 18,
	        minZoom: 5,
	        unlimited: true
	    });
	    var normalm2 = L.tileLayer.chinaProvider('Geoq.Normal.Color', {
	        maxZoom: 18,
	        minZoom: 5,
	        unlimited: true
	    });
	    var normalm3 = L.tileLayer.chinaProvider('Geoq.Normal.PurplishBlue', {
	        maxZoom: 16,
	        minZoom: 5,
	        unlimited: true
	    });
	    var normalm4 = L.tileLayer.chinaProvider('Geoq.Normal.Gray', {
	        maxZoom: 16,
	        minZoom: 5,
	        unlimited: true
	    });
	    var normalm5 = L.tileLayer.chinaProvider('Geoq.Normal.Warm', {
	        maxZoom: 16,
	        minZoom: 5,
	        unlimited: true
	    });
	    var normalm6 = L.tileLayer.chinaProvider('Geoq.Normal.Cold', {
	        maxZoom: 18,
	        minZoom: 5,
	        unlimited: true
	    });
	    /**
	     * 天地图内容
	     */
	    var normalm = L.tileLayer.chinaProvider('TianDiTu.Normal.Map', {
	            maxZoom: 18,
	            minZoom: 5,
		        unlimited: true
	        }),
	        normala = L.tileLayer.chinaProvider('TianDiTu.Normal.Annotion', {
	            maxZoom: 18,
	            minZoom: 5,
		        unlimited: true
	        }),
	        imgm = L.tileLayer.chinaProvider('TianDiTu.Satellite.Map', {
	            maxZoom: 18,
	            minZoom: 5,
		        unlimited: true
	        }),
	        imga = L.tileLayer.chinaProvider('TianDiTu.Satellite.Annotion', {
	            maxZoom: 18,
	            minZoom: 5,
		        unlimited: true
	        });
	 
	    var normal = L.layerGroup([normalm, normala]),
	        image = L.layerGroup([imgm, imga]);
	    /**
	     * 谷歌
	     */
	    var normalMap = L.tileLayer.chinaProvider('Google.Normal.Map', {
	            maxZoom: 18,
	            minZoom: 1,
	            unlimited: true
	            
	        }),
	        satelliteMap = L.tileLayer.chinaProvider('Google.Satellite.Map', {
	            maxZoom: 18,
	            minZoom: 1,
	            unlimited: true
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
	        minZoom: 1,
	        unlimited: true
	    });
	    
	    var GaodeNormalWebst7 = L.tileLayer.chinaProvider('GaoDe.NormalWebst7.Map', {
	        maxZoom: 18,
	        minZoom: 1,
	        unlimited: true
	    });
	    
	    var GaodeNormalWebst8 = L.tileLayer.chinaProvider('GaoDe.NormalWebst8.Map', {
	        maxZoom: 18,
	        minZoom: 1,
	        unlimited: true
	    });
	    
	    var GaodeNormalWprd7 = L.tileLayer.chinaProvider('GaoDe.NormalWprd7.Map', {
	        maxZoom: 18,
	        minZoom: 1,
	        unlimited: true
	    });
	    
	    var GaodeNormalWprd8 = L.tileLayer.chinaProvider('GaoDe.NormalWprd8.Map', {
	        maxZoom: 18,
	        minZoom: 1,
	        unlimited: true
	    });
	    
	    var Gaodimgem = L.tileLayer.chinaProvider('GaoDe.Satellite.Map', {
	    	  maxZoom: 18,
		        minZoom: 1,
		        unlimited: true
	    });
	    var Gaodimga = L.tileLayer.chinaProvider('GaoDe.Satellite.Annotion', {
	    	  maxZoom: 18,
		      minZoom: 1,
		        unlimited: true
	    });
	    var Gaodimage = L.layerGroup([Gaodimgem, Gaodimga]);
	   
	    
	    /**
	     * 腾讯地图
	     */
	    var Tengxun = L.tileLayer.chinaProvider('TengXun.Normal.Map', {
	        maxZoom: 18,
	        minZoom: 5,
	        unlimited: true
	    });
	    
	    /**
	     * OSM地图
	     */
	    var OSM = L.tileLayer.chinaProvider('OSM.Normal.Map', {
	        maxZoom: 18,
	        minZoom: 5,
	        unlimited: true
	    });

	    /**
	     * EMG地图
	     */
	    var EMG = L.tileLayer.chinaProvider('EMG.Normal.Map', {
	        maxZoom: 16,
	        minZoom: 5,
	        unlimited: true
	    });

	    /**
	     *百度地图
	     */
/*	    var BaiDu = new L.tileLayer.baidu({ layer: 'custom',customid:'midnight'});
	    var BaiDu2 = L.tileLayer.chinaProvider('BaiDu.Normal.Map', {
	        maxZoom: 18,
	        minZoom: 5
	    });*/
	    
	    /**
	     * geoq地图
	     */
	    var Geoq = L.tileLayer.chinaProvider('Geoq.Normal.Map', {
	        maxZoom: 18,
	        minZoom: 5,
	        unlimited: true
	    });
	    var GeoqPurplishBlue = L.tileLayer.chinaProvider('Geoq.Normal.PurplishBlue', {
	        maxZoom: 16,
	        minZoom: 5,
	        unlimited: true
	    });
	    var GeoqGray = L.tileLayer.chinaProvider('Geoq.Normal.Gray', {
	        maxZoom: 16,
	        minZoom: 5,
	        unlimited: true
	    });
	    var GeoqWarm = L.tileLayer.chinaProvider('Geoq.Normal.Warm', {
	        maxZoom: 16,
	        minZoom: 5,
	        unlimited: true
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
