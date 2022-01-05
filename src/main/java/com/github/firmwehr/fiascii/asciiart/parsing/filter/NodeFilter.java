package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.nodes.Node;
import java.util.Map;

public interface NodeFilter {

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
	 * @return false if a node was encountered twice, i.e. the match was not real
	 */
	boolean storeMatch(Map<String, Node> matches, Node matchedNode);
}
