package com.alien_roger.court_deadlines.utils;

import java.util.Calendar;

import com.alien_roger.court_deadlines.statics.StaticData;

/**
 * CommonUtils class
 *
 * @author alien_roger
 * @created at: 19.01.12 8:10
 */
public class CommonUtils {

	public static Calendar getDateByCode(Calendar courtCalendar,String code) throws NumberFormatException{
		Calendar calendar = (Calendar) courtCalendar.clone();
//		Calendar calendar = Calendar. getInstance();
		// ATC60
		// all codes are XXYDD..(TTjj)
		// where:
		// XX - before or after court/trial date
		// Y  - type of days - calendar/works
		// DD - number of days
		// TT - time of the day
		// jj - am/pm
//		public static final String D_AFTER_T 	= "AT";
//		public static final String D_BEFORE_T 	= "BT";
//		public static final String D_AFTER_C 	= "AC";
//		public static final String D_BEFORE_C 	= "BC";
//		public static final String D_WORK 		= "C";
//		public static final String D_CALENDAR	= "W";

		String posCode = code.substring(0,2);
		String daysCode = code.substring(2,3);
		String numberOfDays;
		if(code.contains(StaticData.D_TIME_1)){
			numberOfDays = code.substring(3,code.indexOf(StaticData.D_TIME_1));
		}else{
			numberOfDays = code.substring(3);
		}

		int daysCnt = 0 ;

//		try {
			daysCnt = Integer.parseInt(numberOfDays);
//		} catch (NumberFormatException e) {
////			throw NumberFormatException
//		}


		if(posCode.equals(StaticData.D_AFTER_T) || posCode.equals(StaticData.D_AFTER_C)){
			calendar.add(Calendar.DATE, daysCnt);
		}else if(posCode.equals(StaticData.D_BEFORE_T) || posCode.equals(StaticData.D_BEFORE_C)){
			calendar.add(Calendar.DATE, -daysCnt);
		}

		return calendar;
	}



}
