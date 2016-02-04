/**
 *  2013-6-5  上午10:59:38  IAuthServer.java
 */
package org.aves.transfer.api;

import java.util.List;
import java.util.Map;

import org.aves.transfer.AuthServerException;
import org.aves.transfer.bean.Account;

/**
 * @author nikin
 * 
 */
public interface IAuthServer {

	/**
	 * generate uni id
	 * 
	 * @return
	 */
	public String nextSequenceNumberForset();

	/**
	 * 
	 * @param Account
	 * @return
	 * @throws AuthServerException
	 */
	public Account createAccount(Account acount) throws AuthServerException;

	/**
	 * 
	 * @param Account
	 * @throws AuthServerException
	 */
	public void ResetAccount(Account acount) throws AuthServerException;

	/**
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @return
	 * @throws AuthServerException
	 */
	public List<Map<String, Object>> listAccounts(
			Map<String, String> condition, int start, int limit)
			throws AuthServerException;

	/**
	 * 
	 * @param condition
	 * @return
	 * @throws AuthServerException
	 */
	public int totalAccounts(Map<String, String> condition)
			throws AuthServerException;

	/**
	 * 
	 * @param account
	 * @return
	 * @throws AuthServerException
	 */
	public Account AccountByAcct(String account) throws AuthServerException;

	/**
	 * 
	 * @param account
	 * @return
	 * @throws AuthServerException
	 */
	public Account AccountById(String id) throws AuthServerException;

	/**
	 * 
	 * @param account
	 * @throws AuthServerException
	 */
	public void suspendAccount(Account account) throws AuthServerException;

}
