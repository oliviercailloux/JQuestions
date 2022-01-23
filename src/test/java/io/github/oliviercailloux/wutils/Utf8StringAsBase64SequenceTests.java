package io.github.oliviercailloux.wutils;

import static io.github.oliviercailloux.wutils.AsciiSequenceTests.BYTE_A9;
import static io.github.oliviercailloux.wutils.AsciiSequenceTests.BYTE_C3;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Utf8StringAsBase64SequenceTests {
	@Test
	void testUtf8StringNul() throws Exception {
		final Utf8StringAsBase64Sequence b = Utf8StringAsBase64Sequence.asBase64Sequence(String.valueOf((char) 0));
		assertEquals(String.valueOf((char) 0), b.decodeToString());
		assertArrayEquals(new byte[] { 0x00 }, String.valueOf((char) 0).getBytes());
		assertArrayEquals(new byte[] { 0x00 }, b.decode());
		assertEquals("AA==", b.toString());
	}

	@Test
	void testUtf8StringSpace() throws Exception {
		final Utf8StringAsBase64Sequence b = Utf8StringAsBase64Sequence.asBase64Sequence(" ");
		assertEquals(" ", b.decodeToString());
		assertArrayEquals(new byte[] { 0x20 }, " ".getBytes());
		assertArrayEquals(new byte[] { 0x20 }, b.decode());
		// I is 001000.
		// A is 000000.
		// Concatenation is 0010 0000 0000.
		assertEquals("IA==", b.toString());
	}

	@Test
	void testUtf8StringAccent() throws Exception {
		final Utf8StringAsBase64Sequence b = Utf8StringAsBase64Sequence.asBase64Sequence("é");
		assertEquals("é", b.decodeToString());
		// é is 1100 0011 1010 1001.
		assertArrayEquals(new byte[] { BYTE_C3, BYTE_A9 }, "é".getBytes());
		assertArrayEquals(new byte[] { BYTE_C3, BYTE_A9 }, b.decode());
		// w is 110000.
		// 6 is 111010.
		// k is 100100.
		assertEquals("w6k=", b.toString());
	}

	@Test
	void testUtf8StringEncodedNul() throws Exception {
		final Utf8StringAsBase64Sequence b = Utf8StringAsBase64Sequence.fromUtf8StringAsBase64Sequence("AA==");
		assertEquals(Utf8StringAsBase64Sequence.asBase64Sequence(String.valueOf((char) 0)), b);
	}
}
