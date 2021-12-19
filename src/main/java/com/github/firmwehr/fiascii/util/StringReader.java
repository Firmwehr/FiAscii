package com.github.firmwehr.fiascii.util;

import java.util.function.Predicate;

/**
 * A utility reader for a string.
 */
public class StringReader {

	private final String underlying;
	private int position;

	/**
	 * Creates a new string reader.
	 *
	 * @param underlying the underlying string
	 */
	public StringReader(String underlying) {
		this.underlying = underlying;
		this.position = 0;
	}

	public boolean canRead() {
		return position < underlying.length();
	}

	/**
	 * Peeks at a single char.
	 *
	 * @return the char or 0 if EOF is reached
	 */
	public char peek() {
		if (position >= underlying.length()) {
			return 0;
		}
		return underlying.charAt(position);
	}

	public char readChar() {
		return underlying.charAt(position++);
	}

	/**
	 * Reads for as long as {@link #canRead()} is true and the predicate matches.
	 * <p>
	 * Will place the cursor at the first char that did not match.
	 *
	 * @param predicate the predicate
	 * @return the read string
	 */
	public String readWhile(Predicate<Character> predicate) {
		int start = position;
		while (canRead() && predicate.test(peek())) {
			readChar();
		}

		return underlying.substring(start, position);
	}

	public String readWhitespace() {
		int start = position;
		while (canRead() && Character.isWhitespace(peek())) {
			readChar();
		}

		return underlying.substring(start, position);
	}
}
