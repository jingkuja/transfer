package org.aves.transfer.temp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.aves.transfer.api.IMerchantDeviceServer;
import org.aves.transfer.api.IMerchantServer;
import org.aves.transfer.api.ISettlesServer;
import org.aves.transfer.bean.Merchant;

@Path("api")
public class TempResource {

	private static final Logger logger = Logger.getLogger(TempResource.class
			.getName());

	private IMerchantServer merchantServer;

	private IMerchantDeviceServer merchantDeviceServer;

	private ISettlesServer setteleServer;

	private TempServer tempServer;

	private String SHA1Key;

	private String accountname;

	private String bankfl;

	private String bankaccount;

	public TempResource() {

	}

	public void setMerchantServer(IMerchantServer merchantServer) {
		this.merchantServer = merchantServer;
	}

	public void setMerchantDeviceServer(
			IMerchantDeviceServer merchantDeviceServer) {
		this.merchantDeviceServer = merchantDeviceServer;
	}

	public void setSetteleServer(ISettlesServer setteleServer) {
		this.setteleServer = setteleServer;
	}

	public void setTempServer(TempServer tempServer) {
		this.tempServer = tempServer;
	}

	public void setSHA1Key(String sHA1Key) {
		SHA1Key = sHA1Key;
	}

	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getBankfl() {
		return bankfl;
	}

	public void setBankfl(String bankfl) {
		this.bankfl = bankfl;
	}

