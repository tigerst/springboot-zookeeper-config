package com.tiger.dubbo.utils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component("mailUtil")
public class MailUtil {

	protected static final String PATH_SPLITER = "/";

	@Autowired
	@Qualifier("mailSender")
	private JavaMailSender javaMailSender; // 发送邮件对象

	/**
	 * 功能：发送邮件
	 * 
	 * @param subject
	 *            主题
	 * @param fromMail
	 *            发件箱
	 * @param toMailArray
	 *            收件箱
	 * @param ccMailArray
	 *            抄送邮件箱
	 * @param bccMailArray
	 *            暗送邮件箱
	 * @param text
	 *            邮件内容
	 * @param inlineFileMap
	 *            内容嵌入文件
	 * @param attachmentPathList
	 *            附件列表
	 * @throws MessagingException
	 */
	public void send(String subject, String fromMail, String[] toMailArray, String[] ccMailArray, String[] bccMailArray,
			String text, boolean html, Map<String, String> inlineFileMap, List<String> attachmentPathList)
			throws MessagingException {
		MimeMessage mailMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);
		messageHelper.setSentDate(new Date()); // 设置发送时间
		messageHelper.setSubject(subject); // 设置主题
		messageHelper.setValidateAddresses(true); // 校验邮箱地址
		messageHelper.setFrom(fromMail); // 发件箱
		messageHelper.setTo(toMailArray); // 收件邮箱，其他人能看到
		if (ccMailArray != null && ccMailArray.length > 0)
			messageHelper.setCc(ccMailArray); // 抄送邮箱，其他人能看到
		if (bccMailArray != null && bccMailArray.length > 0)
			messageHelper.setBcc(bccMailArray); // 暗送邮箱,其他人看不到
		// 发送HTML内容邮件
		messageHelper.setText(text, html); // 设置邮件内容
		if (inlineFileMap != null && inlineFileMap.size() > 0)
			for (Entry<String, String> entry : inlineFileMap.entrySet())
				messageHelper.addInline(entry.getKey(), new File(entry.getValue()));
		// 设置附件
		if (attachmentPathList != null && attachmentPathList.size() >= 0)
			for (String path : attachmentPathList)
				messageHelper.addAttachment(path.substring(path.lastIndexOf(PATH_SPLITER) + 1), new File(path));
		javaMailSender.send(mailMessage); // 发送邮件
	}

	/**
	 * 功能：发送普通邮件
	 * 
	 * @param subject
	 *            主题
	 * @param fromMail
	 *            发件箱
	 * @param toMailArray
	 *            收件箱
	 * @param text
	 *            邮件内容
	 * @throws MessagingException
	 */
	public void sendHtmlMail(String subject, String fromMail, String[] toMailArray, String[] ccMailArray, String[] bccMailArray,
			String text) throws MessagingException {
		this.send(subject, fromMail, toMailArray, ccMailArray, bccMailArray, text, true, null, null);
	}

	public void sendHtmlMail(String subject, String fromMail, String[] toMailArray, String text)
			throws MessagingException {
		this.send(subject, fromMail, toMailArray, null, null, text, true, null, null);
	}
	
	public void sendHtmlMail(String subject, String fromMail, String[] toMailArray, String text, List<String> attachmentPathList)
			throws MessagingException {
		this.send(subject, fromMail, toMailArray, null, null, text, true, null, attachmentPathList);
	}

}
