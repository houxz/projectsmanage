package com.emg.projectsmanage.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class assists skin writers in getting parameters.
 */
public class ParamUtils {

	/**
	 * Gets a parameter as a string.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @return The value of the parameter or null if the parameter was not found
	 *         or if the parameter is a zero-length string.
	 */
	public static String getParameter(HttpServletRequest request, String name) {
		return getParameter(request, name, false);
	}

	/**
	 * Gets a parameter as a string.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @param emptyStringsOK
	 *            Return the parameter values even if it is an empty string.
	 * @return The value of the parameter or null if the parameter was not
	 *         found.
	 */
	public static String getParameter(HttpServletRequest request, String name,
			boolean emptyStringsOK) {
		String temp = request.getParameter(name);
		if (temp != null) {
			if (temp.equals("") && !emptyStringsOK) {
				return null;
			} else {
				return temp;
			}
		} else {
			return null;
		}
	}

	public static String getParameter(HttpServletRequest request, String name,
			String defaultVal) {
 		String temp = request.getParameter(name);
		if (temp != null) {
			if (temp.equalsIgnoreCase("")) {
				return "";
			} else {
				return temp;
			}
		} else {
			return defaultVal;
		}
	}

	public static final Map<String, Object> getMapParameter(HttpServletRequest request,
			Object[] objs, Object[] defObjs) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		for (int i = 0; i < objs.length; i++) {
			getParameterToMap(request, objs[i].toString(), defObjs[i]
					.toString(), map);
		}
		return map;
	}

	public static final void getParameterToMap(HttpServletRequest request,
			String name, String defaultVal, Map<String, Object> map) {
		String value = getParameter(request, name, defaultVal);
		map.put(name, value);
	}

	/**
	 * Gets a parameter as a boolean.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @return True if the value of the parameter was "true", false otherwise.
	 */
	public static boolean getBooleanParameter(HttpServletRequest request,
			String name) {
		return getBooleanParameter(request, name, false);
	}

	/**
	 * Gets a parameter as a boolean.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @return True if the value of the parameter was "true", false otherwise.
	 */
	public static boolean getBooleanParameter(HttpServletRequest request,
			String name, boolean defaultVal) {
		String temp = request.getParameter(name);
		if ("true".equals(temp) || "on".equals(temp)) {
			return true;
		} else if ("false".equals(temp) || "off".equals(temp)) {
			return false;
		} else {
			return defaultVal;
		}
	}

	/**
	 * Gets a parameter as an int.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @return The int value of the parameter specified or the default value if
	 *         the parameter is not found.
	 */
	public static int getIntParameter(HttpServletRequest request, String name,
			int defaultNum) {
		String temp = request.getParameter(name);
		if (temp != null && !temp.equals("")) {
			temp = temp.trim();
			int num = defaultNum;
			try {
				num = Integer.parseInt(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	/**
	 * Gets a list of int parameters.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @param defaultNum
	 *            The default value of a parameter, if the parameter can't be
	 *            converted into an int.
	 */
	public static int[] getIntParameters(HttpServletRequest request,
			String name, int defaultNum) {
		String[] paramValues = request.getParameterValues(name);
		if (paramValues == null) {
			return null;
		}
		if (paramValues.length < 1) {
			return new int[0];
		}
		int[] values = new int[paramValues.length];
		for (int i = 0; i < paramValues.length; i++) {
			try {
				values[i] = Integer.parseInt(paramValues[i]);
			} catch (Exception e) {
				values[i] = defaultNum;
			}
		}
		return values;
	}

	/**
	 * Gets a parameter as a double.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @return The double value of the parameter specified or the default value
	 *         if the parameter is not found.
	 */
	public static double getDoubleParameter(HttpServletRequest request,
			String name, double defaultNum) {
		String temp = request.getParameter(name);
		if (temp != null && !temp.equals("")) {
			double num = defaultNum;
			try {
				num = Double.parseDouble(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	/**
	 * Gets a parameter as a long.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @return The long value of the parameter specified or the default value if
	 *         the parameter is not found.
	 */
	public static long getLongParameter(HttpServletRequest request,
			String name, long defaultNum) {
//		String temp = request.getParameter(name);
//		if (temp != null && !temp.equals("")) {
//			long num = defaultNum;
//			try {
//				num = Long.parseLong(temp);
//			} catch (Exception ignored) {
//			}
//			return num;
//		} else {
//			return defaultNum;
//		}
		Object o =request.getParameter(name);
		String temp =null;
		if(null != o){
			if(o instanceof Long)
				return (Long) o;
		 temp= o.toString();
		}else{
			return defaultNum;
		}
		if (temp != null && !temp.equals("")) {
			Long num = defaultNum;
			try {
				num = Long.parseLong(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	/**
	 * Gets a list of long parameters.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @param defaultNum
	 *            The default value of a parameter, if the parameter can't be
	 *            converted into a long.
	 */
	public static long[] getLongParameters(HttpServletRequest request,
			String name, long defaultNum) {
		String[] paramValues = request.getParameterValues(name);
		if (paramValues == null) {
			return null;
		}
		if (paramValues.length < 1) {
			return new long[0];
		}
		long[] values = new long[paramValues.length];
		for (int i = 0; i < paramValues.length; i++) {
			try {
				values[i] = Long.parseLong(paramValues[i]);
			} catch (Exception e) {
				values[i] = defaultNum;
			}
		}
		return values;
	}

	/**
	 * Gets a parameter as a string.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @return The value of the parameter or null if the parameter was not found
	 *         or if the parameter is a zero-length string.
	 */
	public static String getAttribute(HttpServletRequest request, String name) {
		return getAttribute(request, name, false);
	}

	/**
	 * Gets a parameter as a string.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the parameter you want to get
	 * @param emptyStringsOK
	 *            Return the parameter values even if it is an empty string.
	 * @return The value of the parameter or null if the parameter was not
	 *         found.
	 */
	public static String getAttribute(HttpServletRequest request, String name,
			boolean emptyStringsOK) {
		Object o = request.getAttribute(name);
		if (o == null) {
			return null;
		}
		String temp = o.toString();
		if (temp != null) {
			if (temp.equals("") && !emptyStringsOK) {
				return null;
			} else {
				return temp;
			}
		} else {
			return null;
		}
	}
	
	public static String getAttribute(HttpServletRequest request, String name, String defaultStr) {
		Object o = request.getAttribute(name);
		if (o == null) {
			return defaultStr;
		}
		String temp = o.toString();
		if (temp != null) {
			if ("".equals(temp)) {
				return defaultStr;
			} else {
				return temp;
			}
		} else {
			return defaultStr;
		}
	}
	
	public static String getSessionAttribute(HttpSession session, String name, String defaultStr) {
		Object o = session.getAttribute(name);
		if (o == null) {
			return defaultStr;
		}
		String temp = o.toString();
		if (temp != null) {
			if ("".equals(temp)) {
				return defaultStr;
			} else {
				return temp;
			}
		} else {
			return defaultStr;
		}
	}

	/**
	 * Gets an attribute as a boolean.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the attribute you want to get
	 * @return True if the value of the attribute is "true", false otherwise.
	 */
	public static boolean getBooleanAttribute(HttpServletRequest request,
			String name) {
		String temp = String.valueOf(request.getAttribute(name));
		if (temp != null && temp.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets an attribute as a int.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the attribute you want to get
	 * @return The int value of the attribute or the default value if the
	 *         attribute is not found or is a zero length string.
	 */
	public static int getIntAttribute(HttpServletRequest request, String name,
			int defaultNum) {
//		String temp = (String) request.getAttribute(name);
//		if (temp != null && !temp.equals("")) {
//			int num = defaultNum;
//			try {
//				num = Integer.parseInt(temp);
//			} catch (Exception ignored) {
//			}
//			return num;
//		} else {
//			return defaultNum;
//		}
		Object o =request.getAttribute(name);
		String temp =null;
		if(null != o){
			if(o instanceof Integer)
				return (Integer) o;
		 temp= o.toString();
		}else{
			return defaultNum;
		}
		if (temp != null && !temp.equals("")) {
			int num = defaultNum;
			try {
				num = Integer.parseInt(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}
	public static int getIntAttribute(HttpSession session, String name,
			int defaultNum) {
		Object o =session.getAttribute(name);
		String temp =null;
		if(null != o){
			if(o instanceof Integer)
				return (Integer) o;
		 temp= o.toString();
		}else{
			return defaultNum;
		}
		if (temp != null && !temp.equals("")) {
			int num = defaultNum;
			try {
				num = Integer.parseInt(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	/**
	 * Gets an attribute as a Map.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the attribute you want to get
	 * @return The Map value of the attribute or the default value if the
	 *         attribute is not found or is a zero length string.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapAttribute(HttpServletRequest request, String name,
			Map<String, Object> defaultMap) {
		try {
			Map<String, Object> temp = (Map<String, Object>) request.getAttribute(name);
			if (temp != null) {
				return temp;
			} else {
				return defaultMap;
			}
		} catch (Exception ignored) {
			return defaultMap;
		}
	}
	
	/**
	 * Gets an attribute as a List.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the attribute you want to get
	 * @return The List value of the attribute or the default value if the
	 *         attribute is not found or is a zero length string.
	 */
	@SuppressWarnings("rawtypes")
	public static List getListAttribute(HttpServletRequest request, String name,
			List defaultList) {
		try {
			List temp = (List) request.getAttribute(name);
			if (temp != null) {
				return temp;
			} else {
				return defaultList;
			}
		} catch (Exception ignored) {
			return defaultList;
		}
	}

	@SuppressWarnings("rawtypes")
	public static List getListAttribute(HttpSession session, String name,
			List defaultList) {
		try {
			List temp = (List) session.getAttribute(name);
			if (temp != null) {
				return temp;
			} else {
				return defaultList;
			}
		} catch (Exception ignored) {
			return defaultList;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Vector getVectorAttribute(HttpServletRequest request, String name,
			Vector defaultVector) {
		try {
			Vector temp = (Vector) request.getAttribute(name);
			if (temp != null) {
				return temp;
			} else {
				return defaultVector;
			}
		} catch (Exception ignored) {
			return defaultVector;
		}
	}
	/**
	 * Gets an attribute as a long.
	 * 
	 * @param request
	 *            The HttpServletRequest object, known as "request" in a JSP
	 *            page.
	 * @param name
	 *            The name of the attribute you want to get
	 * @return The long value of the attribute or the default value if the
	 *         attribute is not found or is a zero length string.
	 */
	public static long getLongAttribute(HttpServletRequest request,
			String name, long defaultNum) {
		//李镭 20110628
		////////////////////////////////////////////////////////////
		Object o =request.getAttribute(name);
		String temp =null;
		if(null != o){
			if(o instanceof Long)
				return (Long) o;
		 temp= o.toString();
		}else{
			return defaultNum;
		}
		//String temp = (String) request.getAttribute(name);
		/////////////////////////////////////////////////////////////////
		if (temp != null && !temp.equals("")) {
			long num = defaultNum;
			try {
				num = Long.parseLong(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}
	public static Long getLongAttribute(HttpSession session,
			String name, long defaultNum) {
		Object o =session.getAttribute(name);
		String temp =null;
		if(null != o){
			if(o instanceof Long)
				return (Long) o;
		 temp= o.toString();
		}else{
			return defaultNum;
		}
		if (temp != null && !temp.equals("")) {
			Long num = defaultNum;
			try {
				num = Long.parseLong(temp);
			} catch (Exception ignored) {
			}
			return num;
		} else {
			return defaultNum;
		}
	}

	public static String replace(String template, String placeholder,
			String value) {
		if (template == null)
			return (null);
		if ((placeholder == null) || (value == null))
			return (template);
		while (true) {
			int index = template.indexOf(placeholder);
			if (index < 0)
				break;
			StringBuffer temp = new StringBuffer(template.substring(0, index));
			temp.append(value);
			temp.append(template.substring(index + placeholder.length()));
			template = temp.toString();
		}
		return (template);

	}

	/**
	 * 取得提交参数的数组
	 * 
	 * @author zhouyu Date:Aug 8, 2007
	 * @param request
	 * @param key
	 * @return Return_Type:Collection<Long> Description:
	 */
	public static final Collection<Long> getParameterValues(
			HttpServletRequest request, String key) {

		String[] ids = request.getParameterValues(key);
		if (ids == null) {
			return new ArrayList<Long>(0);
		}
		List<Long> videoIds = new ArrayList<Long>(ids.length);
		Long id = null;
		for (int i = 0; i < ids.length; i++) {
			try {
				id = Long.parseLong(ids[i]);
			} catch (Exception e) {
				id = null;
			}
			if (id != null) {
				videoIds.add(id);
			}
		}
		return videoIds;
	}

}