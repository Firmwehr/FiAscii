package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.nodes.Const;
import firm.nodes.Node;
import java.util.Map;

public class ConstFilter implements NodeFilter {
	private final String key;
	private final long value;

	public ConstFilter(String key, long value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean doesNotMatch(Node node) {
		if (node.getClass() != Const.class) {
			return true;
		}

		return value != ((Const) node).getTarval().asLong();
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode) {
		Node old = matches.put(key, matchedNode);
		return old == null || old.equals(matchedNode);
	}
}
