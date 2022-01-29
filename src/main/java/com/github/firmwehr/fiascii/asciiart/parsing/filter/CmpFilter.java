package com.github.firmwehr.fiascii.asciiart.parsing.filter;

import com.github.firmwehr.fiascii.util.NodeComparator;
import firm.Relation;
import firm.nodes.Cmp;
import firm.nodes.Node;
import java.util.Map;

public class CmpFilter implements NodeFilter {

	private final String key;
	private final firm.Relation relation;

	public CmpFilter(String key, Relation relation) {
		this.key = key;
		this.relation = relation;
	}

	@Override
	public String key() {
		return key;
	}

	@Override
	public boolean doesNotMatch(Node node) {
		if (node.getClass() != Cmp.class) {
			return true;
		}

		return relation != ((Cmp) node).getRelation();
	}

	@Override
	public boolean storeMatch(Map<String, Node> matches, Node matchedNode, Backedges backedges) {
		Node old = matches.put(key, matchedNode);
		return old == null || NodeComparator.isSame(old, matchedNode);
	}
}
