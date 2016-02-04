/**
 *  2013-6-3  下午3:17:37  IMerchantDeviceDevicServer.java
 */
package org.aves.transfer.imp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aves.transfer.MerchantDeviceServerException;
import org.aves.transfer.api.IMerchantDeviceServer;
import org.aves.transfer.bean.MerchantDevice;

/**
 * @author nikin
 * 
 */
public class MerchantDeviceServer implements IMerchantDeviceServer {

	private PosRepository posRepository;

	public void setPosRepository(PosRepository posRepository) {
		this.posRepository = posRepository;
	}

	@Override
	public String nextSequenceNumberForMerd() {
		try {
			Integer id = posRepository.nextSequenceNumber();
			if (null == id) {
				posRepository.initSequenceNumber();
				id = 1000;
			} else {
				posRepository.updateSequenceNumber(id + 1);
			}
			return String.format("%d", id);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public MerchantDevice createMerchantDevicedevice(
			MerchantDevice MerchantDevice) throws MerchantDeviceServerException {
		try {

			MerchantDevice mer = MerchantDevice;
			mer.setId(nextSequenceNumberForMerd());
			mer.setStatus("1");
			Date date = new Date();
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			mer.setCreatetime(sm.format(date));
			if (totalMerchantDeviceBynum(mer) > 0) {
				throw new MerchantDeviceServerException("该pos编号已存在!");
			}

			posRepository.insertMerchantDevice(mer);
			return mer;
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().equals("该pos编号已存在!"))
				throw new MerchantDeviceServerException(e.getMessage());

			throw new MerchantDeviceServerException("创建商户pos机失败!");
		}
	}

	@Override
	public void updateMerchantDevice(MerchantDevice mer)
			throws MerchantDeviceServerException {
		try {
			MerchantDevice merd = mer;
			posRepository.updateMerchantDevice(merd);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantDeviceServerException("修改商户pos机失败!");
		}

	}

	@Override
	public List<Map<String, Object>> listMerchantDevice(
			Map<String, String> condition, int start, int limit)
			throws MerchantDeviceServerException {
		// TODO Auto-generated method stub
		try {
			List<Map<String, Object>> ml = posRepository.listMerchantDevice(
					condition, start, limit);
			return ml;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantDeviceServerException("获取商户pos列表失败!");
		}
	}

	@Override
	public List<Map<String, Object>> tlistMerchantDevice(
			Map<String, String> condition) throws MerchantDeviceServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int totalMerchantDevice(Map<String, String> condition)
			throws MerchantDeviceServerException {
		try {
			int i = posRepository.totalMerchantDevice(condition);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantDeviceServerException("获取总数失败!");
		}
	}

	@Override
	public int totalMerchantDeviceBynum(MerchantDevice md)
			throws MerchantDeviceServerException {
		try {
			int i = posRepository.totalMerchantDeviceBynum(md.getPosnum(),
					md.getMerchantid());
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantDeviceServerException("获取总数失败!");
		}
	}

	@Override
	public List<MerchantDevice> MerchantDeviceBymid(String code)
			throws MerchantDeviceServerException {
		try {
			List<MerchantDevice> i = posRepository.merchantDevicebymid(code);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantDeviceServerException("获取商户pos机失败!");
		}
	}

	@Override
	public MerchantDevice MerchantDeviceByid(String id)
			throws MerchantDeviceServerException {
		try {
			MerchantDevice i = posRepository.merchantDevicebyid(id);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantDeviceServerException("获取商户pos机失败!");
		}
	}

	@Override
	public MerchantDevice MerchantDeviceBynum(String deviceNum)
			throws MerchantDeviceServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void suspendMerchantDevice(MerchantDevice mer)
			throws MerchantDeviceServerException {
		try {

			MerchantDevice merd = mer;
			posRepository.suspendMerchantDevice(mer.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantDeviceServerException("注销商户pos机失败!");
		}

	}

}
