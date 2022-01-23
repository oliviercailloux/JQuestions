package io.github.oliviercailloux.wutils;

import static io.github.oliviercailloux.wutils.AsciiSequenceTests.BYTE_AF;
import static io.github.oliviercailloux.wutils.AsciiSequenceTests.BYTE_BD;
import static io.github.oliviercailloux.wutils.AsciiSequenceTests.BYTE_FF;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class Base64SequenceTests {
	@Test
	void testBase64SequenceAA() throws Exception {
		final Base64Sequence b = Base64Sequence.fromBase64Sequence("AA==");
		assertEquals("AA==", b.toString());
		assertArrayEquals(new byte[] { 0x00 }, b.decode());
	}

	@Test
	void testBase64SequenceAAAA() throws Exception {
		final Base64Sequence b = Base64Sequence.fromBase64Sequence("AAAA");
		assertEquals("AAAA", b.toString());
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00 }, b.decode());
	}

	@Test
	void testBase64SequenceAaPlus9() throws Exception {
		final Base64Sequence b = Base64Sequence.fromBase64Sequence("Aa+9");
		assertEquals("Aa+9", b.toString());
		// A is 000000.
		// a is 011010.
		// + is 111110.
		// 9 is 111101.
		// Concatenation is 0000 0001 1010 1111 1011 1101.
		assertArrayEquals(new byte[] { 0x01, BYTE_AF, BYTE_BD }, b.decode());
	}

	@Test
	void testBase64SequenceFourSlashes() throws Exception {
		final Base64Sequence b = Base64Sequence.fromBase64Sequence("////");
		assertEquals("////", b.toString());
		assertArrayEquals(new byte[] { BYTE_FF, BYTE_FF, BYTE_FF }, b.decode());
	}

	@Test
	void testBase64SequenceFromBytes0() throws Exception {
		final Base64Sequence b = Base64Sequence.encode(new byte[] { 0x00 });
		assertArrayEquals(new byte[] { 0x00 }, b.decode());
		assertEquals("AA==", b.toString());
	}

	@Test
	void testBase64SequenceFromBytes() throws Exception {
		final Base64Sequence b = Base64Sequence.encode(new byte[] { 0x01, BYTE_AF, BYTE_BD });
		assertArrayEquals(new byte[] { 0x01, BYTE_AF, BYTE_BD }, b.decode());
		assertEquals("Aa+9", b.toString());
	}

	@Test
	void testNonBase64SequenceA() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> Base64Sequence.fromBase64Sequence("A"));
	}
}
