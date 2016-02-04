/**
 *  2013-5-29  上午10:52:18  IMerchantServer.java
 */
package org.aves.transfer.api;

import java.util.List;
import java.util.Map;

import org.aves.transfer.MerchantServerException;
import org.aves.transfer.bean.Merchant;


/**
 * @author nikin
 * 
 * 
 */

public interface IMerchantServer {

	/**
	 * generate uni id
	 * 
	 * @return
	 */
	public String nextSequenceNumberForMer();

	/**
	 * create a new merchant info
	 * 
	 * @param merchant
	 * @return Merchant
	 * @throws MerchantServerException
	 */
	public Merchant createMerchant(Merchant merchant)
			throws MerchantServerException;

	/**
	 * 
	 * @param mer
	 * @return
	 * @throws MerchantServerException
	 */
	public void updateMerchant(Merchant mer) throws MerchantServerException;

	/**
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @return
	 * @throws MerchantServerException
	 */
	public List<Map<String,Object>> listMerchant(Map<String, String> condition,
			int start, int limit) throws MerchantServerException;

	public List<Map> chartMerchant(Map<String, String> condition, int start,
			int limit) throws MerchantServerException;

	/**
	 * 
	 * @param condition
	 * @return
	 * @throws MerchantServerException
	 */
	public int totalMerchant(Map<String, String> condition)
			throws MerchantServerException;

	/**
	 * 
	 * @param code
	 * @return
	 * @throws MerchantServerException
	 */
	public Merchant MerchantBycode(String code) throws MerchantServerException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws MerchantServerException
	 */
	public Merchant MerchantByid(String id) throws MerchantServerException;

	/**
	 * 
	 * @param code
	 * @throws MerchantServerException
	 */
	public void suspendMerchant(Merchant merchant)
			throws MerchantServerException;
}
