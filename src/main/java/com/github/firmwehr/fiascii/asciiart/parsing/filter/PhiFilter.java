package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.nodes.Node;
import firm.nodes.Phi;
import java.util.Map;

public class PhiFilter implements NodeFilter {
	private final String key;
	private final boolean shouldbeLoop;

	public PhiFilter(String key, boolean shouldbeLoop) {
		this.key = key;
		this.shouldbeLoop = shouldbeLoop;
	}

	@Override
	public boolean matches(Node node) {
		if (node.getClass() != Phi.class) {
			return false;
		}
		return shouldbeLoop == (((Phi) node).getLoop() == 1);
	}

	@Override
	public void storeMatch(Map<String, Node> matches, Node matchedNode) {
		matches.put(key, matchedNode);
	}
}
