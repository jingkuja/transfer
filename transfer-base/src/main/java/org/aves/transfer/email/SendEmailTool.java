/**
 *  2013-7-4  上午10:48:57  SendEmailTool.java
 */
package org.aves.transfer.email;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * @author nikin
 * 
 */
public class SendEmailTool {
	private MimeMessage mimeMsg; // MIME邮件对象
	private Session session; // 邮件会话对象
	private Properties props; // 系统属性
	private boolean needAuth = false; // smtp是否需要认证
	// smtp认证用户名和密码
	private String username;
	private String password;
	private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

	/**
	 * Constructor
	 * 
	 * @param smtp
	 *            邮件发送服务器
	 */
	public SendEmailTool(Emailbean em) {
		setSmtpHost(em.getSmtp());
		createMimeMessage(em);
	}

	/**
	 * 设置邮件发送服务器
	 * 
	 * @param hostName
	 *            String
	 */
	public void setSmtpHost(String hostName) {
		if (props == null)
			props = System.getProperties(); // 获得系统属性对象
		props.put("mail.smtp.host", hostName); // 设置SMTP主机
	}

	/**
	 * 创建MIME邮件对象
	 * 
	 * @return
	 */
	public boolean createMimeMessage(Emailbean eml) {
		try {
			final Emailbean em = eml;
			session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(em.getFrom(), em
							.getPassword());
				}
			}); // 获得邮件会话对象
		} catch (Exception e) {
			return false;
		}
		try {
			mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
			mp = new MimeMultipart();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置SMTP是否需要验证
	 * 
	 * @param need
	 */
	public void setNeedAuth(boolean need) {
		if (props == null)
			props = System.getProperties();
		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
	}

	/**
	 * 设置用户名和密码
	 * 
	 * @param name
	 * @param pass
	 */
	public void setNamePass(String name, String pass) {
		props.put("mail.smtp.user", name);
		props.put("mail.smtp.password", pass);
		username = name;
		password = pass;
	}

	/**
	 * 设置邮件主题
	 * 
	 * @param mailSubject
	 * @return
	 */
	public boolean setSubject(String mailSubject) {
		try {
			mimeMsg.setSubject(mailSubject);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置邮件正文
	 * 
	 * @param mailBody
	 *            String
	 */
	public boolean setBody(String mailBody) {
		try {
			BodyPart bp = new MimeBodyPart();
			bp.setContent("" + mailBody, "text/html;charset=GBK");
			mp.addBodyPart(bp);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param filename
	 *            String
	 */
	public boolean addFileAffix(String filename) {
		try {
			BodyPart bp = new MimeBodyPart();
			FileDataSource fileds = new FileDataSource(filename);
			bp.setDataHandler(new DataHandler(fileds));
			bp.setFileName(MimeUtility.encodeText(fileds.getName()));
			mp.addBodyPart(bp);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置发信人
	 * 
	 * @param from
	 *            String
	 */
	public boolean setFrom(String from) {
		try {
			mimeMsg.setFrom(new InternetAddress(from)); // 设置发信人
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置收信人
	 * 
	 * @param to
	 *            String
	 */
	public boolean setTo(String to) {
		if (to == null)
			return false;
		try {
			mimeMsg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置抄送人
	 * 
	 * @param copyto
	 *            String
	 */
	public boolean setCopyTo(String copyto) {
		if (copyto == null)
			return false;
		try {
			mimeMsg.setRecipients(Message.RecipientType.CC,
					(Address[]) InternetAddress.parse(copyto));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	/**
	 * 发送邮件
	 */
	public boolean sendOutcc() {
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), username,
					password);
			transport.sendMessage(mimeMsg,
					mimeMsg.getRecipients(Message.RecipientType.TO));
			transport.sendMessage(mimeMsg,
					mimeMsg.getRecipients(Message.RecipientType.CC));
			transport.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 发送邮件
	 */
	public boolean sendOut() {
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), username,
					password);
			transport.sendMessage(mimeMsg,
					mimeMsg.getRecipients(Message.RecipientType.TO));
			transport.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 调用sendOut方法完成邮件发送
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public static boolean send(Emailbean emailbean) {
		SendEmailTool theMail = new SendEmailTool(emailbean);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(emailbean.getSubject()))
			return false;
		if (!theMail.setBody(emailbean.getContent()))
			return false;
		if (!theMail.setTo(emailbean.getTo()))
			return false;
		if (!theMail.setFrom(emailbean.getFrom()))
			return false;
		theMail.setNamePass(emailbean.getUsername(), emailbean.getPassword());
		if (!theMail.sendOut())
			return false;
		return true;
	}

	/**
	 * 调用sendOut方法完成邮件发送,带抄送
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param copyto
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public static boolean sendAndCc(Emailbean emailbean) {
		SendEmailTool theMail = new SendEmailTool(emailbean);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(emailbean.getSubject()))
			return false;
		if (!theMail.setBody(emailbean.getContent()))
			return false;
		if (!theMail.setTo(emailbean.getTo()))
			return false;
		if (!theMail.setCopyTo(emailbean.getCopyto()))
			return false;
		if (!theMail.setFrom(emailbean.getFrom()))
			return false;
		theMail.setNamePass(emailbean.getUsername(), emailbean.getPassword());

		if (!theMail.sendOutcc())
			return false;
		return true;
	}

	/**
	 * 调用sendOut方法完成邮件发送,带附件
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @param filename
	 *            附件路径
	 * @return
	 */
	public static boolean sendex(Emailbean emailbean) {
		SendEmailTool theMail = new SendEmailTool(emailbean);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(emailbean.getSubject()))
			return false;
		if (!theMail.setBody(emailbean.getContent()))
			return false;
		if (!theMail.addFileAffix(emailbean.getFilename()))
			return false;
		if (!theMail.setTo(emailbean.getTo()))
			return false;
		if (!theMail.setFrom(emailbean.getFrom()))
			return false;
		theMail.setNamePass(emailbean.getUsername(), emailbean.getPassword());

		if (!theMail.sendOut())
			return false;
		return true;
	}

	/**
	 * 调用sendOut方法完成邮件发送,带附件和抄送
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param copyto
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @param filename
	 * @return
	 */
	public static boolean sendAndCcex(Emailbean emailbean) {
		SendEmailTool theMail = new SendEmailTool(emailbean);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(emailbean.getSubject()))
			return false;
		if (!theMail.setBody(emailbean.getContent()))
			return false;
		if (!theMail.addFileAffix(emailbean.getFilename()))
			return false;
		if (!theMail.setTo(emailbean.getTo()))
			return false;
		if (!theMail.setCopyTo(emailbean.getCopyto()))
			return false;
		if (!theMail.setFrom(emailbean.getFrom()))
			return false;
		theMail.setNamePass(emailbean.getUsername(), emailbean.getPassword());

		if (!theMail.sendOutcc())
			return false;
		return true;
	}

}
