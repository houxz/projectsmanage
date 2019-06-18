package com.emg.poiwebeditor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

public class HttpClientUtils {
	public static HttpClientResult doGet(String httpurl) {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader br = null;
		HttpClientResult result = new HttpClientResult();
		try {
			URL url = new URL(httpurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(60000);
			connection.connect();
			if (connection.getResponseCode() == HttpStatus.OK.value()) {
				is = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				StringBuffer sbf = new StringBuffer();
				String temp = null;
				while ((temp = br.readLine()) != null) {
					sbf.append(temp);
					sbf.append("\r\n");
				}
				result.setStatus(HttpStatus.OK);
				result.setJson(sbf.toString());
			} else {
				result.setStatus(HttpStatus.valueOf(connection.getResponseCode()));
				result.setResultMsg(connection.getResponseMessage());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			connection.disconnect();
		}

		return result;
	}

	public static HttpClientResult doPost(String httpUrl, String contentType, String param) {
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
		HttpClientResult result = new HttpClientResult();
		try {
			URL url = new URL(httpUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(60000);

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", contentType);
			if (param != null && !param.isEmpty()) {
				os = connection.getOutputStream();
				os.write(param.getBytes("UTF-8"));
			}
			if (connection.getResponseCode() == HttpStatus.OK.value()) {

				is = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

				StringBuffer sbf = new StringBuffer();
				String temp = null;
				while ((temp = br.readLine()) != null) {
					sbf.append(temp);
					sbf.append("\r\n");
				}
				result.setStatus(HttpStatus.OK);
				result.setJson(sbf.toString());
			} else {
				result.setStatus(HttpStatus.valueOf(connection.getResponseCode()));				
				result.setResultMsg(connection.getResponseMessage());
				
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			connection.disconnect();
		}
		return result;
	}
	
	/**
	 * 
	 * @param url
	 * @param contentType
	 * @param param
	 */
	public static HttpClientResult doPostHttpClient(String url, String contentType,String param) {
		HttpClientResult result = new HttpClientResult();
 
		// ���Http�ͻ���(�������Ϊ:�������һ�������;ע��:ʵ����HttpClient��������ǲ�һ����)
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
 
		// ����Post����
		HttpPost httpPost = new HttpPost(url);
		
		StringEntity entity = new StringEntity(param, "UTF-8");
 
		// post�����ǽ������������������洫��ȥ��;���ｫentity����post��������
		httpPost.setEntity(entity);
 
		// httpPost.setHeader("Content-Type", "application/json;charset=utf8");
		httpPost.setHeader("Content-Type", contentType);
 
		// ��Ӧģ��
		CloseableHttpResponse response = null;
		try {
			// �ɿͻ���ִ��(����)Post����
			response = httpClient.execute(httpPost);
			// ����Ӧģ���л�ȡ��Ӧʵ��
			HttpEntity responseEntity = response.getEntity();
			
			result.setStatus(HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
			// System.out.println("��Ӧ״̬Ϊ:" + response.getStatusLine());
			if (responseEntity != null) {
				/*System.out.println("��Ӧ���ݳ���Ϊ:" + responseEntity.getContentLength());
				System.out.println("��Ӧ����Ϊ:" + EntityUtils.toString(responseEntity));*/
				result.setJson(EntityUtils.toString(responseEntity));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// �ͷ���Դ
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}