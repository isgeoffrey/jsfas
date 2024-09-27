package jsfas.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;

import jsfas.common.constants.AppConstants;

/**
 * @version 1.2
 */
public class PasswordProtector {
	private Environment env;
	private String passwordBase64;
	private String saltBase64;

	/** logger */
	private final Logger log = LoggerFactory.getLogger(PasswordProtector.class);
	
	public PasswordProtector(final Environment environment) {
		this.env = environment;
		
		String fileFolder = env.getProperty("password.protector.file", "");		
	    File file = new File(fileFolder); //for ex foo.txt
	    
	    if ((!file.exists() || !file.isFile()) && env.getActiveProfiles().length > 0) {
	    	String catalinaBase = System.getProperty("catalina.base");
	    	
	    	switch (env.getActiveProfiles()[0]) {
	    		case "dev":
	    			fileFolder = catalinaBase + "/key/password_protector_key_DEV.txt";
	    			break;
	    		case "test":
	    			fileFolder = catalinaBase + "/key/password_protector_key_SIT.txt";
	    			break;
	    		case "uat":
	    			fileFolder = catalinaBase + "/key/password_protector_key_UAT.txt";
	    			break;
	    		case "production":
	    			fileFolder = catalinaBase + "/key/password_protector_key_PROD.txt";
	    			break;
	    		default:
	    			fileFolder = catalinaBase + "/key/password_protector_key_DEV.txt";
	    	}
	    	
	    	file = new File(fileFolder);
	    }
	    
	    try {
	    	FileInputStream propFile = new FileInputStream(file);
	        Properties prop = new Properties();
	        
	        prop.load(propFile);
	        
	        passwordBase64 = prop.getProperty("password.protector.password");
	        saltBase64 = prop.getProperty("password.protector.salt");
	    } catch (IOException e) {
	    	log.error("encrypt password key salt file read ERROR...use DEFAULT key and salt instead...");
	    	log.error("read file target: {}", file.getAbsolutePath());
	    	passwordBase64 = AppConstants.PASSWORD_PROTECTOR_PASSWORD;
		    saltBase64 = AppConstants.PASSWORD_PROTECTOR_SALT;
	    } 
	}
	
	/**
	 * Constructor by passing key and salt
	 * @param key
	 * @param salt
	 */
	public PasswordProtector(String key, String salt) {
		passwordBase64 = key;
	    saltBase64 = salt;
	}
	
    /**
     * Convert a byte to HEX representation.
     *
     * @param b  byte to be converted.
     * @return HEX representation of the byte.
     */
    public String toHexString(byte b) {
        String hex1 = Integer.toHexString((b>>>4)&15);
        String hex2 = Integer.toHexString(b&15);
        return hex1+hex2;
    }

    /**
     * Convert a byte array to HEX representation.
     *
     * @param b  byte array to be converted.
     * @return HEX representation of the byte;
     *         null if <code>b</code> is null or
     *         length of b is zero;
     */
    public String toHexString(byte[] b) {
        if (b==null || b.length==0) return null;

        String hex = "";

        for (int i=0; i<b.length; i++)
            hex += toHexString(b[i]);

        return hex;
    }

    /**
     * Check if a string is a valid HEX representation of a byte. A valid HEX
     * representation of a byte must follow all the conditions below:<br>
     * - Each character must be either a digit (from 0 to 9) or an alphabet
     *   (from a to f, in small or capital letter).<br>
     * - The length of the string must be either 1 or 2 character(s) long.<br>
     *
     * @param hex       HEX representation.
     * @return byte value of the HEX representation.
     * @throws java.lang.IllegalArgumentException
     */
    public boolean isValidHexStrOfAByte(String hex) {
        String validChars = "0123456789abcdef";

        if (hex==null || hex.trim().length()==0) return false;
        if (hex.length() > 2) return false;
        for (int i=0; i<hex.length(); i++) {
            if (validChars.indexOf(Character.toLowerCase(hex.charAt(i))) == -1)
                return false;
        }
        return true;
    }

