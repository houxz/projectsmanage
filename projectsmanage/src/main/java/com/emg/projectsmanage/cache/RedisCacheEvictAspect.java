package com.emg.projectsmanage.cache;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.vividsolutions.jts.geom.Geometry;

@Aspect
@Component
public class RedisCacheEvictAspect {

	@Autowired
	private RedisCache redisCache;

	// 切入点
	@Pointcut(value = "@annotation(com.emg.projectsmanage.cache.RedisCacheEvict)")
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
	public Object around(ProceedingJoinPoint point, RedisCacheEvict reCache) {
		try {
			return point.proceed();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return throwable.getMessage();
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
	public Object afterReturning(JoinPoint point, RedisCacheEvict reCache, Object result) {
		try {
			if (!redisCache.getEnabled())
				return result;

			List<Object> key = formatKey(point, reCache.key());
			Set<String> bounds = new HashSet<String>();
			List<Geometry> geos = new ArrayList<Geometry>();
			if (reCache.layer().equalsIgnoreCase("way")) {} else if (reCache.layer().equalsIgnoreCase("changeset")) {} else {

			}

			bounds = RedisUtils.getBoundsByGeometry(geos);
			if (bounds.size() > 0)
				redisCache.deleteKeysByBounds(bounds);
		} catch (Exception e) {
			e.printStackTrace();
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
	public void afterThrowing(JoinPoint point, RedisCacheEvict reCache, Exception ex) {
		System.out.println("++++执行了afterThrowing方法++++");
		System.out.println("请求：" + reCache.key() + " 出现异常");
	}

	private List<Object> formatKey(JoinPoint point, String strKey) {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		String[] keys = strKey.split("\\+");

		List<Object> ret = new ArrayList<>();
		for (String key : keys) {
			key = key.trim();
			if (key.startsWith("#")) {
				String _key = key.substring(1, key.length());
				if (_key.equalsIgnoreCase("root.methodName")) {
					ret.add(method.getName());
				} else {
					ret.add(point.getArgs()[Integer.valueOf(_key) - 1]);
				}
			} else {
			}
		}
		return ret;
	}

}
