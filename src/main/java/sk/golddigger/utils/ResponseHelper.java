package sk.golddigger.utils;

import java.util.Map;

public class ResponseHelper {

	public static String getAsTextTable(Map<String, Object> data) {

		StringBuilder responseTable = new StringBuilder();

		// rekurzivne prechadzat hodnoty ak su to mapy
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			responseTable.append(entry.getKey());
			responseTable.append(": ");
			responseTable.append(entry.getValue());
			responseTable.append(System.lineSeparator());
		}

		return responseTable.toString();
	}

}
