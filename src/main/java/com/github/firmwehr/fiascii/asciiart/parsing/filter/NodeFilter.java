package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.nodes.Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface NodeFilter {

	/**
	 * Returns the key of this filter, the name in the match object.
	 *
	 * @return the name in the match object.
	 */
	String key();

	/**
	 * Checks if the node does not match. Must not have any false-<em>negatives</em>, but can have
	 * false-positives.
	 *
	 * @param node the node to check
	 * @return true if the node might match
	 */
	boolean doesNotMatch(Node node);

	/**
	 * Stores the match in the given map.
	 *
	 * @param matches the matches to store it in
	 * @param matchedNode the node that was matched
	 * @param backedges backedge information
	 * @return false if a node was encountered twice, i.e. the match was not real
	 */
	boolean storeMatch(Map<String, Node> matches, Node matchedNode, Backedges backedges);

	default void buildBackedges(Node matchedNode, Backedges backedges) {
	}

	record Backedges(
		Map<String, Set<String>> keyEdges,
		Map<Node, Set<Node>> nodeEdges
	) {

		public Backedges() {
			this(new HashMap<>(), new HashMap<>());
		}

		public void addEdge(String parent, String child) {
			keyEdges.computeIfAbsent(parent, ignored -> new HashSet<>()).add(child);
		}

		public void addEdge(Node parent, Node child) {
			nodeEdges.computeIfAbsent(parent, ignored -> new HashSet<>()).add(child);
		}

		public Set<String> getForKey(String key) {
			return keyEdges.getOrDefault(key, Set.of());
		}

		public Set<Node> getForNode(Node key) {
			return nodeEdges.getOrDefault(key, Set.of());
		}
	}
}
