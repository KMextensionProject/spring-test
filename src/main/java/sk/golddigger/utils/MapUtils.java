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
		String result = getAsStringTree(map, tree);
		depth = 0;
		return result;
	}

	@SuppressWarnings("unchecked")
	private static String getAsStringTree(Map<String, Object> map, StringBuilder tree) {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (depth > 0) {
				appendTab(tree);
			}

			tree.append(entry.getKey());
			tree.append(": ");

			Object value = entry.getValue();

			if (value instanceof Map) {
				depth++;
				tree.append(System.lineSeparator());
				getAsStringTree((Map<String, Object>) value, tree);
			} else {
				tree.append(value);
				tree.append(System.lineSeparator());
			}
		}

		depth--;
		return tree.toString();
	}

	private static void appendTab(StringBuilder tree) {
		for(int i = 0; i < depth; i++) {
			tree.append("\t");
		}
	}

	public static void renameKey(Map<String, Object> map, String oldKey, String newKey) {
		Object value = map.remove(oldKey);
		map.put(newKey, value);
	}

}
