package com.emg.poiwebeditor.client;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	
	private final static String getReferdataUrl = "http://%s:%s/%s/poi/ids/%s";
	
	public KeywordModel selectKeywordsByID(Long keywordid) throws Exception {
		KeywordModel keyword = new KeywordModel();
		try {
			// TODO:
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return keyword;
	}
	
	public List<ReferdataModel> selectReferdatasByKeywordid(Long keywordid) throws Exception {
		List<ReferdataModel> referdatas = new ArrayList<ReferdataModel>();
		try {
			// TODO:
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return referdatas;
	}
}
