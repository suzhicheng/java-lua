package com.lua.platform;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.lua.utils.ByteUtil;
import com.lua.utils.DesUtil;
import com.lua.utils.SocketUtil;

public class LuaLibrary extends TwoArgFunction {

	private static Log logger = LogFactory.getLog(LuaLibrary.class);

	@Override
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaValue library = tableOf();
		library.set("Transmit", new Transmit());
		library.set("Xor", new Xor());
		library.set("DesEnCrypt", new DesEnCrypt());
		library.set("DesDeCrypt", new DesDeCrypt());
		library.set("ANSI9x19MAC", new ANSI9x19MAC());
		env.set("Core", library);
		env.get("package").get("loaded").set("Core", library);
		
		
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		try {
			String lua= env.get("package").get("path").tojstring();
			String url= URLDecoder.decode(path,"UTF-8");
			url=url.substring(1, url.length());
			url=url+"?.lua;"+lua;
			env.get("package").set("path",url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String t=env.get("package").get("path").tojstring();
		System.out.println("package.path:"+t);
		return library;
	}

	static class Transmit extends ThreeArgFunction {

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			String addr = arg1.tojstring();
			int port = arg2.toint();
			String send = arg3.tojstring();

			logger.info("请求\tAddr:" + addr + " Port:" + port);
			logger.info("\t" + send);
			SocketUtil socket = new SocketUtil(addr, port, 30);

			send = send.replace(" ", "");

			int len = send.length();
			byte[] sendBuff = new byte[len + 2];
			sendBuff[0] = (byte) (len / 256);
			sendBuff[1] = (byte) (len % 256);
			byte[] ss = send.getBytes();
			System.arraycopy(ss, 0, sendBuff, 2, len);
			byte[] resvByteArr = socket.socketSendAndReseive(sendBuff, ((sendBuff[0] << 8) + sendBuff[1]) + 2);
			String result = ByteUtil.getStringToGBK(resvByteArr, 2);
			logger.info("响应\tAddr:" + addr + " Port:" + port);
			logger.info("\t" + result);

			return LuaValue.valueOf(result);
		}

	}

	static class Xor extends TwoArgFunction {

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2) {
			String data1 = arg1.tojstring();
			String data2 = arg2.tojstring();
			int len1 = data1.length();
			int len2 = data2.length();
			if (len1 != len2) {
				logger.error("入参数据长度不一样");
			}

			byte[] arr1 = new byte[len1 / 2];
			ByteUtil.hexStringToMulBytes(data1, arr1, 0);

			byte[] arr2 = new byte[len2 / 2];
			ByteUtil.hexStringToMulBytes(data2, arr2, 0);

			byte[] arr = new byte[len1 / 2];
			for (int i = 0; i < len1 / 2; i++) {
				arr[i] = (byte) (arr1[i] ^ arr2[i]);
			}
			String result = ByteUtil.byteToHexString(arr);
			return LuaValue.valueOf(result);
		}

	}

	static class DesEnCrypt extends ThreeArgFunction {

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			String data = arg1.tojstring();
			String key = arg2.tojstring();
			//String init = arg3.tojstring();
			String result = DesUtil.ecb2DesEn(data, key);
			return LuaValue.valueOf(result);
		}

	}

	static class DesDeCrypt extends ThreeArgFunction {

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			String data = arg1.tojstring();
			String key = arg2.tojstring();
			//String init = arg3.tojstring();
			String result = DesUtil.ecb2DesDe(data, key);
			return LuaValue.valueOf(result);
		}

	}

	static class ANSI9x19MAC extends ThreeArgFunction {

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			String data = arg1.tojstring();
			String key = arg2.tojstring();
			String vec = arg3.tojstring();
			String result = DesUtil.pboc3DesMac(vec, key, data);

			return LuaValue.valueOf(result);
		}

	}

}
