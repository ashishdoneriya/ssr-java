package com.csetutorials.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static Date parse(String sDate, String pattern) {
		if (StringUtils.isBlank(sDate)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(sDate.trim());
		} catch (ParseException e) {
			return null;
		}

	}
	public String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

}
