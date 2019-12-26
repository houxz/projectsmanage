package com.emg.poiwebeditor.client;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.pojo.POIDo;
import com.emg.poiwebeditor.pojo.PoiMergeDO;

@Service
public class POIClient {
	
	@Value("${poiApi.host}")
	private String host;
	@Value("${poiApi.port}")
	private String port;
	@Value("${poiApi.path}")
	private String path;
	
	private static final Logger logger = LoggerFactory.getLogger(POIClient.class);
	
	private final static String selectPOIByOidUrl = "http://%s:%s/%s/poi/ids/merge/%s";
	// private final static String selectPOIByOidUrl = "http://%s:%s/%s/poi/load/%s/%s";
	private final static String deletePOIByOidUrl = "http://%s:%s/%s/poi/delete";
	private final static String updatePOIRelationUrl = "http://%s:%s/%s/poi/upload/merge";
	
	
	private final static String updatePOIInfoUrl = "http://%s:%s/%s/poi/updateinfo";
	private final static String getPOIId = "http://%s:%s/%s/poi/maxid";
	private final static String poiRelation =  "http://%s:%s/%s/poiMerge/oid/%s";
	
	private final static String updateManucheck =  "http://%s:%s/%s/poi/manucheck/project/%s";
	private final static String updateForReady =  "http://%s:%s/%s/poi/redayforqc/%s";
	private final static String selectAdminGeojsonByAdaid =  "http://%s:%s/%s/admin/adaids/%s";
	private final static String selectBuiltupareaGeojsonByOwner =  "http://%s:%s/%s/builtuparea/owners/%s";
	private final static String selectPOIGeojsonByBoundUrl = "http://%s:%s/%s/poigeojson/bound?bound=%s"; //&minzoom=%s&namec=%s&colums=%s
	private final static String selectPOIGeojsonByOidUrl = "http://%s:%s/%s/poigeojson/ids/%s";
	private final static String selectPoiByDistance =  "http://%s:%s/%s/poi/load/distance/%s/%s/%s";
	
	private final static String selectWayByDistance = "http://%s:%s/%s/way/distance/%s/all";
	private final static String selectBackgroundByDistance = "http://%s:%s/%s/background/distance/%s/all";
	
	private final static String selectDataByBox = "http://%s:%s/%s/poi/box/%s/%s/%s";
	private String contentType = "application/json";
	
