package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import firm.nodes.Node;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
	public boolean matches(Node node) {
		if (!underlying.matches(node)) {
			return false;
		}
		return matchesAndDo(node, (filter, pred) -> {
		});
	}

	private boolean matchesAndDo(Node node, BiConsumer<NodeFilter, Node> action) {
		Node[] preds = Iterables.toArray(node.getPreds(), Node.class);
		if (preds.length != inputs.size()) {
			return false;
		}

		for (List<NodeFilter> filters : Collections2.permutations(inputs)) {
			if (matches(preds, filters)) {
				for (int i = 0; i < preds.length; i++) {
					action.accept(filters.get(i), preds[i]);
				}
				return true;
			}
		}

		return false;
	}

	private boolean matches(Node[] preds, List<NodeFilter> filters) {
		for (int i = 0; i < preds.length; i++) {
			if (!filters.get(i).matches(preds[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void storeMatch(Map<String, Node> matches, Node matchedNode) {
		matchesAndDo(matchedNode, (nodeFilter, node) -> nodeFilter.storeMatch(matches, node));
		matches.put(key, matchedNode);
	}
}
