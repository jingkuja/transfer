/**
 *  2013-6-9  上午11:02:23  IBillServer.java
 */
package org.aves.transfer.api;

import java.util.List;
import java.util.Map;

import org.aves.transfer.BillException;
import org.aves.transfer.bean.Bill;

/**
 * @author nikin
 * 
 */
public interface IBillServer {

	/**
	 * generate uni id
	 * 
	 * @return
	 */
	public String nextSequenceNumber();

	/**
	 * 
	 * @param bill
	 * @return
	 * @throws BillException
	 */
	public void importbills(Bill bill) throws BillException;

	/**
	 * 
	 * @param id
	 * @param status
	 * @param exce
	 * @throws BillException
	 */
	public void settlebill(String id, String status, String desc)
			throws BillException;

	/**
	 * 
	 * @param bill
	 * @return
	 * @throws BillException
	 */
	public Bill billbyid(String id) throws BillException;

	public Bill billbytradno(String tradno) throws BillException;

	/**
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @return
	 * @throws BillException
	 */
	public List<Map<String, Object>> listbills(Map<String, String> condition,
			int start, int limit) throws BillException;

	/**
	 * 
	 * @param condition
	 * @return
	 * @throws BillException
	 */
	public int totalBills(Map<String, String> condition) throws BillException;

}
