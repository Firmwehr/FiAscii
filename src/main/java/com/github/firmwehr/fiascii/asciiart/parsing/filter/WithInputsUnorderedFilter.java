package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.github.firmwehr.fiascii.util.NodeComparator;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import firm.nodes.Node;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
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

		Node[] preds = Iterables.toArray(node.getPreds(), Node.class);
		if (preds.length != inputs.size()) {
			return true;
		}

		for (List<NodeFilter> filters : Collections2.permutations(inputs)) {
			if (matches(preds, filters)) {
				return false;
			}
		}

		return true;
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
		// We only enter this method if #doesNotMatch is false (i.e. it matched)
		Node[] preds = Iterables.toArray(matchedNode.getPreds(), Node.class);

		// We could not add our node as it failed the "same node" constraints. No further match
		// necessary.
		Node ourOld = matches.put(key, matchedNode);
		if (ourOld != null && !NodeComparator.isSame(ourOld, matchedNode)) {
			return false;
		}

		// Copy as we might need to rewrite it a few times
		Map<String, Node> finalExistingMatches = new HashMap<>(matches);

		permutationsLoop:
		for (List<NodeFilter> filters : Collections2.permutations(inputs)) {
			// This permutation is a correct one for basic class based matchers
			// We still need to verify the "same node" constraints hold
			if (matches(preds, filters)) {

				// Check every input filter recursively
				for (int i = 0; i < preds.length; i++) {
					if (!filters.get(i).storeMatch(finalExistingMatches, preds[i])) {
						// Rollback, try next permutation
						finalExistingMatches = new HashMap<>(matches);
						continue permutationsLoop;
					}
				}

				// Match was successful! Commit and return
				matches.putAll(finalExistingMatches);
				return true;
			}
		}

		return false;
	}
}
