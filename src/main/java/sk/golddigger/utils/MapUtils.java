package sk.golddigger.utils;

import java.util.Map;

public class MapUtils {

	private static int depth;

	/**
	 * @return the contents of the map as a tree structured key-value 
	 * pairs with encapsulation depth of map values.
	 */
	public static String toStringTree(Map<String, Object> map) {
		StringBuilder tree = new StringBuilder();
		return getAsStringTree(map, tree);
	}

	// TODO: add dynamic tabs based on depth (depth++ / depth--)
	@SuppressWarnings("unchecked")
	private static String getAsStringTree(Map<String, Object> map, StringBuilder tree) {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (depth > 0) {
				tree.append("\t");
			}

			tree.append(entry.getKey());
			tree.append(": ");

			Object value = entry.getValue();

			if (value instanceof Map) {
				depth = 1;
				tree.append(System.lineSeparator());
				getAsStringTree((Map<String, Object>) value, tree);
			} else {
				tree.append(value);
				tree.append(System.lineSeparator());
			}
		}

		depth = 0;
		return tree.toString();
	}
}
