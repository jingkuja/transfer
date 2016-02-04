package org.aves.transfer.imp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.aves.transfer.api.IMerchantDeviceServer;
import org.aves.transfer.api.IMerchantServer;
import org.aves.transfer.api.ISettlesServer;
import org.aves.transfer.bean.Bill;
import org.aves.transfer.bean.Merchant;
import org.aves.transfer.bean.MerchantDevice;
import org.aves.transfer.bean.Settles;
import org.aves.transfer.json.JSONArray;
import org.aves.transfer.json.JSONObject;

@Path("api")
public class BillResource {

	private static final Logger logger = Logger.getLogger(BillResource.class
			.getName());

	private IMerchantServer merchantServer;

	private IMerchantDeviceServer merchantDeviceServer;

	private ISettlesServer setteleServer;

	private BillServer billServer;

	private String SHA1Key;

	private String accountname;

	private String bankfl;

	private String bankaccount;

	public BillResource() {

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

	public void setBillServer(BillServer billServer) {
		this.billServer = billServer;
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

	@POST
	@Path("agreements/{agrmtId}/terminals/{terminalId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(@PathParam("agrmtId") String agrmtId,
			@PathParam("terminalId") String terminal,
			@HeaderParam("Authorization") String Authorization,
			String jsonStringContent) {
		JSONObject js;
		try {
			js = new JSONObject(jsonStringContent);
			String postradeNo = js.optString("postradeNo", "");
			String tradeTime = js.optString("tradeTime", "");
			String tradeType = js.optString("tradeType", "");
			String sum = js.optString("sum", "");
			if (!auth(agrmtId, terminal, Authorization))
				return Response.status(401).entity("").build();

			Bill bill = billServer.billbytradno(postradeNo);
			if (bill != null) {
				return Response.status(204).entity("该交易序号已导入").build();
			}

			Settles se = setteleServer.SettlesByAid(agrmtId, false);

			bill = new Bill();

			bill.setPostradeNo(postradeNo);
			bill.setSum(sum);
			bill.setTerminal(terminal);
			bill.setTradetype(tradeType);
			bill.setTradetime(tradeTime);
			bill.setPaysrvCode(agrmtId);
			bill.setMerid(se.getMerchantid());

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
					String rate = se.getRebate();
					String ratea = se.getRebatea();
					bill.setRate(rate);
					bill.setRatea(ratea);
					BigDecimal dsum = new BigDecimal(sum);
					BigDecimal drate = new BigDecimal(rate);
					BigDecimal dratea = new BigDecimal(ratea);
					BigDecimal hunder = new BigDecimal(100);

					double mfee = dsum.multiply(drate)
							.divide(hunder, 2, RoundingMode.HALF_UP)
							.doubleValue();
					double fee = dsum.multiply(dratea)
							.divide(hunder, 2, RoundingMode.HALF_UP)
							.doubleValue();
					double income = new BigDecimal(mfee)
							.subtract(new BigDecimal(fee))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					double transfermer = dsum.subtract(new BigDecimal(mfee))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					if (fee >= 0.01)
						bill.setFee(String.valueOf(fee));
					if (mfee >= 0.01)
						bill.setMfee(String.valueOf(mfee));
					bill.setIncome(String.valueOf(income));
					bill.setTransfermer(String.valueOf(transfermer));

				} else {
					bill.setTransfermer("-" + sum);
				}
				billServer.importbills(bill);
				status = 201;
				return Response.status(status).entity("导入成功").build();

			} else {
				bill.setStatus("0");
				if (status == 202) {
					bill.setSdesc("商户或pos终端不存在");
				}
				if (status == 203) {
					bill.setSdesc("商户无激活协议");
				}
				billServer.importbills(bill);
				return Response.status(status).entity("导入失败").build();

			}

		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
			return Response.status(501).entity("").build();
		}
	}

