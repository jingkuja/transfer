/**
 * DateKit.java
 * Created on Dec 17, 2013 3:34:47 PM
 * Copyright (c) 2012-2014 Aves Team of Sichuan Abacus Co.,Ltd. All rights reserved.
 */
package org.aves.transfer.json;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Steven Chen
 * 
 */
public abstract class Utils {

	private static final String ISO8601StringRegx = "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):?(\\d{2})?([\\+|\\-])(\\d{2}):(\\d{2})";

	public static Double roundHalfUp2Scale(BigDecimal value) {
		if (value == null)
			return null;
		BigDecimal bd = value.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static BigDecimal roundHalfUp4Scale(Double value) {
		if (value == null)
			return null;
		String sval = String.valueOf(value);
		BigDecimal bd = new BigDecimal(sval);
		return bd.setScale(4, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal roundHalfUp2Scale(Double value) {
		if (value == null)
			return null;
		String sval = String.valueOf(value);
		BigDecimal bd = new BigDecimal(sval);
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static String toTrim(String original) {
		if (original == null)
			return null;
		String value = original.trim();
		if (value.length() == 0)
			return null;
		return value;
	}

	public static String toCSVString(String[] strArray) {
		if (strArray == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> itr = Arrays.asList(strArray).iterator(); itr
				.hasNext();) {
			String s = itr.next();
			sb.append(s);
			if (itr.hasNext())
				sb.append(",");
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		String str = "1980-05-07T10:00:00-05:00";
		System.out.println(fromISO8601String(str));
	}

	public static Date fromISO8601String(String dateAsString) {
		Pattern p = Pattern.compile(ISO8601StringRegx);
		Matcher m = p.matcher(dateAsString);
		if (!m.matches())
			return null;

		String[] parts = dateAsString.split("T");
		if (parts == null || parts.length < 2)
			return null;
		String[] ymd = parts[0].split("-");
		if (ymd == null || ymd.length < 3)
			return null;

		Calendar calendar = new GregorianCalendar();

		calendar.set(Calendar.YEAR, Integer.valueOf(ymd[0]));
		calendar.set(Calendar.MONTH, Integer.valueOf(ymd[1]) - 1);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(ymd[2]));

		String[] timePart = parts[1].split("[+|-]");

		if (timePart == null || timePart.length < 2)
			return calendar.getTime();

		String[] times = timePart[0].split(":");
		if (times == null || times.length < 2)
			return calendar.getTime();
		if (times.length == 2) {
			calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(times[0]));
			calendar.set(Calendar.MINUTE, Integer.valueOf(times[1]));
		} else {
			calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(times[0]));
			calendar.set(Calendar.MINUTE, Integer.valueOf(times[1]));
			calendar.set(Calendar.SECOND, Integer.valueOf(times[2]));
		}
		TimeZone timeZone = null;
		if (dateAsString.lastIndexOf("+") != -1) {
			timeZone = TimeZone.getTimeZone(String.format("GMT%s%s", "+",
					timePart[1]));
		} else {
			timeZone = TimeZone.getTimeZone(String.format("GMT%s%s", "-",
					timePart[1]));
		}
		calendar.setTimeZone(timeZone);
		return calendar.getTime();
	}

	public static String toISO8601String(Date date) {
		String pattern = "%d-%02d-%02dT%02d:%02d:%02d%s";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int md = calendar.get(Calendar.DAY_OF_MONTH);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		TimeZone timeZone = calendar.getTimeZone();
		String zoneStr = timeZone.getDisplayName().replace("GMT", "");
		return String.format(pattern, year, month + 1, md, hours, minutes,
				seconds, zoneStr);

	}
}
