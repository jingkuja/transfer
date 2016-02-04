/**
 * JSONUtil.java
 * Created on Dec 1, 2013 11:52:18 AM
 * Copyright (c) 2012-2014 Aves Team of Sichuan Abacus Co.,Ltd. All rights reserved.
 */
package org.aves.transfer.json;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Steven Chen
 * 
 */
public abstract class JSONUtil {

	public static void put(JSONObject jsonObject, String key, Object value) {
		try {
			if (value == null)
				return;
			if (value instanceof String) {
				String strValue = (String) value;
				strValue = strValue.trim();
				if ("".equals(strValue) || "null".equals(strValue))
					return;
			}
			if (value instanceof Date) {
				value = ((Date) value).getTime();
			}
			if (value instanceof BigDecimal) {
				BigDecimal _value = (BigDecimal) value;
				value = Utils.roundHalfUp2Scale(_value);
			}
			jsonObject.putOpt(key, value);
		} catch (JSONException e) {
		}
	}

	public static String getString(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			String _value = jsonObject.getString(key);
			if (_value == null) {
				return null;
			}
			_value = _value.trim();
			if (_value.equals("null"))
				return null;
			return _value;
		} catch (JSONException e) {
			return null;
		}
	}

	public static Date getTimemillisAsDate(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			long _value = jsonObject.getLong(key);
			if (_value < -28800000L)
				return null;
			return new Date(_value);
		} catch (JSONException e) {
			return null;
		}
	}

	public static BigDecimal getBigDecimal(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			double _value = jsonObject.getDouble(key);
			return Utils.roundHalfUp2Scale(_value);
		} catch (JSONException e) {
			return null;
		}
	}

	public static Double getDouble(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			double _value = jsonObject.getDouble(key);
			return new Double(_value);
		} catch (JSONException e) {
			return null;
		}
	}

	public static Long getLong(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			long _value = jsonObject.getLong(key);
			return new Long(_value);
		} catch (JSONException e) {
			return null;
		}
	}

	public static Integer getInteger(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			int _value = jsonObject.getInt(key);
			return new Integer(_value);
		} catch (JSONException e) {
			return null;
		}
	}

	public static Boolean getBoolean(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			boolean _value = jsonObject.getBoolean(key);
			return new Boolean(_value);
		} catch (JSONException e) {
			return null;
		}
	}

	public static JSONObject[] getJSONObjectArray(JSONObject jsonObject,
			String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			if (jsonArray == null || jsonArray.length() == 0)
				return null;
			int len = jsonArray.length();
			JSONObject[] result = new JSONObject[len];
			for (int i = 0; i < len; i++)
				result[i] = jsonArray.getJSONObject(i);
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			return jsonObject.getJSONObject(key);
		} catch (JSONException e) {
			return null;
		}
	}

	public static String getNumberArrayAsString(JSONObject jsonObject,
			String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			if (jsonArray == null || jsonArray.length() == 0)
				return null;
			StringBuilder sb = new StringBuilder();
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				int number = jsonArray.getInt(i);
				sb.append(String.valueOf(number));
				if (i != (len - 1))
					sb.append(",");
			}
		} catch (JSONException e) {
			return null;
		}
		return null;
	}

	public static Date getISO8601DateStringAsDate(JSONObject jsonObject,
			String key) {
		try {
			if (!jsonObject.has(key))
				return null;
			String timeStr = jsonObject.getString(key);
			return Utils.fromISO8601String(timeStr);
		} catch (Exception e) {
			return null;
		}
	}

	public static void putAsISO8601DateString(JSONObject jsonObject,
			String key, Date date) {
		try {
			if (date == null)
				return;
			String timeStr = Utils.toISO8601String(date);
			jsonObject.put(key, timeStr);
		} catch (JSONException e) {
		}
	}

	public static void putAsNumberArray(JSONObject jsonObject, String key,
			String numberArrayString) {
		try {
			if (numberArrayString == null || numberArrayString.length() == 0)
				return;
			String[] numberStrArray = numberArrayString.split(",");
			JSONArray jsonArray = new JSONArray();
			for (String item : numberStrArray) {
				jsonArray.put(Integer.parseInt(item));
			}
			jsonObject.put(key, jsonArray);
		} catch (NumberFormatException e) {
		} catch (JSONException e) {
		}
	}

	public static Object getObjectValue(JSONObject jsonObject, String key) {
		if (!jsonObject.has(key))
			return null;
		return jsonObject.opt(key);
	}

}
