package org.aves.transfer.temp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aves.transfer.BillException;
import org.aves.transfer.api.IBillServer;
import org.aves.transfer.bean.Bill;

public class TempServer implements IBillServer {

	private TempRepository tempRepository;

	public void setTempRepository(TempRepository tempRepository) {
		this.tempRepository = tempRepository;
	}

	@Override
	public String nextSequenceNumber() {
		try {
			Integer id = tempRepository.nextSequenceNumber();
			if (null == id) {
				tempRepository.initSequenceNumber();
				id = 1000;
			} else {
				tempRepository.updateSequenceNumber(id + 1);
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
			Date date = new Date();
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			bill.setCreatetime(sm.format(date));
			if (bill.getId() != null) {
				tempRepository.resettlebill(bill);
			} else {
				bill.setId(nextSequenceNumber());
				tempRepository.insertBill(bill);
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
			tempRepository.settlebill(bill);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("清算账单失败!");
		}

	}

	@Override
	public Bill billbyid(String id) throws BillException {
		try {
			Bill bill = tempRepository.billbyid(id);
			return bill;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("获取账单信息出错!");
		}
	}

	@Override
	public Bill billbytradno(String tradno) throws BillException {
		try {
			Bill bill = tempRepository.billbytradno(tradno);
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
			List<Map<String, Object>> ml = tempRepository.listbills(condition,
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
			int i = tempRepository.totalBills(condition);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BillException("获取总数失败!");
		}
	}

}
