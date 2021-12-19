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
	public boolean matches(Node node) {
		if (node.getClass() != Proj.class) {
			return false;
		}
		if (number.isEmpty()) {
			return true;
		}
		return ((Proj) node).getNum() == number.getAsInt();
	}

	@Override
	public void storeMatch(Map<String, Node> matches, Node matchedNode) {
		matches.put(key, matchedNode);
	}
}
