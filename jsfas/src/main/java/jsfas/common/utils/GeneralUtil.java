package jsfas.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jsfas.common.Arith;
import jsfas.common.json.CommonJson;
import jsfas.common.object.Coa;
import jsfas.common.object.CoaObject;
import jsfas.web.filter.XSSRequestWrapper;

/**
 * @author iseric
 * @since 09/05/2016
 */

public class GeneralUtil {

	/*
	 * Util List :
	 * isBlankString
	 * isZeroInteger
	 * isZeroDouble
	 * isBlankTimestamp
	 * isAlphabetString
	 * isInteger
	 * isDouble
	 * isAlphaNumericString
	 * isDate
	 * isTime
	 * checkPattern
	 * roundUp2Decimal
	 * dateRangeOk
	 * checkLength
	 * initNullString
	 * initNullInteger
	 * initNullTimestamp
	 * initBlankString
	 * convertTimestampToString
	 * getCurrentTimestamp
	 * replaceSpace
	 * genModCtrlTxt
	 * stringToCommonJsonObject
	 * jsonObjectToCommonJson
	 * commonJsonToJsonObject
	 * jsonArrayToCommonJson
	 */
	
	private static final Logger log = LoggerFactory.getLogger(GeneralUtil.class);
	
	public static final Timestamp NULLTIMESTAMP = getTimestamp(1858, 11, 17);
	public static final Timestamp NULLTIMESTAMP2 = getTimestamp(1858, 11, 16);
	public static final Timestamp NULLTIMESTAMP3 = getTimestamp(1958, 11, 17);
	public static final Timestamp NULLTIMESTAMP4 = getTimestamp(1958, 11, 18);
	public static final Timestamp INFINTYTIMESTAMP = getTimestamp(2099, 12, 30);
	//public static final double NULLDOUBLEVALUE=Double.MIN_VALUE;
	public static final double NULLDOUBLEVALUE = 0;
	public static final int NULLINT = 0;
	public static final Double NULLDOUBLE = new Double(0);
	public static final BigDecimal NULLBIGDECIMAL = new BigDecimal(0);
	
    public static final String DFORMATTERSTR = "dd/MM/yyyy";
    public static final String DFORMATTERSTR2 = "dd-MM-yyyy";
    public static final String DFORMATTERSTR3 = "dd/MM/yyyy HH:mm";
    public static final String DFORMATTERSTR4 = "dd-MM-yyyy HH:mm";
    public static final String DFORMATTERSTR5 = "dd-MMM-yyyy HH:mm";
    public static final String DFORMATTERSTR6 = "dd-MMMMM-yyyy HH:mm";
    public static final String DFORMATTERSTR7 = "yyyyMMddHHmmssSSS";
    public static final String DFORMATTERSTR8 = "dd/MM/yyyy HH:mm:ss.SSS";
    public static final String DFORMATTERSTR9 = "dd/MM/yyyy HH:mm:ss";
    public static final String DFORMATTERSTR10 = "MM/dd/yyyy";
    public static final String DFORMATTERSTR11 = "yyyy-MM";
	public static final String DFORMATTERSTR12 = "yyyy-MM-dd";
	public static final String DFORMATTERSTR13 = "yyyy-MM-dd HH:mm:ss.S";
    
    static private SimpleDateFormat dFormatter = new SimpleDateFormat(DFORMATTERSTR);
    static private SimpleDateFormat dFormatter2 = new SimpleDateFormat(DFORMATTERSTR2);
    static private SimpleDateFormat dFormatter3 = new SimpleDateFormat(DFORMATTERSTR3);
    static private SimpleDateFormat dFormatter4 = new SimpleDateFormat(DFORMATTERSTR4);
    static private SimpleDateFormat dFormatter5 = new SimpleDateFormat(DFORMATTERSTR5);
    static private SimpleDateFormat dFormatter6 = new SimpleDateFormat(DFORMATTERSTR6);
    static private SimpleDateFormat dFormatter7 = new SimpleDateFormat(DFORMATTERSTR7);
    static private SimpleDateFormat dFormatter8 = new SimpleDateFormat(DFORMATTERSTR8);
    static private SimpleDateFormat dFormatter9 = new SimpleDateFormat(DFORMATTERSTR9);
    static private SimpleDateFormat dFormatter10 = new SimpleDateFormat(DFORMATTERSTR10);
    static private  SimpleDateFormat dFormatter11 = new SimpleDateFormat(DFORMATTERSTR11);
	static private  SimpleDateFormat dFormatter12 = new SimpleDateFormat(DFORMATTERSTR12);
	static private  SimpleDateFormat dFormatter13 = new SimpleDateFormat(DFORMATTERSTR13);
    
