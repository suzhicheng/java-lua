package com.lua.utils;

 

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class MacUtil {

	/*
	 * MAC/TAC的产生使用以下单倍长dea算法： 第一步：将一个8个字节长的初始值（Initial Vector）设定为16进制的’0x 00 00
	 * 00 00 00 00 00 00’。 第二步：将所有的输入数据按指定顺序连接成一个数据块。
	 * 第三步：将连接成的数据块分割为8字节长的数据块组，标识为D1, D2, D3, D4等等。
	 * 分割到最后，余下的字节组成一个长度小于等于8字节的最后一块数据块。
	 * 第四步：如果最后一个数据块长度为8字节，则在此数据块后附加一个8字节长的数据块， 附加的数据块为：16进制的’0x 80 00 00 00 00
	 * 00 00 00’。 如果最后一个数据块长度小于8字节，则该数据块的最后填补一个值为16进制'0x80'的字节。
	 * 如果填补之后的数据块长度等于8字节，则跳至第五步。
	 * 如果填补之后的数据块长度仍小于8字节，则在数据块后填补16进制'0x00'的字节至数据块长度为8字节。
	 * 第五步：MAC的产生是通过上述方法产生的数据块组，由过程密钥进行加密运算，过程密钥的产生方法见 图B-3。
	 * TAC的产生是通过上述方法产生的数据块组，由DTK密钥左右8位字节进行异或运算的结果进行加密运算。 MAC或TAC的算法见图B-4描述。
	 * 第六步：最终值的左4字节为MAC或TAC。
	 */
	/**
	 * 计算MAC
	 * 
	 * @param kma
	 *            MAC Session Key A
	 * @param b
	 *            data buffer
	 * @param off
	 *            start position
	 * @param len
	 *            valid data length
	 */
	public static byte[] genMAC(byte[] kma, byte[] b, int off, int len) {

		byte[] o = { 0, 0, 0, 0, 0, 0, 0, 0 }; // Initial Vector
		byte[] d = new byte[8];// data block
		for (; len >= 0;) {
			getBlock(d, b, off, len);
			off += 8;
			len -= 8;
			xor8(o, d); // o^d = l
			o = dea(true, o, kma); // dea(l, MAK) -> o

		}
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

	public static byte[] xor8(byte[] block, byte[] b) {
		for (int i = 0; i < 8; i++) {
			block[i] ^= b[i];
		}

		return block;
	}

	private static byte[] dea(boolean enc, byte[] block, byte[] key) {
		return dea(enc, block, key, 0);
	}

	private static byte[] dea(boolean enc, byte[] block, byte[] key, int off) {
		if (enc) {
			byte[] x = desEncode(key, off, block, 0, 8);
			return x;
		}

		byte[] x = desDecode(key, off, block, 0, 8);
		return x;
	}

	// 子密钥
	// 推导双倍长DPK左半部分的方法：
	// ── 将应用序列号的最右16个数字作为输入数据
	// ── 将MPK作为加密密钥
	// ── 用MPK对输入数据进行3dea运算
	// 推导双倍长DPK右半部分的方法：
	// ── 将应用序列号的最右16个数字的求反作为输入数据
	// ── 将MPK作为加密密钥
	// ── 用MPK对输入数据进行3dea运算
	// 应用序列号(10B, cn)//格式cn的数据元左靠齐并且右补十六进制'F'。
	// 发卡方分配的一个数字，符合国家标准GB/T14504-93
	public static byte[] getDKey(byte[] app, byte[] mk) {
		byte[] key = new byte[16];

		byte[] left = getLeftHalf(app);
		// E
		left = dea(true, left, mk);
		// D
		left = dea(false, left, mk, 8);
		// E
		left = dea(true, left, mk);

		byte[] right = getRightHalf(app);
		// E
		right = dea(true, right, mk);
		// D
		right = dea(false, right, mk, 8);
		// E
		right = dea(true, right, mk);

		System.arraycopy(left, 0, key, 0, 8);
		System.arraycopy(right, 0, key, 8, 8);
		return key;
	}

	private static byte[] getLeftHalf(byte[] app) {
		byte[] key = new byte[8];
		System.arraycopy(app, 2, key, 0, 8);
		return key;
	}

	private static byte[] getRightHalf(byte[] app) {
		byte[] key = new byte[8];
		for (int i = 0; i < 8; i++) {
			key[i] = (byte) ~app[i + 2];
		}
		return key;
	}

	// 过程密钥是在交易过程中用可变数据产生的单倍长密钥。
	// 过程密钥产生后只能在某过程/交易中使用一次。
	// 过程密钥 8B
	public static byte[] getSessionKey(byte[] idata, byte[] dk) {
		byte[] xdata;
		// E
		xdata = dea(true, idata, dk);
		// D
		xdata = dea(false, xdata, dk, 8);
		// E
		xdata = dea(true, xdata, dk);
		return xdata;
	}

	// 用来产生过程密钥SESLK的输入数据如下:
	// SESLK：伪随机数（ICC）||联机交易序号||'8000'
	// 用SESLK对以下数据加密产生MAC1(按所列顺序):
	// ──电子存折余额（交易前）或者电子钱包余额（交易前）(4B)
	// ──交易金额 (4B)
	// ──交易类型标识(1B,cn)
	// ──终端机编号(6B,cn)

	// 主机产生一个报文签别码(MAC2)，用于IC卡对主机进行合法性检查。
	// 用SESLK对以下数据加密产生MAC2(按所列顺序):
	// ──交易金额(4B)
	// ──交易类型标识(1B,cn)
	// ──终端机编号(6B,cn)
	// ──交易日期 (主机)(4B,cn)
	// ──交易时间 (主机)(3B,cn)

	// //////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////
	// for test
	public static byte[] desEncode(byte[] keyData, int koff, byte[] data,
			int off, int len) {
		try {
			SecretKey key = getSecretKey(keyData, koff);
			return desEncode(key, data, off, len);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] desDecode(byte[] keyData, int koff, byte[] data,
			int off, int len) {
		try {
			SecretKey key = getSecretKey(keyData, koff);
			return desDecode(key, data, off, len);
		} catch (Exception e) {
			return null;
		}
	}

	public static SecretKey getSecretKey(byte[] keyData, int off)
			throws Exception {
		/* throws */

		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(keyData, off);

		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);

		return key;
	}

	public static byte[] desEncode(Key key, byte[] data, int off, int len)
			throws Exception {
		// SecureRandom sr = new SecureRandom();

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

		// 用密匙初始化Cipher对象
		// cipher.init(Cipher.ENCRYPT_MODE, key, sr);
		cipher.init(Cipher.ENCRYPT_MODE, key);

		// System.out.printf("data=%02X %02X %02X %02X\n ", data[0], data[1],
		// data[2], data[3]);
		// 正式执行加密操作
		byte[] encryptedData = cipher.doFinal(data, off, len);

		// System.out.printf("enc=%02X %02X %02X %02X\n ", encryptedData[0],
		// encryptedData[1], encryptedData[2], encryptedData[3]);

		return encryptedData;
	}

	public static byte[] desDecode(Key key, byte[] data, int off, int len)
			throws Exception {
		// SecureRandom sr = new SecureRandom();

		// Cipher对象实际完成解密操作
		// Cipher cipher = Cipher.getInstance("DES", "BC");
		Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

		// 用密匙初始化Cipher对象
		// cipher.init(Cipher.DECRYPT_MODE, key, sr );
		cipher.init(Cipher.DECRYPT_MODE, key);

		// System.out.printf("data=%02X %02X %02X %02X\n ", data[0], data[1],
		// data[2], data[3]);
		// 正式执行解密操作
		byte[] decryptedData = cipher.doFinal(data, off, len);

		// System.out.printf("enc=%02X %02X %02X %02X\n ", decryptedData[0],
		// decryptedData[1], decryptedData[2], decryptedData[3]);

		return decryptedData;
	}
}
