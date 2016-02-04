/**
 *  2013-6-5  上午10:59:38  ISettlesServer.java
 */
package org.aves.transfer.api;

import java.util.List;
import java.util.Map;

import org.aves.transfer.SettlesServerException;
import org.aves.transfer.bean.Settles;

/**
 * @author nikin
 * 
 */
public interface ISettlesServer {

	/**
	 * generate uni id
	 * 
	 * @return
	 */
	public String nextSequenceNumberForset();

	/**
	 * 
	 * @param settles
	 * @return
	 * @throws SettlesServerException
	 */
	public Settles createSettles(Settles settles) throws SettlesServerException;

	/**
	 * 
	 * @param settles
	 * @throws SettlesServerException
	 */
	public void updateSettles(Settles settles) throws SettlesServerException;

	/**
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @return
	 * @throws SettlesServerException
	 */
	public List<Map<String, Object>> listSettles(Map<String, String> condition,
			int start, int limit) throws SettlesServerException;

	/**
	 * 
	 * @param condition
	 * @return
	 * @throws SettlesServerException
	 */
	public int totalSettles(Map<String, String> condition)
			throws SettlesServerException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SettlesServerException
	 */
	public Settles SettlesByid(String id) throws SettlesServerException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SettlesServerException
	 */
	public Settles SettlesByAid(String id, boolean all)
			throws SettlesServerException;

	/**
	 * 
	 * @param conditon
	 * @return
	 */
	public Settles settlesbymid(Map<String, String> conditon)
			throws SettlesServerException;

	public List<Settles> settlesbymidnt(Map<String, String> conditon)
			throws SettlesServerException;

	/**
	 * 
	 * @param code
	 * @throws SettlesServerException
	 */
	public void suspendSettles(Settles settles) throws SettlesServerException;

	/**
	 * 
	 * @param code
	 * @throws SettlesServerException
	 */
	public void approveSettles(Settles settles) throws SettlesServerException;

	/**
	 * 
	 * @param settles
	 * @throws SettlesServerException
	 */
	public void delaySettles(Settles settles) throws SettlesServerException;

}
