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
	public boolean matches(Node node) {
		if (node.getClass() != Const.class) {
			return false;
		}

		return value == ((Const) node).getTarval().asLong();
	}

	@Override
	public void storeMatch(Map<String, Node> matches, Node matchedNode) {
		matches.put(key, matchedNode);
	}
}
