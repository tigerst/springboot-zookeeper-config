package com.tiger.dubbo.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String PATH_SPLITER = "/";	//路径分隔符
    
    private Session session = null;
    private Channel channel = null;
    
	private String host;	//主机ip
	
	private int port;	//端口号
	
	private String userName;	//用户名
	
	private String password;	//密码
	
    private ChannelSftp sftp;
    
    public SFTPUtil(String host, int port, String userName, String password) {
    	this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
    
    /**
     * 功能：获取连接
     * @throws Exception 
     */
    public void getConnection() throws Exception {  
        try {
            JSch jsch = new JSch();  
            jsch.getSession(userName, host, port);  
            session = jsch.getSession(userName, host, port);  
            session.setPassword(password);  
            Properties sshConfig = new Properties();  
            sshConfig.put("StrictHostKeyChecking", "no");  
            session.setConfig(sshConfig);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;  
        } catch (Exception e) {
        	throw new Exception(userName + " log in failed");
        }  
    }  
    
    /**
     * 功能：上传文件
     * @param localFile 本地文件
     * @param remoteTargetDirectory	远程目录
     * @param writeType	写入方式:0-覆盖，1-断点续传，2-追加
	 * @return	返回处理结果
     */
    public boolean upload(String localFile, String remoteTargetDirectory, int writeType) {
    	try {
        	if(sftp == null)
        		getConnection();
        	String remoteFile = localFile.substring(localFile.lastIndexOf(PATH_SPLITER) + 1);	//获取带后缀名的文件名
        	remoteTargetDirectory = remoteTargetDirectory.endsWith(PATH_SPLITER) ? remoteTargetDirectory
    				: (remoteTargetDirectory + PATH_SPLITER);	//目录处理
        	sftp.put(localFile, remoteTargetDirectory + remoteFile, writeType);
        	return true;
		} catch (Exception e) {
			logger.error("sftp上传文件异常", e);
			return false;
		}
    }
    
    /**
     * 功能：上传文件
     * @param localFile 本地文件
     * @param remoteTargetDirectory	远程目录
     * @param writeType	写入方式:0-覆盖，1-断点续传，2-追加
	 * @return	返回处理结果。只有成功和失败两种状态。部分成功也是失败
     */
    public boolean upload(List<String> localFileList, String remoteTargetDirectory, int writeType) {
    	try {
        	if(sftp == null)
        		getConnection();
        	remoteTargetDirectory = remoteTargetDirectory.endsWith(PATH_SPLITER) ? remoteTargetDirectory
    				: (remoteTargetDirectory + PATH_SPLITER);	//目录处理
        	for (String localFile : localFileList) {
        		String remoteFile = localFile.substring(localFile.lastIndexOf(PATH_SPLITER) + 1);	//获取带后缀名的文件名
            	sftp.put(localFile, remoteTargetDirectory + remoteFile, writeType);
			}
        	return true;
		} catch (Exception e) {
			logger.error("sftp上传文件异常", e);
			return false;
		}
    }
    
    /**
     * 功能：批量上传文件，记录上传失败的文件
     * @param localFileList	本地文件列表
     * @param remoteTargetDirectory	远程目录
     * @param writeType	写入方式:0-覆盖，1-断点续传，2-追加
     * @return	返回失败文件列表
     */
    public List<String> uploadWithFailure(List<String> localFileList, String remoteTargetDirectory, int writeType) {
    	List<String> failureList = new ArrayList<String>();
    	try {
        	if(sftp == null)
        		getConnection();
        	remoteTargetDirectory = remoteTargetDirectory.endsWith(PATH_SPLITER) ? remoteTargetDirectory
    				: (remoteTargetDirectory + PATH_SPLITER);	//目录处理
        	for (String localFile : localFileList) {
        		String remoteFile = localFile.substring(localFile.lastIndexOf(PATH_SPLITER) + 1);	//获取带后缀名的文件名
        		try {
                	sftp.put(localFile, remoteTargetDirectory + remoteFile, writeType);
				} catch (Exception e) {
					failureList.add(remoteFile);	//记录失败发送失败的文件名（不包含路径）
				}
			}
        	return failureList;
		} catch (Exception e) {
			logger.error("sftp上传文件异常", e);
			return localFileList;
		}
    }
    
    /**
     * 功能：下载文件
     * @param remoteFile	远程文件名
     * @param localTargetDirectory 本地保存目录
     * @throws Exception 
     */
    public boolean download(String remoteFile, String localTargetDirectory) {  
    	try {
    		if(sftp == null)
        		getConnection();
        	String localFile = remoteFile.substring(remoteFile.lastIndexOf(PATH_SPLITER) + 1);	//获取带后缀名的文件名
        	localTargetDirectory = localTargetDirectory.endsWith(PATH_SPLITER) ? localTargetDirectory
    				: (localTargetDirectory + PATH_SPLITER);	//目录处理
        	sftp.get(remoteFile, localTargetDirectory + localFile);
        	return true;
		} catch (Exception e) {
			logger.error("sftp下载文件异常", e);
			return false;
		}
    }
    
    /**
     * 功能：下载文件为字符串
     * @param remoteFile	远程文件名
     * @throws Exception 
     */
    public String download(String remoteFile) throws Exception {  
    	if(sftp == null)
    		getConnection();
    	InputStream  stream = sftp.get(remoteFile);
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
     * 功能：下载多个文件
     * @param remoteFileList	远程文件名列表
     * @param localTargetDirectory 本地保存目录
	 * @return	返回处理结果。只有成功和失败两种状态。部分成功也是失败
     */
    public boolean download(List<String> remoteFileList, String localTargetDirectory) {  
    	try {
    		if(sftp == null)
        		getConnection();
    		localTargetDirectory = localTargetDirectory.endsWith(PATH_SPLITER) ? localTargetDirectory
    				: (localTargetDirectory + PATH_SPLITER);	//目录处理
    		for (String remoteFile : remoteFileList) {
    			String localFile = remoteFile.substring(remoteFile.lastIndexOf(PATH_SPLITER) + 1);	//获取带后缀名的文件名
    			sftp.get(remoteFile, localTargetDirectory + localFile);
			}
    		return true;
		} catch (Exception e) {
			logger.error("sftp下载文件异常", e);
			return false;
		}
    }
    
    /**
     * 功能：下载多个文件
     * @param remoteFileList	远程文件名列表
     * @param localTargetDirectory 本地保存目录
	 * @return	返回失败文件列表
     */
    public List<String> downloadWithFailure(List<String> remoteFileList, String localTargetDirectory) {  
    	List<String> failureList = new ArrayList<String>();
    	try {
    		if(sftp == null)
        		getConnection();
    		localTargetDirectory = localTargetDirectory.endsWith(PATH_SPLITER) ? localTargetDirectory
    				: (localTargetDirectory + PATH_SPLITER);	//目录处理
    		for (String remoteFile : remoteFileList) {
    			String localFile = remoteFile.substring(remoteFile.lastIndexOf(PATH_SPLITER) + 1);	//获取带后缀名的文件名
    			try {
    				sftp.get(remoteFile, localTargetDirectory + localFile);
				} catch (Exception e) {
					failureList.add(remoteFile);	//记录失败下载失败的文件名（不包含路径）
				}
			}
    		return failureList;
		} catch (Exception e) {
			logger.error("sftp下载文件异常", e);
			return remoteFileList;
		}
    }
    
    /**
	 * 功能：创建目录
	 * @param remoteDir	远程目录
	 * @return	返回处理结果
	 * @throws IOException
	 */
	public boolean createDir(String remoteDir) throws IOException{
		try {
			sftp.mkdir(remoteDir);
			return true;
		} catch (Exception e) {
			logger.error("sftp创建目录异常", e);
			return false;
		}
	}
	
    /**
     * 功能：关闭资源
     */
    public void closeSource() {
    	if(sftp != null){
    		sftp.quit();
    	}
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }
    
    public static void main(String[] args) throws Exception {
    	SFTPUtil sftpUtil = new SFTPUtil("192.168.126.128", 22, "tiger", "111");
        sftpUtil.upload("D:/opt/lkl-statement-2016-10.xlsx", "/opt", 1);
        sftpUtil.download("/opt/lkl-statement-2016-10.xlsx", "D:/tmp");
//    	String str = sftpUtil.download("/opt/approveList.jsp");
    	sftpUtil.closeSource();
//    	System.out.println(str);
	}
}
