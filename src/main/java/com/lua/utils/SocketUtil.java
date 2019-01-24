package com.lua.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @ClassName:  SocketUtil   
 * @Description:SOCKET通讯工具类  
 * @author: yiwenhao
 * @date:   2018年7月20日 下午4:30:33   
 *     
 * @Copyright: 2018 www.xiongdi.cn. All rights reserved. 
 * 注意：本内容仅限于深圳市雄帝科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class SocketUtil {

	private int port = 0;//默认端口号
	private String host = "127.0.0.1";//默认IP
	private int timeout = 30;//默认超时时间，单位秒。
	private Socket client = null;//通讯句柄
	private DataOutputStream writer = null;//数据输出流对象
	private DataInputStream reader = null;//数据输入流对象

	/**
	 * 
	 * @Title:  SocketUtil   
	 * @Description:    构造函数，输入IP、端口和超时时间。
	 * @param:  @param host
	 * @param:  @param port
	 * @param:  @param timeout  
	 * @throws
	 */
	public SocketUtil(String host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}


	/**
	 * 
	 * @Title: SocketConnect   
	 * @Description: 打开通讯连接
	 * @param: @return      
	 * @return: boolean      
	 * @throws
	 */
	private boolean socketConnect() {
		boolean bo = false;
		try {
			if (client == null || client.isClosed()) {
				client = new Socket(host, port);
				client.setSoTimeout(timeout * 1000);
				writer = new DataOutputStream(client.getOutputStream());
				reader = new DataInputStream(client.getInputStream());
			}
			bo = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bo;
	}

	/**
	 * 
	 * @Title: SocketClose   
	 * @Description: 关闭通讯链接
	 * @param: @return      
	 * @return: boolean      
	 * @throws
	 */
	private boolean socketClose() {
		boolean bo = false;

		try {
			if (writer != null) {
				writer.close();
				writer = null;
			}
			if (reader != null) {
				reader.close();
				reader = null;
			}
			if (client != null && client.isConnected()) {
				client.close();
				client = null;
			}

			bo = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bo;
	}

	/**
	 * 
	 * @Title: SocketSendAndReseive   
	 * @Description: 数据发送和接收（同步）
	 * @param: @param sendbuff
	 * @param: @param len
	 * @param: @return      
	 * @return: byte[]      
	 * @throws
	 */
	public byte[] socketSendAndReseive(byte[] sendbuff, int len) {
		boolean bo = false;
		byte[] chars = null;
		
		try {
			bo = socketConnect();

			if (bo) {
				writer.write(sendbuff, 0, len);
				writer.flush();

				chars = new byte[1024 * 4];
				len = reader.read(chars);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			chars = null;
			e.printStackTrace();
		} finally {
			socketClose();
		}

		return chars;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SocketUtil sUtil = new SocketUtil("192.168.1.138", 8312, 30);

		String sendMsg = "Test, Test,打野 ";
		byte[] sendBuff = null;
		try {
			sendBuff = sendMsg.getBytes("gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] reseived = sUtil
				.socketSendAndReseive(sendBuff, sendMsg.length());
		if (reseived != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(new String(reseived, 0, reseived.length));
			System.out.println(sb.toString());
		} else {
			System.out.println("Test.............");
		}

	}

}