    /**
     * Convert a HEX representation of a byte to byte.
     *
     * @param hex       HEX representation.
     * @return byte value of the HEX representation.
     * @throws java.lang.IllegalArgumentException - if the <code>hex</code> is
     *                                              not a valid representation
     *                                              of a byte.
     * @see #isValidHexStrOfAByte(String)
     */
    public byte hexToByte(String hex)
    throws java.lang.IllegalArgumentException {
        if (!isValidHexStrOfAByte(hex))
            throw new java.lang.IllegalArgumentException("HEX is not a valid" +
                                                         " representation of" +
                                                         " a byte.");
        hex = hex.toLowerCase();
        String hexDigits = "0123456789abcdef";
        byte val = (byte)0;

        if (hex.length() == 2)
            val = (byte)(hexDigits.indexOf(hex.charAt(0)) * 16);
        val += (byte)hexDigits.indexOf(hex.charAt(hex.length()-1));

        return val;
    }

    /**
     * To pad preceding character(s) to a string until the length of the string
     * reaches the width specified. If the string length already exceeds the
     * width specified, the original string would be returned.
     *
     * @param s         the string to be padded.
     * @param width     minimum width of the string.
     * @param padChar  the character to pad.
     * @return the string with specified character padded if necessary.
     * @see #leftPad(String, int)
     */
    public String leftPad(String s, int width, char padChar) {
        if (s==null) s = "";

        StringBuffer sb = new StringBuffer(s);
        int len = sb.length();

        for (int i=0; i<width-len; i++)
            sb = sb.insert(0, padChar);

        return sb.toString();
    }
    
    /**
     * To pad trailing character(s) to a string until the length of the string
     * reaches the width specified. If the string length already exceeds the
     * width specified, the original string would be returned.
     *
     * @param s         the string to be padded.
     * @param width     minimum width of the string.
     * @param padChar  the character to pad.
     * @return the string with specified character padded if necessary.
     * @see #rightPad(String, int)
     */
    public String rightPad(String s, int width, char padChar) {
        if (s==null) s = "";

        StringBuffer sb = new StringBuffer(s);
        int len = sb.length();

        for (int i=0; i<width-len; i++)
            sb = sb.append(padChar);

        return sb.toString();
    }

    /**
     * Convert a HEX representation of a byte array to byte array.
     *
     * @param hex       HEX representation.
     * @return byte value of the HEX representation.
     * @throws java.lang.IllegalArgumentException - if the <code>hex</code> is
     *                                              not a valid representation
     *                                              of a byte array.
     */
    public byte[] hexToByteArray(String hex)
    throws java.lang.IllegalArgumentException {
        if (hex==null || hex.trim().length()==0 || hex.length()%2!=0)
            throw new java.lang.IllegalArgumentException("HEX is not a valid" +
                                                         " representation of" +
                                                         " a byte array.");
        byte[] b = new byte[hex.length()/2];
        String eachHex = null;

        for (int i=0; i<hex.length(); i+=2) {
            eachHex = hex.substring(i, i+2);
            if (!isValidHexStrOfAByte(eachHex))
                throw new java.lang.IllegalArgumentException("HEX is not a"+
                                                             " valid" +
                                                             " representation" +
                                                             " of a byte" +
                                                             " array.");
            b[i/2] = hexToByte(eachHex);
        }

        return b;
    }
    
    /**
     * Convert the characters in a string into HEX string format. Each character
     * will be converted to 2-character HEX value.
     *
     * @param s     the string to be converted
     * @return the HEX string; null if the string <code>s</code> is null.
     * @see #toHexString(char)
     */
    public String toHexString(String s) {
        if (s == null) return null;

        StringBuffer sb = new StringBuffer("");

        for (int i=0; i<s.length(); i++) {
            sb.append(leftPad(
                        Integer.toHexString((int)s.charAt(i)), 2, '0'));
        }

        return sb.toString();
    }
    
