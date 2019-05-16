package com.emg.poiwebeditor.client;

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
import com.emg.poiwebeditor.common.RoleEnum;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.pojo.ChangePOIVO;
import com.emg.poiwebeditor.pojo.POIDo;

@Service
public class POIClient {
	
	@Value("${poiApi.host}")
	private String host;
	@Value("${poiApi.port}")
	private String port;
	@Value("${poiApi.path}")
	private String path;
	
	private static final Logger logger = LoggerFactory.getLogger(POIClient.class);
	
	private final static String selectPOIByOidUrl = "http://%s:%s/%s/poi/load/%s/%s";
	private final static String deletePOIByOidUrl = "http://%s:%s/%s/poi/delete";
	private final static String updatePOIUrl = "http://%s:%s/%s/poi/upload/merge";
	
	private String contentType = "application/json";
	
	public POIDo selectPOIByOid(Long oid) throws Exception {
		POIDo poi = new POIDo();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectPOIByOidUrl, host, port, path, oid, SystemType.poi_polymerize.getValue()));
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
	
	public Long deletePOIByOid(Long oid) throws Exception {
		try {
			StringBuilder params = new StringBuilder();
			params.append("{");
			params.append("\"id\":");
			params.append(oid);
			params.append(",");
			params.append("\"systemId\":");
			params.append(SystemType.poi_polymerize.getValue());
			params.append("}");
			HttpClientResult result = HttpClientUtils.doPost(String.format(deletePOIByOidUrl, host, port, path), contentType, params.toString() );
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
		ChangePOIVO changeVO = new ChangePOIVO();
		changeVO.setRole(RoleEnum.edit);
		List<POIDo> poiModify = new ArrayList<POIDo>();
		poiModify.add(poi);
		changeVO.setPoiModify(poiModify);
		changeVO.setuId(uId);
		
		JSONObject json = (JSONObject) JSON.toJSON(changeVO);
		
		HttpClientResult result = HttpClientUtils.doPost(String.format(updatePOIUrl, host, port, path), contentType, json.toString());
		if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error")) {
			return Long.valueOf(1);
		} else {
			return -1L;
		}
	}
}
