package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import firm.nodes.Node;
import java.util.Map;

public interface NodeFilter {

	boolean matches(Node node);

	void storeMatch(Map<String, Node> matches, Node matchedNode);
}
