package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.google.common.collect.Iterables;
import firm.nodes.Node;
import java.util.List;
import java.util.Map;

public class WithInputsOrderedFilter implements
	com.github.firmwehr.fiascii.asciiart.parsing.filter.NodeFilter {
	private final String key;
	private final com.github.firmwehr.fiascii.asciiart.parsing.filter.NodeFilter underlying;
	private final List<com.github.firmwehr.fiascii.asciiart.parsing.filter.NodeFilter> inputs;

	public WithInputsOrderedFilter(String key, com.github.firmwehr.fiascii.asciiart.parsing.filter.NodeFilter underlying, List<NodeFilter> inputs) {
		this.key = key;
		this.underlying = underlying;
		this.inputs = inputs;
	}

	@Override
	public boolean matches(Node node) {
		if (!underlying.matches(node)) {
			return false;
		}
		Node[] preds = Iterables.toArray(node.getPreds(), Node.class);
		if (preds.length != inputs.size()) {
			return false;
		}

		for (int i = 0; i < preds.length; i++) {
			if (!inputs.get(i).matches(preds[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void storeMatch(Map<String, Node> matches, Node matchedNode) {
		Node[] preds = Iterables.toArray(matchedNode.getPreds(), Node.class);
		for (int i = 0; i < preds.length; i++) {
			inputs.get(i).storeMatch(matches, preds[i]);
		}

		matches.put(key, matchedNode);
	}
}
