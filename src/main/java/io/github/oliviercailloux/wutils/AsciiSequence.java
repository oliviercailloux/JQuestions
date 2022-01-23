package io.github.oliviercailloux.wutils;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * An immutable sequence of ascii characters.
 */
public interface AsciiSequence extends CharSequence {
	public static AsciiSequence fromAsciiSequence(CharSequence asciiSequence) {
		return new AsciiSequenceImpl(asciiSequence);
	}

	/**
	 * Returns a sequence of ascii characters corresponding to representing the
	 * given bytes in {@link StandardCharsets#US_ASCII}, iff the given bytes can be
	 * so represented (that is, iff their first bit is zero).
	 *
	 * @param bytes the bytes to consider
	 * @return the corresponding sequence of ascii characters
	 * @throws IllegalArgumentException iff at least one of the given byte starts
	 *                                  with one.
	 */
	public static AsciiSequence fromBytes(byte[] bytes) {
		return new AsciiSequenceImpl(bytes);
	}

	/**
	 * Returns an array of bytes of same length as this sequence, each ascii
	 * character being converted to its corresponding byte according to the ascii
	 * encoding.
	 *
	 * @return an array of bytes, each byte starting with 0.
	 */
	public byte[] getBytes();

	/**
	 * Returns true iff the given object is an {@link AsciiSequence} that represents
	 * the same sequence of characters than this object.
	 * <p>
	 * Note that, as a result, an {@code AsciiString} instance created from a
	 * {@link String} instance <i>{@code s}</i> is equal to an {@code AsciiString}
	 * instance created from a {@link CharBuffer} instance <i>{@code c}</i>,
	 * provided that <i>{@code s}</i> and <i>{@code c}</i> represent the same
	 * sequence of characters, whereas <i>{@code s}</i> is never equal to
	 * <i>{@code c}</i>.
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Returns this ascii sequence as a String.
	 *
	 * @return this ascii sequence.
	 */
	@Override
	public String toString();
}
