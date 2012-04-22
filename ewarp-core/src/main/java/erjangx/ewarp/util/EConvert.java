package erjangx.ewarp.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import erjang.ECons;
import erjang.EInteger;
import erjang.EObject;
import erjang.EString;

public class EConvert {
	public static List<EObject> toList(ECons cons) {
		List<EObject> list = new LinkedList<EObject>();
		for (Iterator<EObject> it = cons.iterator(); it.hasNext();) {
			EObject object = it.next();
			list.add(object);
		}
		
		return list;
	}
	
	public static int toInteger(EObject obj) {
		return toInteger(obj, 0);
	}
	
	public static int toInteger(EObject obj, int defVal) {
		if (obj == null) {
			return defVal;
		}
		EInteger eint = obj.testInteger();
		if (eint == null) {
			return defVal;
		}
		return eint.asInt();
	}
	
	public static String toString(EObject obj) {
		return toString(obj, null);
	}
	
	public static String toString(EObject obj, String defVal) {
		if (obj == null) {
			return defVal;
		}
		EString estr = obj.testString();
		if (estr != null) {
			return estr.toString();
		}
		
		return obj.toString();
	}
}
