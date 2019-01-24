/*
字节工具类
 */

package com.lua.utils;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

/**
 * 
 * @author Administrator
 */
public class ByteUtil {
	/**
	 * @Description 字节转BCD码
	 * 
	 * @param
	 * 
	 * @return byte
	 * 
	 * @author xd
	 * @throws
	 * 
	 * @Date 2016-6-17
	 * 
	 */
	public static byte byteToBCD(int data) {
		data &= 0xFF;

		return (byte) (((data / 10) << 4) + (data % 10));
	}

	public static int bcdToINT(int data) {
		data &= 0xFF;

		return (((data >> 4) * 10) + (data & 0x0F));
	}

	public static String byteToHexString(byte[] data) {
		String out = "";
		try {
			for (int i = 0; i < data.length; i++) {
				String tmp = Integer.toHexString((int) data[i] & 0xFF);
				if (tmp.length() == 1) {
					tmp = "0" + tmp;
				}
				out = out + (tmp.toUpperCase() + " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public static String oneIntToString(int b, char c, int len) {
		String tmp = Integer.toString(b);
		int length = tmp.length();
		if (length < len) {
			for (int i = 0; i < (len - length); i++) {
				tmp = c + tmp;
			}
		}
		return tmp;
	}

	public static String byteToHexString(byte b) {

		String tmp = Integer.toHexString((int) b & 0xFF);
		if (tmp.length() == 1) {
			tmp = "0" + tmp;
		}
		return (tmp.toUpperCase());

	}

	public static int ascByteToInt(byte b) {
		int dat = b & 0x0F;
		if (b > '9') {
			dat += 9;
		}
		return dat;
	}

	public static String mulByteToHexString(byte[] arr, int offset, int len) {
		String tmp = "";
		for (int i = 0; i < len; i++) {
			tmp += byteToHexString(arr[offset + i]);
		}
		return tmp;
	}

	public static void hexStringToMulBytes(String str, byte[] arr, int offset) {
		int len = str.length() & 0xFFFFFFFE;

		byte[] pByte = str.getBytes();

		for (int i = 0; i < len; i += 2) {
			arr[offset++] = (byte) ((ascByteToInt(pByte[i]) << 4) + ascByteToInt(pByte[i + 1]));
		}
	}

	 
	 

	public static int byteToInt(byte b) {
		return (b & 0xFF);
	}

	public static int intToShort(int b) {
		return (b & 0xFFFF);
	}

	public static String dcdByteToString(byte b) {
		String tmp = "";
		int a = (b >> 4) & 0x0f;
		tmp = tmp + a;
		a = b & 0x0f;
		tmp = tmp + a;
		return (tmp.toUpperCase() + " ");
	}

	public static void memset(byte[] arr, int offset, byte ch, int len) {
		for (int i = 0; i < len; i++) {
			arr[offset + i] = ch;
		}
	}

	public static void memcpy(byte[] desArr, int desOffset, byte[] srcArr,
			int srcOffset, int len) {
		System.arraycopy(srcArr, srcOffset, desArr, desOffset, len);
	}

	public static byte[] cmemcpy(byte[] desArr, int desOffset, byte[] srcArr,
			int srcOffset, int len) {
		for (int i = 0; i < len; i++) {
			desArr[desOffset + len - i - 1] = srcArr[srcOffset + i];
		}
		return desArr;
	}

	public static byte[] memcopy(byte[] srcArr, int srcOffset, byte[] desArr,
			int desOffset, int len) {
		for (int i = 0; i < len; i++) {
			desArr[desOffset + i] = srcArr[srcOffset + i];
		}
		return desArr;
	}

	public static int memcmp(byte[] desArr, int desOffset, byte[] srcArr,
			int srcOffset, int len) {
		int ret = 0;
		for (int i = 0; i < len; i++) {
			if (((int) desArr[desOffset + i] & 0xFF) > ((int) srcArr[srcOffset
					+ i] & 0xFF)) {
				return 1;
			}
			if (((int) desArr[desOffset + i] & 0xFF) < ((int) srcArr[srcOffset
					+ i] & 0xFF)) {
				return -1;
			}
		}

		return ret;
	}

	public static byte getCRC(byte[] arr, int offset, int len) {
		byte crc = 0;

		for (int i = 0; i < len; i++) {
			crc += arr[offset + i];
		}
		crc = (byte) (-crc);

		return crc;
	}

	public static int getUINT4(byte ba[], int start) {
		int r = 0;
		r |= 0xff & ba[start];
		r <<= 8;
		r |= 0xff & ba[start + 1];
		r <<= 8;
		r |= 0xff & ba[start + 2];
		r <<= 8;
		r |= 0xff & ba[start + 3];
		return r;
	}

	public static void setUINT4(byte ba[], int start, int value) {
		ba[start] = (byte) (value >> 24 & 0xff);
		ba[start + 1] = (byte) (value >> 16 & 0xff);
		ba[start + 2] = (byte) (value >> 8 & 0xff);
		ba[start + 3] = (byte) (value & 0xff);
	}

	public static void setUSHORT4(byte ba[], int start, short value) {
		ba[start + 0] = (byte) (value >> 8 & 0xff);
		ba[start + 1] = (byte) (value & 0xff);
	}

	public static short getUSHORT4(byte ba[], int start) {
		short r = 0;
		r |= 0xff & ba[start];
		r <<= 8;
		r |= 0xff & ba[start + 1];
		return r;
	}

	public static void appen(byte[] rt, byte[] bodys, int start) {
		for (int i = 0; i < bodys.length; i++) {
			rt[start + i] = bodys[i];
		}
	}

 
	public static String getStringToGBK(byte[] bytes, int start) {
		byte[] rt = new byte[bytes.length - start];
		for (int i = 0; i < rt.length; i++) {
			rt[i] = bytes[i + start];
		}
		try {
			return new String(rt, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(rt);
		}
	}

	public static String formatBalance(int balance) {
		String str;

		if (balance < 0) {
			str = Integer.toString(-balance);
		} else {
			str = Integer.toString(balance);
		}

		StringBuffer sb = new StringBuffer(str);

		int len = sb.length();

		if (len >= 3) {
			sb.insert(len - 2, '.');
		} else if (len >= 2) {
			sb.insert(0, "0.");
		} else {
			sb.insert(0, "0.0");
		}

		if (balance < 0) {
			sb.insert(0, '-');
		}

		str = sb.toString();

		return str;
	}

	public static int lsbByteToShort(byte[] data, int offset) {
		return (((int) data[offset + 1] & 0xFF) << 8)
				+ ((int) data[offset + 0] & 0xFF);
	}

	public static int lsbByteToInteger(byte[] data, int offset) {
		return (((int) data[offset + 3] & 0xFF) << 24)
				+ (((int) data[offset + 2] & 0xFF) << 16)
				+ (((int) data[offset + 1] & 0xFF) << 8)
				+ ((int) data[offset + 0] & 0xFF);
	}

	public static int lsbByteToIntegerm(byte[] data, int offset) {
		return ((int) data[offset + 0] & 0xFF)
				+ (((int) data[offset + 1] & 0xFF) << 8)
				+ (((int) data[offset + 2] & 0xFF) << 16)
				+ (((int) data[offset + 3] & 0xFF) << 24);
	}

	public static int msbByteToShort(byte[] data, int offset) {
		return (((int) data[offset + 0] & 0xFF) << 8)
				+ ((int) data[offset + 1] & 0xFF);
	}

	public static int msbByteToInteger(byte[] data, int offset) {
		return (((int) data[offset + 0] & 0xFF) << 24)
				+ (((int) data[offset + 1] & 0xFF) << 16)
				+ (((int) data[offset + 2] & 0xFF) << 8)
				+ ((int) data[offset + 3] & 0xFF);
	}

	public static void getBCDTime(byte[] dst, int offset) {
		TimeZone mTz;
		Calendar mCalendar;
		mTz = TimeZone.getTimeZone("GMT+08:00");
		Date date = new Date();
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeZone(mTz);
		mCalendar.setTime(date);

		dst[offset++] = byteToBCD(mCalendar.get(Calendar.YEAR) / 100);
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.YEAR) % 100);
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.MONTH) + 1);
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.DAY_OF_MONTH));
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.HOUR_OF_DAY));
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.MINUTE));
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.SECOND));
	}

	public static void getPCABCDTime(byte[] dst, int offset) {
		TimeZone mTz;
		Calendar mCalendar;
		mTz = TimeZone.getTimeZone("GMT+08:00");
		Date date = new Date();
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeZone(mTz);
		mCalendar.setTime(date);

		dst[offset++] = byteToBCD(mCalendar.get(Calendar.DAY_OF_MONTH));
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.MONTH) + 1);
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.YEAR) / 100);
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.YEAR) % 100);
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.HOUR_OF_DAY));
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.MINUTE));
		dst[offset++] = byteToBCD(mCalendar.get(Calendar.SECOND));
		// dst[offset++] = (byte)(mCalendar.get(Calendar.DAY_OF_MONTH)&0xFF);
		// dst[offset++] = (byte)((mCalendar.get(Calendar.MONTH)+1)&0xFF);
		// dst[offset++] = (byte)((mCalendar.get(Calendar.YEAR)/100)&0xFF);
		// dst[offset++] = (byte)((mCalendar.get(Calendar.YEAR)%100)&0xFF);
		// dst[offset++] = (byte)(mCalendar.get(Calendar.HOUR_OF_DAY)&0xFF);
		// dst[offset++] = (byte)(mCalendar.get(Calendar.MINUTE)&0xFF);
		// dst[offset++] = (byte)(mCalendar.get(Calendar.SECOND)&0xFF);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void getMCMTime(byte[] dst, int offset, Vector v) {
		TimeZone mTz;
		Calendar mCalendar;
		mTz = TimeZone.getTimeZone("GMT+08:00");
		Date date = new Date();
		mCalendar = Calendar.getInstance();
		mCalendar.setTimeZone(mTz);
		mCalendar.setTime(date);
		int yy = mCalendar.get(Calendar.YEAR);
		int mm = mCalendar.get(Calendar.MONTH) + 1;
		int dd = mCalendar.get(Calendar.DAY_OF_MONTH);
		int hh = mCalendar.get(Calendar.HOUR_OF_DAY);
		int ff = mCalendar.get(Calendar.MINUTE);
		int ss = mCalendar.get(Calendar.SECOND);

		dst[offset++] = byteToBCD(yy / 100);
		dst[offset++] = byteToBCD(yy % 100);
		dst[offset++] = byteToBCD(mm);
		dst[offset++] = byteToBCD(dd);
		dst[offset++] = byteToBCD(hh);
		dst[offset++] = byteToBCD(ff);
		dst[offset++] = byteToBCD(ss);

		String syy = String.valueOf(yy);
		String smm = String.valueOf(mm);
		String sdd = String.valueOf(dd);
		String shh = String.valueOf(hh);
		String sff = String.valueOf(ff);
		String sss = String.valueOf(ss);
		if (mm < 10) {
			smm = "0" + smm;
		}
		if (dd < 10) {
			sdd = "0" + sdd;
		}
		if (hh < 10) {
			shh = "0" + hh;
		}
		if (ff < 10) {
			sff = "0" + sff;
		}
		if (ss < 10) {
			sss = "0" + sss;
		}
		String time = syy + smm + sdd + shh + sff + sss;
		if (v.size() > 0) {
			v.setElementAt(time, 0);
		} else {
			v.addElement(time);
		}
	}

	public static void integerToLsbByte(byte[] dst, int offset, int data) {
		dst[offset++] = (byte) (data);
		dst[offset++] = (byte) (data >> 8);
		dst[offset++] = (byte) (data >> 16);
		dst[offset++] = (byte) (data >> 24);
	}

	public static String byteToString(byte[] src, int offset, int len) {
		String str = "";

		while (len-- != 0) {
			String tmp = Integer.toHexString(src[offset++] & 0xFF);
			if (tmp.length() == 1) {
				tmp = "0" + tmp;
			}
			str += tmp;
		}

		return str;
	}

	public static String mcmTimeToString(byte[] byMCMTime, int offset) {
		int iMCMTime = lsbByteToInteger(byMCMTime, offset);
		byte[] time = new byte[7];

		time[6] = (byte) byteToBCD(iMCMTime & 0x3F);
		time[5] = (byte) byteToBCD((iMCMTime >> 6) & 0x3F);
		time[4] = (byte) byteToBCD((iMCMTime >> 12) & 0x1F);
		time[3] = (byte) byteToBCD((iMCMTime >> 17) & 0x1F);
		time[2] = (byte) byteToBCD((iMCMTime >> 22) & 0x0F);
		time[1] = (byte) ((iMCMTime >> 24) & 0x3F);
		if (iMCMTime < 0) {
			time[1] |= 0x20;
		}
		time[1] = (byte) byteToBCD((iMCMTime >> 24) & 0x3F);
		time[0] = 0x20;

		return byteToString(time, 0, 7);
	}

	public static String slent(String str, int len) {
		str = str + "";
		String stringbuffer = "";
		String zero = "";

		int strlen = str.length();
		if (strlen == len) {
			stringbuffer = str;
		}

		if (strlen > len) {
			stringbuffer = str.substring(strlen - len, strlen);
		}

		if (strlen < len) {
			for (int i = 0; i < len - strlen; i++) {
				zero = zero + "0";
			}
			stringbuffer = zero + str;
		}
		return stringbuffer;
	}

	public static int getUINT4(byte ba[], int start, boolean flag) {
		int r = 0;

		if (flag)// 大端模式 00 00 00 64 = 100
		{
			r |= 0xff & ba[start + 0];
			r <<= 8;
			r |= 0xff & ba[start + 1];
			r <<= 8;
			r |= 0xff & ba[start + 2];
			r <<= 8;
			r |= 0xff & ba[start + 3];
		} else// 小端模式 64 00 00 00 = 100
		{
			r |= 0xff & ba[start + 3];
			r <<= 8;
			r |= 0xff & ba[start + 2];
			r <<= 8;
			r |= 0xff & ba[start + 1];
			r <<= 8;
			r |= 0xff & ba[start + 0];
		}
		return r;
	}

	public static int getInt(String str, int startOffset, int offset) {
		int ret = Integer.parseInt(str.substring(startOffset, offset));
		return ret;
	}

	public static void setUINT4(byte ba[], int start, int value, boolean flag) {
		if (flag)// 大端模式 00 00 00 64 = 100
		{
			ba[start + 0] = (byte) (value >> 24 & 0xff);
			ba[start + 1] = (byte) (value >> 16 & 0xff);
			ba[start + 2] = (byte) (value >> 8 & 0xff);
			ba[start + 3] = (byte) (value & 0xff);
		} else // 小端模式 64 00 00 00 = 100
		{
			ba[start + 3] = (byte) (value >> 24 & 0xff);
			ba[start + 2] = (byte) (value >> 16 & 0xff);
			ba[start + 1] = (byte) (value >> 8 & 0xff);
			ba[start + 0] = (byte) (value & 0xff);
		}
	}

	// 有修改

	public static String makeupZero(String str, int lent) {
		int strLent = str.length();
		if (strLent >= lent) {
			return str.substring(strLent - lent);
		} else {
			/*
			 * String ret = ""; for(int i=0;i<(lent-strLent);i++) { ret="0"+ret;
			 * } return ret+str;
			 */
			String ret = "0000000000" + str;
			return ret.substring(10 + strLent - lent);

		}
	}

	public static String getInputString(int keyCode) {
		String str = "";
		switch (keyCode) {
		case 48:
			str = "0";
			break;
		case 49:
			str = "1";
			break;
		case 50:
			str = "2";
			break;
		case 51:
			str = "3";
			break;
		case 52:
			str = "4";
			break;
		case 53:
			str = "5";
			break;
		case 54:
			str = "6";
			break;
		case 55:
			str = "7";
			break;
		case 56:
			str = "8";
			break;
		case 57:
			str = "9";
			break;
		default:
			str = "";
		}
		return str;
	}

	// 将BCD转换为十进制数

	public static int bcdToBin(byte bcd) {
		return bcd / 16 * 10 + bcd % 16;
	}

	public static String reverseHexString(String hexStr) {
		int strLen = hexStr.length()/2;
		byte [] bytes = new byte[strLen];
		
		hexStringToMulBytes(hexStr, bytes, 0);
		byte temp = 0;
		for(int i=0;i<bytes.length;i++){
            temp = bytes[i];
            bytes[i] = (byte) (~temp);
        }
		
		return mulByteToHexString(bytes, 0, strLen);

	}
	
	public static void getMCMTime(byte[] strTM, byte[] outTM) {
		byte[] bcdTM = new byte[8];
		for (int i = 0; i < 7; i++) {
			bcdTM[i] = (byte) (((strTM[i * 2] - '0') << 4) + (strTM[i * 2 + 1] - '0'));
		}

		int ttt = bcdToBin(bcdTM[1]) << 26;
		ttt |= (bcdToBin(bcdTM[2]) << 22);
		ttt |= (bcdToBin(bcdTM[3]) << 17);
		ttt |= (bcdToBin(bcdTM[4]) << 12);
		ttt |= (bcdToBin(bcdTM[5]) << 6);
		ttt |= bcdToBin(bcdTM[6]);
		integerToLsbByte(outTM, 0, ttt);

	}

	public static String mcmTMtoString(byte[] data, int offset) {
		int itm = ByteUtil.lsbByteToInteger(data, offset);
		int yy = ((itm >> 26) & 0x3f) + 2000;
		int mm = ((itm >> 22) & 0xf);
		int dd = ((itm >> 17) & 0x1f);
		int hh = ((itm >> 12) & 0x1f);
		int ff = ((itm >> 6) & 0x3f);
		int m = (itm & 0x3f);
		StringBuffer sb = new StringBuffer();
		sb.append(yy);
		sb.append(mm);
		sb.append(dd);
		sb.append(hh);
		sb.append(ff);
		sb.append(m);
		return sb.toString();
	}

	public static String calCrc16(byte data[], int start, int len) {
		int temp = 25443;
		byte[] crcData = new byte[2];
		for (int i = 0; i < len; i++) {
			temp ^= data[start + i] & -1;
			for (int j = 0; j < 8; j++) {
				if ((temp & 1) != 0) {
					temp = temp >> 1 ^ 0x8408;
				}else {
					temp >>= 1;
				}
			}
		}

		crcData[0] = (byte) (temp & -1);
		crcData[1] = (byte) (temp >> 8 & -1);
		return ByteUtil.makeupZero(
				String.valueOf(ByteUtil.lsbByteToShort(crcData, 0)), 5);
		// return
		// ByteTool.makeupZero(String.valueOf(ByteTool.oneByteToInt(crcData[1])+ByteTool.oneByteToInt(crcData[0])),5);
	}

 

	private static int[] crc16_ccitt_table = // [256]
	{ 0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf, 0x8c48,
			0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7, 0x1081,
			0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e, 0x9cc9,
			0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876, 0x2102,
			0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd, 0xad4a,
			0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5, 0x3183,
			0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c, 0xbdcb,
			0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974, 0x4204,
			0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb, 0xce4c,
			0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3, 0x5285,
			0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a, 0xdecd,
			0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72, 0x6306,
			0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9, 0xef4e,
			0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1, 0x7387,
			0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738, 0xffcf,
			0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70, 0x8408,
			0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7, 0x0840,
			0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff, 0x9489,
			0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036, 0x18c1,
			0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e, 0xa50a,
			0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5, 0x2942,
			0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd, 0xb58b,
			0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134, 0x39c3,
			0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c, 0xc60c,
			0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3, 0x4a44,
			0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb, 0xd68d,
			0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232, 0x5ac5,
			0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a, 0xe70e,
			0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1, 0x6b46,
			0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9, 0xf78f,
			0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330, 0x7bc7,
			0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78 };

	public static String crc16Ccitt(String data) {
		char[] ch = data.toCharArray();
		int crcReg = 0;
		int i = 0;
		try {
			for (i = 0; i < ch.length; i++) {
				crcReg = (crcReg >> 8)
						^ crc16_ccitt_table[(crcReg ^ ((byte) ch[i])) & 0xff]; // (crcReg^((byte)ch[i]))&0xff
			}
		} catch (Exception e) {
			System.out.println("ccitt Exception: " + e + ",  i=" + i);
		}
		return oneIntToString(crcReg, '0', 5);
	}
	
	
	public static String bytesToHexString(byte[] src) {
 		StringBuilder stringBuilder = new StringBuilder("");
 		if (src == null || src.length <= 0) {
 			return null;
 		}
 		for (int i = 0; i < src.length; i++) {
 			int v = src[i] & 0xFF;
 			String hv = Integer.toHexString(v);
 			if (hv.length() < 2) {
 				stringBuilder.append(0);
 			}
 			stringBuilder.append(hv);
 		}
 		return stringBuilder.toString();
 	}
  
 	public static byte[] hexStringToBytes(String hexString) {
 		if (hexString == null || hexString.equals("")) {
 			return null;
 		}
 		hexString = hexString.toUpperCase();
 		int length = hexString.length() / 2;
 		char[] hexChars = hexString.toCharArray();
 		byte[] d = new byte[length];
 		for (int i = 0; i < length; i++) {
 			int pos = i * 2;
 			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
 		}
 		return d;
 	}
 
 	private static byte charToByte(char c) {
 		return (byte) "0123456789ABCDEF".indexOf(c);
 	}

}