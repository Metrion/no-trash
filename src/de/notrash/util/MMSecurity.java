package de.notrash.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MMSecurity {

	public static byte[] getMessageDigest(String file, String algo)
			throws Exception {
		byte md[] = new byte[8192];
		int n = 0;
		MessageDigest messagedigest = MessageDigest.getInstance(algo);
		FileInputStream in = new FileInputStream(new File(file));

		while ((n = in.read(md)) > -1) 
			messagedigest.update(md, 0, n);

		return messagedigest.digest();
	}

	public static String getDigest(String file, String algo) throws Exception {
		byte digest[] = getMessageDigest(file, algo);

		StringBuffer k = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			String s = Integer.toHexString(digest[i] & 0xFF);
			k.append((s.length() == 1) ? "0" + s : s);
		}
		return k.toString();
	}
}
