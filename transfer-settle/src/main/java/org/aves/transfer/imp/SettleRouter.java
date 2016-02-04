/**
 * AccountDataRouter.java
 * Created on Dec 4, 2013 11:43:34 PM
 * Copyright (c) 2012-2014 Aves Team of Sichuan Abacus Co.,Ltd. All rights reserved.
 */
package org.aves.transfer.imp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.aves.transfer.api.IMerchantDeviceServer;
import org.aves.transfer.api.IMerchantServer;
import org.aves.transfer.api.ISettlesServer;
import org.aves.transfer.email.EmailTask;
import org.aves.transfer.email.Emailbean;

/**
 * @author nikin
 * 
 */
public class SettleRouter extends RouteBuilder {
	private static final Logger logger = Logger.getLogger(SettleRouter.class
			.getName());

	private static final String quartzURITemplate = "quartz://Transsettles/sendemailtime?cron=0 10 06 * * ?";

	/**
	 *
	 */
	public SettleRouter() {
	}

	private IMerchantServer merchantServer;

	private IMerchantDeviceServer merchantDeviceServer;

	private ISettlesServer setteleServer;

	private BillServer billServer;

	private String emailaccont;

	private String emailaccont1;

	private String emailaccont2;

	private String emailaddress;

	private String emailpass;

	private String smtp;

	public IMerchantServer getMerchantServer() {
		return merchantServer;
	}

	public void setMerchantServer(IMerchantServer merchantServer) {
		this.merchantServer = merchantServer;
	}

	public IMerchantDeviceServer getMerchantDeviceServer() {
		return merchantDeviceServer;
	}

	public void setMerchantDeviceServer(
			IMerchantDeviceServer merchantDeviceServer) {
		this.merchantDeviceServer = merchantDeviceServer;
	}

	public ISettlesServer getSetteleServer() {
		return setteleServer;
	}

	public void setSetteleServer(ISettlesServer setteleServer) {
		this.setteleServer = setteleServer;
	}

	public BillServer getBillServer() {
		return billServer;
	}

	public void setBillServer(BillServer billServer) {
		this.billServer = billServer;
	}

	public String getEmailaccont() {
		return emailaccont;
	}

	public void setEmailaccont(String emailaccont) {
		this.emailaccont = emailaccont;
	}

	public void setEmailaccont1(String emailaccont1) {
		this.emailaccont1 = emailaccont1;
	}

	public void setEmailaccont2(String emailaccont2) {
		this.emailaccont2 = emailaccont2;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String getEmailpass() {
		return emailpass;
	}

	public void setEmailpass(String emailpass) {
		this.emailpass = emailpass;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public static String getQuartzuritemplate() {
		return quartzURITemplate;
	}

	public void configure() throws Exception {
		final String quartzURL = quartzURITemplate;
		final String[] account = new String[] { emailaccont, emailaccont1,
				emailaccont2 };
		final Random ri = new Random();

		from(quartzURL).process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				ExecutorService service = Executors.newFixedThreadPool(3);
				Date now = new Date();
				Calendar startDT = Calendar.getInstance();
				startDT.setTime(now);
				startDT.add(Calendar.DAY_OF_MONTH, -1);
				Date newd = startDT.getTime();
				SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
				String dates = sm.format(newd);
				Map<String, String> conditiond = new HashMap<String, String>();

				List<Map<String, Object>> ml = merchantServer.listMerchant(
						conditiond, 0, merchantServer.totalMerchant(conditiond));

				for (int i = 0; i < ml.size(); i++) {
					Map<String, Object> md = ml.get(i);
					String id = (String) md.get("id");
					boolean flage = false;
					String email = null;
					try {
						email = (String) md.get("email");
						if (email != null && !email.trim().equals("")) {
							flage = true;
						}

					} catch (Exception e) {

					}

					if (flage) {
						Map<String, String> condition = new HashMap<String, String>();
						String mname = (String) md.get("mname");
						condition.put("merid", id);
						condition.put("sdate", dates);
						condition.put("status", "1");
						List<Map<String, Object>> cl = billServer.listbills(
								condition, 0, billServer.totalBills(condition));

						String content = getBody(cl, mname);

						String subject = dates.substring(0, 10) + "移动支付刷卡对账单";

						int pi = ri.nextInt(3);
						if (pi > 2)
							pi = 2;
						emailaddress = account[pi];
						Emailbean em = new Emailbean(smtp, emailaddress, email,
								null, subject, content, emailaddress,
								emailpass, null);

						try {
							if (cl.size() > 0)
								service.execute(new EmailTask(em));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				service.shutdown();
			}
		});

	}

	private String getBody(List<Map<String, Object>> list, String mname) {
		try {
			StringBuffer sb = new StringBuffer(
					"<table><tr><td>来源</td><td>商户名</td><td>交易时间</td><td>交易方式</td><td>交易金额</td><td>手续费</td><td>转账金额</td></tr>");
			BigDecimal dsum = new BigDecimal("0");
			BigDecimal dmfee = new BigDecimal("0");
			BigDecimal dtransfermer = new BigDecimal("0");

			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> cm = list.get(i);
				String pt = "";
				try {
					String payCode = (String) cm.get("payCode");
					String cg = payCode.substring(2, 5);
					if (cg.endsWith("101"))
						pt = "支付宝";
					if (cg.endsWith("201"))
						pt = "微信";
					if (cg.endsWith("301"))
						pt = "翼支付";
					if (cg.endsWith("401"))
						pt = "银商虚拟卡";
					if (cg.endsWith("402"))
						pt = "银商零售卡";
					if (cg.length() < 11) {
						pt = "建行pos";
					}
				} catch (Exception e) {

				}
				String tradetime = (String) cm.get("tradetime");
				String sum = getStringI(cm.get("sum"));
				String mfee = getStringI(cm.get("mfee"));
				String tradetype = (String) cm.get("tradetype");
				String transfermer = getStringI(cm.get("transfermer"));
				dmfee = dmfee.add(new BigDecimal(mfee));
				String tp = "未知";

				if (tradetype.equals("pay")) {
					tp = "支付";
					dsum = dsum.add(new BigDecimal(sum));
				} else if (tradetype.equals("refund")) {
					tp = "退款";
					dsum = dsum.subtract(new BigDecimal(sum));
				} else {
					tp = "未知";
					dsum = dsum.add(new BigDecimal(sum));
				}
				dtransfermer = dtransfermer.add(new BigDecimal(transfermer));
				sb.append("<td>" + pt + "</td><td>" + mname + "</td><td>"
						+ tradetime + "</td><td>" + tp + "</td><td>" + sum
						+ "</td><td>" + mfee + "</td><td>" + transfermer
						+ "</td></tr>");
			}
			sb.append("<td></td><td></td><td>总计</td><td>" + "交易数:"
					+ list.size() + "</td><td>"
					+ dsum.setScale(2, RoundingMode.HALF_UP).toString()
					+ "</td><td>"
					+ dmfee.setScale(2, RoundingMode.HALF_UP).toString()
					+ "</td><td>"
					+ dtransfermer.setScale(2, RoundingMode.HALF_UP).toString()
					+ "</td></tr>");

			sb.append("</table>");
			return sb.toString();
		} catch (Exception e) {
			return "系统出错!账单发送错误，请联系管理人员";
		}

	}

	private String getStringI(Object o) {
		String op = null;
		try {
			op = (String) (o);
			if (op == null)
				op = "0";
		} catch (Exception e) {
			op = "0";
		}
		return op;
	}
}
