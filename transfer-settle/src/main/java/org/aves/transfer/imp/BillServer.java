package org.aves.transfer.imp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aves.transfer.BillException;
import org.aves.transfer.api.IBillServer;
import org.aves.transfer.bean.Bill;

public class BillServer implements IBillServer {

	private BillRepository billRepository;

	public void setBillRepository(BillRepository billRepository) {
		this.billRepository = billRepository;
	}

	@Override
	public String nextSequenceNumber() {
		try {
			Integer id = billRepository.nextSequenceNumber();
			if (null == id) {
				billRepository.initSequenceNumber();
				id = 1000;
			} else {
				billRepository.updateSequenceNumber(id + 1);
			}
			return String.format("%d", id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void importbills(Bill bill) throws BillException {
		try {
			if (bill.getCreatetime() == null || bill.getCreatetime().equals("")) {
				Date date = new Date();
				SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
				bill.setCreatetime(sm.format(date));
			}
			if (bill.getId() != null) {
				billRepository.resettlebill(bill);
			} else {
				Bill bills = billRepository.billbytradno(bill.getPostradeNo());
				if (bills != null) {
					throw new BillException("该交易序号重复");
				}

				bill.setId(nextSequenceNumber());
				billRepository.insertBill(bill);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException(e.getMessage());
		}

	}

	@Override
	public void settlebill(String id, String status, String desc)
			throws BillException {
		try {
			Bill bill = new Bill();
			bill.setId(id);
			bill.setStatus(status);
			Date date = new Date();
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			bill.setCreatetime(sm.format(date));
			bill.setSdesc(desc);
			billRepository.settlebill(bill);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("清算账单失败!");
		}

	}

	@Override
	public Bill billbyid(String id) throws BillException {
		try {
			Bill bill = billRepository.billbyid(id);
			return bill;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("获取账单信息出错!");
		}
	}

	@Override
	public Bill billbytradno(String tradno) throws BillException {
		try {
			Bill bill = billRepository.billbytradno(tradno);
			return bill;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("获取账单信息出错!");
		}
	}

	@Override
	public List<Map<String, Object>> listbills(Map<String, String> condition,
			int start, int limit) throws BillException {
		try {
			List<Map<String, Object>> ml = billRepository.listbills(condition,
					start, limit);
			return ml;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("获取账单列表失败!");
		}
	}

	@Override
	public int totalBills(Map<String, String> condition) throws BillException {
		try {
			int i = billRepository.totalBills(condition);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("获取总数失败!");
		}
	}

}
