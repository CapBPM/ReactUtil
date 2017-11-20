package com.capbpm.react.util;

import org.apache.commons.codec.binary.Base64;

public class DecodeEncodeUtil {
	public static String decodeBase64(String str) {
		return new String(Base64.decodeBase64(str));
	}

	
	public static String encodeBase64(String str) {
		return new String(Base64.encodeBase64(str.getBytes()));
	}
	
}
