package com.emg.poiwebeditor.client;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ExecuteSQLApiClientUtils {
	
	public static Object postModel(String httpUrl, String contentType, String params, Class<?> modelClass) throws Exception {
		Object model = modelClass.newInstance();
		HttpClientResult result = HttpClientUtils.doPost(httpUrl, contentType, params);
		if (!result.getStatus().equals(HttpStatus.OK))
			return null;
		
		JSONObject json = JSONObject.parseObject(result.getJson());
		if (json.containsKey("data")) {
			Object data = json.get("data");
			if (data instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) data;
				model = JSON.parseObject(jsonArray.get(0).toString(), modelClass);
			}
		}
		return model;
	}
	
	public static Object getModel(String httpurl, Class<?> modelClass) throws Exception {
		Object model = modelClass.newInstance();
		HttpClientResult result = HttpClientUtils.doGet(httpurl);
		if (!result.getStatus().equals(HttpStatus.OK))
			return null;
		
		JSONObject json = JSONObject.parseObject(result.getJson());
		if (json.containsKey("data")) {
			Object data = json.get("data");
			if (data instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) data;
				model = JSON.parseObject(jsonArray.get(0).toString(), modelClass);
			}
		}
		return model;
	}
	
	public static JSONObject getGeoJSON(String httpurl) throws Exception {
		HttpClientResult result = HttpClientUtils.doGet(httpurl);
		if (!result.getStatus().equals(HttpStatus.OK))
			return null;
		
		JSONObject json = JSONObject.parseObject(result.getJson());
		if (json.containsKey("data")) {
			Object data = json.get("data");
			if (data instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) data;
				return jsonArray.getJSONObject(0).getJSONObject("jsonb_build_object").getJSONObject("value");
			} else
				return null;
		} else 
			return null;
	}
	
	public static ArrayList<Object> getList(String httpurl, Class<?> modelClass) throws Exception {
		ArrayList<Object> list = new ArrayList<Object>();
		HttpClientResult result = HttpClientUtils.doGet(httpurl);
		if (!result.getStatus().equals(HttpStatus.OK))
			return list;
		
		JSONObject json = JSONObject.parseObject(result.getJson());
		if (json.containsKey("data")) {
			Object data = json.get("data");
			if (data instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) data;
				for (Integer i = 0, len = jsonArray.size(); i < len; i++) {
					Object model = modelClass.newInstance();
					model = JSON.parseObject(jsonArray.get(i).toString(), modelClass);
					list.add(model);
				}
			}
		}
		return list;
	}
	
	public static Long update(String httpurl) throws Exception {
		Long ret = -1L;
		HttpClientResult result = HttpClientUtils.doGet(httpurl);
		if (!result.getStatus().equals(HttpStatus.OK))
			return -1L;
		
		ret = 1L;
		return ret;
	}
	
	public static ArrayList<Object> updateAndGetList(String httpurl, Class<?> modelClass) throws Exception {
		ArrayList<Object> list = new ArrayList<Object>();
		HttpClientResult result = HttpClientUtils.doGet(httpurl);
		if (!result.getStatus().equals(HttpStatus.OK))
			return list;
		
		JSONObject json = JSONObject.parseObject(result.getJson());
		if (json.containsKey("data")) {
			Object data = json.get("data");
			if (data instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) data;
				for (Integer i = 0, len = jsonArray.size(); i < len; i++) {
					Object model = modelClass.newInstance();
					model = JSON.parseObject(jsonArray.get(i).toString(), modelClass);
					list.add(model);
				}
			}
		}
		return list;
	}
}