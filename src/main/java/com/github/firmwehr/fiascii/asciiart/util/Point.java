package com.github.firmwehr.fiascii.asciiart.util;

public record Point(
	int x,
	int y
) {

	public Point translate(Point other) {
		return new Point(x + other.x, y + other.y);
	}
}
