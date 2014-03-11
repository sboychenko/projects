package ru.bserg.pricegen.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Commons {
	private static String DATE_FORMAT =  "dd MMMM yyyy";

	public static Date getDate() {
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date date = new Date(cal.getTimeInMillis());
		return date;
	}

	public static String getCurrentDateStr() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(new Date());
	}

	public static void setDATE_FORMAT(String dATE_FORMAT) {
		DATE_FORMAT = dATE_FORMAT;
	}
}
