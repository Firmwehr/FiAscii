package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.nodes.Node;
import firm.nodes.Proj;
import java.util.Map;
import java.util.OptionalInt;

public class ProjFilter implements NodeFilter {

	private final String key;
	private final OptionalInt number;

	public ProjFilter(String key, OptionalInt number) {
		this.key = key;
		this.number = number;
	}

	@Override
	public boolean doesNotMatch(Node node) {
		if (node.getClass() != Proj.class) {
			return true;
		}
		if (number.isEmpty()) {
			return false;
		}
		return ((Proj) node).getNum() != number.getAsInt();
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode) {
		Node old = matches.put(key, matchedNode);
		return old == null || old.equals(matchedNode);
	}
}
