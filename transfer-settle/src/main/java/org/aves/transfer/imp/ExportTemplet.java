/**
 * 
 */
package org.aves.transfer.imp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kuja
 * 
 */
public class ExportTemplet {

	public List<List<Map<String, String>>> outAlist(String desc, String sdate,
			String flag, List<Map<String, String>> rl) {
		List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
		{
			List<Map<String, String>> dda = new ArrayList<Map<String, String>>();
			Map<String, String> dm = new HashMap<String, String>();
			dm.put("cellno", "3");
			dm.put("value", sdate + desc);
			dm.put("setlen", "1");
			dda.add(dm);
			alist.add(dda);
			alist.add(new ArrayList<Map<String, String>>());
		}

		{
			List<Map<String, String>> la = new ArrayList<Map<String, String>>();
			Map<String, String> cm0 = new HashMap<String, String>();
			cm0.put("cellno", "1");
			cm0.put("value", "商户名");
			cm0.put("setlen", "1");

			Map<String, String> cm1 = new HashMap<String, String>();
			cm1.put("cellno", "2");
			cm1.put("value", "商户编码");
			cm1.put("setlen", "1");

			Map<String, String> cm11 = new HashMap<String, String>();
			cm11.put("cellno", "3");
			cm11.put("value", "银行账户");
			cm11.put("setlen", "1");

			Map<String, String> cm12 = new HashMap<String, String>();
			cm12.put("cellno", "4");
			cm12.put("value", "银行账号");
			cm12.put("setlen", "1");

			Map<String, String> cm2 = new HashMap<String, String>();
			cm2.put("cellno", "5");
			cm2.put("value", "交易总单数");
			cm2.put("setlen", "1");

			Map<String, String> cm3 = new HashMap<String, String>();
			cm3.put("cellno", "6");
			cm3.put("value", "支付单数");
			cm3.put("setlen", "1");

			Map<String, String> cm4 = new HashMap<String, String>();
			cm4.put("cellno", "7");
			cm4.put("value", "支付总金额");
			cm4.put("setlen", "1");

			Map<String, String> cm5 = new HashMap<String, String>();
			cm5.put("cellno", "8");
			cm5.put("value", "退款单数");
			cm5.put("setlen", "1");

			Map<String, String> cm6 = new HashMap<String, String>();
			cm6.put("cellno", "9");
			cm6.put("value", "退款总金额");
			cm6.put("setlen", "1");

			Map<String, String> cm7 = new HashMap<String, String>();
			cm7.put("cellno", "10");
			cm7.put("value", "商户手续费");
			cm7.put("setlen", "1");

			Map<String, String> cm8 = new HashMap<String, String>();
			cm8.put("cellno", "11");
			cm8.put("value", "第三方手续费");
			cm8.put("setlen", "1");

			Map<String, String> cm9 = new HashMap<String, String>();
			cm9.put("cellno", "12");
			cm9.put("value", "营收");
			cm9.put("setlen", "1");

			Map<String, String> cm10 = new HashMap<String, String>();
			cm10.put("cellno", "13");
			cm10.put("value", "商户转账额");
			cm10.put("setlen", "1");

			la.add(cm0);
			la.add(cm1);
			la.add(cm11);
			la.add(cm12);
			la.add(cm2);
			la.add(cm3);
			la.add(cm4);
			la.add(cm5);
			la.add(cm6);
			la.add(cm7);
			la.add(cm8);
			la.add(cm9);
			la.add(cm10);
			alist.add(la);
		}
		Map<String, String> tm = new HashMap<String, String>();
		tm.put("tp", "0");
		tm.put("tt", "0");
		tm.put("tr", "0");
		tm.put("tsum", "0");
		tm.put("trefund", "0");
		tm.put("tmfee", "0");
		tm.put("tfee", "0");
		tm.put("tincome", "0");
		tm.put("ttransfermer", "0");

		for (int i = 0; i < rl.size(); i++) {
			Map<String, String> rm = rl.get(i);
			String paycode = rm.get("paycode");
			if (flag.equals("001")) {
				if (paycode.length() > 10)
					continue;
			} else {
				String pflag = paycode.substring(2, 5);
				if (!pflag.equals(flag))
					continue;
			}
			tm.put("tp",
					new BigDecimal(tm.get("tp"))
							.add(new BigDecimal(rm.get("tp")))
							.setScale(0, RoundingMode.HALF_DOWN).toString());
			tm.put("tt",
					new BigDecimal(tm.get("tt"))
							.add(new BigDecimal(rm.get("tt")))
							.setScale(0, RoundingMode.HALF_DOWN).toString());
			tm.put("tr",
					new BigDecimal(tm.get("tr"))
							.add(new BigDecimal(rm.get("tr")))
							.setScale(0, RoundingMode.HALF_DOWN).toString());
			tm.put("tsum",
					new BigDecimal(tm.get("tsum"))
							.add(new BigDecimal(rm.get("tsum")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("trefund",
					new BigDecimal(tm.get("trefund"))
							.add(new BigDecimal(rm.get("trefund")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("tmfee",
					new BigDecimal(tm.get("tmfee"))
							.add(new BigDecimal(rm.get("tmfee")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("tfee",
					new BigDecimal(tm.get("tfee"))
							.add(new BigDecimal(rm.get("tfee")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("tincome",
					new BigDecimal(tm.get("tincome"))
							.add(new BigDecimal(rm.get("tincome")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("ttransfermer",
					new BigDecimal(tm.get("ttransfermer"))
							.add(new BigDecimal(rm.get("ttransfermer")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());

			List<Map<String, String>> la = new ArrayList<Map<String, String>>();
			Map<String, String> cm0 = new HashMap<String, String>();
			cm0.put("cellno", "1");
			cm0.put("value", rm.get("mname"));
			cm0.put("setlen", "1");
			Map<String, String> cm1 = new HashMap<String, String>();
			cm1.put("cellno", "2");
			cm1.put("value", rm.get("mcode"));
			cm1.put("setlen", "1");
			Map<String, String> cm11 = new HashMap<String, String>();
			cm11.put("cellno", "3");
			cm11.put("value", rm.get("bankaname"));
			cm11.put("setlen", "1");
			Map<String, String> cm12 = new HashMap<String, String>();
			cm12.put("cellno", "4");
			cm12.put("value", rm.get("bankaccount"));

			Map<String, String> cm2 = new HashMap<String, String>();
			cm2.put("cellno", "5");
			cm2.put("value", rm.get("tt"));

			Map<String, String> cm3 = new HashMap<String, String>();
			cm3.put("cellno", "6");
			cm3.put("value", rm.get("tp"));

			Map<String, String> cm4 = new HashMap<String, String>();
			cm4.put("cellno", "7");
			cm4.put("value", rm.get("tsum"));
			cm4.put("setlen", "1");

			Map<String, String> cm5 = new HashMap<String, String>();
			cm5.put("cellno", "8");
			cm5.put("value", rm.get("tr"));

			Map<String, String> cm6 = new HashMap<String, String>();

			cm6.put("cellno", "9");
			cm6.put("value", rm.get("trefund"));
			cm6.put("setlen", "1");

			Map<String, String> cm7 = new HashMap<String, String>();
			cm7.put("cellno", "10");
			cm7.put("value", rm.get("tmfee"));

			Map<String, String> cm8 = new HashMap<String, String>();
			cm8.put("cellno", "11");
			cm8.put("value", rm.get("tfee"));

			Map<String, String> cm9 = new HashMap<String, String>();
			cm9.put("cellno", "12");
			cm9.put("value", rm.get("tincome"));

			Map<String, String> cm10 = new HashMap<String, String>();
			cm10.put("cellno", "13");
			cm10.put("value", rm.get("ttransfermer"));
			cm10.put("setlen", "1");

			la.add(cm0);
			la.add(cm1);
			la.add(cm11);
			la.add(cm12);
			la.add(cm2);
			la.add(cm3);
			la.add(cm4);
			la.add(cm5);
			la.add(cm6);
			la.add(cm7);
			la.add(cm8);
			la.add(cm9);
			la.add(cm10);
			alist.add(la);
		}

		{
			alist.add(new ArrayList<Map<String, String>>());
			List<Map<String, String>> la = new ArrayList<Map<String, String>>();
			Map<String, String> cm1 = new HashMap<String, String>();
			cm1.put("cellno", "4");
			cm1.put("value", "总计:");
			cm1.put("setlen", "1");
			Map<String, String> cm2 = new HashMap<String, String>();
			cm2.put("cellno", "5");
			cm2.put("value", tm.get("tt"));

			Map<String, String> cm3 = new HashMap<String, String>();
			cm3.put("cellno", "6");
			cm3.put("value", tm.get("tp"));

			Map<String, String> cm4 = new HashMap<String, String>();
			cm4.put("cellno", "7");
			cm4.put("value", tm.get("tsum"));
			cm4.put("setlen", "1");

			Map<String, String> cm5 = new HashMap<String, String>();
			cm5.put("cellno", "8");
			cm5.put("value", tm.get("tr"));

			Map<String, String> cm6 = new HashMap<String, String>();

			cm6.put("cellno", "9");
			cm6.put("value", tm.get("trefund"));
			cm6.put("setlen", "1");

			Map<String, String> cm7 = new HashMap<String, String>();
			cm7.put("cellno", "10");
			cm7.put("value", tm.get("tmfee"));

			Map<String, String> cm8 = new HashMap<String, String>();
			cm8.put("cellno", "11");
			cm8.put("value", tm.get("tfee"));

			Map<String, String> cm9 = new HashMap<String, String>();
			cm9.put("cellno", "12");
			cm9.put("value", tm.get("tincome"));

			Map<String, String> cm10 = new HashMap<String, String>();
			cm10.put("cellno", "13");
			cm10.put("value", tm.get("ttransfermer"));
			cm10.put("setlen", "1");

			la.add(cm1);
			la.add(cm2);
			la.add(cm3);
			la.add(cm4);
			la.add(cm5);
			la.add(cm6);
			la.add(cm7);
			la.add(cm8);
			la.add(cm9);
			la.add(cm10);
			alist.add(la);
		}
		return alist;
	}

	public List<List<Map<String, String>>> outTAlist(String desc, String sdate,
			String flag, List<Map<String, String>> rl) {
		List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
		{
			List<Map<String, String>> dda = new ArrayList<Map<String, String>>();
			Map<String, String> dm = new HashMap<String, String>();
			dm.put("cellno", "3");
			dm.put("value", sdate + desc);
			dm.put("setlen", "1");
			dda.add(dm);
			alist.add(dda);
			alist.add(new ArrayList<Map<String, String>>());
		}

		{
			List<Map<String, String>> la = new ArrayList<Map<String, String>>();

			Map<String, String> cm2 = new HashMap<String, String>();
			cm2.put("cellno", "5");
			cm2.put("value", "交易总单数");
			cm2.put("setlen", "1");

			Map<String, String> cm3 = new HashMap<String, String>();
			cm3.put("cellno", "6");
			cm3.put("value", "支付单数");
			cm3.put("setlen", "1");

			Map<String, String> cm4 = new HashMap<String, String>();
			cm4.put("cellno", "7");
			cm4.put("value", "支付总金额");
			cm4.put("setlen", "1");

			Map<String, String> cm5 = new HashMap<String, String>();
			cm5.put("cellno", "8");
			cm5.put("value", "退款单数");
			cm5.put("setlen", "1");

			Map<String, String> cm6 = new HashMap<String, String>();
			cm6.put("cellno", "9");
			cm6.put("value", "退款总金额");
			cm6.put("setlen", "1");

			Map<String, String> cm7 = new HashMap<String, String>();
			cm7.put("cellno", "10");
			cm7.put("value", "商户手续费");
			cm7.put("setlen", "1");

			Map<String, String> cm8 = new HashMap<String, String>();
			cm8.put("cellno", "11");
			cm8.put("value", "第三方手续费");
			cm8.put("setlen", "1");

			Map<String, String> cm9 = new HashMap<String, String>();
			cm9.put("cellno", "12");
			cm9.put("value", "营收");
			cm9.put("setlen", "1");

			Map<String, String> cm10 = new HashMap<String, String>();
			cm10.put("cellno", "13");
			cm10.put("value", "商户转账额");
			cm10.put("setlen", "1");

			la.add(cm2);
			la.add(cm3);
			la.add(cm4);
			la.add(cm5);
			la.add(cm6);
			la.add(cm7);
			la.add(cm8);
			la.add(cm9);
			la.add(cm10);
			alist.add(la);
		}
		Map<String, String> tm = new HashMap<String, String>();
		tm.put("tp", "0");
		tm.put("tt", "0");
		tm.put("tr", "0");
		tm.put("tsum", "0");
		tm.put("trefund", "0");
		tm.put("tmfee", "0");
		tm.put("tfee", "0");
		tm.put("tincome", "0");
		tm.put("ttransfermer", "0");

		for (int i = 0; i < rl.size(); i++) {
			Map<String, String> rm = rl.get(i);
			String paycode = rm.get("paycode");
			String pflag = paycode.substring(2, 5);

			tm.put("tp",
					new BigDecimal(tm.get("tp"))
							.add(new BigDecimal(rm.get("tp")))
							.setScale(0, RoundingMode.HALF_DOWN).toString());
			tm.put("tt",
					new BigDecimal(tm.get("tt"))
							.add(new BigDecimal(rm.get("tt")))
							.setScale(0, RoundingMode.HALF_DOWN).toString());
			tm.put("tr",
					new BigDecimal(tm.get("tr"))
							.add(new BigDecimal(rm.get("tr")))
							.setScale(0, RoundingMode.HALF_DOWN).toString());
			tm.put("tsum",
					new BigDecimal(tm.get("tsum"))
							.add(new BigDecimal(rm.get("tsum")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("trefund",
					new BigDecimal(tm.get("trefund"))
							.add(new BigDecimal(rm.get("trefund")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("tmfee",
					new BigDecimal(tm.get("tmfee"))
							.add(new BigDecimal(rm.get("tmfee")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("tfee",
					new BigDecimal(tm.get("tfee"))
							.add(new BigDecimal(rm.get("tfee")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("tincome",
					new BigDecimal(tm.get("tincome"))
							.add(new BigDecimal(rm.get("tincome")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
			tm.put("ttransfermer",
					new BigDecimal(tm.get("ttransfermer"))
							.add(new BigDecimal(rm.get("ttransfermer")))
							.setScale(2, RoundingMode.HALF_DOWN).toString());
		}

		{
			alist.add(new ArrayList<Map<String, String>>());
			List<Map<String, String>> la = new ArrayList<Map<String, String>>();
			Map<String, String> cm1 = new HashMap<String, String>();
			cm1.put("cellno", "4");
			cm1.put("value", "总计:");
			cm1.put("setlen", "1");
			Map<String, String> cm2 = new HashMap<String, String>();
			cm2.put("cellno", "5");
			cm2.put("value", tm.get("tt"));

			Map<String, String> cm3 = new HashMap<String, String>();
			cm3.put("cellno", "6");
			cm3.put("value", tm.get("tp"));

			Map<String, String> cm4 = new HashMap<String, String>();
			cm4.put("cellno", "7");
			cm4.put("value", tm.get("tsum"));
			cm4.put("setlen", "1");

			Map<String, String> cm5 = new HashMap<String, String>();
			cm5.put("cellno", "8");
			cm5.put("value", tm.get("tr"));

			Map<String, String> cm6 = new HashMap<String, String>();

			cm6.put("cellno", "9");
			cm6.put("value", tm.get("trefund"));
			cm6.put("setlen", "1");

			Map<String, String> cm7 = new HashMap<String, String>();
			cm7.put("cellno", "10");
			cm7.put("value", tm.get("tmfee"));

			Map<String, String> cm8 = new HashMap<String, String>();
			cm8.put("cellno", "11");
			cm8.put("value", tm.get("tfee"));

			Map<String, String> cm9 = new HashMap<String, String>();
			cm9.put("cellno", "12");
			cm9.put("value", tm.get("tincome"));

			Map<String, String> cm10 = new HashMap<String, String>();
			cm10.put("cellno", "13");
			cm10.put("value", tm.get("ttransfermer"));
			cm10.put("setlen", "1");

			la.add(cm1);
			la.add(cm2);
			la.add(cm3);
			la.add(cm4);
			la.add(cm5);
			la.add(cm6);
			la.add(cm7);
			la.add(cm8);
			la.add(cm9);
			la.add(cm10);
			alist.add(la);
		}
		return alist;
	}

}
