package com.emg.poiwebeditor.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.emg.poiwebeditor.common.SystemType;
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
}