    private static String dayShortDesc[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static String dayLongDesc[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    
    public static Timestamp convertStringToTimestamp(String strDate) {
    	try {
        	DateFormat formatter;
        	formatter = new SimpleDateFormat("dd/MM/yyyy");
        	// you can change format of date
        	Date date = formatter.parse(strDate);
        	Timestamp timeStampDate = new Timestamp(date.getTime());

        	return timeStampDate;
        }
        catch(ParseException e) {
        	return null;
        }
    }
    
    public static Timestamp convertStringToTimestamp(String strDate, String inputFormat) {
    	try {
    		
    		Date date = null;
    		
    		if(inputFormat.equalsIgnoreCase("D")) {
    			date = dFormatter.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("D2")) {
    			date = dFormatter2.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("N")) {
    			date = dFormatter3.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("N2")) {
    			date = dFormatter4.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("S")) {
    			date = dFormatter5.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("L")) {
    			date = dFormatter6.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("T")) {
    			date = dFormatter7.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("N4")) {
    			date = dFormatter8.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("N3")) {
    			date = dFormatter9.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("N5")) {
    			date = dFormatter10.parse(strDate);
    		}
    		else if(inputFormat.equalsIgnoreCase("DB")) {
    			date = dFormatter13.parse(strDate);
    		}
    		
        	Timestamp timeStampDate = new Timestamp(date.getTime());
        	return timeStampDate;
        }
        catch(ParseException e) {
        	return null;
        }
    }
	
	public static String formatDate(Timestamp t) {
        String date = formatDate2(t);
        
        return date.isEmpty()? date: date.substring(0, 14);
    }
    
    public static String formatDate2(Timestamp t) {
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH); //eg. November 01,2005
        if ((t == null) || t.equals(NULLTIMESTAMP)) {
            return "";
        }
        else {
            return f.format(t).substring(0, 16);
        }
    }
	
	public static Timestamp addDate(Timestamp t, int days) {
        if(t == null) {
        	return null;
        }
        
        GregorianCalendar c = new GregorianCalendar();
        c.setTime((Date)t);
        c.add(Calendar.DATE, days);
        
        return new Timestamp(c.getTime().getTime());
    }
	
	public static Timestamp getTimestamp(int y, int m, int d) {
        return getTimestamp(y, m, d, 0, 0, 0);
	}
	
	public static  Timestamp getTimestamp(int y, int m, int d, int h, int min, int s) {
    	String[] ids = TimeZone.getAvailableIDs(8 * 60 * 60 * 1000);
		SimpleTimeZone hkt = new SimpleTimeZone(8 * 60 * 60 * 1000, ids[0]);
    	Calendar c = Calendar.getInstance(hkt);
    	int month = Calendar.JANUARY;
    	if(m == 1) month = Calendar.JANUARY;
    	if(m == 2) month = Calendar.FEBRUARY;
    	if(m == 3) month = Calendar.MARCH;
    	if(m == 4) month = Calendar.APRIL;
    	if(m == 5) month = Calendar.MAY;
    	if(m == 6) month = Calendar.JUNE;
    	if(m == 7) month = Calendar.JULY;
    	if(m == 8) month = Calendar.AUGUST;
    	if(m == 9) month = Calendar.SEPTEMBER;
    	if(m == 10) month = Calendar.OCTOBER;
    	if(m == 11) month = Calendar.NOVEMBER;
    	if(m == 12) month = Calendar.DECEMBER;
    	
    	c.set(Calendar.MILLISECOND,0);
    	c.set(y, month, d, h, min, s);
    	
        //return Timestamp.valueOf(y+"-"+m+"-"+d+" "+h+":"+min+":"+s+".000000000");
    	return new Timestamp(c.getTimeInMillis());
    }
	
	
	public static String getStringByDate(Timestamp t) { //dd/MM/yyyy
        
        if((t == null) || t.equals(NULLTIMESTAMP)) {
        	return "";
        }
        else {
        	return getDFormatter().format(t);
        }
        
    }
	
	public static String getStringByDate2(Timestamp t) { //dd-MM-yyyy
        
        if((t == null) || t.equals(NULLTIMESTAMP)) {
        	return "";
        }
        else {
        	return getDFormatter2().format(t);
        }
        
    }
	
	public static String getStringByDate3(Timestamp t) { //dd/MM/yyyy HH:mm
        
        if((t == null) || t.equals(NULLTIMESTAMP)) {
        	return "";
        }
        else {
        	return getDFormatter3().format(t);
        }
        
    }

	public static String getStringByDate4(Timestamp t) { //dd-MM-yyyy HH:mm
        
        if((t==null) || t.equals(NULLTIMESTAMP)) {
        	return "";
        }
        else {
        	return getDFormatter4().format(t);
        }
        
    }

	public static String getStringByDate5(Timestamp t) { //dd/MMM/yyyy HH:mm
        
        if((t==null) || t.equals(NULLTIMESTAMP)) {
        	return "";
        }
        else {
        	return getDFormatter5().format(t);
        }
        
    }
	
	public static String getStringByDate6(Timestamp t) { //dd/MMMMM/yyyy HH:mm
        
        if((t==null) || t.equals(NULLTIMESTAMP)) {
        	return "";
        }
        else {
        	return getDFormatter6().format(t);
        }
        
    }
	
	public static String getStringByDate7(Timestamp t) { //yyyyMMddHHmmssSSS
        
        if((t==null) || t.equals(NULLTIMESTAMP)) {
        	return "";
        }
        else {
        	return getDFormatter7().format(t);
        }
        
    }
	
    public static String getStringByDate8(Timestamp t) { //dd/MM/yyyy HH:mm:ss.SSS
        
        if((t==null) || t.equals(NULLTIMESTAMP)) {
            return "";
        }
        else {
            return getDFormatter8().format(t);
        }
        
    }
    
    public static String getStringByDate9(Timestamp t) { //dd/MM/yyyy HH:mm:ss
        
        if((t==null) || t.equals(NULLTIMESTAMP)) {
            return "";
        }
        else {
            return getDFormatter9().format(t);
        }
        
    }
	
    public static String getStringByDate10(Timestamp t) { 
    	 if((t==null) || t.equals(NULLTIMESTAMP)) {
             return "";
         }
         else {
             return getDFormatter10().format(t);
         }
    }
    
    public static String getStringByDate11(Timestamp t) { //YYYY-MM
   	 if((t==null) || t.equals(NULLTIMESTAMP)) {
            return "";
        }
        else {
            return getDFormatter11().format(t);
        }
   }
    
	public static SimpleDateFormat getDFormatter() {
        return dFormatter;
    }
	
	public static SimpleDateFormat getDFormatter2() {
        return dFormatter2;
    }
	
	public static SimpleDateFormat getDFormatter3() {
        return dFormatter3;
    }

	public static SimpleDateFormat getDFormatter4() {
        return dFormatter4;
    }

	public static SimpleDateFormat getDFormatter5() {
        return dFormatter5;
    }
	
	public static SimpleDateFormat getDFormatter6() {
        return dFormatter6;
    }
	
	public static SimpleDateFormat getDFormatter7() {
        return dFormatter7;
    }
	
	public static SimpleDateFormat getDFormatter8() {
        return dFormatter8;
    }
	
	public static SimpleDateFormat getDFormatter9() {
        return dFormatter9;
    }
	
	public static SimpleDateFormat getDFormatter10() {
		return dFormatter10;
	}
	
	public static SimpleDateFormat getDFormatter11() {
		return dFormatter11;
	}

	public static SimpleDateFormat getDFormatter12() {
		return dFormatter12;
	}
	
	public static boolean isSameDay(Timestamp ts1, Timestamp ts2) {
		if((ts1 == null) && (ts2 == null)) {
			return true;
		}
		if((ts1 == null) || (ts2 == null)) {
			return false;
		}
		return ((getTimestampField(ts1, Calendar.DAY_OF_MONTH) == getTimestampField(ts2, Calendar.DAY_OF_MONTH))&&
				(getTimestampField(ts1, Calendar.MONTH) == getTimestampField(ts2, Calendar.MONTH))&&
				(getTimestampField(ts1, Calendar.YEAR) == getTimestampField(ts2, Calendar.YEAR))
		);
		
	}
	
	public static int getTimestampField(Timestamp ts, int calendarField) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String ds = df.format(ts);
        int r = 0;
        if (calendarField == Calendar.YEAR) {
            r = Integer.parseInt(ds.substring(0, 4), 10);
        }
        else if(calendarField == Calendar.MONTH) {
            r = Integer.parseInt(ds.substring(5, 7), 10);
        }
        else if(calendarField == Calendar.DAY_OF_MONTH) {
            r = Integer.parseInt(ds.substring(8, 10), 10);
        }
        else if(calendarField == Calendar.HOUR_OF_DAY) {
            r = Integer.parseInt(ds.substring(11, 13), 10);
        }
        else if(calendarField == Calendar.MINUTE) {
            r = Integer.parseInt(ds.substring(14, 16), 10);
        }
        else if(calendarField == Calendar.SECOND) {
            r = Integer.parseInt(ds.substring(17, 19), 10);
        }
        return r;
    }
	
