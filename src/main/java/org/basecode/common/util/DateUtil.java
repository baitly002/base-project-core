package org.basecode.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
	
	public static  Logger   log = LoggerFactory.getLogger(DateUtil.class);
	
	/**
	 * 转换日期字符串为日期对象
	 * @param format 日期的格式
	 * @param dateStr 日期
	 * @return
	 */
	public static Date getDate(String format,String dateStr){
		try {
			SimpleDateFormat sf = new SimpleDateFormat ( format ) ;
			return sf.parse(dateStr);
		} catch (ParseException e) {
			log.error(""+e.getMessage());
			return null;
		}
	}
	
	/**
	 * 将日期对象转换为指定日期字符串
	 * @param format 日期格式
	 * @param date 日期
	 * @return
	 */
	public static String getFormatString(String format,Date date){
		  try {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			String strdate = sf.format(date);
			return strdate;
		} catch (Exception e) {
			log.error(""+e.getMessage());
			return "";
		}
	}
	
	/**
	 * 将日期字符串转换为对象
	 * @param dateStr 日期字符串，必需是yyyy-MM-dd HH:mm:ss格式
	 * @return
	 */
	public static Date getDate(String dateStr) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" ) ;
			return sf.parse(dateStr);
		} catch (ParseException e) {
			log.error(""+e.getMessage());
			return null;
		}
	}
	/**
	 * 获取今天是几月几号
	 * @return 比如今天是2月5号，则返回0205
	 */
	public static String gettoday4num(){
		Calendar ca=Calendar.getInstance();
		Integer month=ca.get(Calendar.MONTH)+1;
		String m=month.toString();
		if(m.length()<2){
			m="0"+m;
		}
		Integer day=ca.get(Calendar.DAY_OF_MONTH);
		String d=day.toString();
		if(d.length()<2){
			d="0"+d;
		}
		return m+d;
	}
	/**
	 * 获取今天的月份
	 * @return 比如今天是2019-02-05，则返回201902
	 */
	public static Date tomonth(){
		Calendar ca=Calendar.getInstance();
		Integer year=ca.get(Calendar.YEAR);
		Integer month=ca.get(Calendar.MONTH)+2;
		String m=month.toString();
		if(m.length()<2){
			m="0"+m;
		}
		String datestr=year+m;
		SimpleDateFormat sf = new SimpleDateFormat ( "yyyyMM" ) ;
		
		try {
			return sf.parse(datestr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getTimenum(String dateStr) {
		try {
			Calendar cal=Calendar.getInstance();
			Integer year=cal.get(Calendar.YEAR);
			Integer month=cal.get(Calendar.MONTH)+1;
			if( Integer.parseInt(dateStr.substring(0,2))>month){
				year=year-1;
			}
//			String yearstr=year.toString();
			dateStr=year+dateStr;
			SimpleDateFormat sf = new SimpleDateFormat ( "yyyyMMdd" ) ;
			return sf.parse(dateStr);
		} catch (ParseException e) {
			log.error(""+e.getMessage());
			return null;
		}
	}
	public static java.sql.Date getsqldate(String datestr){
		try {
//			Date d=new Date();
			Calendar right=Calendar.getInstance();
			
			Integer year =right.get(Calendar.YEAR);
			Integer month=right.get(Calendar.MONTH)+1;
			if( Integer.parseInt(datestr.substring(0,2))>month){
				year=year-1;
			}
			datestr=year+datestr;
			
			SimpleDateFormat sf = new SimpleDateFormat ( "yyyyMMdd" ) ;
			Date utildate= sf.parse(datestr);
			java.sql.Date sqldate=new  java.sql.Date(utildate.getTime());
			
			return  sqldate;
		} catch (ParseException e) {
			log.error(""+e.getMessage());
			return null;
		}
	}
	
	public static Date getDateUnkownFormat(String dateStr) {
		 for (Iterator<SimpleDateFormat> iterator = sdfs.iterator(); iterator.hasNext();) {
			SimpleDateFormat sdf =  iterator.next();
			try {
				System.out.println(sdf.toPattern());
				
				return sdf.parse(dateStr);
			} catch (ParseException e) {
			}catch (NullPointerException e) {
				return null;
			} 
		}
		return null;
	}
	
	public static String getFormatString(Date date){
		  try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String strdate = sf.format(date);
			return strdate;
		} catch (Exception e) {
			log.error(""+e.getMessage());
			return "";
		}
	}
	
	public static String getFormatString(Date date , String format){
		  try {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			String strdate = sf.format(date);
			return strdate;
		} catch (Exception e) {
			log.error(""+e.getMessage());
			return "";
		}
	}
	
	
	
	public static List<SimpleDateFormat> sdfs = Arrays.asList(

			new SimpleDateFormat("yyyyMMddHHmmssS"), 
			new SimpleDateFormat("yyyyMMddHHmmss"), 
			new SimpleDateFormat("yyyyMMddHHmm"), 
			new SimpleDateFormat("yyyyMMddHH"), 
			new SimpleDateFormat("yyyyMMdd"),
			 
			new SimpleDateFormat("yyMMddHHmmssS"), 
			new SimpleDateFormat("yyMMddHHmmss"), 
			new SimpleDateFormat("yyMMddHHmm"), 
			new SimpleDateFormat("yyMMddHH"), 
			new SimpleDateFormat("yyMMdd"),
//////////////////////////////////////////////////////////////////
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"), 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), 
			new SimpleDateFormat("yyyy-MM-dd HH:mm"), 
			new SimpleDateFormat("yyyy-MM-dd HH"), 
			new SimpleDateFormat("yyyy-MM-dd"),
			 
			new SimpleDateFormat("yy-MM-dd HH:mm:ss.S"), 
			new SimpleDateFormat("yy-MM-dd HH:mm:ss"), 
			new SimpleDateFormat("yy-MM-dd HH:mm"), 
			new SimpleDateFormat("yy-MM-dd HH"), 
			new SimpleDateFormat("yy-MM-dd")
	
	);
	
	
	 
	
	
	public static boolean checkTimeDot(String timeDot, String timeCfg) {
		String[] timeDots = timeDot.trim().split(",");
		String[] timeCfgs = timeCfg.trim().split(";");
		
		int len = timeDots.length;
		for(int i = 0; i < len; i++){
			if("1".equals(timeDots[i])){
				String[] dateTimeTemp = timeCfgs[i].split("-");
				if(DateUtil.getYYYYMMDate().compareTo(dateTimeTemp[0]) >= 0 && DateUtil.getYYYYMMDate().compareTo(dateTimeTemp[1]) <= 0){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static String getYYYYMMDDHHMMSS(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "yyyyMMddhhmmss" ) ;
		  String strdate = sf.format ( now ) ;
		  return strdate;
	}
	
	
	public static String getYYYYMMDD(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "yyyyMMdd" ) ;
		  String strdate = sf.format ( now ) ;
		  return strdate;
	}
	
	
	public static String getMMDDHHMMSS(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "MMddhhmmss" ) ;
		  String strdate = sf.format ( now ) ;
		  return strdate;
	}	

	
	public static String getHHMMSS(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "HHmmss" ) ;
		  String strdate = sf.format ( now ) ;
		  return strdate;
	}
	
	
	public static String getLongDate(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" ) ;
		  String strdate = sf.format ( now ) ;
		  return strdate;
	}
	
	public static String getShortDate(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "HH:mm:ss" ) ;
		  String strdate = sf.format ( now ) ;
		  //System.out.println(strdate);
		  return strdate;
	}
	
	public static String getYYYYMMDate(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "HH:mm" ) ;
		  String strdate = sf.format ( now ) ;
		  return strdate;
	}
	
	
	public static String getyyyyMMddHHmmssSSS(){
		  Date now=new Date();
		  SimpleDateFormat sf = new SimpleDateFormat ( "yyyyMMddHHmmssSSS" ) ;
		  String strdate = sf.format ( now ) ;
		  return strdate;
	}
	
	
	public static String getWeekStr(){
		StringBuffer buf = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		for (int i = 1; i <= 7; i++) {
			if (i == dayOfWeek) {
				buf.append("1");
			}else{
				buf.append("_");
			}
		}
		return buf.toString();
	}
	/** 
	 * 获取给定时间所在周的第一天(Sunday)的日期和最后一天(Saturday)的日期 
	 * @param calendar 
	 * @return Date数组，[0]为第一天的日期，[1]最后一天的日期 */ 
	public Date[] getWeekStartAndEndDate(Calendar calendar) { 
		Date[] dates = new Date[2]; 
		Date firstDateOfWeek, lastDateOfWeek; // 得到当天是这周的第几天 
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 减去dayOfWeek,得到第一天的日期，因为Calendar用0－6代表一周七天，所以要减一 
		calendar.add(Calendar.DAY_OF_WEEK, -(dayOfWeek - 1)); firstDateOfWeek = calendar.getTime(); // 每周7天，加6，得到最后一天的日子 
		calendar.add(Calendar.DAY_OF_WEEK, 6); 
		lastDateOfWeek = calendar.getTime(); 
		dates[0] = firstDateOfWeek; 
		dates[1] = lastDateOfWeek; 
		return dates; 
	} 
	/** 
	 *  获取给定时间所在月的第一天F的日期和最后一天的日期 * * 
	 * @param calendar * 
	 * @return Date数组，[0]为第一天的日期，[1]最后一天的日期 */ 
	public Date[] getMonthStartAndEndDate(Calendar calendar) { 
		Date[] dates = new Date[2]; 
		Date firstDateOfMonth, lastDateOfMonth; // 得到当天是这月的第几天 
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); // 减去dayOfMonth,得到第一天的日期，因为Calendar用0代表每月的第一天，所以要减一 
		calendar.add(Calendar.DAY_OF_MONTH, -(dayOfMonth - 1)); 
		firstDateOfMonth = calendar.getTime(); // calendar.getActualMaximum(Calendar.DAY_OF_MONTH)得到这个月有几天 
		calendar.add(Calendar.DAY_OF_MONTH, calendar .getActualMaximum(Calendar.DAY_OF_MONTH) - 1); 
		lastDateOfMonth = calendar.getTime(); 
		dates[0] = firstDateOfMonth; 
		dates[1] = lastDateOfMonth; 
		return dates; 
	}
	public static void main(String[] args) { 
		DateUtil dateUtil = new DateUtil(); 
		Calendar now = Calendar.getInstance(); 
		Date[] weekDates = dateUtil.getWeekStartAndEndDate(now); 
		Date[] monthDates = dateUtil.getMonthStartAndEndDate(now); 
		System.out.println("firstDateOfWeek: " + weekDates[0] + " ,lastDateOfWeek: " + weekDates[1]); 
		System.out.println("firstDateOfMonth: " + monthDates[0] + " ,lastDateOfMonth: " + monthDates[1]); 
	}

	public static Date formatDate(String dateString){
		Date date = null;
		if(dateString.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}:\\d{1,3}")){
			//yyyy-MM-dd HH:mm:ss:SSS
			try {
				SimpleDateFormat dtSF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
				date = dtSF.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else if(dateString.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}")){
			//yyyy-MM-dd HH:mm:ss
			try {
				SimpleDateFormat dtSF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = dtSF.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else if(dateString.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}")){
			//yyyy-MM-dd HH:mm
			try {
				SimpleDateFormat dtSF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				date = dtSF.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else if(dateString.matches("\\d{4}-\\d{1,2}-\\d{1,2}")){
			//yyyy-MM-dd
			try {
				SimpleDateFormat dtSF = new SimpleDateFormat("yyyy-MM-dd");
				date = dtSF.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			long dtLongTime = Long.parseLong(dateString);
			date = new Date(dtLongTime);
		}
		return date;
	}
}