	@POST
	@Path("setbills")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setbills(@QueryParam("sdate") String sdate) {
		try {
			Map<String, String> condition = new HashMap<String, String>();

			condition.put("sdate", sdate);
			condition.put("status", "0");

			List<Map<String, Object>> ml = billServer.listbills(condition, 0,
					billServer.totalBills(condition));

			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String postradeNo = (String) op.get("postradeNo");
				Bill bill = billServer.billbytradno(postradeNo);
				String merchant = bill.getPaysrvCode();
				Settles se = setteleServer.SettlesByAid(merchant, false);
				String tradeType = bill.getTradetype();
				boolean flag = true;
				int status = 201;

				if (se == null || se.getId() == null) {
					flag = false;
					status = 203;
				} else {
					bill.setMerid(se.getMerchantid());
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
						String rate = se.getRebate();
						String ratea = se.getRebatea();
						bill.setRate(rate);
						bill.setRatea(ratea);
						BigDecimal dsum = new BigDecimal(bill.getSum());
						BigDecimal drate = new BigDecimal(rate);
						BigDecimal dratea = new BigDecimal(ratea);
						BigDecimal hunder = new BigDecimal(100);

						double mfee = dsum.multiply(drate)
								.divide(hunder, 2, RoundingMode.HALF_UP)
								.doubleValue();
						double fee = dsum.multiply(dratea)
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
						if (fee >= 0.01)
							bill.setFee(String.valueOf(fee));
						if (mfee >= 0.01)
							bill.setMfee(String.valueOf(mfee));
						bill.setIncome(String.valueOf(income));
						bill.setTransfermer(String.valueOf(transfermer));

					} else {
						bill.setTransfermer("-" + bill.getSum());
					}
					billServer.importbills(bill);
					status = 201;

				} else {
					bill.setStatus("0");
					if (status == 202) {
						bill.setSdesc("商户或pos终端不存在");
					}
					if (status == 203) {
						bill.setSdesc("商户无激活协议");
					}
					billServer.importbills(bill);
				}
			}
			String re = "{\"status\":\"0\",\"msg\":\"" + "完成" + "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + "完成" + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("listbills")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listmerchant(String jsonStringContent,
			@QueryParam("st") String st, @QueryParam("li") String li) {
		JSONObject js;
		try {
			Map<String, String> condition = new HashMap<String, String>();
			js = new JSONObject(jsonStringContent);

			condition.put("mname", js.optString("mname", ""));
			condition.put("ssdate", js.optString("acquiredate", ""));
			condition.put("edate", js.optString("acquiredate1", ""));
			condition.put("status", "1");

			int s = 0;
			int l = 2;

			try {
				s = Integer.valueOf(st);
				l = Integer.valueOf(li);

			} catch (Exception e) {
			}
			List<Map<String, Object>> ml = billServer
					.listbills(condition, s, l);
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");
				String paysrvCode = (String) op.get("payCode");

				Merchant mer = merchantServer.MerchantByid(merid);
				if (mer != null)
					op.put("mname", mer.getMname());

				Settles se = setteleServer.SettlesByAid(paysrvCode, true);
				if (se != null)
					op.put("pname", se.getTitle());
			}

			JSONObject da = new JSONObject();
			JSONArray ja = new JSONArray(ml);
			da.put("total", billServer.totalBills(condition));
			da.put("ne", s);
			da.put("con", ja);
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			logger.info(e.getMessage());
			return Response.ok().entity("").build();
		}
	}

	@POST
	@Path("clistbills")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response clistbills(String jsonStringContent,
			@CookieParam("pinfo") String pinfo, @QueryParam("st") String st,
			@QueryParam("li") String li) {
		JSONObject js;
		try {
			Map<String, String> condition = new HashMap<String, String>();
			String[] pin = pinfo.split("[*]");
			js = new JSONObject(jsonStringContent);
			condition.put("merid", pin[3]);
			condition.put("mname", js.optString("mname", ""));
			condition.put("sdate", js.optString("acquiredate", ""));
			condition.put("status", "1");

			int s = 0;
			int l = 15;

			try {
				s = Integer.valueOf(st);
				l = Integer.valueOf(li);

			} catch (Exception e) {
			}
			List<Map<String, Object>> ml = billServer
					.listbills(condition, s, l);
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");
				String paysrvCode = (String) op.get("payCode");

				Merchant mer = merchantServer.MerchantByid(merid);
				if (mer != null)
					op.put("mname", mer.getMname());

				Settles se = setteleServer.SettlesByAid(paysrvCode, true);
				if (se != null)
					op.put("pname", se.getTitle());
			}

			JSONObject da = new JSONObject();
			JSONArray ja = new JSONArray(ml);
			da.put("total", billServer.totalBills(condition));
			da.put("ne", s);
			da.put("con", ja);
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			logger.info(e.getMessage());
			return Response.ok().entity("").build();
		}
	}

	@POST
	@Path("flistbills")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response flistmerchant(String jsonStringContent,
			@QueryParam("st") String st, @QueryParam("li") String li) {
		JSONObject js;
		try {
			Map<String, String> condition = new HashMap<String, String>();
			js = new JSONObject(jsonStringContent);

			condition.put("mname", js.optString("mname", ""));
			condition.put("sdate", js.optString("acquiredate", ""));
			condition.put("status", "0");

			int s = 0;
			int l = 2;

			try {
				s = Integer.valueOf(st);
				l = Integer.valueOf(li);

			} catch (Exception e) {
			}
			List<Map<String, Object>> ml = billServer
					.listbills(condition, s, l);
			for (int i = 0; i < ml.size(); i++) {
				Map<String, Object> op = ml.get(i);
				String merid = (String) op.get("merid");
				String paysrvCode = (String) op.get("payCode");

				Merchant mer = merchantServer.MerchantByid(merid);
				if (mer != null)
					op.put("mname", mer.getMname());

				Settles se = setteleServer.SettlesByAid(paysrvCode, true);
				if (se != null)
					op.put("pname", se.getTitle());
			}

			JSONObject da = new JSONObject();
			JSONArray ja = new JSONArray(ml);
			da.put("total", billServer.totalBills(condition));
			da.put("ne", s);
			da.put("con", ja);
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			return Response.ok().entity("").build();
		}
	}

	@GET
	@Path("tranl")
	public Response tranl(@QueryParam("acquiredate") String sdate,
			@QueryParam("edate") String edate) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("ssdate", sdate);
			condition.put("edate", edate);
			condition.put("status", "1");
			List<Map<String, Object>> ml = billServer.listbills(condition, 0,
					billServer.totalBills(condition));

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
					(sdate + "-" + edate + "公对公转账单" + toal.setScale(2,
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
	public Response trane(@QueryParam("acquiredate") String sdate,
			@QueryParam("edate") String edate) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("ssdate", sdate);
			condition.put("edate", edate);
			condition.put("status", "1");
			List<Map<String, Object>> ml = billServer.listbills(condition, 0,
					billServer.totalBills(condition));
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
					(sdate + "-" + edate + "代发代扣转账单" + toal.setScale(2,
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

	@POST
	@Path("ttranl")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// 提交的数据类型是application/form-data
	public Response ttranl(@Multipart("fileToUpload") byte[] file) {
		try {
			InputStream sbs = new ByteArrayInputStream(file);
			ReadExcel re = new ReadExcel();
			List<List<String>> result = re.extractExcel(sbs);
			String sdate = "";
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dm = new SimpleDateFormat("yyyy/MM/dd");
			Map<String, String> cm = new HashMap<String, String>();
			int fa = 0;
			for (int i = 1; i < result.size(); i++) {
				List<String> op = result.get(i);
				if (i == 10) {
					String ss = op.get(3);
					try {
						Date ddate = dm.parse(ss);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(ddate);
						calendar.add(Calendar.DAY_OF_WEEK, -1);
						sdate = sm.format(calendar.getTime());
					} catch (Exception e) {
						throw new Exception(
								"读取收单日期出错，请将excel文件日期内单元格改为纯文本，格式 'yyyy/MM/dd'");
					}
				}

				String na = op.get(2);
				if (isNumeric(na) && fa == 0) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					bd = bd.subtract(bd.multiply(new BigDecimal(se.getRebate())));
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}
				if (na.equals("人民币卡合计")) {
					fa = 1;
				}
				if (isNumeric(na) && fa == 1) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					BigDecimal be = new BigDecimal(op.get(6));
					bd = bd.subtract(be);
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}

			}

			Map<String, String> condition = new HashMap<String, String>();
			condition.put("sdate", sdate);
			condition.put("status", "1");
			List<Map<String, Object>> ml = billServer.listbills(condition, 0,
					billServer.totalBills(condition));

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			int pos = 1;

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
					(sdate + "汇总公对公转账单" + toal.setScale(2,
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
			return Response.ok().entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("ltranl")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// 提交的数据类型是application/form-data
	public Response ltranl(@Multipart("fileToUpload") byte[] file) {
		try {
			InputStream sbs = new ByteArrayInputStream(file);
			ReadExcel re = new ReadExcel();
			List<List<String>> result = re.extractExcel(sbs);
			String sdate = "";
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dm = new SimpleDateFormat("yyyy/MM/dd");
			Map<String, String> cm = new HashMap<String, String>();
			int fa = 0;
			for (int i = 1; i < result.size(); i++) {
				List<String> op = result.get(i);
				if (i == 10) {
					String ss = op.get(3);
					try {
						Date ddate = dm.parse(ss);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(ddate);
						calendar.add(Calendar.DAY_OF_WEEK, -1);
						sdate = sm.format(calendar.getTime());
					} catch (Exception e) {
						throw new Exception(
								"读取收单日期出错，请将excel文件日期内单元格改为纯文本，格式 'yyyy/MM/dd'");
					}
				}

				String na = op.get(2);
				if (isNumeric(na) && fa == 0) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					bd = bd.subtract(bd.multiply(new BigDecimal(se.getRebate())));
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}
				if (na.equals("人民币卡合计")) {
					fa = 1;
				}
				if (isNumeric(na) && fa == 1) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					BigDecimal be = new BigDecimal(op.get(6));
					bd = bd.subtract(be);
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}

			}

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			int pos = 1;

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
					(sdate + "建行公对公转账单" + toal.setScale(2,
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
			return Response.ok().entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("ttrane")
	public Response ttrane(@Multipart("fileToUpload") byte[] file) {
		try {
			InputStream sbs = new ByteArrayInputStream(file);
			ReadExcel re = new ReadExcel();
			List<List<String>> result = re.extractExcel(sbs);
			String sdate = "";
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dm = new SimpleDateFormat("yyyy/MM/dd");
			Map<String, String> cm = new HashMap<String, String>();
			int fa = 0;
			for (int i = 1; i < result.size(); i++) {
				List<String> op = result.get(i);
				if (i == 10) {
					String ss = op.get(3);
					try {
						Date ddate = dm.parse(ss);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(ddate);
						calendar.add(Calendar.DAY_OF_WEEK, -1);
						sdate = sm.format(calendar.getTime());
					} catch (Exception e) {
						throw new Exception(
								"读取收单日期出错，请将excel文件日期内单元格改为纯文本，格式 'yyyy/MM/dd'");
					}
				}

				String na = op.get(2);
				if (isNumeric(na) && fa == 0) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					bd = bd.subtract(bd.multiply(new BigDecimal(se.getRebate())));
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}

				if (na.equals("人民币卡合计")) {
					fa = 1;
				}
				if (isNumeric(na) && fa == 1) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					BigDecimal be = new BigDecimal(op.get(6));
					bd = bd.subtract(be);
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}

			}

			Map<String, String> condition = new HashMap<String, String>();
			condition.put("sdate", sdate);
			condition.put("status", "1");
			List<Map<String, Object>> ml = billServer.listbills(condition, 0,
					billServer.totalBills(condition));
			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			int pos = 1;
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
					(sdate + "汇总代发代扣转账单" + toal.setScale(2,
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

	@POST
	@Path("ltrane")
	public Response ltrane(@Multipart("fileToUpload") byte[] file) {
		try {
			InputStream sbs = new ByteArrayInputStream(file);
			ReadExcel re = new ReadExcel();
			List<List<String>> result = re.extractExcel(sbs);
			String sdate = "";
			SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dm = new SimpleDateFormat("yyyy/MM/dd");
			Map<String, String> cm = new HashMap<String, String>();
			int fa = 0;
			for (int i = 1; i < result.size(); i++) {
				List<String> op = result.get(i);
				if (i == 10) {
					String ss = op.get(3);
					try {
						Date ddate = dm.parse(ss);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(ddate);
						calendar.add(Calendar.DAY_OF_WEEK, -1);
						sdate = sm.format(calendar.getTime());
					} catch (Exception e) {
						throw new Exception(
								"读取收单日期出错，请将excel文件日期内单元格改为纯文本，格式 'yyyy/MM/dd'");
					}
				}

				String na = op.get(2);
				if (isNumeric(na) && fa == 0) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					bd = bd.subtract(bd.multiply(new BigDecimal(se.getRebate())));
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}
				if (na.equals("人民币卡合计")) {
					fa = 1;
				}
				if (isNumeric(na) && fa == 1) {
					Settles se = setteleServer.SettlesByAid(na, false);
					BigDecimal bd = new BigDecimal(op.get(5));
					BigDecimal be = new BigDecimal(op.get(6));
					bd = bd.subtract(be);
					if (cm.get(se.getMerchantid()) == null)
						cm.put(se.getMerchantid(),
								bd.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					else {
						String dsum = cm.get(se.getMerchantid());
						BigDecimal dg = new BigDecimal(dsum);
						// 不计算退款
						// if (sg.compareTo(new BigDecimal(0)) < 0)
						// continue;
						dg = dg.add(bd);
						cm.put(se.getMerchantid(),
								dg.setScale(2, RoundingMode.HALF_DOWN)
										.toString());
					}
				}

			}

			final List<List<Map<String, String>>> alist = new ArrayList<List<Map<String, String>>>();
			int pos = 1;
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
					(sdate + "建行代发代扣转账单" + toal.setScale(2,
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
	@Path("detailbills")
	public Response detaiabills(@QueryParam("sdate") String sdate,
			@QueryParam("edate") String edate, @QueryParam("qa") String qa) {
		try {
			Map<String, String> condition = new HashMap<String, String>();

			condition.put("ssdate", sdate);
			condition.put("edate", edate);
			condition.put("status", "1");

			List<Map<String, Object>> ml = billServer.listbills(condition, 0,
					billServer.totalBills(condition));
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
			ExportTemplet ex = new ExportTemplet();
			final Map<String, List<List<Map<String, String>>>> am = new HashMap<String, List<List<Map<String, String>>>>();

			List<List<Map<String, String>>> alist = ex.outAlist("商户支付宝清分汇总单",
					sdate + "-" + edate, "101", rl);
			am.put("支付宝", alist);
			List<List<Map<String, String>>> alist1 = ex.outAlist("商户微信清分汇总单",
					sdate + "-" + edate, "201", rl);
			am.put("微信", alist1);

			List<List<Map<String, String>>> alist2 = ex.outAlist(
					"商户建行pos清分汇总单", sdate + "-" + edate, "001", rl);
			am.put("建行pos", alist2);

			List<List<Map<String, String>>> alist3 = ex.outAlist("商户翼支付清分汇总单",
					sdate + "-" + edate, "301", rl);
			am.put("翼支付", alist3);

			List<List<Map<String, String>>> alista = ex.outTAlist("清分汇总总计",
					sdate + "-" + edate, "000", rl);
			am.put("总计", alista);

			String pdfDisplayName = new String(
					("支付对账单" + sdate + "-" + edate).getBytes("gb2312"),
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
							oe.processExcel(am, outputStream, false, true, true);
						}
					}).build();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok().entity("").build();
		}
	}

	private boolean auth(String vendorsIdentifier, String terminal, String auth) {
		StringBuilder sb = new StringBuilder(vendorsIdentifier)
				.append(terminal);
		byte[] contentBytes = sb.toString().getBytes();
		byte[] keyBytes = Base64.decodeBase64(SHA1Key);
		byte[] signContent = new byte[contentBytes.length + keyBytes.length];
		System.arraycopy(contentBytes, 0, signContent, 0, contentBytes.length);
		System.arraycopy(keyBytes, 0, signContent, contentBytes.length,
				keyBytes.length);
		byte[] signature = DigestUtils.sha(signContent);
		String signatureString = Base64.encodeBase64String(signature);
		if (signatureString.equals(auth))
			return true;
		return false;
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
				om.put("paycode", getString(op.get("payCode")));

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
		cm2.put("setlen", "1");
		Map<String, String> cm3 = new HashMap<String, String>();
		cm3.put("cellno", "3");
		cm3.put("value", bill);
		cl.add(cm);
		cl.add(cm1);
		cl.add(cm2);
		cl.add(cm3);
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

	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]{1,}");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

}
