/**
 *  2013-6-3  下午3:17:37  IMerchantDeviceDevicServer.java
 */
package org.aves.transfer.api;

import java.util.List;
import java.util.Map;

import org.aves.transfer.MerchantDeviceServerException;
import org.aves.transfer.bean.MerchantDevice;

/**
 * @author nikin
 * 
 */
public interface IMerchantDeviceServer {
	/**
	 * generate uni id
	 * 
	 * @return
	 */
	public String nextSequenceNumberForMerd();

	/**
	 * create a new MerchantDevice info
	 * 
	 * @param MerchantDevice
	 * @return MerchantDevice
	 * @throws MerchantDeviceServerException
	 */
	public MerchantDevice createMerchantDevicedevice(
			MerchantDevice MerchantDevice) throws MerchantDeviceServerException;

	/**
	 * 
	 * @param mer
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public void updateMerchantDevice(MerchantDevice mer)
			throws MerchantDeviceServerException;

	/**
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public List<Map<String, Object>> listMerchantDevice(
			Map<String, String> condition, int start, int limit)
			throws MerchantDeviceServerException;

	/**
	 * 
	 * @param condition
	 * 
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public List<Map<String, Object>> tlistMerchantDevice(
			Map<String, String> condition) throws MerchantDeviceServerException;

	/**
	 * 
	 * @param condition
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public int totalMerchantDevice(Map<String, String> condition)
			throws MerchantDeviceServerException;

	/**
	 * 
	 * @param condition
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public int totalMerchantDeviceBynum(MerchantDevice md)
			throws MerchantDeviceServerException;

	/**
	 * 
	 * @param code
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public List<MerchantDevice> MerchantDeviceBymid(String code)
			throws MerchantDeviceServerException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public MerchantDevice MerchantDeviceByid(String id)
			throws MerchantDeviceServerException;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws MerchantDeviceServerException
	 */
	public MerchantDevice MerchantDeviceBynum(String deviceNum)
			throws MerchantDeviceServerException;

	/**
	 * 
	 * @param code
	 * @throws MerchantDeviceServerException
	 */
	public void suspendMerchantDevice(MerchantDevice mer)
			throws MerchantDeviceServerException;
}
