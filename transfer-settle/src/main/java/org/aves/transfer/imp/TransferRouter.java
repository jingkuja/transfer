/**
 * AccountDataRouter.java
 * Created on Dec 4, 2013 11:43:34 PM
 * Copyright (c) 2012-2014 Aves Team of Sichuan Abacus Co.,Ltd. All rights reserved.
 */
package org.aves.transfer.imp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
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
import org.aves.transfer.bean.Bill;
import org.aves.transfer.bean.Merchant;
import org.aves.transfer.bean.MerchantDevice;
import org.aves.transfer.bean.Settles;
import org.aves.transfer.email.EmailTask;
import org.aves.transfer.email.Emailbean;

/**
 * @author nikin
 * 
 */
public class TransferRouter extends RouteBuilder {
	private static final Logger logger = Logger.getLogger(TransferRouter.class
			.getName());

	private static final String fielURITemplate = "file:/home/ccb?noop=true&delay=2500";

	/**
	 *
	 */
	public TransferRouter() {
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
		return fielURITemplate;
	}

	public void configure() throws Exception {
		final String quartzURL = fielURITemplate;

		from(quartzURL).process(new Processor() {

			public void process(Exchange exchange) throws Exception {
				try {
					boolean flag = false;
					String fileName = (String) exchange.getIn().getHeader(
							Exchange.FILE_NAME);
					String fdate = getNowDateShort(fileName);
					log.info("loading filename:" + fileName);
					if (fdate.equals("0"))
						return;
					InputStream body = exchange.getIn().getBody(
							InputStream.class);
					if (body == null) {
						log.info("loading file:" + "loaded content is null");
					}
					BufferedReader in = new BufferedReader(
							new InputStreamReader(body));
					String str = null;
					while ((str = in.readLine()) != null) {
						boolean ash = importbills(str, fdate);
						if (ash)
							flag = true;
					}
					if (flag)
						sendmail(fdate);
				} catch (Exception e) {
					log.info(e.getMessage());
				}
			}
		}).to("file:/home/ccb/backup");

	}

	private String getNowDateShort(String datestr) {
		try {
			String dates = datestr.split("_")[2].split("\\.")[0];
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date date = formatter.parse(dates);
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
			return formatter1.format(date);
		} catch (Exception e) {
			return "0";
		}
	}

	private boolean importbills(String str, String fdate) {
		try {
			String[] resu = null;
			if (str != null)
				resu = str.split("\\|");
			if (resu != null) {
				String agrmtId = resu[1];
				String posno = resu[4];
				String cflag = resu[7];
				String sum = resu[8];
				String fees = resu[9];
				String fund = resu[10];
				String date = resu[11];
				String time = resu[12];
				String pdate = date.replace("-", "");
				String ptime = time.replace(":", "");
				String postradeNo = posno + pdate + ptime;
				String tradeTime = resu[11] + " " + resu[12];
				String tradeType = "pay";

				Bill bill = billServer.billbytradno(postradeNo);
				if (bill != null) {
					return false;
				}

				Settles se = setteleServer.SettlesByAid(agrmtId, false);

				bill = new Bill();

				bill.setPostradeNo(postradeNo);
				bill.setSum(sum);
				bill.setTerminal(agrmtId);
				bill.setTradetype(tradeType);
				bill.setTradetime(tradeTime);
				bill.setPaysrvCode(agrmtId);
				bill.setMerid(se.getMerchantid());
				bill.setCreatetime(fdate);

				boolean flag = true;
				int status = 201;
				if (se == null || se.getId() == null) {
					flag = false;
					bill.setMerid("");
					status = 203;
				}

				Merchant md = merchantServer.MerchantByid(se.getMerchantid());
				if (md == null || md.getId() == null) {
					flag = false;
					status = 202;
				}
				if (flag) {
					List<MerchantDevice> mde = merchantDeviceServer
							.MerchantDeviceBymid(se.getId());
					if (mde.size() < 1) {
						flag = false;
						status = 202;
					}
				}

				if (flag) {
					bill.setStatus("1");
					bill.setSdesc("导入成功");
					if (tradeType.equals("pay")) {
						if (cflag.equals("1")) {
							String rate = se.getRebate();
							String ratea = se.getRebatea();
							bill.setRate(rate);
							bill.setRatea(ratea);
							BigDecimal dsum = new BigDecimal(sum);
							BigDecimal drate = new BigDecimal(rate);
							BigDecimal dratea = new BigDecimal(ratea);
							BigDecimal hunder = new BigDecimal(100);
							double fee = Double.valueOf(fees);
							double mfee = dsum.multiply(drate)
									.divide(hunder, 2, RoundingMode.HALF_UP)
									.doubleValue();
							double income = new BigDecimal(mfee)
									.subtract(new BigDecimal(fee))
									.setScale(2, RoundingMode.HALF_UP)
									.doubleValue();
							double transfermer = dsum
									.subtract(new BigDecimal(mfee))
									.setScale(2, RoundingMode.HALF_UP)
									.doubleValue();
							if (!(fee <= 0.01 && fee >= -0.01))
								bill.setFee(String.valueOf(fee));
							if (!(fee <= 0.01 && fee >= -0.01))
								bill.setMfee(String.valueOf(mfee));
							bill.setIncome(String.valueOf(income));
							bill.setTransfermer(String.valueOf(transfermer));
						} else {
							bill.setRate("-1");
							bill.setRatea("-1");
							bill.setFee(String.valueOf(fees));
							bill.setMfee(String.valueOf(fees));
							bill.setIncome("0");
							bill.setTransfermer(fund);
						}
					} else {
						bill.setTransfermer("-" + sum);
					}
					billServer.importbills(bill);
					status = 201;
					return true;
				} else {
					bill.setStatus("0");
					if (status == 202) {
						bill.setSdesc("商户或pos终端不存在");
					}
					if (status == 203) {
						bill.setSdesc("商户无激活协议");
					}
					billServer.importbills(bill);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;

		}
	}

	private void sendmail(String date) throws Exception {

		String[] account = new String[] { emailaccont, emailaccont1,
				emailaccont2 };
		Random ri = new Random();
		ExecutorService service = Executors.newFixedThreadPool(3);

		Map<String, String> conditiond = new HashMap<String, String>();

		List<Map<String, Object>> ml = merchantServer.listMerchant(conditiond,
				0, merchantServer.totalMerchant(conditiond));

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
				condition.put("sdate", date);
				condition.put("status", "1");
				List<Map<String, Object>> cl = billServer.listbills(condition,
						0, billServer.totalBills(condition));

				String content = getBody(cl, mname);

				String subject = date.substring(0, 10) + "移动支付刷卡对账单";

				int pi = ri.nextInt(3);
				if (pi > 2)
					pi = 2;
				emailaddress = account[pi];
				Emailbean em = new Emailbean(smtp, emailaddress, email, null,
						subject, content, emailaddress, emailpass, null);

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
					if (payCode.length() < 11) {
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
