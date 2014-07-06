package com.jfinal.ext.base;

import java.util.List;

/***
 * for jfinal
 * 
 * @author 12
 */
public class ListUtil {

	public static String listToString(List<?> list, String param) {

		String str = "";

		for (Object m : list) {

			if (m instanceof Model<?>) {
				str += ((Model<?>) m).get(param) + ",";
			}

		}

		if (str.length() > 1)
			return str.substring(0, str.length() - 1);
		else
			return str;

	}

	public static Object[][] stringToArray(Object obj, String objArray) {
		String[] array = objArray.split(",");
		Object[][] objs = new Object[array.length][2];

		for (int i = 0; i < array.length; i++) {

			objs[i] = new Object[] { obj, Integer.parseInt(array[i]) };
		}

		return objs;
	}

	public static Object[][] ArrayToArray(Integer obj, Integer[] objArray) {
		Object[][] objs = new Object[objArray.length][2];

		for (int i = 0; i < objArray.length; i++) {
			objs[i] = new Object[] { obj, objArray[i] };
		}

		return objs;

	}

	public static Object[][] ArrayToArray(String ids, Integer[] objArray) {
		String[] idArray = ids.split(",");

		Object[][] objs = new Object[objArray.length * idArray.length][2];

		int z = 0;
		for (int j = 0; j < idArray.length; j++) {
			for (int i = 0; i < objArray.length; i++) {
				objs[z++] = new Object[] { idArray[j], objArray[i] };
			}
		}

		return objs;
	}
}
