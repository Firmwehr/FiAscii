package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.github.firmwehr.fiascii.util.NodeComparator;
import com.google.common.collect.Iterables;
import firm.nodes.Node;
import java.util.List;
import java.util.Map;

public class WithInputsOrderedFilter implements NodeFilter {

	private final String key;
	private final NodeFilter underlying;
	private final List<NodeFilter> inputs;

	public WithInputsOrderedFilter(String key, NodeFilter underlying, List<NodeFilter> inputs) {
		this.key = key;
		this.underlying = underlying;
		this.inputs = inputs;
	}

	@Override
	public boolean doesNotMatch(Node node) {
		if (underlying.doesNotMatch(node)) {
			return true;
		}
		Node[] preds = Iterables.toArray(node.getPreds(), Node.class);
		if (preds.length != inputs.size()) {
			return true;
		}

		for (int i = 0; i < preds.length; i++) {
			if (inputs.get(i).doesNotMatch(preds[i])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode) {
		Node[] preds = Iterables.toArray(matchedNode.getPreds(), Node.class);
		for (int i = 0; i < preds.length; i++) {
			if (!inputs.get(i).storeMatch(matches, preds[i])) {
				return false;
			}
		}

		Node old = matches.put(key, matchedNode);
		return old == null || NodeComparator.isSame(old, matchedNode);
	}
}
