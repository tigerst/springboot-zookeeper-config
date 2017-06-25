package com.tiger.dubbo.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

public class SCPUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String DEFAULT_MODEL = "0755"; // 文件默认权限

	private Connection connection; // ssh2连接对象

	private SCPClient scpClient; // scp客户端对象

	private String host; // 主机ip

	private int port; // 端口号

	private String userName; // 用户名

	private String password; // 密码

	public SCPUtil(String host, int port, String userName, String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * 功能：获取连接
	 * 
	 * @throws Exception
	 */
	private void getConnection() throws Exception {
		// 连接
		connection = new Connection(host, port);
		connection.connect();
		// 远程服务器的用户名密码
		boolean isAuthed = connection.authenticateWithPassword(userName, password);
		if (isAuthed != true)
			throw new Exception(userName + " log in failed");
		scpClient = connection.createSCPClient();
	}

	/**
	 * 功能：上传文件
	 * 
	 * @param localFile
	 *            本地文件
	 * @param remoteTargetDirectory
	 *            远程目录
	 * @param model
	 *            权限
	 * @return 返回处理结果
	 */
	public boolean upload(String localFile, String remoteTargetDirectory, String model) {
		try {
			if (scpClient == null)
				getConnection();
			scpClient.put(localFile, remoteTargetDirectory,
					(model != null && !"".equalsIgnoreCase(model)) ? model : DEFAULT_MODEL);
			return true;
		} catch (Exception e) {
			logger.error("上传文件异常", e);
			return false;
		}
	}

	/**
	 * 功能：批量上传文件
	 * 
	 * @param localFileList
	 *            本地文件列表
	 * @param remoteTargetDirectory
	 *            远程目录
	 * @param model
	 *            权限
	 * @return 返回处理结果。
	 */
	public boolean upload(List<String> localFileList, String remoteTargetDirectory, String model) {
		try {
			if (scpClient == null)
				getConnection();
			scpClient.put(localFileList.toArray(new String[] {}), remoteTargetDirectory,
					(model != null && !"".equalsIgnoreCase(model)) ? model : DEFAULT_MODEL);
			return true;
		} catch (Exception e) {
			logger.error("上传文件异常", e);
			return false;
		}
	}

	/**
	 * 功能：下载文件
	 * 
	 * @param remoteFile
	 *            远程文件
	 * @param localTargetDirectory
	 *            本地目录
	 * @return 返回处理结果
	 */
	public boolean download(String remoteFile, String localTargetDirectory) {
		try {
			if (scpClient == null)
				getConnection();
			scpClient.get(remoteFile, localTargetDirectory);
			return true;
		} catch (Exception e) {
			logger.error("下载文件异常", e);
			return false;
		}
	}

	/**
	 * 功能：下载文件
	 * 
	 * @param remoteFile
	 *            远程文件名
	 * @return 返回文件内容
	 * @throws Exception
	 */
	public String download(String remoteFile) throws Exception {
		if (scpClient == null)
			getConnection();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		scpClient.get(remoteFile, baos);
		baos.flush();
		String result = baos.toString();
		baos.close();
		return result;
	}

	/**
	 * 功能：下载多个文件，失败时将已下载的本地文件删除
	 * 
	 * @param remoteFileList
	 *            远程文件名列表
	 * @param localTargetDirectory
	 *            本地目录
	 * @return 返回处理结果。
	 */
	public boolean download(List<String> remoteFileList, String localTargetDirectory) {
		try {
			if (scpClient == null)
				getConnection();
			scpClient.get(remoteFileList.toArray(new String[] {}), localTargetDirectory);
			return true;
		} catch (Exception e) {
			logger.error("下载文件异常", e);
			try {
				File file = null;
				for (String tempfilePath : remoteFileList) {
					file = new File(localTargetDirectory + Constants.PATH_SPLITER
							+ tempfilePath.substring(tempfilePath.lastIndexOf(Constants.PATH_SPLITER) + 1));
					file.delete();
				}
			} catch (Exception e1) {

			}
			return false;
		}
	}
	
	/**
	 * 功能：下载多个文件，失败时将已下载的本地文件删除
	 * 
	 * @param remoteFileList
	 *            远程文件名列表
	 * @param localTargetDirectory
	 *            本地目录
	 * @return 返回处理结果。
	 */
	public List<String> downloadWithReturn(List<String> remoteFileList, String localTargetDirectory) {
		try {
			List<String> localFileList = new ArrayList<String>();
			if (scpClient == null)
				getConnection();
			for (String remoteFile : remoteFileList)	//生成本地文件集
				localFileList.add(localTargetDirectory + Constants.PATH_SPLITER + remoteFile.substring(remoteFile.lastIndexOf(Constants.PATH_SPLITER) + 1));
			scpClient.get(remoteFileList.toArray(new String[] {}), localTargetDirectory);	//复制到本地文件
			return localFileList;
		} catch (Exception e) {
			logger.error("下载文件异常", e);
			try {
				File file = null;
				if(remoteFileList != null && remoteFileList.size() > 0)
					for (String tempfilePath : remoteFileList) {
						file = new File(tempfilePath);
						file.delete();
					}
			} catch (Exception e1) {

			}
			return null;
		}
	}

	/**
	 * 功能：关闭资源
	 */
	public void closeSource() {
		if (scpClient != null)
			scpClient = null;
		if (connection != null)
			connection.close();
	}

	public static void main(String[] args) throws Exception {
		SCPUtil scpUtil = new SCPUtil("192.168.126.132", 22, "tiger", "111");
		scpUtil.upload("D:/opt/lkl-statement-2016-10.xlsx", "/opt", null);
		scpUtil.download("/opt/lkl-statement-2016-10.xlsx", "D:/tmp");
		// String str = scpUtil.download("/opt/approveList.jsp");
		scpUtil.closeSource();
		// System.out.println(str);
	}
}