	public POIDo selectPOIByOid(Long oid) throws Exception {
		POIDo poi = new POIDo();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectPOIByOidUrl, host, port, path, oid));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					poi = JSON.parseObject(data.getJSONObject(0).toJSONString(), POIDo.class);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return poi;
	}
	
	public List<POIDo> selectPOIByOids(String oid) throws Exception {
		List<POIDo> pois = new ArrayList<POIDo>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectPOIByOidUrl, host, port, path, oid));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						POIDo poi = new POIDo();
						poi = JSON.parseObject(data.getJSONObject(i).toJSONString(), POIDo.class);
						pois.add(poi);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return pois;
	}
	
	/**
	 * 查询relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<PoiMergeDO> selectPOIRelation(String oid) throws Exception {
		List<PoiMergeDO> relations = new ArrayList<PoiMergeDO>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(poiRelation, host, port, path, oid));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						PoiMergeDO referdata = new PoiMergeDO();
						referdata = JSON.parseObject(data.getJSONObject(i).toJSONString(), PoiMergeDO.class);
						relations.add(referdata);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return relations;
	}
	
	
	
	public Long deletePOIByOid(POIDo poi) throws Exception {
		try {
			JSONObject poiJson = (JSONObject) JSON.toJSON(poi);
			HttpClientResult result = HttpClientUtils.doPost(String.format(deletePOIByOidUrl, host, port, path), contentType, poiJson.toString() );
			if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error")) {
				return Long.valueOf(1);
			} else {
				return -1L;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public Long updatePOI(Long uId, POIDo poi) throws Exception {

		HttpClientResult result = null;
		long ret = -1l;
		if (poi != null) {
			JSONObject poiJson = (JSONObject) JSON.toJSON(poi);
			result = HttpClientUtils.doPostHttpClient(String.format(updatePOIInfoUrl, host, port, path), contentType, poiJson.toString());
			if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error")) {
				String isstr = result.getJson().replace("\r\n", "");
				ret =  Long.parseLong(isstr);
			}
		}
		if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error") && ret < 0) {
			ret = 1l;
		} else if (result.getStatus().equals(HttpStatus.BAD_REQUEST) && result.getJson() != null) {
			JSONObject obj = JSONObject.parseObject(result.getJson());
			String error = obj.getString("errMsg");
			throw new Exception(error);
		}
		return ret;
	}
	
	public Long getPoiId() throws Exception {
		POIDo poi = new POIDo();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(getPOIId, host, port, path,  SystemType.poi_polymerize.getValue()));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			String isstr = result.getJson().replace("\r\n", "");
			
			return Long.parseLong(isstr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
			
	}
	public Long updatePOIToDB(POIDo poi) throws Exception {
		HttpClientResult result = null;
		if ( poi != null) {
			JSONObject poiJson = (JSONObject) JSON.toJSON(poi);
			result = HttpClientUtils.doPost(String.format(updatePOIInfoUrl, host, port, path), contentType, poiJson.toString());
			if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error")) {
				String isstr = result.getJson().replace("\r\n", "");
				
				return Long.parseLong(isstr);
			}
			
		} 
		// if (relations == null)
		if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error")) {
			return Long.valueOf(1);
		} else {
			return -1L;
		}
	}
	
	public Long updateManucheck(String projectId) throws Exception {
		HttpClientResult result = HttpClientUtils.doGet(String.format(updateManucheck, host, port, path, projectId));
		if (!result.getStatus().equals(HttpStatus.OK))
			return null;
		
		if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error")) {
			return Long.valueOf(1);
		} else {
			return -1L;
		}
	}
	
	public Long updateForReady(String oids) throws Exception {
		HttpClientResult result = HttpClientUtils.doGet(String.format(updateForReady, host, port, path, oids));
		if (!result.getStatus().equals(HttpStatus.OK))
			return null;
		
		if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error")) {
			return Long.valueOf(1);
		} else {
			return -1L;
		}
	}
	
	public String selectAdminGeojsonByAdaid(String adaid) throws Exception {
		try {
			String url = String.format(selectAdminGeojsonByAdaid, host, port, path, adaid);
			HttpClientResult result = HttpClientUtils.doGet(url);
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			return result.getJson();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public String selectBuiltupareaGeojsonByOwner(String owner) throws Exception {
		try {
			String url = String.format(selectBuiltupareaGeojsonByOwner, host, port, path, owner);
			HttpClientResult result = HttpClientUtils.doGet(url);
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			return result.getJson();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	public String selectPOIGeojsonByBound(String bound, String minzoom, String namec) throws Exception {
		try {
			String url = String.format(selectPOIGeojsonByBoundUrl, host, port, path, bound);
			if (minzoom != null) {
				url += "&minzoom=" + minzoom;
			}
			if (namec != null) {
				namec = URLEncoder.encode(namec, "utf-8");
				url += "&namec=" + namec;
			}
			
			HttpClientResult result = HttpClientUtils.doGet(url);
			if (!result.getStatus().equals(HttpStatus.OK)) {
				logger.error(result.getStatus().name()+"---"+url);
				return null;
			}
			return result.getJson();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public String selectPOIGeojsonById(String ids) throws Exception {
		try {
			String url = String.format(selectPOIGeojsonByOidUrl, host, port, path, ids);
			
			HttpClientResult result = HttpClientUtils.doGet(url);
			if (!result.getStatus().equals(HttpStatus.OK)) {
				logger.error(result.getStatus().name()+"---"+url);
				return null;
			}
			return result.getJson();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 查询relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<POIDo> selectPoiByDistance(Long distance, String location, String featcodes) throws Exception {
		List<POIDo> pois = new ArrayList<POIDo>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectPoiByDistance, host, port, path, distance, URLEncoder.encode(location, "UTF-8"), featcodes));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						POIDo poi = new POIDo();
						poi = JSON.parseObject(data.getJSONObject(i).toJSONString(), POIDo.class);
						pois.add(poi);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return pois;
	}
	
	/**
	 * 查询relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<String> selectWayByDistance( String location) throws Exception {
		List<String> pois = new ArrayList<String>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectWayByDistance, host, port, path,  URLEncoder.encode(location, "UTF-8")));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						
						String geo = data.getString(i);
						pois.add(geo);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return pois;
	}
	
	/**
	 * 查询relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<String> selectBackgroundByDistance(String location) throws Exception {
		List<String> pois = new ArrayList<String>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectBackgroundByDistance, host, port, path,  URLEncoder.encode(location, "UTF-8")));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						
						String geo = data.getString(i);
						pois.add(geo);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return pois;
	}
	
	/**
	 * 查询relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<POIDo> selectDataByBox( String location, String featcode, String exceptOids) throws Exception {
		List<POIDo> pois = new ArrayList<POIDo>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectDataByBox, host, port, path,  URLEncoder.encode(location, "UTF-8"), featcode, exceptOids));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						POIDo poi = new POIDo();
						poi = JSON.parseObject(data.getJSONObject(i).toJSONString(), POIDo.class);
						pois.add(poi);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return pois;
	}
}
