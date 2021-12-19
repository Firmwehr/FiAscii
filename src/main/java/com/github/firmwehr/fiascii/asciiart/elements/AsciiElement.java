package com.github.firmwehr.fiascii.asciiart.elements;

import com.github.firmwehr.fiascii.asciiart.util.Point;

public sealed interface AsciiElement permits AsciiBox, AsciiMergeNode {

	Point location();

	boolean contains(Point point);
}
