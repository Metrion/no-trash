package de.markware.toolbox.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;


public class MMString {

	public static String fillByZero(String b) {
		int lead = 8 - b.length();
		char[] pre = new char[lead];
		Arrays.fill(pre, '0');
		String binary = (new String(pre)) + b;
		return binary;
	}

	public static String decode(String charset, ByteBuffer buffer)
			throws CharacterCodingException {
		CharsetDecoder decoder = Charset.forName(charset).newDecoder();
		CharBuffer cbuf = decoder.decode(buffer);
		return cbuf.toString();
	}

	public static ByteBuffer encode(String charset, String text)
			throws CharacterCodingException {
		CharsetEncoder encoder = Charset.forName(charset).newEncoder();
		ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(text));
		return bbuf;
	}
	
	/**
	 * Pruefung, ob Zeichenkette NULL bzw. die Laenge 0 ist.
	 * 
	 * @param source
	 *            Zeichenkette.
	 * @return true=Zeichenkette NULL bzw. die Laenge ist 0, false=Zeichenkette
	 *         ist nicht NULL bzw. die Laenge ist nicht 0
	 */
	public static boolean isNull(String source) {
		return (source == null) || (source.length() == 0);
	}
	
	protected static final byte[] Hexhars = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String byte2Hex(byte[] b) {
		StringBuilder s = new StringBuilder(2 * b.length);
	
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
	
			s.append((char) Hexhars[v >> 4]);
			s.append((char) Hexhars[v & 0xf]);
		}
		return s.toString();
	}	
}