    /**
     * Convert a hexadecimal value to decimal value. Negative value is not
     * supported.
     *
     * @param hex       hexadecimal value.
     * @return the decimal value; -1 if <code>hex</code> is invalid.
     */
    public int hexToDec(String hex) {
        if (hex==null || hex.length()==0) return -1;
        return hexToDec(hex.toCharArray());
    }

    /**
     * Convert a hexadecimal value to decimal value. Negative value is not
     * supported.
     *
     * @param hex       hexadecimal value.
     * @return the decimal value; -1 if the <code>hex</code> is invalid.
     */
    public int hexToDec(char[] hex) {
        if (hex==null || hex.length==0) return -1;

        String allowChars = "0123456789ABCDEF";
        int dec = 0;

        for (int i=hex.length-1; i>=0; i--) {
            if (allowChars.indexOf(Character.toUpperCase(hex[i])) == -1)
                return -1;
            dec += (int)Math.pow((double)16,(double)(hex.length-1-i)) *
                    hexDigitToDec(hex[i]);
        }

        return dec;
    }

    /* =========================================================================
     * Private methods
     */
    /**
     * Convert a character which represent hexadecimal value to the 
     * corresponding decimal value.
     * 
     * @param hexdigit      hexadecimal value.
     * @return the decimal value; -1 if the <code>hexdigit</code> is invalid.
     */
    private int hexDigitToDec(char hexdigit) {
        String allowChars = "0123456789ABCDEF";

        return allowChars.indexOf(Character.toUpperCase(hexdigit));
    }

    /**
     * Convert string in HEX format to normal string. This is the reverse
     * of <code>toHexString()</code>.
     *
     * @param hexstr    the string in HEX format to be converted.
     * @return the converted string; null if the string
     *         <code>hexstr</code> is null, or the HEX string is not valid.
     */
    public String hexStringToString(String hexstr) {
        if (hexstr == null) return null;

        String eachChar=null, eachChar2=null;
        String allowChars = "0123456789abcdef";
        int ascVal = 0;

        StringBuffer sb = new StringBuffer("");

        if (hexstr.length()%2 != 0) return null;
        for (int i=0; i<hexstr.length()-1; i+=2) {
            eachChar = hexstr.substring(i, i+1);
            eachChar2 = hexstr.substring(i+1, i+2);
            if (allowChars.indexOf(eachChar.toLowerCase())==-1 ||
            allowChars.indexOf(eachChar2.toLowerCase())==-1)
                return null;
            ascVal = hexToDec(eachChar+eachChar2);
            if (ascVal == -1) return null;
            sb = sb.append((char)ascVal);
        }

        return sb.toString();
    }
    
    /**
     * @param dataHex
     * @return String
     * @throws Exception
     */
    public String encrypt(String data) {
    	String passwordHex = toHexString(Base64.getDecoder().decode(passwordBase64));
    	String saltHex = toHexString(Base64.getDecoder().decode(saltBase64));
    	
    	String password = hexStringToString(passwordHex);
    	
    	BytesEncryptor be = Encryptors.stronger(password, saltHex);
    	
    	return Base64.getEncoder().encodeToString(be.encrypt(hexToByteArray(toHexString(data))));
    }
    
    /**
     * @param dataHex
     * @return String
     * @throws Exception
     */
    public String decrypt(String dataBase64) {
    	String passwordHex = toHexString(Base64.getDecoder().decode(passwordBase64));
    	String saltHex = toHexString(Base64.getDecoder().decode(saltBase64));
    	
    	String password = hexStringToString(passwordHex);
    	
    	BytesEncryptor be = Encryptors.stronger(password, saltHex);
    	
    	return hexStringToString(toHexString(be.decrypt(Base64.getDecoder().decode(dataBase64))));
    }
    
    /*
    public static void main(String[] arg) {
    	try {
    		System.out.println(PasswordProtector.encrypt("dwwh1pass"));
    		System.out.println(PasswordProtector.decrypt("KEBovxzDhEWQi/raeH4dXeJkj9rM5jSCXqgyo6iP+1TT7jpOsZOIp6A="));
    	} catch(Exception e) {
    		System.out.println(e);
    	}
     }
     */
}
