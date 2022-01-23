package io.github.oliviercailloux.wutils;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.io.BaseEncoding;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sequence of characters that use exclusively characters from the
 * <a href="https://datatracker.ietf.org/doc/html/rfc4648#section-4">Base 64
 * alphabet</a> and that can be decoded to a sequence of bytes.
 * <p>
 * For example, the (singleton) sequence <tt>A</tt> uses exclusively characters
 * from the Base 64 alphabet, but cannot be decoded to a sequence of bytes.
 * </p>
 * TODO check RFC url.
 */
public class Base64Sequence extends AsciiSequenceImpl implements AsciiSequence {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Base64Sequence.class);

	/**
	 * We could also use java.util.Base64.getEncoder() here, but it is more
	 * difficult to use correctly; in particular,
	 * {@link Base64.Encoder#encodeToString(byte[])} oddly uses the ISO-8859-1
	 * charset.
	 */
	private static final BaseEncoding ENCODER = BaseEncoding.base64();

	/**
	 * Returns the same sequence as a base64 sequence, if the given sequence indeed
	 * represents a base64 sequence of characters.
	 *
	 * @param base64Sequence the sequence to consider
	 * @return an instance of this class representing the given sequence
	 * @throws IllegalArgumentException iff the given sequence is not the base64
	 *                                  encoding of any byte sequence
	 */
	public static Base64Sequence fromBase64Sequence(CharSequence base64Sequence) {
		return new Base64Sequence(base64Sequence);
	}

	/**
	 * Returns the base64 sequence corresponding to encoding the given bytes.
	 *
	 * @param bytes the bytes to encode
	 * @return the corresponding base64 sequence.
	 */
	public static Base64Sequence encode(byte[] bytes) {
		return new Base64Sequence(bytes);
	}

	protected Base64Sequence(CharSequence base64Sequence) {
		super(base64Sequence);
		checkArgument(ENCODER.canDecode(base64Sequence));
	}

	protected Base64Sequence(byte[] bytes) {
		super(ENCODER.encode(bytes));
	}

	public byte[] decode() {
		return ENCODER.decode(this);
	}

	/**
	 * Returns this base64 sequence as a String.
	 *
	 * @return this base64 sequence.
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
