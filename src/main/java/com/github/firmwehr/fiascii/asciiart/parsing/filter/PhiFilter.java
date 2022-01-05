package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.github.firmwehr.fiascii.util.NodeComparator;
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
	public boolean doesNotMatch(Node node) {
		if (node.getClass() != Phi.class) {
			return true;
		}
		return shouldbeLoop != (((Phi) node).getLoop() == 1);
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode) {
		Node old = matches.put(key, matchedNode);
		return old == null || NodeComparator.isSame(old, matchedNode);
	}
}
