package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.github.firmwehr.fiascii.util.NodeComparator;
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
	public boolean doesNotMatch(Node node) {
		if (underlying.doesNotMatch(node)) {
			return true;
		}
		if (negated) {
			return expectedMode.equals(node.getMode());
		}
		return !expectedMode.equals(node.getMode());
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode) {
		if (!underlying.storeMatch(matches, matchedNode)) {
			return false;
		}
		Node old = matches.put(key, matchedNode);
		return old == null || NodeComparator.isSame(old, matchedNode);
	}
}
