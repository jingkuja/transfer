/**
 *  2013-5-29  上午11:12:08  MerchantServer.java
 */
package org.aves.transfer.imp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aves.transfer.MerchantServerException;
import org.aves.transfer.api.IMerchantServer;
import org.aves.transfer.bean.Merchant;

/**
 * @author nikin
 * 
 */
public class MerchantServer implements IMerchantServer {

	private MerchantRepository merchantRepository;

	public void setMerchantRepository(MerchantRepository merchantRepository) {
		this.merchantRepository = merchantRepository;
	}

	@Override
	public String nextSequenceNumberForMer() {
		try {
			Integer id = merchantRepository.nextSequenceNumber();
			if (null == id) {
				merchantRepository.initSequenceNumber();
				id = 1000;
			} else {
				merchantRepository.updateSequenceNumber(id + 1);
			}
			return String.format("%d", id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Merchant MerchantBycode(String code) throws MerchantServerException {
		try {
			Merchant mer = merchantRepository.merchantbycode(code);
			return mer;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantServerException("获取商户信息出错!");
		}
	}

	@Override
	public Merchant MerchantByid(String id) throws MerchantServerException {
		try {
			Merchant mer = merchantRepository.merchantbyid(id);
			return mer;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantServerException("获取商户信息出错!");
		}
	}

	@Override
	public List<Map> chartMerchant(Map<String, String> arg0, int arg1, int arg2)
			throws MerchantServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merchant createMerchant(Merchant merchant)
			throws MerchantServerException {
		try {
			Merchant mer = merchant;
			mer.setStatus("1");
			mer.setId(nextSequenceNumberForMer());
			Date date = new Date();
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			mer.setCreatetime(sm.format(date));
			Map<String, String> cm = new HashMap<String, String>();
			cm.put("lcode", mer.getMcode());
			int cl = merchantRepository.totalMerchant(cm);
			if (cl > 0) {
				throw new MerchantServerException("该商户编号已被使用，请检查修改!");
			}
			merchantRepository.insertMerchant(merchant);
			return mer;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "该商户编号已被使用，请检查修改!";
			if (msg.equals(e.getMessage()))
				throw new MerchantServerException(msg);
			else
				throw new MerchantServerException("创建商户账号失败!");
		}
	}

	@Override
	public List<Map<String, Object>> listMerchant(Map<String, String> para,
			int start, int limit) throws MerchantServerException {
		// TODO Auto-generated method stub
		try {
			List<Map<String, Object>> ml = merchantRepository.listMerchant(
					para, start, limit);
			return ml;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantServerException("获取商户列表失败!");
		}
	}

	@Override
	public void suspendMerchant(Merchant merchant)
			throws MerchantServerException {
		try {
			merchantRepository.suspendMerchant(merchant.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantServerException("废弃商户信息出错!");
		}
	}

	@Override
	public int totalMerchant(Map<String, String> condition)
			throws MerchantServerException {
		try {
			int i = merchantRepository.totalMerchant(condition);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MerchantServerException("获取总数失败!");
		}
	}

	@Override
	public void updateMerchant(Merchant merd) throws MerchantServerException {
		try {
			Merchant mer = merd;
			Merchant dmer = merchantRepository.merchantbyid(merd.getId());
			if (!dmer.getMcode().equals(mer.getMcode())) {
				Map<String, String> cm = new HashMap<String, String>();
				cm.put("lcode", mer.getMcode());
				int cl = merchantRepository.totalMerchant(cm);
				if (cl > 0) {
					throw new MerchantServerException("该商户编号已被使用，请检查修改!");
				}
			}
			merchantRepository.updateMerchant(mer);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "该商户编号已被使用，请检查修改!";
			if (msg.equals(e.getMessage()))
				throw new MerchantServerException(msg);
			else
				throw new MerchantServerException("修改商户账号失败!");
		}
	}

}
