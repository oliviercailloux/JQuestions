package io.github.oliviercailloux.wutils;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.VerifyException;
import io.github.oliviercailloux.jaris.exceptions.Unchecker;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.IntStream;

class AsciiSequenceImpl implements AsciiSequence {
	public static final Unchecker<CharacterCodingException, IllegalArgumentException> UNCHECKER = Unchecker
			.wrappingWith(IllegalArgumentException::new);
	public static final Unchecker<CharacterCodingException, VerifyException> UNCHECKER_VERIFY = Unchecker
			.wrappingWith(VerifyException::new);
	private static final CharsetDecoder ASCII_DECODER = StandardCharsets.US_ASCII.newDecoder()
			.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
	private static final CharsetEncoder ASCII_ENCODER = StandardCharsets.US_ASCII.newEncoder()
			.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);

	private final CharSequence asciiSequence;

	protected AsciiSequenceImpl(CharSequence asciiSequence) {
		this.asciiSequence = asciiSequence;
		checkArgument(ASCII_ENCODER.canEncode(asciiSequence));
	}

	protected AsciiSequenceImpl(byte[] bytes) {
		this(UNCHECKER.<ByteBuffer, CharBuffer>wrapFunction(ASCII_DECODER::decode).apply(ByteBuffer.wrap(bytes)));
	}

	public byte[] getBytes() {
		return UNCHECKER_VERIFY.<CharBuffer, ByteBuffer>wrapFunction(ASCII_ENCODER::encode).apply(CharBuffer.wrap(this))
				.array();
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof AsciiSequenceImpl)) {
			return false;
		}
		final AsciiSequenceImpl t2 = (AsciiSequenceImpl) o2;
		return asciiSequence.equals(t2.asciiSequence);
	}

	@Override
	public int hashCode() {
		return Objects.hash(asciiSequence);
	}

	@Override
	public boolean isEmpty() {
		return asciiSequence.isEmpty();
	}

	@Override
	public int length() {
		return asciiSequence.length();
	}

	@Override
	public IntStream chars() {
		return asciiSequence.chars();
	}

	@Override
	public char charAt(int index) {
		return asciiSequence.charAt(index);
	}

	@Override
	public IntStream codePoints() {
		return asciiSequence.codePoints();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return asciiSequence.subSequence(start, end);
	}

	@Override
	public String toString() {
		return asciiSequence.toString();
	}

}
