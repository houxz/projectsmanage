package com.emg.poiwebeditor.client;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emg.poiwebeditor.common.RoleEnum;
import com.emg.poiwebeditor.pojo.ChangePOIVO;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.ModifiedlogDO;
import com.emg.poiwebeditor.pojo.PoiMergeDO;
import com.emg.poiwebeditor.pojo.ReferdataModel;
import com.emg.poiwebeditor.pojo.TaskModel;

@Service
public class PublicClient {
	
	@Value("${publicApi.host}")
	private String host;
	@Value("${publicApi.port}")
	private String port;
	@Value("${publicApi.path}")
	private String path;
	
	private String contentType = "application/json";
	private String contentType_urlencoded = "x-www-form-urlencoded";
	
	private static final Logger logger = LoggerFactory.getLogger(PublicClient.class);
	
	private final static String selectKeywordsByIDUrl = "http://%s:%s/%s/keyword/loadbyids/%s";
	private final static String selectReferdatasByKeywordidUrl = "http://%s:%s/%s/referdata/loadbykeyword/%s";
	private final static String updateKeyword = "http://%s:%s/%s/keyword/update";
	private final static String updatePOIRelationUrl = "http://%s:%s/%s/poiMerge/upload";
	private final static String getRelationByKeword =  "http://%s:%s/%s/poiMerge/%s/%s";
	private final static String getRelationByOid =  "http://%s:%s/%s/poiMerge/oid/%s";
	private final static String uploadModifiedlog =  "http://%s:%s/%s/modifiedlog/upload";
	
	private final static String loadModifiedlog =  "http://%s:%s/%s/modifiedlog/load/%s";
	
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
	
	public List<KeywordModel> selectKeywordsByIDs(List<Long> keywordids) throws Exception {
		List<KeywordModel> keywordlist = new ArrayList< KeywordModel>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(selectKeywordsByIDUrl, host, port, path, StringUtils.join(keywordids, ",")));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray array = (JSONArray)json;
				if (array.size() <1) return keywordlist;
				for(int i = 0 ;i < array.size(); i++) {
					JSONObject data = ((JSONArray) json).getJSONObject(i);
					KeywordModel keyword = JSON.parseObject(data.toJSONString(), KeywordModel.class);
					keywordlist.add(keyword);
				}
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return keywordlist;
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
	
	public Long updateKeyword(KeywordModel keyword) throws Exception {
		List<ReferdataModel> referdatas = new ArrayList<ReferdataModel>();
		HttpClientResult result = null;
		if(keyword != null) {
			JSONObject json = (JSONObject) JSON.toJSON(keyword);
			result = HttpClientUtils.doPost(String.format(updateKeyword, host, port, path), contentType, json.toString());
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
	
	public Long updateRelations(Long uId, List<PoiMergeDO> relations) throws Exception {

		ChangePOIVO changeVO = new ChangePOIVO();
		changeVO.setRole(RoleEnum.edit);
		changeVO.setuId(uId);
		
		changeVO.setPoiMergeModify(relations);
		JSONObject json = (JSONObject) JSON.toJSON(changeVO);
		HttpClientResult result = null;
		long ret = -1l;
		
			changeVO.setPoiMergeModify(relations);
			result = HttpClientUtils.doPostHttpClient(String.format(updatePOIRelationUrl, host, port, path), contentType, json.toString());
		
		if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error") && ret < 0) {
			ret = 1l;
		} else if (result.getStatus().equals(HttpStatus.BAD_REQUEST) && result.getJson() != null) {
			JSONObject obj = JSONObject.parseObject(result.getJson());
			String error = obj.getString("errMsg");
			throw new Exception(error);
		}
		return ret;
	}
	
	public Long updateModifiedlogs( List<ModifiedlogDO> logs) throws Exception {

		JSONArray json= JSONArray.parseArray(JSON.toJSONString(logs));
		HttpClientResult result = null;
		long ret = -1l;
			result = HttpClientUtils.doPostHttpClient(String.format(uploadModifiedlog, host, port, path), contentType,  json.toString());
		
		if (result.getStatus().equals(HttpStatus.OK) && !result.getJson().contains("error") && ret < 0) {
			ret = 1l;
		} else if (result.getStatus().equals(HttpStatus.BAD_REQUEST) && result.getJson() != null) {
			JSONObject obj = JSONObject.parseObject(result.getJson());
			String error = obj.getString("errMsg");
			throw new Exception(error);
		}
		return ret;
	}
	
	/**
	 * ����keywordȥ��ѯrelation,����д��ڵ�relation������ҵ��Ĺ�ϵ�е�oid,��ȥ���Ҹ���relation��ص�relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<PoiMergeDO> selectPOIRelation(String srcInnerId, int srcType) throws Exception {
		List<PoiMergeDO> relations = new ArrayList<PoiMergeDO>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(getRelationByKeword, host, port, path, srcInnerId, srcType));
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
	
	/**
	 * ����keywordȥ��ѯrelation,����д��ڵ�relation������ҵ��Ĺ�ϵ�е�oid,��ȥ���Ҹ���relation��ص�relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<PoiMergeDO> selectPOIRelation(long oid) throws Exception {
		List<PoiMergeDO> relations = new ArrayList<PoiMergeDO>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(getRelationByOid, host, port, path, oid));
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
	
	/**
	 * ����keywordȥ��ѯrelation,����д��ڵ�relation������ҵ��Ĺ�ϵ�е�oid,��ȥ���Ҹ���relation��ص�relation
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public List<ModifiedlogDO> loadModifiedLog(long keywordid) throws Exception {
		List<ModifiedlogDO> logs = new ArrayList<ModifiedlogDO>();
		try {
			HttpClientResult result = HttpClientUtils.doGet(String.format(loadModifiedlog, host, port, path, keywordid));
			if (!result.getStatus().equals(HttpStatus.OK))
				return null;
			
			Object json = JSONArray.parse(result.getJson());
			if (json instanceof JSONArray) {
				JSONArray data = (JSONArray) json;
				if (data != null && data.size() > 0) {
					for (Integer i = 0, len = data.size(); i < len; i++) {
						ModifiedlogDO logitem = new ModifiedlogDO();
						logitem = JSON.parseObject(data.getJSONObject(i).toJSONString(), ModifiedlogDO.class);
						logs.add(logitem);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return logs;
	}
	
}
