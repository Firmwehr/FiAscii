package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.github.firmwehr.fiascii.util.NodeComparator;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import firm.nodes.Node;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class WithInputsUnorderedFilter implements NodeFilter {

	private final String key;
	private final NodeFilter underlying;
	private final Collection<NodeFilter> inputs;

	public WithInputsUnorderedFilter(String key, NodeFilter underlying,
		Collection<NodeFilter> inputs) {
		this.key = key;
		this.underlying = underlying;
		this.inputs = inputs;
	}

	@Override
	public boolean doesNotMatch(Node node) {
		if (underlying.doesNotMatch(node)) {
			return true;
		}
		return !matchesAndDo(node, (filter, pred) -> true);
	}

	private boolean matchesAndDo(Node node, BiFunction<NodeFilter, Node, Boolean> action) {
		Node[] preds = Iterables.toArray(node.getPreds(), Node.class);
		if (preds.length != inputs.size()) {
			return false;
		}

		for (List<NodeFilter> filters : Collections2.permutations(inputs)) {
			if (matches(preds, filters)) {
				for (int i = 0; i < preds.length; i++) {
					if (!action.apply(filters.get(i), preds[i])) {
						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

	private boolean matches(Node[] preds, List<NodeFilter> filters) {
		for (int i = 0; i < preds.length; i++) {
			if (filters.get(i).doesNotMatch(preds[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode) {
		if (!matchesAndDo(matchedNode, (nodeFilter, node) -> nodeFilter.storeMatch(matches, node))) {
			return false;
		}
		Node old = matches.put(key, matchedNode);
		return old == null || NodeComparator.isSame(old, matchedNode);
	}
}
