package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.Mode;
import firm.nodes.Node;
import java.util.Map;

public class ModeFilter implements NodeFilter {

	private final String key;
	private final Mode expectedMode;
	private final boolean negated;
	private final NodeFilter underlying;

	public ModeFilter(String key, Mode expectedMode, boolean negated, NodeFilter underlying) {
		this.key = key;
		this.expectedMode = expectedMode;
		this.negated = negated;
		this.underlying = underlying;
	}

	@Override
	public boolean matches(Node node) {
		if (!underlying.matches(node)) {
			return false;
		}
		if (negated) {
			return !expectedMode.equals(node.getMode());
		}
		return expectedMode.equals(node.getMode());
	}

	@Override
	public void storeMatch(Map<String, Node> matches, Node matchedNode) {
		underlying.storeMatch(matches, matchedNode);
		matches.put(key, matchedNode);
	}
}
