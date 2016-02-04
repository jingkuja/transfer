/**
 *  2013-7-3  下午4:09:26  AbstracEmailTask.java
 */
package org.aves.transfer.email;

/**
 * @author nikin
 * 
 */
public class EmailTask implements Runnable {

	private Emailbean emailbean;

	public EmailTask(Emailbean em) {
		emailbean = em;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			doAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doAction() throws Exception {
		SendEmailTool.send(emailbean);
	}
}
