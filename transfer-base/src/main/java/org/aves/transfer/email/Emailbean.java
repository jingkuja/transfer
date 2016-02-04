/**
 *  2013-7-4  上午10:45:59  Emailbean.java
 */
package org.aves.transfer.email;

/**
 * @author nikin
 * 
 */
public class Emailbean {
	private String smtp;
	private String from;
	private String to;
	private String copyto;
	private String subject;
	private String content;
	private String username;
	private String password;
	private String filename;

	/**
	 * @param smtp
	 * @param from
	 * @param to
	 * @param copyto
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @param filename
	 */
	public Emailbean(String smtp, String from, String to, String copyto,
			String subject, String content, String username, String password,
			String filename) {
		this.smtp = smtp;
		this.from = from;
		this.to = to;
		this.copyto = copyto;
		this.subject = subject;
		this.content = content;
		this.username = username;
		this.password = password;
		this.filename = filename;
	}

	/**
	 * @return the smtp
	 */
	public String getSmtp() {
		return smtp;
	}

	/**
	 * @param smtp
	 *            the smtp to set
	 */
	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the copyto
	 */
	public String getCopyto() {
		return copyto;
	}

	/**
	 * @param copyto
	 *            the copyto to set
	 */
	public void setCopyto(String copyto) {
		this.copyto = copyto;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Emailbean [smtp=" + smtp + ", from=" + from + ", to=" + to
				+ ", copyto=" + copyto + ", subject=" + subject + ", content="
				+ content + ", username=" + username + ", password=" + password
				+ ", filename=" + filename + "]";
	}

}
