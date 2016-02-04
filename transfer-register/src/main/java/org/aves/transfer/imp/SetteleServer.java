/**
 *  2013-5-29  上午11:12:08  MerchantServer.java
 */
package org.aves.transfer.imp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aves.transfer.SettlesServerException;
import org.aves.transfer.api.ISettlesServer;
import org.aves.transfer.bean.Settles;

/**
 * @author nikin
 * 
 */
public class SetteleServer implements ISettlesServer {

	private SetteleRepository setteleRepository;

	public void setSetteleRepository(SetteleRepository setteleRepository) {
		this.setteleRepository = setteleRepository;
	}

	@Override
	public String nextSequenceNumberForset() {
		try {
			Integer id = setteleRepository.nextSequenceNumber();
			if (null == id) {
				setteleRepository.initSequenceNumber();
				id = 1000;
			} else {
				setteleRepository.updateSequenceNumber(id + 1);
			}
			return String.format("%d", id);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Settles createSettles(Settles settles) throws SettlesServerException {
		try {
			String id = nextSequenceNumberForset();
			settles.setId(id);
			settles.setApprover("");
			settles.setApprovetime("");
			Date date = new Date();
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			settles.setCreatetime(sm.format(date));
			settles.setStatus("1");
			Settles se = setteleRepository.settlesbyAid(
					settles.getContractNum(), true);
			if (se != null && se.getId() != null)
				throw new SettlesServerException("该协议编号"
						+ settles.getContractNum() + "已使用。无法再添加！");

			if (setteleRepository.totalsettlesbytime(settles) > 0) {
				throw new SettlesServerException("该时间段内商户已有"
						+ settles.getTitle() + "协议。无法再添加");
			}

			setteleRepository.insertSettles(settles);
			return settles;
		} catch (Exception e) {
			if (e.getMessage().equals(
					"该协议编号" + settles.getContractNum() + "已使用。无法再添加！"))
				throw new SettlesServerException("该协议编号"
						+ settles.getContractNum() + "已使用。无法再添加！");
			else if (e.getMessage().equals(
					"该时间段内商户已有" + settles.getTitle() + "协议。无法再添加"))
				throw new SettlesServerException("该时间段内商户已有"
						+ settles.getTitle() + "协议。无法再添加");

			else
				throw new SettlesServerException("系统出错。稍后再试！");
		}
	}

	@Override
	public void updateSettles(Settles settles) throws SettlesServerException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Map<String, Object>> listSettles(Map<String, String> condition,
			int start, int limit) throws SettlesServerException {
		try {
			List<Map<String, Object>> ml = setteleRepository.listSettles(
					condition, start, limit);
			return ml;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SettlesServerException("获取商户合约列表失败!");
		}
	}

	@Override
	public int totalSettles(Map<String, String> condition)
			throws SettlesServerException {
		try {
			int i = setteleRepository.totalSettles(condition);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SettlesServerException("获取总数失败!");
		}
	}

	@Override
	public Settles SettlesByid(String id) throws SettlesServerException {
		// TODO Auto-generated method stub
		try {
			Settles i = setteleRepository.settlesbyid(id);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SettlesServerException("获取商户合约失败!");
		}
	}

	@Override
	public Settles settlesbymid(Map<String, String> conditon)
			throws SettlesServerException {
		try {

			Settles i = setteleRepository.settlesbymid(conditon);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SettlesServerException("获取商户合约失败!");
		}
	}

	@Override
	public void suspendSettles(Settles settles) throws SettlesServerException {
		try {
			setteleRepository.suspSettles(settles.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SettlesServerException("修改合约状态状态失败!");
		}
	}

	@Override
	public void approveSettles(Settles settles) throws SettlesServerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delaySettles(Settles settles) throws SettlesServerException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Settles> settlesbymidnt(Map<String, String> conditon)
			throws SettlesServerException {
		try {

			List<Settles> i = setteleRepository.settlesbymidnt(conditon);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SettlesServerException("获取商户协议失败!!");
		}
	}

	@Override
	public Settles SettlesByAid(String id, boolean all)
			throws SettlesServerException {
		try {
			Settles i = setteleRepository.settlesbyAid(id, all);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SettlesServerException("获取商户协议失败!");
		}
	}

}
