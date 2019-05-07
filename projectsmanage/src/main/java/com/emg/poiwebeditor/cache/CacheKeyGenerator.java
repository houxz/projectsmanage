package com.emg.poiwebeditor.cache;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import net.sf.json.JSONObject;

@Component("baseCacheKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {

	// custom cache key
	@Value("${redis.key.prefix}")
	private String PREFIX;

	@Override
	public Object generate(Object target, Method method, Object... params) {
		StringBuilder key = new StringBuilder();
		String className = target.getClass().getSimpleName();
		String methodName = method.getName();

		key.append(PREFIX + "->");
		key.append(className + ".");
		key.append(methodName);

		if (params.length <= 0) {
			return key.toString();
		}

		key.append("->[");
		for (Object param : params) {
			if (param == null) {
				key.append("NULL");
			} else if (ClassUtils.isPrimitiveArray(param.getClass()) || ClassUtils.isPrimitiveWrapperArray(param.getClass())) {
				for (int i = 0, length = Array.getLength(param); i < length; i++) {
					key.append(Array.get(param, i));
					key.append(',');
				}
			} else if(param instanceof List) {
				for (int i = 0, length = ((List<?>) param).size(); i < length; i++) {
					key.append(((List<?>) param).get(i));
					key.append(',');
				}
			} else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {
				key.append(param);
			} else {
				key.append(JSONObject.fromObject(param).toString());
			}
			key.append(",");
		}
		key = key.deleteCharAt(key.lastIndexOf(","));
		key.append("]");
		return key.toString();
	}
}
