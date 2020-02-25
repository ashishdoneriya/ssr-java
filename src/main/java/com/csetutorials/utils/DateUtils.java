package com.csetutorials.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	public static Date parse(String sDate, String pattern) throws ParseException {
		if (StringUtils.isBlank(sDate)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(sDate.trim());

	}

	public String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public synchronized static String getSiteMapString(Date date) {
		if (date == null) {
			return null;
		}
		return sdf.format(date);
	}

}
