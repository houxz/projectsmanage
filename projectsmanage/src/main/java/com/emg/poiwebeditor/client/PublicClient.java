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
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.ReferdataModel;

@Service
public class PublicClient {
	
	@Value("${publicApi.host}")
	private String host;
	@Value("${publicApi.port}")
	private String port;
	@Value("${publicApi.path}")
	private String path;
	
	private static final Logger logger = LoggerFactory.getLogger(PublicClient.class);
	
	private final static String selectKeywordsByIDUrl = "http://%s:%s/%s/keyword/loadbyids/%s";
	private final static String selectReferdatasByKeywordidUrl = "http://%s:%s/%s/referdata/loadbykeyword/%s";
	
	public KeywordModel selectKeywordsByID(Long keywordid) throws Exception {
		KeywordModel keyword = new KeywordModel();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectKeywordsByIDUrl, host, port, path, keywordid));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray array = (JSONArray)json;
				if (array.size() <1) return keyword;
				JSONObject data = ((JSONArray) json).getJSONObject(0);
				keyword = JSON.parseObject(data.toJSONString(), KeywordModel.class);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return keyword;
	}
	
	public List<ReferdataModel> selectReferdatasByKeywordid(Long keywordid) throws Exception {
		List<ReferdataModel> referdatas = new ArrayList<ReferdataModel>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectReferdatasByKeywordidUrl, host, port, path, keywordid));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						ReferdataModel referdata = new ReferdataModel();
						referdata = JSON.parseObject(data.getJSONObject(i).toJSONString(), ReferdataModel.class);
						referdatas.add(referdata);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return referdatas;
	}
	
}
