package erjangx.ewarp.util;

public class Util {
	public static String getCanonicalString(String string) {
		if (string == null) {
			return null;
		}
		string = string.trim();
		if (string.length() == 0) {
			return null;
		}
		
		return string;
	}
	
	public static boolean isValidString(String string) {
		string = getCanonicalString(string);
		if (string == null) {
			return false;
		}
		return true;
	}
}
