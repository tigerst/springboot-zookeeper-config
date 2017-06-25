package com.tiger.dubbo.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String PATH_SPLITER = "/"; // 路径分隔符

	private FTPClient ftpClient; // ftp客户端连接对象

	private String host; // 主机ip

	private int port; // 端口号

	private String userName; // 用户名

	private String password; // 密码

	private boolean passiveMode = false; // 被动模式标记，默认是主动。true时是被动模式,false时是主动动模式

	public FTPUtil(String host, int port, String userName, String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public FTPUtil(String host, int port, String userName, String password, boolean passiveMode) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.passiveMode = passiveMode;
	}

	/**
	 * 功能：获取ftp连接
	 * @throws Exception
	 */
	private void getConnection() throws Exception {
		ftpClient = new FTPClient();
		ftpClient.connect(host, port);// 连接FTP服务器
		if (passiveMode)
			ftpClient.enterLocalPassiveMode(); // 被动模式
		else
			ftpClient.enterLocalActiveMode(); // 主动模式
		if (!ftpClient.login(userName, password))
			throw new Exception(userName + " log in failed");
	}

	/**
	 * 功能：上传文件
	 * @param localFile	本地文件
	 * @param remoteTargetDirectory	远程目录
	 * @return	返回处理结果
	 * @throws Exception
	 */
	public boolean upload(String localFile, String remoteTargetDirectory) throws Exception {
		FileInputStream input = null;
		boolean successFlag = true;
		try {
			if (ftpClient == null)
				getConnection();
			String remoteFile = localFile.substring(localFile.lastIndexOf(PATH_SPLITER) + 1); // 获取带后缀名的文件名
			input = new FileInputStream(new File(localFile));
			remoteTargetDirectory = remoteTargetDirectory.endsWith(PATH_SPLITER) ? remoteTargetDirectory
					: (remoteTargetDirectory + PATH_SPLITER); // 目录处理
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置二进制传输方式
			if (successFlag)
				successFlag = ftpClient.storeFile(remoteTargetDirectory + remoteFile, input);
			return successFlag;
		} catch (Exception e) {
			logger.error("ftp上传文件异常", e);
			return false;
		} finally {
			if(input != null)
				input.close();
		}
	}

	/**
	 * 功能：批量上传文件
	 * @param localFileList	本地文件列表
	 * @param remoteTargetDirectory	远程文件目录
	 * @return	返回处理结果，只有成功和失败。部分成功也为失败
	 * @throws Exception
	 */
	public boolean upload(List<String> localFileList, String remoteTargetDirectory) throws Exception {
		FileInputStream input = null;
		try {
			boolean successFlag = true;
			if (ftpClient == null)
				getConnection();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置二进制传输方式
			remoteTargetDirectory = remoteTargetDirectory.endsWith(PATH_SPLITER) ? remoteTargetDirectory
					: (remoteTargetDirectory + PATH_SPLITER); // 目录处理
			for (String localFile : localFileList) {
				String remoteFile = localFile.substring(localFile.lastIndexOf(PATH_SPLITER) + 1); // 获取带后缀名的文件名
				input = new FileInputStream(new File(localFile));
				if (!successFlag)
					break;
				successFlag = ftpClient.storeFile(remoteTargetDirectory + remoteFile, input);
				input.close();
			}
			return successFlag;
		} catch (Exception e) {
			logger.error("ftp上传文件异常", e);
			return false;
		} finally {
			if(input != null)
				input.close();
		}
	}

	/**
	 * 功能：批量上传文件，记录上传失败文件
	 * 
	 * @param localFileList
	 *            本地文件列表
	 * @param remoteTargetDirectory
	 *            远程文件目录
	 * @return 返回失败文件列表
	 * @throws Exception
	 */
	public List<String> uploadWithFailure(List<String> localFileList, String remoteTargetDirectory) throws Exception {
		List<String> failureList = new ArrayList<String>();
		FileInputStream input = null;
		try {
			if (ftpClient == null)
				getConnection();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置二进制传输方式
			remoteTargetDirectory = remoteTargetDirectory.endsWith(PATH_SPLITER) ? remoteTargetDirectory
					: (remoteTargetDirectory + PATH_SPLITER); // 目录处理
			String remoteFile = null; // 带后缀名的文件名
			for (String localFile : localFileList) {
				try {
					remoteFile = localFile.substring(localFile.lastIndexOf(PATH_SPLITER) + 1); // 获取带后缀名的文件名
					input = new FileInputStream(new File(localFile));
					if (!ftpClient.storeFile(remoteTargetDirectory + remoteFile, input))
						failureList.add(remoteFile);
				} catch (Exception e) {
					failureList.add(remoteFile);
				} finally {	//关闭资源
					if(input!=null)
						input.close();
				}
			}
			return failureList;
		} catch (Exception e) {
			logger.error("ftp上传文件异常", e);
			return localFileList;
		}
	}

	/**
	 * 功能：下载文件
	 * 
	 * @param remoteFile
	 *            远程文件名
	 * @param localTargetDirectory
	 *            本地保存目录
	 * @return	返回处理结果
	 */
	public boolean download(String remoteFile, String localTargetDirectory) {
		OutputStream os = null;
		try {
			if (ftpClient == null)
				getConnection();
			String localFile = remoteFile.substring(remoteFile.lastIndexOf(PATH_SPLITER) + 1); // 获取带后缀名的文件名
			localTargetDirectory = localTargetDirectory.endsWith(PATH_SPLITER) ? localTargetDirectory
					: (localTargetDirectory + PATH_SPLITER); // 目录处理
			os = new FileOutputStream(new File(localTargetDirectory + localFile));
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置二进制传输方式
			boolean successFlag = ftpClient.retrieveFile(remoteFile, os);
			os.flush();
			os.close();
			return successFlag;
		} catch (Exception e) {
			logger.error("ftp下载文件异常", e);
			return false;
		} finally {
			os = null;
		}
	}

	/**
	 * 功能：下载多个文件
	 * 
	 * @param remoteFileList
	 *            远程文件名列表
	 * @param localTargetDirectory
	 *            本地保存目录
	 * @return 返回处理结果，只有成功和失败。部分成功也为失败
	 */
	public boolean download(List<String> remoteFileList, String localTargetDirectory) {
		OutputStream os = null;
		try {
			boolean successFlag = true;
			if (ftpClient == null)
				getConnection();
			localTargetDirectory = localTargetDirectory.endsWith(PATH_SPLITER) ? localTargetDirectory
					: (localTargetDirectory + PATH_SPLITER); // 目录处理
			for (String remoteFile : remoteFileList) {
				String localFile = remoteFile.substring(remoteFile.lastIndexOf(PATH_SPLITER) + 1); // 获取带后缀名的文件名
				os = new FileOutputStream(new File(localTargetDirectory + localFile));
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置二进制传输方式
				if (!successFlag) // 失败时退出
					break;
				ftpClient.retrieveFile(remoteFile, os);
				os.flush();
				os.close();
			}
			return successFlag;
		} catch (Exception e) {
			logger.error("ftp下载文件异常", e);
			return false;
		} finally {
			if(os!=null)
				os = null;
		}
	}

	/**
	 * 功能：下载多个文件
	 * 
	 * @param remoteFileList
	 *            远程文件名列表
	 * @param localTargetDirectory
	 *            本地保存目录
	 * @return 返回失败文件列表
	 */
	public boolean downloadWithFailure(List<String> remoteFileList, String localTargetDirectory) {
		List<String> failureList = new ArrayList<String>();
		OutputStream os = null;
		try {
			boolean successFlag = true;
			if (ftpClient == null)
				getConnection();
			localTargetDirectory = localTargetDirectory.endsWith(PATH_SPLITER) ? localTargetDirectory
					: (localTargetDirectory + PATH_SPLITER); // 目录处理
			String localFile = null;
			for (String remoteFile : remoteFileList) {
				try {
					localFile = remoteFile.substring(remoteFile.lastIndexOf(PATH_SPLITER) + 1); // 获取带后缀名的文件名
					os = new FileOutputStream(new File(localTargetDirectory + localFile));
					ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置二进制传输方式
					if (!ftpClient.retrieveFile(remoteFile, os)) // 失败时退出
						failureList.add(localFile);
				} catch (Exception e) {
					failureList.add(localFile);
				} finally {
					os.flush();
					os.close();
				}
			}
			return successFlag;
		} catch (Exception e) {
			logger.error("ftp下载文件异常", e);
			return false;
		} finally {
			if(os!=null)
				os = null;
		}
	}

	/**
	 *  功能：下载文件
	 * @param remoteFile	远程文件名
	 * @return	返回文件内容
	 * @throws Exception
	 */
	public String download(String remoteFile) throws Exception {
		if (ftpClient == null)
			getConnection();
		InputStream stream = ftpClient.retrieveFileStream(remoteFile);
		if (stream != null) {
			BufferedInputStream buf = new BufferedInputStream(stream);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i;
			while ((i = buf.read()) != -1) {
				baos.write(i);
			}
			baos.flush();
			String restult = baos.toString();
			baos.close();
			buf.close();
			stream.close();
			return restult;
		}
		return null;
	}

	/**
	 * 功能：创建目录
	 * @param remoteDir	远程目录
	 * @return	返回处理结果
	 * @throws IOException
	 */
	public boolean createDir(String remoteDir) throws IOException{
		return ftpClient.makeDirectory(remoteDir);
	}
	
	/**
	 * 功能：关闭资源
	 */
	public void closeSource() {
		if (ftpClient != null) {
			try {
				ftpClient.logout();
			} catch (IOException e) {
				logger.error("ftp注销异常", e);
				ftpClient = null;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// FTPUtil ftpUtil = new FTPUtil("192.168.126.128", 21, "tiger", "111");
//		FTPUtil ftpUtil = new FTPUtil("192.168.126.132", 21, "tiger", "111");
		FTPUtil ftpUtil = new FTPUtil("10.7.111.222", 21, "admin", "123_lakala");
		// ftpUtil.uploadMultiFiles(localFileList, "/opt");
		ftpUtil.upload("D:/opt/lkl-statement-2016-10.xlsx", "/opt");
		// ftpUtil.download("/opt/lkl-statement-2016-10.xlsx", "D:/tmp");
		// System.out.println(ftpUtil.download("/opt/approveList.jsp",
		// "D:/tmp"));
//		 String str = ftpUtil.download("/opt/aggregation/lkl-statement-200508-822290054111425-91000001-20161130-20161201-aggregation-1480559202758.html");
		ftpUtil.closeSource();
//		System.out.println(str);
		// System.out.println(str);
	}
}
