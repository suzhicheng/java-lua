package com.lua.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DesUtil {

	private static Log logger = LogFactory.getLog(DesUtil.class);

	public static String pbocDesMac(String strVector, String strToken, String strBuff) {
		String mac = "";
		byte[] bToken = new byte[16];
		byte[] bVector = new byte[8];
		byte[] bMac = new byte[8];

		byte[] datas = new byte[strBuff.length() / 2];

		ByteUtil.hexStringToMulBytes(strToken, bToken, 0);
		ByteUtil.hexStringToMulBytes(strVector, bVector, 0);

		ByteUtil.hexStringToMulBytes(strBuff, datas, 0);

		bMac = genMACV(bToken, bVector, datas);

		mac = ByteUtil.mulByteToHexString(bMac, 0, 4);
		return mac;
	}

	public static String pboc3DesMac(String vec, String key, String data) {
		String mac = null;
		byte[] dataBuff = null;
		byte[] keyBuff = null;
		byte[] ramBuff = null;

		// 1、随机数获取4字节秘钥初始值，后补4字节0x00。
		int vecLen = vec.length(); 
		vecLen += 8;
		ramBuff = new byte[vecLen / 2];
		ByteTool.hexStringToMulBytes(vec, ramBuff, 0); 

		// 2、秘钥长度必须是16字节。
		int keyLen = key.length();
		if (keyLen != 32) {
			return mac;
		}
		keyBuff = new byte[keyLen / 2];
		ByteTool.hexStringToMulBytes(key, keyBuff, 0); 
		int dataLen = data.length();
		int leftLen = 0;
		String padStr = "8000000000000000";
		if ((leftLen = dataLen % 16) == 0) {
			data += padStr;
			dataLen += 16;
		} else {
			data += padStr.substring(0, 16 - leftLen);
			dataLen += (16 - leftLen);
		}
		dataBuff = new byte[dataLen / 2];
		ByteTool.hexStringToMulBytes(data, dataBuff, 0);
		 

		int blockCount = dataLen / 16;
		byte[] tBlock = new byte[8];
		byte[] tData = new byte[8];
		byte[] tKey = new byte[8];

		for (int i = 0; i < blockCount; i++) {
			System.arraycopy(dataBuff, (i * 8), tBlock, 0, 8);
			 
			if (i == 0) {// 第一块
				tData = xor(tBlock, ramBuff, 8);
			} else {
				tData = xor(tBlock, tKey, 8);
			}
			 
 			tKey = MacUtil.desEncode(keyBuff, 0, tData, 0, 8);
			 
		}
		tKey = MacUtil.desDecode(keyBuff, 8, tKey, 0, 8);
	 	tKey = MacUtil.desEncode(keyBuff, 0, tKey, 0, 8);
	 	mac = ByteTool.byteToString(tKey, 0, 8).substring(0, 8).toUpperCase();
		return mac;
	}

	private static byte[] genMACV(byte[] kma, byte[] o, byte[] b) {
		return genMacInitialVector(kma, o, b, 0, b.length);
	}

	private static byte[] genMacInitialVector(byte[] kma, byte[] o, byte[] b, int off, int len) {

		// byte[] o = { 0, 0, 0, 0, 0, 0, 0, 0 }; // Initial Vector
		byte[] d = new byte[8];// data block

		for (; len >= 0;) {
			getBlock(d, b, off, len);
			off += 8;
			len -= 8;
			xor(o, d, 8); // o^d = l
			o = dea(true, o, kma); // DEA(l, MAK) -> o
		}

		o = dea(false, o, kma, 8);
		o = dea(true, o, kma);

		return o;
	}

	private static byte[] getBlock(byte[] block, byte[] b, int off, int len) {
		int i = 0;
		for (; (i < len) && (i < 8);) {
			block[i++] = b[off++];
		}
		if (i < 8) {
			block[i++] = (byte) 0x80;
		}
		for (; i < 8;) {
			block[i++] = 0;
		}
		return block;
	}

	private static byte[] xor(byte[] block, byte[] b, int len) {
		for (int i = 0; i < len; i++) {
			block[i] ^= b[i];
		}

		return block;
	}

	private static byte[] dea(boolean isEn, byte[] block, byte[] key) {
		return dea(isEn, block, key, 0);
	}

	private static byte[] dea(boolean isEn, byte[] block, byte[] key, int off) {
		// enc=true 加密
		if (isEn) {
			byte[] x = desEncode(key, off, block, 0, 8);
			return x;
		}
		byte[] x = desDecode(key, off, block, 0, 8);
		return x;
	}

	private static byte[] desEncode(byte[] keyData, int koff, byte[] data, int off, int len) {
		try {
			SecretKey key = getSecretKey(keyData, koff);
			return desEncode(key, data, off, len);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	private static byte[] desDecode(byte[] keyData, int koff, byte[] data, int off, int len) {
		try {
			SecretKey key = getSecretKey(keyData, koff);
			return desDecode(key, data, off, len);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	private static SecretKey getSecretKey(byte[] keyData, int off) throws Exception {
		DESKeySpec dks = new DESKeySpec(keyData, off);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);
		return key;
	}

	private static byte[] desEncode(Key key, byte[] data, int off, int len) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedData = cipher.doFinal(data, off, len);
		return encryptedData;
	}

	private static byte[] desDecode(Key key, byte[] data, int off, int len) throws Exception {

		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedData = cipher.doFinal(data, off, len);
		return decryptedData;
	}

	public static String ecb2DesEn(String data, String key) {
		String secretData = null;
		byte[] dataBuff = null;
		byte[] secretBuff = null;
		byte[] keyBuff = null; 
		int keyLen = key.length();
		if (keyLen != 32) {
			return secretData;
		}
		keyBuff = new byte[keyLen / 2];
		ByteUtil.hexStringToMulBytes(key, keyBuff, 0); 
		int dataLen = data.length();
		int leftLen = 0;
		String padStr = "8000000000000000";
		if ((leftLen = dataLen % 16) == 0) {
			data += padStr;
			dataLen += 16;
		} else {
			data += padStr.substring(0, 16 - leftLen);
			dataLen += (16 - leftLen);
		}
		dataBuff = new byte[dataLen / 2];
		secretBuff = new byte[dataLen / 2];
		ByteUtil.hexStringToMulBytes(data, dataBuff, 0); 
		int blockCount = dataLen / 16;
		byte[] tKey = new byte[8];
		for (int i = 0; i < blockCount; i++) {
			// Des加密
			tKey = DesUtil.desEncode(keyBuff, 0, dataBuff, i * 8, 8); 
			tKey = DesUtil.desDecode(keyBuff, 8, tKey, 0, 8); 
			tKey = DesUtil.desEncode(keyBuff, 0, tKey, 0, 8); 
			System.arraycopy(tKey, 0, secretBuff, i * 8, 8);

		}
		secretData = ByteUtil.byteToString(secretBuff, 0, dataLen / 2).toUpperCase();
		return secretData;
	}

	public static String ecb2DesDe(String data, String key) {
		String secretData = null;
		byte[] dataBuff = null;
		byte[] secretBuff = null;
		byte[] keyBuff = null; 
		int keyLen = key.length();
		if (keyLen != 32) {
			return secretData;
		}
		keyBuff = new byte[keyLen / 2];
		ByteUtil.hexStringToMulBytes(key, keyBuff, 0); 
		int dataLen = data.length();
		int leftLen = 0;
		String padStr = "8000000000000000";
		if ((leftLen = dataLen % 16) == 0) {
			data += padStr;
			dataLen += 16;
		} else {
			data += padStr.substring(0, 16 - leftLen);
			dataLen += (16 - leftLen);
		}
		dataBuff = new byte[dataLen / 2];
		secretBuff = new byte[dataLen / 2];
		ByteUtil.hexStringToMulBytes(data, dataBuff, 0); 
		int blockCount = dataLen / 16;
		byte[] tKey = new byte[8];
		for (int i = 0; i < blockCount; i++) { 
			tKey = DesUtil.desDecode(keyBuff, 0, dataBuff, i * 8, 8); 
			tKey = DesUtil.desEncode(keyBuff, 8, tKey, 0, 8); 
			tKey = DesUtil.desDecode(keyBuff, 0, tKey, 0, 8); 
			System.arraycopy(tKey, 0, secretBuff, i * 8, 8);
		}
		secretData = ByteUtil.byteToString(secretBuff, 0, dataLen / 2).toUpperCase();
		return secretData;
	}

}
