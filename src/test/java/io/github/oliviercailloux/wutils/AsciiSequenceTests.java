package io.github.oliviercailloux.wutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.MoreCollectors;
import com.google.common.primitives.UnsignedBytes;
import org.junit.jupiter.api.Test;

public class AsciiSequenceTests {
	public static final byte BYTE_9D = UnsignedBytes.parseUnsignedByte("9D", 16);

	public static final byte BYTE_80 = UnsignedBytes.parseUnsignedByte("80", 16);

	public static final byte BYTE_A0 = UnsignedBytes.parseUnsignedByte("A0", 16);

	public static final byte BYTE_A7 = UnsignedBytes.parseUnsignedByte("A7", 16);

	public static final byte BYTE_A9 = UnsignedBytes.parseUnsignedByte("A9", 16);

	public static final byte BYTE_AF = UnsignedBytes.parseUnsignedByte("AF", 16);

	public static final byte BYTE_BD = UnsignedBytes.parseUnsignedByte("BD", 16);

	public static final byte BYTE_C3 = UnsignedBytes.parseUnsignedByte("C3", 16);

	public static final byte BYTE_FF = UnsignedBytes.parseUnsignedByte("FF", 16);

	@Test
	void testAscii() throws Exception {
		final AsciiSequence a = AsciiSequence.fromAsciiSequence("Aa=");
		assertEquals("Aa=", a.toString());
	}

	@Test
	void testNotAscii() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> AsciiSequence.fromAsciiSequence("é"));
	}

	@Test
	void testAsciiByte7F() throws Exception {
		final byte[] bytes = new byte[] { 0x7F };
		final AsciiSequence a = AsciiSequence.fromBytes(bytes);
		final int codePoint = a.toString().codePoints().boxed().collect(MoreCollectors.onlyElement());
		assertEquals("DELETE", Character.getName(codePoint));
	}

	@Test
	void testAsciiByte0() throws Exception {
		final byte[] bytes = new byte[] { 0x00 };
		final AsciiSequence a = AsciiSequence.fromBytes(bytes);
		final int codePoint = a.toString().codePoints().boxed().collect(MoreCollectors.onlyElement());
		assertEquals("NULL", Character.getName(codePoint));
		assertEquals(0, a.toString().charAt(0));
		assertEquals(String.valueOf((char) 0), a.toString());
	}

	@Test
	void testNonAsciiByte80() throws Exception {
		final byte[] bytes = new byte[] { BYTE_80 };
		assertThrows(IllegalArgumentException.class, () -> AsciiSequence.fromBytes(bytes));
	}

	@Test
	void testNonAsciiByteFF() throws Exception {
		final byte[] bytes = new byte[] { BYTE_FF };
		assertThrows(IllegalArgumentException.class, () -> AsciiSequence.fromBytes(bytes));
	}
}
