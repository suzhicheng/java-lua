package com.lua.utils;


import java.security.GeneralSecurityException;
 
/** 
 * 
 * 为了检查通讯报文是否被篡改，常需要在报文中加上一个MAC（Message Authentication Code，报文校验码）。
 * 
 * 在 JDK 1.4里，已包含一个 Mac 类（javax.crypto.Mac），可以生成MAC。 但它是参照HMAC（Hash-based Message Authentication Code，基于散列的消息验证代码）实现的。 有时，需要采用ANSI-X9.9算法计算MAC。
 * 
 * 1. 算法描述 参与ANSI X9.9 MAC计算的数据主要由三部分产生：初始数据、原始数据、补位数据。 
 * 1) 算法定义：采用DEC CBC（zeroICV）或ECB算法 
 * 2) 初始数据：0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 
 * 3) 原始数据: 
 * 4) * 补位数据：若原始数据不是8的倍数,则右补齐0x00；若原始数据位8的整数倍，则不用补齐0x00。
 * 5) 密钥: MAC密钥
 * 
 * MAC的产生由以下方式完成：(最后一组数据长度若不足8的倍数，则右补齐0x00；若数据长度为8的整数倍，则无需补充0x00) 
 * 初始数据 BLOCK #1 BLOCK #2 BLOCK #3 ... BLOCK #N | | | | | +-----> XOR +---> XOR +---> XOR +---> XOR | | | | | | | DES
 * ---+ DES ---+ DES ---+ DES ---> MAC | | | | KEY KEY KEY KEY
 * 
 * 返回 -- 加密后的缓冲区*/

public class ANSIMacUtils {
	/**
	 * ANSI X9.9MAC算法  <br/>
	 * (1) ANSI X9.9MAC算法只使用单倍长密钥。  <br/>
	 * (2)  MAC数据先按8字节分组，表示为D0～Dn，如果Dn不足8字节时，尾部以字节00补齐。 <br/>
	 * (3) 用MAC密钥加密D0，加密结果与D1异或作为下一次的输入。 <br/>
	 * (4) 将上一步的加密结果与下一分组异或，然后再用MAC密钥加密。<br/>
	 * (5) 直至所有分组结束，取最后结果的左半部作为MAC。<br/>
	 * 采用x9.9算法计算MAC (Count MAC by ANSI-x9.9).
	 * 
	 * @param key  8字节密钥数据
	 * @param data 待计算的缓冲区
	 * @throws GeneralSecurityException 
	 */
	public static byte[] calculateAnsix9Mac(byte[] key, byte[] data) throws GeneralSecurityException {
		
		final int dataLength = data.length;
		final int lastLength = dataLength % 8;
		final int lastBlockLength = lastLength == 0 ? 8 : lastLength;
		final int blockCount = dataLength / 8 + (lastLength > 0 ? 1 : 0);
		
		// 拆分数据（8字节块/Block）
		byte[][] dataBlock = new byte[blockCount][8];
		for (int i = 0; i < blockCount; i++) {
			int copyLength = i == blockCount - 1 ? lastBlockLength : 8;
			System.arraycopy(data, i * 8, dataBlock[i], 0, copyLength);
		}
		
		byte[] desXor = new byte[8];
		for (int i = 0; i < blockCount; i++) {
			byte[] tXor = DesUtils.xOr(desXor, dataBlock[i]);
			desXor = DesUtils.encryptByDesEcb(tXor, key); // DES加密
		}
		return desXor;
	}
	
	/**
	 * 采用ANSI x9.19算法计算MAC (Count MAC by ANSI-x9.19).<br/>
	 * 将ANSI X9.9的结果做如下计算<br/>
	 * (6) 用MAC密钥右半部解密(5)的结果。 <br/>
	 * (7) 用MAC密钥左半部加密(6)的结果。<br/>
	 * (8) 取(7)的结果的左半部作为MAC。<br/>
	 * @param key  16字节密钥数据
	 * @param data 待计算的缓冲区
	 * @throws GeneralSecurityException 
	 */
	public static byte[] calculateAnsix919Mac(byte[] key, byte[] data) throws GeneralSecurityException {
		if (key == null || data == null) {
			return null;
		}
		if (key.length != 16) {
			throw new RuntimeException("秘钥长度错误.");
		}
		
		byte[] keyLeft = new byte[8];
		byte[] keyRight = new byte[8];
		System.arraycopy(key, 0, keyLeft, 0, 8);
		System.arraycopy(key, 8, keyRight, 0, 8);
		
		byte[] result99 = calculateAnsix9Mac(keyLeft, data);
		
		byte[] resultTemp = DesUtils.decryptByDesEcb(result99, keyRight);
		return DesUtils.encryptByDesEcb(resultTemp, keyLeft);
	}
	
	public static final byte[] ZERO_IVC = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
	/**
	 * 计算MAC(hex) PBOC_3DES_MAC(符合ISO9797Alg3Mac标准)
	 * (16的整数补8000000000000000) 前n-1组使用单长密钥DES 使用密钥是密钥的左8字节） 最后1组使用双长密钥3DES （使用全部16字节密钥）
	 * 
	 * 算法步骤：初始数据为D，初始向量为I，3DES秘钥为K0，秘钥低8字节DES秘钥K1；
	 * 1、数据D分组并且填充：将字节数组D进行分组，每组8个字节，分组编号从0开始,分别为D0...Dn；最后一个分组不满8字节的，先填充一个字节80，后续全部填充00，满8字节的，新增一个8字节分组（80000000 00000000）；
	 * 2、进行des循环加密：（1）D0和初始向量I进行按位异或得到结果O0;(2)使用秘钥K1，DES加密结果O0得到结果I1,将I1和D1按位异或得到结果D1；(3)循环第二步骤得到结果Dn；
	 * 3、将Dn使用16字节秘钥K0进行3DES加密，得到的结果就是我们要的MAC。
	 * @param data 带计算的数据
	 * @param key 16字节密钥
	 * @param icv 算法向量
	 * @return mac签名
	 * @throws Exception
	 */
	public static byte[] calculatePboc3desMAC(byte[] data, byte[] key, byte[] icv) throws Exception {
		
		if (key == null || data == null) {
			throw new RuntimeException("data or key is null.");
		}
		if(key.length != 16) {
			throw new RuntimeException("key length is not 16 byte.");
		}
		
		byte[] leftKey = new byte[8];
		System.arraycopy(key, 0, leftKey, 0, 8);
		
		// 拆分数据（8字节块/Block）
		final int dataLength = data.length;
		final int blockCount = dataLength / 8 + 1;
		final int lastBlockLength = dataLength % 8;
		
		byte[][] dataBlock = new byte[blockCount][8];
		for (int i = 0; i < blockCount; i++) {
			int copyLength = i == blockCount - 1 ? lastBlockLength : 8;
			System.arraycopy(data, i * 8, dataBlock[i], 0, copyLength);
		}
		dataBlock[blockCount - 1][lastBlockLength] = (byte) 0x80;
		
		byte[] desXor = DesUtils.xOr(dataBlock[0], icv);
		for (int i = 1; i < blockCount; i++) {
			byte[] des = DesUtils.encryptByDesCbc(desXor, leftKey);
			desXor = DesUtils.xOr(dataBlock[i], des);
		}
		desXor = DesUtils.encryptBy3DesCbc(desXor, key);
		return desXor;
	}

 

}
