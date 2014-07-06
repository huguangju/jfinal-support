package com.jfinal.ext.base.util;

import javax.servlet.http.HttpServletRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * IP工具类
 */
public class IpUtil {

	/**
	 * 获取登录用户的IP地址
	 * 对手机无效
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		if (ip.split(",").length > 1) {
			ip = ip.split(",")[0];
		}
		return ip;
	}

	public static String getIpByMobile(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		if (ip != null && (ip.equals("127.0.0.1") || ip.startsWith("192")))
			ip = getIp(request);

		return ip;
	}

	public static JSONObject getIp(String ip) {
		String result = new Http()
				.get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip="
						+ ip);

		JSONObject obj = (JSONObject) JSON.parse(result);

		return obj;

	}

	public static String getIpAddr() {

		String ip = new Http()
				.get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json");
		String info = "";

		JSONObject obj = (JSONObject) JSON.parse(ip);
		info += obj.getString("province") + " ";
		info += obj.getString("city") + " ";
		info += obj.getString("isp");

		return info;
	}

	public static String getIpCity() {
		String ip = new Http()
				.get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=");
		String info = "";
		JSONObject obj = (JSONObject) JSON.parse(ip);
		info += obj.getString("city");

		return info;
	}

	public static String getWeather(String location) {

		String url = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ location + "&output=json&ak=640f3985a6437dad8135dae98d775a09";
		String ip = new Http().get(url);

		String info = "";

		JSONObject obj = (JSONObject) JSON.parse(ip);

		if ("success".equals(obj.getString("status"))) {
			JSONObject data = obj.getJSONArray("results").getJSONObject(0);

			data = data.getJSONArray("weather_data").getJSONObject(0);
			info += info += location + "天气  ";
			info += data.getString("weather") + " ";
			info += data.getString("wind") + " ";
			info += data.getString("temperature") + " ";
		}

		return info;
	}

}
