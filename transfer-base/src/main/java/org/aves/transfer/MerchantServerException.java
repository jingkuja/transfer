/**
 *  2013-5-29  上午10:58:56  MerchantServerException.java
 */
package org.aves.transfer;

/**
 * @author nikin
 * 
 */
public class MerchantServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7665586716017234322L;

	/**
	 * 
	 */
	public MerchantServerException() {
	}

	/**
	 * @param message
	 */
	public MerchantServerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MerchantServerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MerchantServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
