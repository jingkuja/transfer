/**
 * 
 */
package org.aves.transfer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aves.transfer.email.Emailbean;

/**
 * @author kuja
 *
 */
public class Test {
	public static void main(String[] args) {

		double fee = 0.011;
		System.out.println((!(fee <= 0.01 && fee >= -0.01)));

		System.out.println(new BigDecimal(1).compareTo(new BigDecimal(2)));

		String io = "1*2*3";

		String hop[] = io.split("[*]");
		System.out.println(hop[1] + "dd" + hop[2]);
		String smtp = "smtp.ym.163.com";
		String emailaccount = "jinlizfb03@cdjinli.com";
		String emailpass = "jlzfbdzd";
		String emailaddress = "jinlizfb03@cdjinli.com";
		String odate = "2013-12-11";
		String subject = odate.substring(0, 10) + "刷卡对账单";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> dm = new HashMap<String, Object>();
		dm.put("tradetime", "2013-21-11");
		dm.put("sum", "12");
		dm.put("mfee", "1");
		dm.put("transfermer", "10");

		Map<String, Object> dm1 = new HashMap<String, Object>();
		dm1.put("tradetime", "2013-21-11");
		dm1.put("sum", "14");
		dm1.put("mfee", "2");
		dm1.put("transfermer", "12");
		list.add(dm1);
		list.add(dm);
		String content = getBody(list, "test");

		Emailbean em = new Emailbean(smtp, emailaddress, "450278815@qq.com",
				null, subject, content, emailaccount, emailpass, null);

		Random ri = new Random();
		System.out.println(ri.nextInt(3));

		ExecutorService service = Executors.newFixedThreadPool(1);
		try {
			// service.execute(new EmailTask(em));
		} catch (Exception e) {
			e.printStackTrace();
		}
		service.shutdown();
	}

	public static String getBody(List<Map<String, Object>> list, String mname) {
		StringBuffer sb = new StringBuffer(
				"<table><tr><td>商户名</td><td>交易时间</td><td>交易金额</td><td>手续费</td><td>转账金额</td></tr>");
		BigDecimal dsum = new BigDecimal("0");
		BigDecimal dmfee = new BigDecimal("0");
		BigDecimal dtransfermer = new BigDecimal("0");

		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> cm = list.get(i);
			String tradetime = (String) cm.get("tradetime");
			String sum = (String) cm.get("sum");
			String mfee = (String) cm.get("mfee");
			String transfermer = (String) cm.get("transfermer");
			dsum = dsum.add(new BigDecimal(sum));
			dmfee = dmfee.add(new BigDecimal(mfee));
			dtransfermer = dtransfermer.add(new BigDecimal(transfermer));
			sb.append("<td>" + mname + "</td><td>" + tradetime + "</td><td>"
					+ sum + "</td><td>" + mfee + "</td><td>" + transfermer
					+ "</td></tr>");
		}
		sb.append("<td>总计</td><td>" + "交易笔数:" + list.size() + "</td><td>"
				+ dsum.setScale(2, RoundingMode.HALF_UP).toString()
				+ "</td><td>"
				+ dmfee.setScale(2, RoundingMode.HALF_UP).toString()
				+ "</td><td>"
				+ dtransfermer.setScale(2, RoundingMode.HALF_UP).toString()
				+ "</td></tr>");

		sb.append("</table>");
		return sb.toString();
	}
}
