package io.github.oliviercailloux.wutils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base64 sequence that decodes to a byte sequence that can be interpreted as
 * the UTF-8 encoding of a sequence of characters.
 */
public class Utf8StringAsBase64Sequence extends Base64Sequence implements AsciiSequence {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Utf8StringAsBase64Sequence.class);

	public static Utf8StringAsBase64Sequence asBase64Sequence(CharSequence unencoded) {
		return new Utf8StringAsBase64Sequence(unencoded, true);
	}

	public static Utf8StringAsBase64Sequence fromUtf8StringAsBase64Sequence(CharSequence utf8StringAsBase64Sequence) {
		return new Utf8StringAsBase64Sequence(utf8StringAsBase64Sequence);
	}

	private static final CharsetDecoder UTF_8_DECODER = StandardCharsets.UTF_8.newDecoder()
			.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);

	private Utf8StringAsBase64Sequence(CharSequence utf8StringAsBase64Sequence) {
		super(utf8StringAsBase64Sequence);
		UNCHECKER.call(() -> UTF_8_DECODER.decode(ByteBuffer.wrap(decode())));
	}

	private Utf8StringAsBase64Sequence(CharSequence unencoded, boolean nonEncoded) {
		super(unencoded.toString().getBytes(StandardCharsets.UTF_8));
		verify(unencoded.equals(decodeToString()));
		LOGGER.debug("Encoded {} to {}.", unencoded, toString());
		checkArgument(nonEncoded);
	}

	/**
	 * Returns the string that this base64 sequence represents, that is, the result
	 * of decoding this base64 sequence to a byte sequence and interpreting this
	 * byte sequence as a UTF-8 encoded string.
	 *
	 * @return the string that this base64 sequence encodes.
	 */
	public String decodeToString() {
		return new String(decode(), StandardCharsets.UTF_8);
	}

	/**
	 * Returns this base64 sequence as a String.
	 * <p>
	 * Note that this does <em>not</em> return the decoded string.
	 * </p>
	 *
	 * @return this base64 sequence.
	 * @see #decodeToString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}

}
