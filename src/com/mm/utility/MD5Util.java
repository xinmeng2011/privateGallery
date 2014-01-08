package com.mm.utility;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <pre>
 * Copyright (C) 1998-2013 TENCENT Inc.All Rights Reserved.
 *
 * Description£º
 * 
 * History£º
 * 
 * User				Date			Info		Reason
 * simonliao		2013-6-18		Create		
 * </pre>
 */
public class MD5Util {
	 
    public static String MD5Encode(byte[] toencode) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(toencode);
            return HexEncode(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
     
    public static String HexEncode(byte[] toencode) {
        StringBuilder sb = new StringBuilder(toencode.length * 2);
        for(byte b: toencode){
            sb.append(Integer.toHexString((b & 0xf0) >>> 4));
            sb.append(Integer.toHexString(b & 0x0f));
        }
        return sb.toString().toUpperCase();
    }
}