	public static Timestamp truncateDate(Timestamp t) {
    	if(t == null) {
    		return t;
    	}
        final int y = getTimestampField(t, Calendar.YEAR);
        final int m = getTimestampField(t, Calendar.MONTH);
        final int d = getTimestampField(t, Calendar.DAY_OF_MONTH);
        return getTimestamp(y, m, d);
	}
	
	public static int dayDiff(Timestamp startDate, Timestamp endDate) {
    	if(startDate == null || endDate == null || startDate.after(endDate)) {
    		return 0;
    	}
    	
    	startDate = truncateDate(startDate);
    	endDate = truncateDate(endDate);
    	
    	long diff = (endDate.getTime() - startDate.getTime())/86400000;
    	
        return (int) diff;
	}
	
	public static String checkNull(String s) {
        return ((s == null)? "" : s.trim());
    }
	
    public static int checkInt(Integer i) {
        return ((i == null)? NULLINT : i.intValue());
    }
	
    @SuppressWarnings("rawtypes")
    public static Integer getIntCol(String col, int i, List v) {
        HashMap h = (HashMap)v.get(i);
        
        return checkInt((Integer)h.get(col));
    }
    
    @SuppressWarnings("rawtypes")
	public static String getStringCol(String col, int i, List v) {
        HashMap h = (HashMap)v.get(i);
        return initBlankString((String)h.get(col));
    }
	
