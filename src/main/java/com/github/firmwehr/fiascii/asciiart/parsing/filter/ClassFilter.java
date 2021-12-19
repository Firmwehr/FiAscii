package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.nodes.Node;
import java.util.Map;

public class ClassFilter implements NodeFilter {
	private final Class<?> clazz;
	private final String key;

	public ClassFilter(String key, Class<?> clazz) {
		this.clazz = clazz;
		this.key = key;
	}

	@Override
	public boolean matches(Node node) {
		return clazz.isAssignableFrom(node.getClass());
	}

	@Override
	public void storeMatch(Map<String, Node> matches, Node matchedNode) {
		matches.put(key, matchedNode);
	}
}