	public String getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}

	@GET
	@Path("dt")
	public Response dt(@Context ContainerRequestContext requestCtx) {
		try {

			return Response.ok(200).entity("12").build();
		} catch (Exception e) {
			logger.info(e.getMessage());
			return Response.ok().entity("11").build();
		}
	}

	@GET
	@Path("agreements/12")
	public Response tt() {
		try {
			return Response.ok(200).entity("232dfsdf").build();
		} catch (Exception e) {
			logger.info(e.getMessage());
			return Response.ok().entity("11").build();
		}
	}

	@GET
	@Path("atranl")
	public Response atranl(@QueryParam("sdate") String sdate,
			@QueryParam("fdate") String fdate) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("mdate", sdate);
			condition.put("fdate", fdate);

			condition.put("status", "1");
			List<Map<String, Object>> ml = tempServer.listbills(condition, 0,
					tempServer.totalBills(condition));

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			int pos = 1;

			Map<String, String> cm = new HashMap<String, String>();
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");

				String sum = getStringI(op.get("transfermer"));

				if (cm.get(merid) == null)
					cm.put(merid, sum);
				else {
					String dsum = cm.get(merid);
					BigDecimal dg = new BigDecimal(dsum);
					BigDecimal sg = new BigDecimal(sum);
					// 不计算退款
					if (sg.compareTo(new BigDecimal(0)) < 0)
						continue;
					dg = dg.add(sg);
					cm.put(merid, dg.setScale(2, RoundingMode.HALF_DOWN)
							.toString());
				}

			}

			for (Map.Entry<String, String> entryd : cm.entrySet()) {
				String merid = entryd.getKey();
				String tsum = entryd.getValue();
				Merchant mer = merchantServer.MerchantByid(merid);
				if (mer == null || mer.getId() == null)
					continue;
				double ds = Double.valueOf(tsum);
				if (ds == 0)
					continue;
				if (mer.getTplate().equals("0")) {

					alist.add(outform1(mer, tsum, accountname, bankaccount,
							bankfl, pos));
					pos++;
				}
			}

			if (alist.size() == 0) {
				return Response.ok().entity("当日无公对公转账单").build();
			}

			String pdfDisplayName = new String(
					(fdate + sdate + "公对公转账单").getBytes("gb2312"), "ISO8859-1");
			return Response
					.ok(200)
					.header("Content-Type",
							"application/vnd.ms-excel;charset=utf-8")
					.header("Content-Disposition",
							"attachment;filename=\"" + pdfDisplayName + ".xls"
									+ "\"").entity(new StreamingOutput() {
						@Override
						public void write(OutputStream outputStream)
								throws IOException, WebApplicationException {
							ExportExcel oe = new ExportExcel();
							oe.extractExcel(alist, outputStream, "sheet1",
									false, true, false);
						}
					}).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity("").build();
		}
	}

	@GET
	@Path("tranl")
	public Response tranl(@QueryParam("sdate") String sdate,
			@QueryParam("fdate") String fdate) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("mdate", sdate);
			condition.put("fdate", fdate);

			condition.put("status", "1");
			List<Map<String, Object>> ml = tempServer.listbills(condition, 0,
					tempServer.totalBills(condition));

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			int pos = 1;

			Map<String, String> cm = new HashMap<String, String>();
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");

				String sum = getStringI(op.get("transfermer"));

				if (cm.get(merid) == null)
					cm.put(merid, sum);
				else {
					String dsum = cm.get(merid);
					BigDecimal dg = new BigDecimal(dsum);
					BigDecimal sg = new BigDecimal(sum);
					// 不计算退款
					// if (sg.compareTo(new BigDecimal(0)) < 0)
					// continue;
					dg = dg.add(sg);
					cm.put(merid, dg.setScale(2, RoundingMode.HALF_DOWN)
							.toString());
				}

			}
			BigDecimal toal = new BigDecimal(0);
			for (Map.Entry<String, String> entryd : cm.entrySet()) {
				String merid = entryd.getKey();
				String tsum = entryd.getValue();
				Merchant mer = merchantServer.MerchantByid(merid);
				if (mer == null || mer.getId() == null)
					continue;
				double ds = Double.valueOf(tsum);
				if (ds == 0)
					continue;
				if (mer.getTplate().equals("0")) {
					toal = toal.add(new BigDecimal(tsum));
					alist.add(outform(mer, tsum, accountname, bankaccount,
							bankfl, pos));
					pos++;
				}
			}

			if (alist.size() == 0) {
				return Response.ok().entity("当日无公对公转账单").build();
			}

			String pdfDisplayName = new String(
					(fdate + sdate + "公对公转账单" + toal.setScale(2,
							RoundingMode.HALF_DOWN).toString())
							.getBytes("gb2312"),
					"ISO8859-1");
			return Response
					.ok(200)
					.header("Content-Type",
							"application/vnd.ms-excel;charset=utf-8")
					.header("Content-Disposition",
							"attachment;filename=\"" + pdfDisplayName + ".xls"
									+ "\"").entity(new StreamingOutput() {
						@Override
						public void write(OutputStream outputStream)
								throws IOException, WebApplicationException {
							ExportExcel oe = new ExportExcel();
							oe.extractExcel(alist, outputStream, "sheet1",
									false, true, false);
						}
					}).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity("").build();
		}
	}

	@GET
	@Path("trane")
	public Response trane(@QueryParam("sdate") String sdate,
			@QueryParam("fdate") String fdate) {
		try {
			Map<String, String> condition = new HashMap<String, String>();

			condition.put("mdate", sdate);
			condition.put("fdate", fdate);
			condition.put("status", "1");
			List<Map<String, Object>> ml = tempServer.listbills(condition, 0,
					tempServer.totalBills(condition));

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			int pos = 1;
			Map<String, String> cm = new HashMap<String, String>();
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");
				String sum = getStringI(op.get("transfermer"));
				if (cm.get(merid) == null)
					cm.put(merid, sum);
				else {
					String dsum = cm.get(merid);
					BigDecimal dg = new BigDecimal(dsum);
					BigDecimal sg = new BigDecimal(sum);
					// 不计算退款
					// if (sg.compareTo(new BigDecimal(0)) < 0)
					// continue;
					dg = dg.add(sg);
					cm.put(merid, dg.setScale(2, RoundingMode.HALF_DOWN)
							.toString());
				}
			}
			BigDecimal toal = new BigDecimal(0);

			for (Map.Entry<String, String> entryd : cm.entrySet()) {
				String merid = entryd.getKey();
				String tsum = entryd.getValue();
				Merchant mer = merchantServer.MerchantByid(merid);
				if (mer == null || mer.getId() == null)
					continue;
				double ds = Double.valueOf(tsum);
				if (ds == 0)
					continue;
				if (mer.getTplate().equals("1")) {
					toal = toal.add(new BigDecimal(tsum));
					alist.add(outprivate(mer, tsum, pos));
					pos++;
				}
			}

			if (alist.size() == 0) {
				return Response.ok().entity("当日无代发代扣转账单").build();
			}
			String pdfDisplayName = new String(
					(fdate + sdate + "代发代扣转账单" + toal.setScale(2,
							RoundingMode.HALF_DOWN).toString())
							.getBytes("gb2312"),
					"ISO8859-1");
			return Response
					.ok(200)
					.header("Content-Type",
							"application/vnd.ms-excel;charset=utf-8")
					.header("Content-Disposition",
							"attachment;filename=\"" + pdfDisplayName + ".xls"
									+ "\"").entity(new StreamingOutput() {
						@Override
						public void write(OutputStream outputStream)
								throws IOException, WebApplicationException {
							ExportExcel oe = new ExportExcel();
							oe.extractExcel(alist, outputStream, "sheet1",
									false, true, false);
						}
					}).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity("").build();
		}
	}

	@GET
	@Path("merbillst")
	public void merbillst(@QueryParam("sdate") String sdate,
			@QueryParam("fdate") String fdate, @QueryParam("qa") String qa) {
		try {

			Map<String, String> conditiond = new HashMap<String, String>();
			List<Map<String, Object>> nl = merchantServer.listMerchant(
					conditiond, 0, merchantServer.totalMerchant(conditiond));
			for (int i = 0; i < nl.size(); i++) {
				Map<String, Object> md = nl.get(i);
				String id = (String) md.get("id");
				Map<String, String> condition = new HashMap<String, String>();
				String mname = getString(md.get("mname"));
				String mcode = getString(md.get("mcode"));
				condition.put("merid", id);
				condition.put("mdate", sdate);
				condition.put("fdate", fdate);
				condition.put("status", "1");
				List<Map<String, Object>> ml = tempServer.listbills(condition,
						0, tempServer.totalBills(condition));
				BigDecimal dsum = new BigDecimal("0");
				BigDecimal dmfee = new BigDecimal("0");
				BigDecimal dtransfermer = new BigDecimal("0");

				List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
				{
					List<Map<String, String>> dda = new ArrayList<Map<String, String>>();
					Map<String, String> dm = new HashMap<String, String>();
					dm.put("cellno", "3");
					dm.put("value", fdate + sdate + "前商户交易详情汇总");
					dm.put("setlen", "1");
					dda.add(dm);
					alist.add(dda);
					alist.add(new ArrayList<Map<String, String>>());
				}

				{
					List<Map<String, String>> la = new ArrayList<Map<String, String>>();

					Map<String, String> cm00 = new HashMap<String, String>();
					cm00.put("cellno", "0");
					cm00.put("value", "来源");
					cm00.put("setlen", "1");

					Map<String, String> cm0 = new HashMap<String, String>();
					cm0.put("cellno", "1");
					cm0.put("value", "商户名");
					cm0.put("setlen", "1");

					Map<String, String> cm1 = new HashMap<String, String>();
					cm1.put("cellno", "2");
					cm1.put("value", "商户编码");
					cm1.put("setlen", "1");

					Map<String, String> cm2 = new HashMap<String, String>();
					cm2.put("cellno", "3");
					cm2.put("value", "交易时间");
					cm2.put("setlen", "1");

					Map<String, String> cm4 = new HashMap<String, String>();
					cm4.put("cellno", "4");
					cm4.put("value", "交易类型");
					cm4.put("setlen", "1");

					Map<String, String> cm41 = new HashMap<String, String>();
					cm41.put("cellno", "5");
					cm41.put("value", "交易流水号");
					cm41.put("setlen", "1");

					Map<String, String> cm5 = new HashMap<String, String>();
					cm5.put("cellno", "6");
					cm5.put("value", "交易金额");
					cm5.put("setlen", "1");

					Map<String, String> cm51 = new HashMap<String, String>();
					cm51.put("cellno", "7");
					cm51.put("value", "优惠券折扣金额");
					cm51.put("setlen", "1");

					Map<String, String> cm7 = new HashMap<String, String>();
					cm7.put("cellno", "8");
					cm7.put("value", "商户手续费");
					cm7.put("setlen", "1");

					Map<String, String> cm10 = new HashMap<String, String>();
					cm10.put("cellno", "9");
					cm10.put("value", "商户转账额");
					cm10.put("setlen", "1");

					la.add(cm00);
					la.add(cm0);
					la.add(cm1);
					la.add(cm2);
					la.add(cm4);
					la.add(cm41);
					la.add(cm5);
					la.add(cm51);
					la.add(cm7);
					la.add(cm10);
					alist.add(la);
				}

				for (int j = 0; j < ml.size(); j++) {
					Map<String, Object> cm = ml.get(j);
					String pt = "";
					try {
						String payCode = (String) cm.get("payCode");
						String cg = payCode.substring(2, 5);
						if (cg.endsWith("101"))
							pt = "支付宝";
						if (cg.endsWith("201"))
							pt = "微信";
					} catch (Exception e) {

					}
					String tradetime = getString(cm.get("tradetime"));
					String postradeNo = getString(cm.get("postradeNo"));

					String sum = getStringI(cm.get("sum"));
					String mfee = getStringI(cm.get("mfee"));
					String tradetype = getString(cm.get("tradetype"));
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
					dtransfermer = dtransfermer
							.add(new BigDecimal(transfermer));

					{
						List<Map<String, String>> la = new ArrayList<Map<String, String>>();

						Map<String, String> cm00 = new HashMap<String, String>();
						cm00.put("cellno", "0");
						cm00.put("value", pt);
						cm00.put("setlen", "1");

						Map<String, String> cm0 = new HashMap<String, String>();
						cm0.put("cellno", "1");
						cm0.put("value", mname);
						cm0.put("setlen", "1");

						Map<String, String> cm1 = new HashMap<String, String>();
						cm1.put("cellno", "2");
						cm1.put("value", mcode);
						cm1.put("setlen", "1");

						Map<String, String> cm2 = new HashMap<String, String>();
						cm2.put("cellno", "3");
						cm2.put("value", tradetime);
						cm2.put("setlen", "1");

						Map<String, String> cm4 = new HashMap<String, String>();
						cm4.put("cellno", "4");
						cm4.put("value", tp);
						cm4.put("setlen", "1");

						Map<String, String> cm41 = new HashMap<String, String>();
						cm41.put("cellno", "5");
						cm41.put("value", postradeNo);

						Map<String, String> cm5 = new HashMap<String, String>();
						cm5.put("cellno", "6");
						cm5.put("value", sum);
						cm5.put("setlen", "1");

						Map<String, String> cm51 = new HashMap<String, String>();
						cm51.put("cellno", "7");
						cm51.put("value", "");
						cm51.put("setlen", "1");

						Map<String, String> cm7 = new HashMap<String, String>();
						cm7.put("cellno", "8");
						cm7.put("value", mfee);
						cm7.put("setlen", "1");

						Map<String, String> cm10 = new HashMap<String, String>();
						cm10.put("cellno", "9");
						cm10.put("value", transfermer);
						cm10.put("setlen", "1");

						la.add(cm00);
						la.add(cm0);
						la.add(cm1);
						la.add(cm2);
						la.add(cm4);
						la.add(cm41);
						la.add(cm5);
						la.add(cm51);
						la.add(cm7);
						la.add(cm10);
						alist.add(la);
					}
				}
				{
					List<Map<String, String>> la = new ArrayList<Map<String, String>>();

					Map<String, String> cm2 = new HashMap<String, String>();
					cm2.put("cellno", "3");
					cm2.put("value", "合计:");
					cm2.put("setlen", "1");

					Map<String, String> cm4 = new HashMap<String, String>();
					cm4.put("cellno", "4");
					cm4.put("value", String.valueOf(ml.size()));
					cm4.put("setlen", "1");

					Map<String, String> cm5 = new HashMap<String, String>();
					cm5.put("cellno", "6");
					cm5.put("value", dsum.setScale(2, RoundingMode.HALF_UP)
							.toString());
					cm5.put("setlen", "1");

					Map<String, String> cm7 = new HashMap<String, String>();
					cm7.put("cellno", "8");
					cm7.put("value", dmfee.setScale(2, RoundingMode.HALF_UP)
							.toString());
					cm7.put("setlen", "1");

					Map<String, String> cm10 = new HashMap<String, String>();
					cm10.put("cellno", "9");
					cm10.put("value",
							dtransfermer.setScale(2, RoundingMode.HALF_UP)
									.toString());
					cm10.put("setlen", "1");
					la.add(cm2);
					la.add(cm4);
					la.add(cm5);
					la.add(cm7);
					la.add(cm10);
					alist.add(la);
				}
				String po = fdate + sdate + mcode;
				String encoding = System.getProperty("file.encoding");
				String pdfDisplayName = new String(mname.getBytes("utf-8"),
						encoding);
				if (ml.size() > 0) {
					FileOutputStream fl = new FileOutputStream("/tempsettle/"
							+ po + ".xls");
					OutputStream out = fl;
					ExportExcel oe = new ExportExcel();
					oe.extractExcel(alist, out, "sheet1", false, true, true);
					out.flush();
					out.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("outbills")
	public Response outbills(@QueryParam("sdate") String sdate,
			@QueryParam("fdate") String fdate, @QueryParam("qa") String qa) {
		try {
			Map<String, String> condition = new HashMap<String, String>();

			condition.put("mdate", sdate);
			condition.put("fdate", fdate);

			condition.put("status", "1");

			List<Map<String, Object>> ml = tempServer.listbills(condition, 0,
					tempServer.totalBills(condition));
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");
				Merchant mer = merchantServer.MerchantByid(merid);
				op.put("mname", mer.getMname());
				op.put("mcode", mer.getMcode());
				op.put("bankaname", mer.getAccountname());
				op.put("bankaccount", mer.getKfbaacount());

			}

			List<Map<String, String>> rl = filByMer(ml);

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			{
				List<Map<String, String>> dda = new ArrayList<Map<String, String>>();
				Map<String, String> dm = new HashMap<String, String>();
				dm.put("cellno", "3");
				dm.put("value", fdate + "-" + sdate + "商户支付宝清分汇总单");
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
				cm8.put("value", "支付宝手续费");
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
				String pflag = paycode.substring(2, 5);
				if (!pflag.equals("101"))
					continue;

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

			String pdfDisplayName = new String(
					("支付宝清分" + fdate + "-" + sdate).getBytes("gb2312"),
					"ISO8859-1");
			return Response
					.ok(200)
					.header("Content-Type",
							"application/vnd.ms-excel;charset=utf-8")
					.header("Content-Disposition",
							"attachment;filename=\"" + pdfDisplayName + ".xls"
									+ "\"").entity(new StreamingOutput() {
						@Override
						public void write(OutputStream outputStream)
								throws IOException, WebApplicationException {
							ExportExcel oe = new ExportExcel();
							oe.extractExcel(alist, outputStream, "sheet1",
									false, true, true);
						}
					}).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity("").build();
		}
	}

	@GET
	@Path("outbillswx")
	public Response outbillswx(@QueryParam("sdate") String sdate,
			@QueryParam("fdate") String fdate, @QueryParam("qa") String qa) {
		try {
			Map<String, String> condition = new HashMap<String, String>();

			condition.put("mdate", sdate);
			condition.put("fdate", fdate);

			condition.put("status", "1");

			List<Map<String, Object>> ml = tempServer.listbills(condition, 0,
					tempServer.totalBills(condition));
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");
				Merchant mer = merchantServer.MerchantByid(merid);
				op.put("mname", mer.getMname());
				op.put("mcode", mer.getMcode());
				op.put("bankaname", mer.getAccountname());
				op.put("bankaccount", mer.getKfbaacount());

			}

			List<Map<String, String>> rl = filByMer(ml);

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			{
				List<Map<String, String>> dda = new ArrayList<Map<String, String>>();
				Map<String, String> dm = new HashMap<String, String>();
				dm.put("cellno", "3");
				dm.put("value", fdate + "-" + sdate + "商户微信清分汇总单");
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
				cm8.put("value", "微信手续费");
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
				String pflag = paycode.substring(2, 5);
				if (!pflag.equals("201"))
					continue;

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

			String pdfDisplayName = new String(
					("微信清分" + fdate + "-" + sdate).getBytes("gb2312"),
					"ISO8859-1");
			return Response
					.ok(200)
					.header("Content-Type",
							"application/vnd.ms-excel;charset=utf-8")
					.header("Content-Disposition",
							"attachment;filename=\"" + pdfDisplayName + ".xls"
									+ "\"").entity(new StreamingOutput() {
						@Override
						public void write(OutputStream outputStream)
								throws IOException, WebApplicationException {
							ExportExcel oe = new ExportExcel();
							oe.extractExcel(alist, outputStream, "sheet1",
									false, true, true);
						}
					}).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity("").build();
		}
	}

	private List<Map<String, String>> filByMer(List<Map<String, Object>> ml) {
		List<Map<String, String>> rl = new ArrayList<Map<String, String>>();
		Map<String, Map<String, String>> dm = new HashMap<String, Map<String, String>>();
		for (int i = 0; i < ml.size(); i++) {
			Map<String, Object> op = ml.get(i);
			String paycode = (String) op.get("payCode");

			String merid = (String) op.get("merid");
			Map<String, String> om = dm.get(paycode);
			String type = (String) op.get("tradetype");
			if (om == null) {
				om = new HashMap<String, String>();
				om.put("mname", (String) op.get("mname"));
				om.put("mcode", (String) op.get("mcode"));
				om.put("bankaname", (String) op.get("bankaname"));
				om.put("bankaccount", (String) op.get("bankaccount"));
				om.put("paycode", (String) op.get("payCode"));

				if (type.equals("pay")) {
					om.put("tt", "1");
					om.put("tp", "1");
					om.put("tr", "0");
					om.put("trefund", "0");
					om.put("tsum", getStringI(op.get("sum")));
					om.put("tmfee", getStringI(op.get("mfee")));
					om.put("tfee", getStringI(op.get("fee")));
					om.put("tincome", getStringI(op.get("income")));
					om.put("ttransfermer", getStringI(op.get("transfermer")));
				} else {
					om.put("tt", "1");
					om.put("tr", "1");
					om.put("tp", "0");
					om.put("trefund", getStringI(op.get("sum")));
					om.put("tsum", "0");
					om.put("tmfee", "0");
					om.put("tfee", "0");
					om.put("tincome", "0");
					om.put("ttransfermer", "-" + getStringI(op.get("sum")));
				}
				dm.put(paycode, om);
			} else {
				om.put("tt",
						new BigDecimal(om.get("tt")).add(new BigDecimal(1))
								.setScale(0, RoundingMode.HALF_DOWN).toString());
				if (type.equals("pay")) {

					om.put("tp",
							new BigDecimal(om.get("tp")).add(new BigDecimal(1))
									.setScale(0, RoundingMode.HALF_DOWN)
									.toString());
					om.put("tsum",
							new BigDecimal(om.get("tsum"))
									.add(new BigDecimal(getStringI(op
											.get("sum"))))
									.setScale(2, RoundingMode.HALF_DOWN)
									.toString());
					om.put("tmfee",
							new BigDecimal(om.get("tmfee"))
									.add(new BigDecimal(getStringI(op
											.get("mfee"))))
									.setScale(2, RoundingMode.HALF_DOWN)
									.toString());
					om.put("tfee",
							new BigDecimal(om.get("tfee"))
									.add(new BigDecimal(getStringI(op
											.get("fee"))))
									.setScale(2, RoundingMode.HALF_DOWN)
									.toString());
					om.put("tincome",
							new BigDecimal(om.get("tincome"))
									.add(new BigDecimal(getStringI(op
											.get("income"))))
									.setScale(2, RoundingMode.HALF_DOWN)
									.toString());
					om.put("ttransfermer",
							new BigDecimal(om.get("ttransfermer"))
									.add(new BigDecimal(getStringI(op
											.get("transfermer"))))
									.setScale(2, RoundingMode.HALF_DOWN)
									.toString());
				} else {
					om.put("tr",
							new BigDecimal(om.get("tr")).add(new BigDecimal(1))
									.setScale(0, RoundingMode.HALF_DOWN)
									.toString());
					om.put("trefund",
							new BigDecimal(om.get("trefund"))
									.add(new BigDecimal(getStringI(op
											.get("sum"))))
									.setScale(2, RoundingMode.HALF_DOWN)
									.toString());
					om.put("ttransfermer",
							new BigDecimal(om.get("ttransfermer"))
									.add(new BigDecimal(getStringI(op
											.get("transfermer"))))
									.setScale(2, RoundingMode.HALF_DOWN)
									.toString());
				}
			}
		}
		for (Map.Entry<String, Map<String, String>> entry : dm.entrySet()) {
			rl.add(entry.getValue());
		}
		return rl;

	}

	private List<Map<String, String>> outform(Merchant merchant, String bill,
			String name, String account, String banknum, int pos) {
		List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
		List<Map<String, String>> cl = new ArrayList<Map<String, String>>();
		Map<String, String> cm = new HashMap<String, String>();
		cm.put("cellno", "0");
		cm.put("value", String.valueOf(pos));
		Map<String, String> cm1 = new HashMap<String, String>();
		cm1.put("cellno", "1");
		cm1.put("value", name);
		cm1.put("setlen", "1");
		Map<String, String> cm2 = new HashMap<String, String>();
		cm2.put("cellno", "2");
		cm2.put("value", account);
		Map<String, String> cm3 = new HashMap<String, String>();
		cm3.put("cellno", "3");
		cm3.put("value", banknum);
		Map<String, String> cm4 = new HashMap<String, String>();
		cm4.put("cellno", "4");
		cm4.put("value", merchant.getKfbaacount().replaceAll("\\s*", ""));
		Map<String, String> cm5 = new HashMap<String, String>();
		cm5.put("cellno", "5");
		cm5.put("value", merchant.getAccountname());
		cm5.put("setlen", "1");
		Map<String, String> cm7 = new HashMap<String, String>();
		cm7.put("cellno", "7");
		cm7.put("value", merchant.getKfbank());
		cm7.put("setlen", "1");
		Map<String, String> cm10 = new HashMap<String, String>();
		cm10.put("cellno", "10");
		cm10.put("value", merchant.getTransign());
		Map<String, String> cm11 = new HashMap<String, String>();
		cm11.put("cellno", "11");
		cm11.put("value", bill);
		Map<String, String> cm12 = new HashMap<String, String>();
		cm12.put("cellno", "12");
		cm12.put("value", "01");
		Map<String, String> cm13 = new HashMap<String, String>();
		cm13.put("cellno", "13");
		cm13.put("value", "转款");
		cm13.put("setlen", "1");
		cl.add(cm);
		cl.add(cm1);
		cl.add(cm2);
		cl.add(cm3);
		cl.add(cm4);
		cl.add(cm5);
		cl.add(cm7);
		cl.add(cm10);
		cl.add(cm11);
		cl.add(cm12);
		cl.add(cm13);
		return cl;
	}

	private List<Map<String, String>> outprivate(Merchant merchant,
			String bill, int pos) {
		List<Map<String, String>> cl = new ArrayList<Map<String, String>>();
		Map<String, String> cm = new HashMap<String, String>();
		cm.put("cellno", "0");
		cm.put("value", String.valueOf(pos));
		Map<String, String> cm1 = new HashMap<String, String>();
		cm1.put("cellno", "1");
		cm1.put("value", merchant.getKfbaacount().replaceAll("\\s*", ""));
		cm1.put("setlen", "1");
		Map<String, String> cm2 = new HashMap<String, String>();
		cm2.put("cellno", "2");
		cm2.put("value", merchant.getAccountname());
		Map<String, String> cm3 = new HashMap<String, String>();
		cm3.put("cellno", "3");
		cm3.put("value", bill);
		cl.add(cm);
		cl.add(cm1);
		cl.add(cm2);
		cl.add(cm3);
		return cl;
	}

	private List<Map<String, String>> outform1(Merchant merchant, String bill,
			String name, String account, String banknum, int pos) {
		List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
		List<Map<String, String>> cl = new ArrayList<Map<String, String>>();
		Map<String, String> cm = new HashMap<String, String>();
		cm.put("cellno", "0");
		cm.put("value", String.valueOf(pos));
		Map<String, String> cm1 = new HashMap<String, String>();
		cm1.put("cellno", "1");
		cm1.put("value", name);
		cm1.put("setlen", "1");
		Map<String, String> cm2 = new HashMap<String, String>();
		cm2.put("cellno", "2");
		cm2.put("value", account);
		Map<String, String> cm3 = new HashMap<String, String>();
		cm3.put("cellno", "3");
		cm3.put("value", banknum);
		Map<String, String> cm4 = new HashMap<String, String>();
		cm4.put("cellno", "4");
		cm4.put("value", merchant.getKfbaacount().replaceAll("\\s*", ""));
		Map<String, String> cm5 = new HashMap<String, String>();
		cm5.put("cellno", "5");
		cm5.put("value", merchant.getAccountname());
		cm5.put("setlen", "1");
		Map<String, String> cm7 = new HashMap<String, String>();
		cm7.put("cellno", "7");
		cm7.put("value", merchant.getKfbank());
		cm7.put("setlen", "1");
		Map<String, String> cm10 = new HashMap<String, String>();
		cm10.put("cellno", "10");
		cm10.put("value", merchant.getTransign());
		Map<String, String> cm11 = new HashMap<String, String>();
		cm11.put("cellno", "11");
		cm11.put("value", bill);
		Map<String, String> cm12 = new HashMap<String, String>();
		cm12.put("cellno", "12");
		cm12.put("value", "01");
		Map<String, String> cm13 = new HashMap<String, String>();
		cm13.put("cellno", "13");
		cm13.put("value", "转款");
		cm13.put("setlen", "1");
		Map<String, String> cm14 = new HashMap<String, String>();
		cm14.put("cellno", "14");
		cm14.put("value", merchant.getMcode());
		cm14.put("setlen", "1");
		cl.add(cm);
		cl.add(cm1);
		cl.add(cm2);
		cl.add(cm3);
		cl.add(cm4);
		cl.add(cm5);
		cl.add(cm7);
		cl.add(cm10);
		cl.add(cm11);
		cl.add(cm12);
		cl.add(cm13);
		cl.add(cm14);

		return cl;
	}

	private String getString(Object ob) {
		try {
			return String.valueOf(ob);
		} catch (Exception e) {
			return "";
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
