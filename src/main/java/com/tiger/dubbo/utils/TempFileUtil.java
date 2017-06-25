package com.tiger.dubbo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TempFileUtil {

	private static final Logger logger = LoggerFactory.getLogger(TempFileUtil.class);

	/**
	 * 功能：删除文件集
	 * 
	 * @param fileList
	 *            文件列表
	 */
	public static void deleteFileList(List<String> fileList) {
		File file = null;
		for (String filePath : fileList) {
			try {
				file = new File(filePath);
				file.delete();
			} catch (Exception e) {
				logger.error("删除文件" + filePath + "异常", e);
			}
		}
	}

	/**
	 * 功能：转换路径 1.遍历原始请求带路径的文件集 2.遍历失败文件集 3.如果原始文件包含失败文件，则放入响应结果集中
	 * 
	 * @param fileList
	 *            原始带路径文件集
	 * @param failureList
	 *            失败文件集
	 * @return 返回带路径的失败文件集
	 */
	public static List<String> transformFileList(List<String> fileList, List<String> failureList) {
		List<String> respFiles = new ArrayList<String>(failureList.size());
		for (String sourceFile : fileList) {
			for (String failureFile : failureList) {
				if (sourceFile.endsWith(failureFile))
					respFiles.add(sourceFile);
			}
		}
		return respFiles;
	}

	/**
	 * 功能：转换路径,截取文件名
	 * 
	 * @param fileList
	 *            原始带路径文件集
	 * @return 返回截取路径后的文件名
	 */
	public static List<String> truncPathFileList(List<String> fileList) {
		List<String> truncFileNameList = new ArrayList<String>(fileList.size());
		for (String filePath : fileList)
			truncFileNameList.add(
					filePath == null ? null : filePath.substring(filePath.lastIndexOf(Constants.PATH_SPLITER) + 1));
		return truncFileNameList;
	}
	
	/**
	 * 功能：nio复制文件
	 * 
	 * @param source
	 *            源文件
	 * @param skipBytes
	 *            跳过字节数
	 * @param dest
	 *            目标文件
	 * @param append
	 *            是否追加
	 * @throws IOException
	 */
	public static void fileChannelCopy(String source, long skipBytes, String dest, boolean append) throws IOException {
		FileInputStream fis = null; // 文件输入流
		FileOutputStream fos = null; // 文件输出流
		FileChannel in = null; // 输入流通道
		FileChannel out = null; // 输出流通道
		try {
			fis = new FileInputStream(source); // 文件输入流
			fos = new FileOutputStream(dest, append); // 文件输出流,追加方式
			in = fis.getChannel();// 得到对应的文件通道
			out = fos.getChannel();// 得到对应的文件通道
			in.transferTo(skipBytes, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			logger.error("复制文件异常", e);
		} finally {
			if (fis != null)
				fis.close();
			if (fis != null)
				in.close();
			if (fis != null)
				fos.close();
			if (fis != null)
				out.close();
		}
	}

	/**
	 * 功能：nio复制文件
	 * 
	 * @param source
	 *            源文件
	 * @param dest
	 *            目标文件
	 * @param append
	 *            是否追加
	 * @throws IOException
	 */
	public static void fileChannelCopy(String source, String dest, boolean append) throws IOException {
		fileChannelCopy(source, 0, dest, append);
	}

	/**
	 * 功能：nio读文件
	 * 
	 * @param source
	 *            源文件
	 * @return 字节数组
	 */
	public static byte[] readFile(String source) {
		try {
			FileInputStream stream = null;
			stream = new FileInputStream(source);
			FileChannel channel = stream.getChannel();
			ByteBuffer byteBuf = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuf)) > 0) {
				// byteBuf.flip();
				// do something
				// byteBuf.clear();
			}
			channel.close();
			stream.close();
			return byteBuf.array();
		} catch (Exception e) {
			logger.error("读文件异常", e);
		}
		return null;
	}
	
	/**
	 * 功能：nio读文件
	 * 
	 * @param source
	 *            源文件
	 * @return 字符串
	 */
	public static String readFileToString(String source) {
		try {
			return new String(readFile(source), Constants.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			logger.error("读文件异常", e);
		}
		return null;
	}

	/**
	 * 功能：读文件的前n行数据
	 * 
	 * @param source
	 *            源文件
	 * @param lines
	 *            行数
	 * @return 字符串
	 * @throws IOException
	 */
	public static String readTopLines(String source, int lines) throws IOException {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String result = "";
			fis = new FileInputStream(source);
			isr = new InputStreamReader(fis, Constants.DEFAULT_ENCODING);	//UTF-8格式
			br = new BufferedReader(isr);
			if (1 == lines)
				return br.readLine();
			for (int i = 0; i < lines; i++)
				result += br.readLine() + "\n";
			return result;
		} catch (Exception e) {
			logger.error("读文件异常", e);
		} finally {
			if (br != null)
				br.close();
			if (isr != null)
				isr.close();
			if (fis != null)
				fis.close();
		}
		return null;
	}

	/**
	 * 功能：采用内存映射读取文件
	 * 
	 * @param source
	 *            源文件
	 * @return 字节数组
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static byte[] readLargeFile(String source) throws IOException {
		FileChannel channel = null;
		try {
			channel = new RandomAccessFile(source, "r").getChannel();
			MappedByteBuffer byteBuffer = channel.map(MapMode.READ_ONLY, 0, channel.size()).load();
			byte[] result = new byte[(int) channel.size()];
			if (byteBuffer.remaining() > 0)
				byteBuffer.get(result, 0, byteBuffer.remaining());
			return result;
		} catch (IOException e) {
			logger.error("读文件异常", e);
		} finally {
			if (channel != null)
				channel.close();
		}
		return null;
	}
	
	/**
	 * 功能：采用内存映射读取文件
	 * 
	 * @param source
	 *            源文件
	 * @return 字符串
	 * @throws IOException
	 */
	public static String readLargeFileToString(String source) throws IOException {
		return new String(readLargeFile(source), Constants.DEFAULT_ENCODING);
	}
	
	/**
	 * 功能：nio写文件,文件不存在事创建目录及文件
	 * @param fileName	文件名
	 * @param byteArrays	内容字节数组
	 */
	public static void wirteFile(String fileName, byte[] byteArrays) {
		File file = new File(fileName);
		FileOutputStream fos = null;
		FileChannel fc = null;
		try {
			if (!file.getParentFile().exists())	//父目录不存在时创建目录
				file.getParentFile().mkdirs();
			fos = new FileOutputStream(file, true);
			fc = fos.getChannel();
			fc.write(ByteBuffer.wrap(byteArrays));	//
			fos.flush();
		} catch (FileNotFoundException e) {
			logger.error("文件不存", e);
		} catch (IOException e) {
			logger.error("写文件失败", e);
		} finally {
			try {
				if(fc != null)
					fc.close();
				if(fos != null)
					fos.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * 功能：nio写文件
	 * @param fileName	文件名
	 * @param context	内容字符串	
	 */
	public static void wirteFile(String fileName, String context) {
		try {
			byte[] byteArrays = context.getBytes(Constants.DEFAULT_ENCODING);
			wirteFile(fileName, byteArrays);
		} catch (UnsupportedEncodingException e) {
			logger.error("不支持该字符编码", e);
		}
	}
	
	public static void main(String[] args) {
//		wirteFile("/opt/aa/bb/cc/dd/aa.txt", "张东，张东");		//
//		JSONObject json = JSON.parseObject("{\"name\":\"tiger\"}");
		String text = "{\"name\":\"tiger\"}";
		Object obj = JSON.parse(text);
		if(obj instanceof JSONObject){
			System.out.println("json string");
		}else{
			System.out.println("not json string");
		}
		
	}
}
