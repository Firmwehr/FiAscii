package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.github.firmwehr.fiascii.util.NodeComparator;
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
	public boolean doesNotMatch(Node node) {
		return !clazz.isAssignableFrom(node.getClass());
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode) {
		Node old = matches.put(key, matchedNode);
		return old == null || NodeComparator.isSame(old, matchedNode);
	}
}
