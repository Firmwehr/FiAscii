package com.github.firmwehr.fiascii.util;

import firm.nodes.Const;
import firm.nodes.Node;
import java.util.Objects;

public class NodeComparator {

	public static boolean isSame(Node a, Node b) {
		if (Objects.equals(a, b)) {
			return true;
		}
		if (a instanceof Const aConst && b instanceof Const bConst) {
			return aConst.getTarval().equals(bConst.getTarval());
		}
		return false;
	}
}
