/**
 *  2013-6-3  下午3:18:45  MerchantDeviceServerException.java
 */
package org.aves.transfer;

/**
 * @author nikin
 * 
 */
public class MerchantDeviceServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7665586716017234322L;

	/**
	 * 
	 */
	public MerchantDeviceServerException() {
	}

	/**
	 * @param message
	 */
	public MerchantDeviceServerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MerchantDeviceServerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MerchantDeviceServerException(String message, Throwable cause) {
		super(message, cause);
	}
}