    @SuppressWarnings("rawtypes")
	public static Timestamp getTimestampCol(String col, int i, List v) {
        HashMap h = (HashMap)v.get(i);
        return (Timestamp)h.get(col);
    }
	
	/*
	public static double checkBigDecimal(BigDecimal i) {
        return ((i == null)? NULLDOUBLEVALUE : i.doubleValue());
    }
	
	public static double getBigDecimalCol(String col, int i, List v) {
        HashMap h = (HashMap)v.get(i);

        return checkBigDecimal((BigDecimal)h.get(col));
    }
    */
	
	public static BigDecimal checkBigDecimal(BigDecimal bd) {
        return ((bd == null) ? NULLBIGDECIMAL : bd);
    }
	
	@SuppressWarnings("rawtypes")
	public static BigDecimal getBigDecimalCol(String col, int i, List v) {
        HashMap h = (HashMap)v.get(i);

        return checkBigDecimal((BigDecimal) h.get(col));
    }

	public static Double checkDouble(Double d) {
        return ((d == null)? NULLDOUBLE : d);
    }

	@SuppressWarnings("rawtypes")
	public static Double getDoubleCol(String col, int i, List v){
        HashMap h = (HashMap)v.get(i);
        return checkDouble((Double)h.get(col));
    }

	public static String getNamebyUSTLD2(String url)  {
		String name = "";
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				//log.info("inputLine = " + inputLine);
				if(inputLine.contains("CN=")){
					String result = inputLine.replace("CN=", "");
					name = result.replace("+", " ");
				}
			}
			in.close();
		}
		catch (Exception e) {
			log.error("getNamebyUSTLD2 Exception = " + e);
		}
		return name;
    }
	
	public static String getUserClassbyUSTLD2(String url) {
		String name = "";
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				//log.info("inputLine = " + inputLine);
				if(inputLine.contains("UserClass=")){
					String result = inputLine.replace("UserClass=", "");
					name = result.replace("+", " ");
				}
			}
			in.close();
		}
		catch(Exception e) {
			log.error("getUserClassbyUSTLD2 Exception = " + e);
		}
		return name;
    }
	
	public static Document loadXMLFromLink(String url) {
		try{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			StringBuffer response = new StringBuffer();
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine + "\n");
			}
			in.close();
			
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(response.toString()));
		    return builder.parse(is);
			
		}
		catch(Exception e) {
			log.error("loadXMLFromLink Exception = " + e);
		}
		return null;
	}
	
	public static Document loadXMLFromLinkWithLineBreak(String url) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			StringBuffer response = new StringBuffer();
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				if (inputLine.length() > 0) {
					response.append(inputLine + "\n");
				}
			}
			in.close();
			
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(response.toString()));
		    return builder.parse(is);
		}
		catch(Exception e) {
			log.error("loadXMLFromLink Exception = " + e);
		}
		return null;
	}
	
	public static String formatDoubleNoCommas(double i, int decimalPoints) {
        return formatDoubleNoCommas(i, decimalPoints, true);
    }    
        
    public static String formatDouble(double i, int decimalPoints) {
    	return formatDouble(i, decimalPoints, true);
        //DecimalFormat df = new DecimalFormat("###,##0.##########");
        //return df.format(i);
    }
    
    //round up to d.p. without thousand separators,can select pad zero or not
    public static String formatDoubleNoCommas(double i, int decimalPoints, boolean padZero) {
        String z = "";
        if(padZero && (decimalPoints > 0)) {
            z = "." + extendZero("", decimalPoints);
        }
        else {
            z = "." + leftPad("", decimalPoints, "#");
        }
        DecimalFormat df = new DecimalFormat("#####0" + z);
        
        final double d = Arith.round(i, decimalPoints);
        
        String s = df.format(d);
        if(s.startsWith("-") && (d == 0)) {
            return s.substring(1, s.length());
        }
        else {
            return s;
        }    
    }
    
    public static String formatDouble(double i, int decimalPoints, boolean padZero) {
        String z = "";
        if(padZero && (decimalPoints > 0)) {
            z = "." + extendZero("", decimalPoints);
        }
        else {
            z = "." + leftPad("", decimalPoints, "#");
        }
        DecimalFormat df = new DecimalFormat("###,##0" + z);
        
        final double d = Arith.round(i, decimalPoints);
        
        String s = df.format(d);
        if(s.startsWith("-") && (d == 0)) {
            return s.substring(1, s.length());
        }
        else {
            return s;
        }    
    }
    
    public static String leftPad(String s, int len, String p){
        StringBuffer buffer = new StringBuffer(s);
        final int length = getTextLength(s);
        for(int i=0; i<len-length; i++) {
            buffer.insert(0, p);
        }
        return buffer.toString();
    }         
    public static String rightPad(String s, int len, String p) {
        StringBuffer buffer = new StringBuffer(s);
        final int length = getTextLength(s);
        for(int i=0; i<len-length; i++){
            buffer.append(p);
        }
        return buffer.toString();
    }  
    
    public static int getTextLength(String text) {
        final int text_length = text.length();
        char a=' ';
        int count = 0;

        for(int i=0; i<text_length; i++){
            a = text.charAt(i);
            if((int)a<256) {
                count += 1;
            }
            else {
                count += 2;
            }
        }
        
        return count;
    }
    
    public static String extendZero(String s, int no) {
        return leftPad(s,no,"0");
    }
    
    public static String extendZero(int s, int no) {
        return leftPad(String.valueOf(s), no, "0");
    } 
	
	public static int randomInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public static String getBodyByRequest(HttpServletRequest servletRequest) {
		StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            // read the payload into the StringBuilder
            InputStream inputStream = servletRequest.getInputStream();
            if(inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
            else {
                // make an empty string since there is no payload
                stringBuilder.append("");
            }
        }
        catch(IOException ex) {
            
        }
        finally {
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch(IOException iox) {
                    // ignore
                }
            }
        }
        return XSSRequestWrapper.stripXSS(stringBuilder.toString());   
	}
	
	/**
	 * @param inputString
	 * @return true if input_string is null or empty
	 */
	public static boolean isBlankString(String inputString) {
		return StringUtils.isEmpty(inputString);
	}
	
	/**
	 * @param inputInteger
	 * @return true if input_integer is null or 0
	 */
	public static boolean isZeroInteger(Integer inputInteger) {
		if(inputInteger == null || inputInteger == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param inputDouble
	 * @return true if input_double is null or 0
	 */
	public static boolean isZeroDouble(Integer inputDouble) {
		if(inputDouble == null || inputDouble == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param inputTimestamp
	 * @return true if input_timestamp is null or 17/11/1958
	 */
	public static boolean isBlankTimestamp(Timestamp inputTimestamp) {
		if(inputTimestamp == null || isSameDay(inputTimestamp, NULLTIMESTAMP)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param inputString
	 * @return true if input_string only include alphabet letters
	 */
	public static boolean isAlphabetString(String inputString) {
	    return StringUtils.isAlpha(inputString);
	}
	
	/**
	 * @param inputString
	 * @return true if input_string is an integer
	 */
	public static boolean isInteger(String inputString) {
		return StringUtils.isNumeric(inputString);
	}
	
	/**
	 * @param inputString
	 * @return true if input_string is an double
	 */
	public static boolean isDouble(String inputString) {
		try{
			Double.parseDouble(inputString);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * @param inputString
	 * @return true if input_string is Alphanumeric
	 */
	public static boolean isAlphaNumericString(String inputString) {
		return StringUtils.isAlphanumeric(inputString);
	}
	
	/**
	 * @param inputString
	 * @return true if input_string is Date
	 */
	public static boolean isDate(String inputString) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
	    
	    dateFormat.setLenient(false);
	    try{
	    	dateFormat.parse(inputString.trim());
	    }
	    catch(ParseException pe) {
	    	dateFormat2.setLenient(false);
		    try {
		    	dateFormat2.parse(inputString.trim());
		    }
		    catch(ParseException pe2) {
		    	return false;
		    }
	    }
	    
	    return true;
	}
	
	/**
	 * @param inputString
	 * @return true if input_string is Time
	 */
	public static boolean isTime(String inputString) {
		Pattern pattern;
		Matcher matcher;
 
		String time24HoursPattern = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
		pattern = Pattern.compile(time24HoursPattern);
		
		matcher = pattern.matcher(inputString);
		return matcher.matches();
	}
	
	/**
	 * @param inputString
	 * @return true if input_string is BigDecimal
	 */
	public static boolean isBigDecimal(String inputString) {
		try{
			new BigDecimal(inputString);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * @param inputString
	 * @param inputPattern
	 * @return true if pattern matched
	 */
	public static boolean checkPattern(String inputString, String inputPattern) {
		if(inputString.length() != inputPattern.length()) {
			return false;
		}
		
		for(int i = 0; i < inputString.length(); i++) {
			String inputStringChar = inputString.substring(i, i+1);
			String inputPatternChar = inputPattern.substring(i, i+1);
			
			if(inputPatternChar.equalsIgnoreCase("^")) {
				if(!isAlphabetString(inputStringChar)) {
					return false;
				}
			}
			else if(inputPatternChar.equalsIgnoreCase("*")) {
				if(!isInteger(inputStringChar)) {
					return false;
				}
			}
			else if(inputPatternChar.equalsIgnoreCase("+")) {
				if(!isAlphaNumericString(inputStringChar)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @param inputDouble
	 * @return round up result
	 */
	public static double roundUp2Decimal(double inputDouble) {
		return Arith.round(inputDouble, 2);
	}
	
	/**
	 * @param inputFromDate
	 * @param inputToDate
	 * @return
	 * inputFromDate & inputToDate must be dd/mm/yyyy format
	 */
	public static boolean dateRangeOk(String inputFromDate, String inputToDate) {
		if(!isDate(inputFromDate) || !isDate(inputToDate)){
			return false;
		}
		
		Timestamp fromDate = convertStringToTimestamp(inputFromDate);
		Timestamp toDate = convertStringToTimestamp(inputToDate);
		
		if(fromDate.after(toDate)){
			return false;
		}
		
		return true;		
	}
	
	/**
	 * @param inputString
	 * @param inputLength
	 * @return true if inputString length equal or smaller than input length 
	 */
	public static boolean checkLength(String inputString, Integer inputLength){
		if(inputString.length() > inputLength){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * @param inputString
	 * @return initialize null string to ""
	 */
	public static String initNullString(String inputString) {
		if (inputString == null) {
			return "";
		}	
		return inputString;
		
	}
	
	public static Double initNullDouble(Double inputDouble) {
		if (inputDouble == null) {
			return  0.0;
		}	
		return inputDouble;
	}
	
	/**
	 * @param inputInteger
	 * @return initialize null integer to ""
	 */
	public static Integer initNullInteger(Integer inputInteger) {
		if (inputInteger == null) {
			return 0;
		}	
		return inputInteger;
	}
	
	/**
	 * @param inputBigDecimal
	 * @return initialize null BigDecimal to 0
	 */
	public static BigDecimal initNullBigDecimal(BigDecimal inputBigDecimal) {
		if (inputBigDecimal == null) {
			return new BigDecimal(0);
		}	
		return inputBigDecimal;
	}
	
	/**
	 * @param inputString
	 * @return initialize null BigDecimal to 0
	 */
	public static BigDecimal initNullBigDecimal(String inputString) {
		
		if (isBigDecimal(inputString)) {
			return new BigDecimal(inputString);
		}	
		return new BigDecimal(0);
	}
	
	/**
	 * @param inputTimestamp
	 * @return initialize null timestamp to 17/11/1958
	 */
	public static Timestamp initNullTimestamp(Timestamp inputTimestamp) {
		if (inputTimestamp == null) {
			return NULLTIMESTAMP;
		}	
		return inputTimestamp;
	}
	
	public static Boolean isNullTimestamp (Timestamp inputTimestamp) {
		if (inputTimestamp == null || inputTimestamp == NULLTIMESTAMP || inputTimestamp == NULLTIMESTAMP2 || inputTimestamp == NULLTIMESTAMP3 || inputTimestamp.before(NULLTIMESTAMP4)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param inputString
	 * @return initialize null or empty string to " "
	 */
	public static String initBlankString(String inputString) {
		if (inputString == null || inputString.isEmpty()) {
			return " ";
		}else if(inputString == "" || inputString.length() == 0){
			return " ";
		}
		return XSSRequestWrapper.stripXSS(inputString);
	}
	
	/**
	 * @param inputTimestamp
	 * @return presented date format
	 * D:  dd/mm/yyyy         		e.g. 24/03/2014
	 * D2: dd-mm-yyyy         		e.g. 24-03-2014 
	 * N:  dd/mm/yyyy HH:MM         e.g. 24/03/2014 14:23
	 * N2: dd-mm-yyyy HH:MM         e.g. 24-03-2014 14:23
	 * N3: dd-mm-yyyy HH:MM:SS.SSS  e.g. 24-03-2014 14:23:01.123
	 * N4: dd-mm-yyyy HH:MM:SS      e.g. 24-03-2014 14:23:01
	 * S:  dd-MMM-yyyy HH:MM        e.g. 24-Mar-2014 14:23
	 * L:  dd-MMMMM-yyyy HH:MM      e.g. 24-March-2014 14:23
	 * T:  yyyyMMddHHmmssSSS      	e.g. 20140324142312515
	 */
	public static String convertTimestampToString(Timestamp inputTimestamp, String inputFormat) {
		if(inputFormat.equalsIgnoreCase("D")) {
			return getStringByDate(inputTimestamp);
		}
		else if(inputFormat.equalsIgnoreCase("D2")) {
			return getStringByDate2(inputTimestamp);
		}
		else if(inputFormat.equalsIgnoreCase("N")) {
			return getStringByDate3(inputTimestamp);
		}
		else if(inputFormat.equalsIgnoreCase("N2")) {
			return getStringByDate4(inputTimestamp);
		}
		else if(inputFormat.equalsIgnoreCase("S")) {
			return getStringByDate5(inputTimestamp);
		}
		else if(inputFormat.equalsIgnoreCase("L")) {
			return getStringByDate6(inputTimestamp);
		}
		else if(inputFormat.equalsIgnoreCase("T")) {
			return getStringByDate7(inputTimestamp);
		}
		return "";
	}
	
	/**
	 * @return current timestamp
	 */
	public static Timestamp getCurrentTimestamp(){
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * @param inputFormat date format code
	 * @return current date in date format
	 * D:  dd/mm/yyyy         		e.g. 24/03/2014
	 * D2: dd-mm-yyyy         		e.g. 24-03-2014 
	 * N:  dd/mm/yyyy HH:MM         e.g. 24/03/2014 14:23
	 * N2: dd-mm-yyyy HH:MM         e.g. 24-03-2014 14:23
	 * N3: dd-mm-yyyy HH:MM:SS.SSS  e.g. 24-03-2014 14:23:01.123
	 * N4: dd-mm-yyyy HH:MM:SS      e.g. 24-03-2014 14:23:01
	 * S:  dd-MMM-yyyy HH:MM        e.g. 24-Mar-2014 14:23
	 * L:  dd-MMMMM-yyyy HH:MM      e.g. 24-March-2014 14:23
	 * T:  yyyyMMddHHmmssSSS      	e.g. 20140324142312515
	 */
	public static String getCurrentDateStr(String inputFormat) {
		return convertTimestampToString(getCurrentTimestamp(), inputFormat);
	}
	
	/**
	 * @param inputString
	 * @param inputToken
	 * @return replace space with input token
	 */
	public static String replaceSpace(String inputString, String inputToken) {
		return inputString.replace(" ", inputToken);
	}
	
	/**
	 * @return modCtrlTxt
	 */
	public static String genModCtrlTxt(){
		return formatDate2(getCurrentTimestamp());
	}
	
	/**
	 * @param code
	 * @return week day short description
	 * 1: Sun
	 * 2: Mon
	 * 3: Tue
	 * 4: Wed
	 * 5: Thu
	 * 6: Fri
	 * 7: Sat
	 */
	public static String getShortWeekdayDesc(int code) {
		if (code > 0 && code <8) {
			return dayShortDesc[(code-1)];
		} else {
			return "";
		}
	}
	
	/**
	 * @param code
	 * @return week day short description
	 * 1: Sunday
	 * 2: Monday
	 * 3: Tuesday
	 * 4: Wednesday
	 * 5: Thursday
	 * 6: Friday
	 * 7: Saturday
	 */
	public static String getLongWeekdayDesc(int code) {
		if (code > 0 && code <8) {
			return dayLongDesc[(code-1)];
		} else {
			return "";
		}
	}
	
	public static String formatBigDecimal(BigDecimal bd) {
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(bd);
	}
	
	@Nullable
	public static CommonJson stringToCommonJsonObject(String str) {
		if (str == null) {
			return null;
		}
		CommonJson commonJson = null;
		try {
			JSONObject jsonObject = new JSONObject(str);
			commonJson = jsonObjectToCommonJson(jsonObject);
	   }catch (JSONException err){
			log.debug("Error", err.toString());
	   }

		return commonJson;
	}

	@Nullable
	public static CommonJson jsonObjectToCommonJson(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		CommonJson commonJson = new CommonJson();

		Iterator<String> jsonKeyIterator = jsonObject.keys();
		try {
			while(jsonKeyIterator.hasNext()) {
				String key = jsonKeyIterator.next();
				Object value = jsonObject.get(key);

				if (value instanceof JSONArray) {
					JSONArray jsonArray = (JSONArray) value;
					List<CommonJson> commonJsonArray = new ArrayList<CommonJson>();
					for (int i = 0; i < jsonArray.length();i++) {
						CommonJson tmp = jsonObjectToCommonJson(jsonArray.getJSONObject(i));
						commonJsonArray.add(tmp);
					}
					commonJson.set(key, commonJsonArray);
					continue;
				}
				if (value instanceof JSONObject) {
					commonJson.set(key, jsonObjectToCommonJson((JSONObject) value));
					continue;
				}
				commonJson.set(key, value);
			}

		} catch (JSONException e) {
			log.debug("jsonObjectToCommonJson error: " + e.getMessage());
		}
		return commonJson;
	}
	
	@Nullable
	public static CoaObject jsonObjectToCoaObject(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		CoaObject coa = new CoaObject(
				jsonObject.optString("analysis_cde"),
				jsonObject.optString("acct_cde"),
				jsonObject.optString("fund_cde"),
				jsonObject.optString("class_cde"),
				jsonObject.optString("dept_id"),
				jsonObject.optString("proj_id"),
				jsonObject.optString("bco_aprv_id"),
				jsonObject.optString("bco_aprv_name")
				);
		return coa;
	}
	
	@Nullable
	public static CoaObject jsonObjectToCoaObject(CoaObject coaObject) {
		return coaObject;
	}

	@Nullable
	public static JSONObject commonJsonToJsonObject(CommonJson inputJson) throws JsonProcessingException, JSONException {
		if (inputJson == null) {
			return null;
		}

		log.debug("input: " + inputJson);

		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObj = new JSONObject(mapper.writeValueAsString(inputJson));

		log.debug("output: " + jsonObj);

		return jsonObj;
	}

	@Nullable
	public static List<CommonJson> jsonArrayToCommonJson(JSONArray jsonArray) throws JSONException {
		if (jsonArray == null) {
			return null;
		}

		List<CommonJson> commonJsonArray = new ArrayList<CommonJson>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			commonJsonArray.add(jsonObjectToCommonJson(jsonObject));
		}

		return commonJsonArray;
	}
	
	public static String refineParam(String inputStr) {
		return StringUtils.isBlank(inputStr) ? "%%" : inputStr;
	}
	
	public static Timestamp initNullTimestampFromLong(Long l) {
		return l == 0 ? GeneralUtil.NULLTIMESTAMP : new Timestamp(l);
	}
	
	public static void sanitizeInputJson(JSONObject inputJson) throws Exception {
    	if (inputJson == null) {
    		return;
    	}
    	// remove uuid
    	inputJson.remove("uuid");
    	
    	// recursively remove uuid and remove empty JSONObject
    	Iterator<String> jsonKeyIterator = inputJson.keys();
    	
		while(jsonKeyIterator.hasNext()) {
			String key = jsonKeyIterator.next();
			Object value = inputJson.get(key);

			if (value instanceof JSONObject) {
				JSONObject nestedObject = (JSONObject) value;
                if (nestedObject.length() == 0) {
                	jsonKeyIterator.remove();
                } else {
                	sanitizeInputJson(nestedObject);
    				if (nestedObject.length() == 0) {
    					jsonKeyIterator.remove();
    				}
                }
				continue;
			}
			else if (value instanceof JSONArray) {
				JSONArray nestedArray = (JSONArray) value;

				for (int i = 0; i < nestedArray.length();i++) {
					Object item = nestedArray.get(i);
					if (item instanceof JSONObject) {
						JSONObject nestedObject = (JSONObject) item;
		                if (nestedObject.length() == 0) {
		                	nestedArray.remove(i);
		                	i--;
		                } else {
		                	sanitizeInputJson(nestedObject);
		    				if (nestedObject.length() == 0) {
			                	nestedArray.remove(i);
			                	i--;
		    				}
		                }
					}
				}
				continue;
			}
			// trim string value
			else if (value instanceof String) {
				inputJson.put(key, ((String) value).trim());
			}
		}
    }
	
	public static String createHyperLinks(String url) {
	    return "<a href=\"" + url +"\" target=\"_blank\">" + url + "</a>";
	}
	
    public static String createEmailLinks(String url) {
        return "<a href=\"mailto: " + url +"\" >" + url + "</a>";
    }
    public static Coa mapToCoa(Map<String, Object> map) {
    	return new Coa((String) map.get("acct_cde"), (String) map.get("analysis_cde"), (String) map.get("fund_cde")
    			, (String) map.get("dept_id"), (String) map.get("proj_id"), (String) map.get("class_cde")
    			, ((BigDecimal) map.get("pymt_sum")));
    }
}
