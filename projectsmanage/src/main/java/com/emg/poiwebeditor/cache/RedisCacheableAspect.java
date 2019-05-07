package com.emg.poiwebeditor.cache;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RedisCacheableAspect {

	@Autowired
	private RedisCache redisCache;

	// 切入点
	@Pointcut(value = "@annotation(RedisCacheable)")
	private void pointcut() {

	}

	/**
	 * 在方法执行前后
	 *
	 * @param point
	 * @param reCache
	 * @return
	 */
	@Around(value = "pointcut() && @annotation(reCache)")
	public Object around(ProceedingJoinPoint point, RedisCacheable reCache) {
		String key = formatKey(point, reCache.key());

		if (redisCache.exist(key)) {
			return redisCache.get(key);
		} else {
			try {
				return point.proceed(); // 执行程序
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				return throwable.getMessage();
			}
		}
	}

	/**
	 * 方法执行后
	 *
	 * @param point
	 * @param reCache
	 * @param result
	 * @return
	 */
	@AfterReturning(value = "pointcut() && @annotation(reCache)", returning = "result")
	public Object afterReturning(JoinPoint point, RedisCacheable reCache, Object result) {
		String key = formatKey(point, reCache.key());
		if (!redisCache.exist(key)) {
			redisCache.cache(key, result);
		}

		return result;
	}

	/**
	 * 方法执行后 并抛出异常
	 *
	 * @param point
	 * @param reCache
	 * @param ex
	 */
	@AfterThrowing(value = "pointcut() && @annotation(reCache)", throwing = "ex")
	public void afterThrowing(JoinPoint point, RedisCacheable reCache, Exception ex) {
		System.out.println("++++执行了afterThrowing方法++++");
		System.out.println("请求：" + reCache.key() + " 出现异常");
	}

	private String formatKey(ProceedingJoinPoint point, String strKey) {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		String[] keys = strKey.split("\\+");
		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			key = key.trim();
			if (key.startsWith("#")) {
				String _key = key.substring(1, key.length());
				if(_key.equalsIgnoreCase("root.className")) {
					sb.append(point.getTarget().getClass().getName());
					sb.append("-");
				} else if (_key.equalsIgnoreCase("root.methodName")) {
					sb.append(method.getName());
					sb.append("-");
				} else {
					sb.append(point.getArgs()[Integer.valueOf(_key) - 1]);
					sb.append("-");
				}
			} else {
				sb.append(key);
				sb.append("-");
			}
		}
		sb = sb.deleteCharAt(sb.lastIndexOf("-"));
		return sb.toString();
	}
	
	private String formatKey(JoinPoint point, String strKey) {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		String[] keys = strKey.split("\\+");

		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			key = key.trim();
			if (key.startsWith("#")) {
				String _key = key.substring(1, key.length());
				if(_key.equalsIgnoreCase("root.className")) {
					sb.append(point.getTarget().getClass().getName());
					sb.append("-");
				} else if (_key.equalsIgnoreCase("root.methodName")) {
					sb.append(method.getName());
					sb.append("-");
				} else {
					sb.append(point.getArgs()[Integer.valueOf(_key) - 1]);
					sb.append("-");
				}
			} else {
				sb.append(key);
				sb.append("-");
			}
		}
		sb = sb.deleteCharAt(sb.lastIndexOf("-"));
		return sb.toString();
	}

}
