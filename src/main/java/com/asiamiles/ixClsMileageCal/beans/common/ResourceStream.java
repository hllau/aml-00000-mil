package com.asiamiles.ixClsMileageCal.beans.common;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p> ResourceStream is used to read the properties file for JSP script.
 * You can get the contents of properties file by getting the InputStream from 
 * ResourceStream. Properties file name should be in the format of "AM_xxx_yyy.prop" while 
 * "xxx" is part of the file name and "yyy" is the language code.
 * <p> Created at: 4/DEC/2000
 * <br> Updated at: 09/APR/2001
 * <BR>           : 10/MAY/2002 by Richard Huang (Ion Global) = Add Simplified Chinese into related language arrays
 *
 * @author	Maggie Yip (IMT E-Business)
 * @version	%I%, %G%
 * @since	JDK1.1.8
 */
 
public class ResourceStream {
	private static String fileNamePrefix = "AM_";
	private static String fileNamePostfix = ".prop";
	private static String[] languages = {"eng", "tch", "sch", "jap", "kor"};
	private static String[] charSets = {"ISO-8859-1", "big5", "UTF-8", "Shift_JIS", "KSC5601"};
	private InputStream in;					//inputstream from reading properties file
	private String propertiesFile;			//name of properties file
	
	/**
	 * class constructor
	 *
	 * @param	inFile		the name of properties file
	 */
	public ResourceStream(String inFile) {
		propertiesFile = inFile;
	}
	
	/**
	 * Method to return the InputStream from reading properties file
	 *
	 * @exception IOException	IOException will be thrown if there is error when reading properties file
	 */
	public InputStream getInputStream() throws IOException {
		in = this.getClass().getResourceAsStream(propertiesFile);
		return in;
	}
	
	/**
	 * Method to close the InputStream from reading properties file
	 *
	 * @exception IOException	IOException will be thrown if there is error when reading properties file
	 */
	public void closeInputStream() throws IOException {
		in.close();
	}
	
	/**
	 * Method to get the appropriate properties file
	 *
	 * @param	language		language code
	 */
	public static String getLanguageFileName(String language) {
		int i = 0;
		try {
			for (i = 0; i < languages.length; i++) {
				if (language.equals(languages[i])) {
					break;
				}
			}
			if (i == languages.length) {
				language = languages[0];
			}
		} catch (NullPointerException e) {
			language = languages[0];
		}
		String fileName = new String(fileNamePrefix + language + fileNamePostfix);
		return fileName;
	}

	/**
	 * Method to get the appropriate properties file
	 *
	 * @param	namePortion		file name portion
	 * @param	language		language code
	 */
	public static String getLanguageFileName(String namePortion, String language) {
		int i = 0;
		try {
			for (i = 0; i < languages.length; i++) {
				if (language.equals(languages[i])) {
					break;
				}
			}
			if (i == languages.length) {
				language = languages[0];
			}
		} catch (NullPointerException e) {
			language = languages[0];
		}
		String fileName = new String(fileNamePrefix + namePortion + "_" + language + fileNamePostfix);
		return fileName;
	}

	public static String getCharSet(String language) {
		int i = 0;
		String charSet = new String();
		try {
			for (i = 0; i < languages.length; i++) {
				if (language.equals(languages[i])) {
					charSet = charSets[i];
					break;
				}
			}
			if (i == languages.length) {
				charSet = charSets[0];
			}
		} catch (NullPointerException e) {
			charSet = charSets[0];
		}
		return charSet;
	}

}
