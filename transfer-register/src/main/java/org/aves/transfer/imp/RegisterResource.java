package org.aves.transfer.imp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.aves.transfer.bean.Merchant;
import org.aves.transfer.bean.MerchantDevice;
import org.aves.transfer.bean.Settles;
import org.aves.transfer.json.JSONArray;
import org.aves.transfer.json.JSONObject;

@Path("register")
public class RegisterResource {

	private static final Logger logger = Logger
			.getLogger(RegisterResource.class.getName());

	private MerchantServer merchantServer;

	private MerchantDeviceServer merchantDeviceServer;

	private SetteleServer setteleServer;

	private String webServiceURL;

	private String SHA1Key;

	private SSLConnectionSocketFactory sslConnectionSocketFactory;

	public RegisterResource() {

	}

	public void setMerchantServer(MerchantServer merchantServer) {
		this.merchantServer = merchantServer;
	}

	public void setMerchantDeviceServer(
			MerchantDeviceServer merchantDeviceServer) {
		this.merchantDeviceServer = merchantDeviceServer;
	}

	public void setSetteleServer(SetteleServer setteleServer) {
		this.setteleServer = setteleServer;
	}

	public void setWebServiceURL(String webServiceURL) {
		this.webServiceURL = webServiceURL;
	}

	public void setSHA1Key(String sHA1Key) {
		SHA1Key = sHA1Key;
	}

	@POST
	@Path("merchant")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(String jsonStringContent) {
		JSONObject js;
		try {
			String msg = "创建成功!";
			String result = "1";
			js = new JSONObject(jsonStringContent);
			Merchant merchant = new Merchant();
			merchant.setAddress(js.optString("merchtAddress", ""));
			merchant.setContactor("");
			merchant.setTransign("0");
			merchant.setKfbaacount(js.optString("merchtBankNum", "")
					.replaceAll("\\s*", ""));
			merchant.setKfbank(js.optString("merchtBankTitle", ""));
			merchant.setMname(js.optString("merchtTilte", ""));
			merchant.setPhone(js.optString("merchtPhone", ""));
			merchant.setMcode(js.optString("merchtNum", "").replaceAll("\\s*",
					""));
			merchant.setEmail(js.optString("email", ""));
			merchant.setTransign(js.optString("transign", ""));
			merchant.setTplate(js.optString("tplate", ""));
			merchant.setAccountname(js.optString("accountname", ""));
			merchantServer.createMerchant(merchant);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("importmerchant")
	@Produces(MediaType.TEXT_PLAIN)
	// 注意这就是前面提到的Content-Type坑！
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// 提交的数据类型是application/form-data
	public String uploadAttributeExcel(@Multipart("fileToUpload") byte[] file) {
		try {
			InputStream sbs = new ByteArrayInputStream(file);
			ReadExcel re = new ReadExcel();
			List<List<String>> result = re.extractExcel(sbs);
			for (int i = 1; i < result.size(); i++) {
				List<String> op = result.get(i);
				String code = op.get(0);
				Merchant merchant = merchantServer.MerchantBycode(code);
				merchant.setMcode(code);
				merchant.setMname(op.get(1));
				merchant.setKfbank(op.get(2));
				merchant.setAccountname(op.get(3));
				merchant.setKfbaacount(op.get(4));
				merchant.setTransign(op.get(5));

				if (op.get(6) != null && !op.get(6).trim().equals(""))
					merchant.setTplate(op.get(6));
				else
					merchant.setTplate("0");
				merchant.setAddress(op.get(7));
				merchant.setPhone(op.get(8));
				merchant.setEmail(op.get(9));
				if (merchant.getId() == null)
					merchantServer.createMerchant(merchant);
				else
					merchantServer.updateMerchant(merchant);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String msg = "操作完成";
		String re = "{\"status\":\"" + 1 + "\",\"msg\":\"" + msg + "\"}";
		return re;

	}

	@POST
	@Path("merchantupdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(String jsonStringContent) {
		JSONObject js;
		try {
			String msg = "修改成功!";
			String result = "1";
			js = new JSONObject(jsonStringContent);
			Merchant merchant = new Merchant();
			merchant.setAddress(js.optString("merchtAddress", ""));
			merchant.setContactor("");
			merchant.setTransign("0");
			merchant.setKfbaacount(js.optString("merchtBankNum", "")
					.replaceAll("\\s*", ""));
			merchant.setKfbank(js.optString("merchtBankTitle", ""));
			merchant.setMname(js.optString("merchtTilte", ""));
			merchant.setPhone(js.optString("merchtPhone", ""));
			merchant.setId(js.optString("userid", ""));
			merchant.setMcode(js.optString("merchtNum", "").replaceAll("\\s*",
					""));
			merchant.setEmail(js.optString("email", ""));
			merchant.setTransign(js.optString("transign", ""));
			merchant.setTplate(js.optString("tplate", ""));
			merchant.setAccountname(js.optString("accountname", ""));
			merchantServer.updateMerchant(merchant);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("merchantsusp")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response susp(String jsonStringContent) {
		try {
			String msg = "注销成功!";
			String result = "1";
			Merchant merchant = new Merchant();
			merchant.setStatus("0");
			merchant.setId(jsonStringContent);
			// Merchant mc = merchantServer.MerchantByid(jsonStringContent);
			merchantServer.suspendMerchant(merchant);

			Map<String, String> conditon = new HashMap<String, String>();
			conditon.put("mid", jsonStringContent);

			List<Settles> me = setteleServer.settlesbymidnt(conditon);

			for (int i = 0; i < me.size(); i++) {
				Settles eo = me.get(i);
				setteleServer.suspendSettles(eo);
				// disWebservice(eo.getContractNum());
				/**
				 * List<MerchantDevice> md = merchantDeviceServer
				 * .MerchantDeviceBymid(eo.getId()); for (int j = 0; j <
				 * md.size(); j++) { MerchantDevice eo1 = md.get(j);
				 * merchantDeviceServer.suspendMerchantDevice(eo1);
				 * disPosWebservice(eo.getContractNum(), eo1.getPosnum()); }
				 **/

			}

			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@GET
	@Path("getmerchant")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getmerchant(@QueryParam("userid") String id) {
		try {
			Merchant me = merchantServer.MerchantByid(id);
			JSONObject da = new JSONObject();
			da.put("mcode", me.getMcode());
			da.put("mname", me.getMname());
			da.put("phone", me.getPhone());
			da.put("address", me.getAddress());
			da.put("kfbaacount", me.getKfbaacount());
			da.put("kfbank", me.getKfbank());
			da.put("userid", me.getId());
			da.put("tplate", me.getTplate());
			da.put("accountname", me.getAccountname());

			if (me.getEmail() == null || me.getEmail().equals("null")) {
				da.put("email", "");
			} else {
				da.put("email", me.getEmail());
			}
			da.put("transign", me.getTransign());
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"" + 1 + "\",\"msg\":\"" + "初始化信息出错"
					+ "\"}";
			return Response.ok().entity(re).build();

		}
	}

	@POST
	@Path("listmerchant")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listmerchant(String jsonStringContent,
			@QueryParam("st") String st, @QueryParam("li") String li) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			if (!jsonStringContent.equals("")) {
				condition.put("mname", jsonStringContent);
			}

			int s = 0;
			int l = 2;

			try {
				s = Integer.valueOf(st);
				l = Integer.valueOf(li);

			} catch (Exception e) {
			}
			List<Map<String, Object>> ml = merchantServer.listMerchant(
					condition, s, l);
			JSONObject da = new JSONObject();
			JSONArray ja = new JSONArray(ml);
			da.put("total", merchantServer.totalMerchant(condition));
			da.put("ne", s);
			da.put("con", ja);
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			return Response.ok().entity("").build();
		}
	}

	@POST
	@Path("merchantdev")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerdev(String jsonStringContent) {
		JSONObject js;
		try {
			String msg = "创建成功!";
			String result = "1";
			js = new JSONObject(jsonStringContent);
			MerchantDevice MerchantDevice = new MerchantDevice();
			MerchantDevice.setMerchantid(js.getString("mid"));
			MerchantDevice.setPosnum(js.getString("posnum"));
			merchantDeviceServer.createMerchantDevicedevice(MerchantDevice);

			String terminals = "[\"" + js.getString("posnum") + "\"]";
			Settles se = setteleServer.SettlesByid(js.getString("mid"));
			if (se != null && se.getId() != null) {
				// registerPosWebservice(se.getContractNum(), terminals,
				// js.getString("posnum"));
			}
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("devupdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response devupdate(String jsonStringContent) {
		JSONObject js;
		try {
			String msg = "修改成功!";
			String result = "1";
			js = new JSONObject(jsonStringContent);
			MerchantDevice MerchantDevice = new MerchantDevice();
			MerchantDevice.setMerchantid(js.getString("mid"));
			MerchantDevice.setId(js.getString("cid"));
			MerchantDevice.setPosnum(js.getString("posnum"));
			merchantDeviceServer.updateMerchantDevice(MerchantDevice);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("devsusp")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response devsusp(@QueryParam("cid") String cid) {
		try {
			String msg = "注销成功!";
			String result = "1";
			MerchantDevice me = merchantDeviceServer.MerchantDeviceByid(cid);

			me.setId(cid);
			me.setStatus("0");
			merchantDeviceServer.suspendMerchantDevice(me);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			// Settles mc = setteleServer.SettlesByid(me.getMerchantid());
			// disPosWebservice(mc.getContractNum(), me.getPosnum());
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@GET
	@Path("getmerchantdev")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getmerchantdev(@QueryParam("mid") String mid) {
		try {
			MerchantDevice me = merchantDeviceServer.MerchantDeviceByid(mid);
			JSONObject da = new JSONObject();
			da.put("id", me.getId());
			da.put("createtime", me.getCreatetime());
			da.put("deviceNum", me.getDeviceNum());
			da.put("merchantid", me.getMerchantid());
			da.put("posnum", me.getPosnum());
			da.put("status", me.getStatus());
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"" + 1 + "\",\"msg\":\"" + "初始化信息出错"
					+ "\"}";
			return Response.ok().entity(re).build();

		}
	}

	@POST
	@Path("listmerchantdev")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listmerchantdev(String jsonStringContent,
			@QueryParam("st") String st, @QueryParam("li") String li) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			if (!jsonStringContent.equals("")) {
				condition.put("merchantid", jsonStringContent);
			}

			int s = 0;
			int l = 2;

			try {
				s = Integer.valueOf(st);
				l = Integer.valueOf(li);

			} catch (Exception e) {
			}
			List<Map<String, Object>> ml = merchantDeviceServer
					.listMerchantDevice(condition, s, l);
			JSONObject da = new JSONObject();

			JSONArray ja = new JSONArray(ml);

			da.put("total", merchantDeviceServer.totalMerchantDevice(condition));
			da.put("ne", s);
			da.put("con", ja);
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			return Response.ok().entity("").build();
		}
	}

	@POST
	@Path("merchantset")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerset(String jsonStringContent) {
		JSONObject js;
		try {
			String msg = "创建成功!";
			String result = "1";
			js = new JSONObject(jsonStringContent);
			Settles se = new Settles();
			String mid = js.optString("mid");
			Merchant mer = merchantServer.MerchantByid(mid);
			StringBuffer sb = new StringBuffer(js.optString("xytype"));
			sb.append(js.optString("paysrvCode"));
			sb.append(mer.getMcode());
			Random r = new Random();
			sb.append(String.format("%04d", r.nextInt(9999)));
			se.setContractNum(sb.toString());
			se.setEndtime(js.optString("etime"));
			se.setMerchantid(js.optString("mid"));
			se.setRebate(js.optString("rebate"));
			se.setRebatea(js.optString("rebatea"));
			se.setStarttime(js.optString("stime"));
			se.setTitle(js.optString("title"));
			se.setPaysrvCode(js.optString("paysrvCode"));
			se.setPaysrvAltAN(js.optString("paysrvAltAN"));
			se.setPaysrvAN(js.optString("paysrvAN"));
			se.setPaysrvKey(js.optString("paysrvKey"));
			se.setPaysrvAgent(js.optString("paysrvAgent"));

			String deliveryURL = js.optString("deliveryURL");
			String deliveryKey = js.optString("deliveryKey");
			JSONObject jo = new JSONObject();
			jo.put("deliveryURL", deliveryURL);
			jo.put("deliveryKey", deliveryKey);
			se.setExcon(jo.toString());

			setteleServer.createSettles(se);

			JSONObject po = new JSONObject();
			po.put("agrmtId", se.getContractNum());
			po.put("title", js.optString("title"));
			po.put("paysrvCode", js.optString("paysrvCode"));
			po.put("paysrvAltAN", js.optString("paysrvAltAN", ""));
			po.put("paysrvAN", js.optString("paysrvAN"));
			po.put("paysrvKey", js.optString("paysrvKey"));
			po.put("paysrvAgent", js.optString("paysrvAgent", ""));
			po.put("deliveryURL", js.optString("deliveryURL", ""));
			po.put("deliveryKey", js.optString("deliveryKey", ""));

			// registerWebservice(po.toString());

			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("importset")
	@Produces(MediaType.TEXT_PLAIN)
	// 注意这就是前面提到的Content-Type坑！
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// 提交的数据类型是application/form-data
	public String importset(@Multipart("fileToUpload") byte[] file) {
		try {
			InputStream sbs = new ByteArrayInputStream(file);
			ReadExcel re = new ReadExcel();
			List<List<String>> result = re.extractExcel(sbs);
			for (int i = 1; i < result.size(); i++) {
				List<String> op = result.get(i);
				String jcode = op.get(0);
				String code = op.get(1);
				String rate = op.get(2);
				String type = "";
				if (op.size() > 3)
					type = op.get(3);
				Merchant merchant = merchantServer.MerchantBycode(code);
				Settles se = new Settles();
				se.setContractNum(jcode);
				se.setMerchantid(merchant.getId());
				se.setEndtime("2016-12-31");
				se.setStarttime("2015-3-15");
				se.setRebate(rate);
				se.setRebatea("0.78");
				se.setTitle("建行刷卡支付");
				if (type.trim().equals("")) {
					se.setPaysrvCode("001");
					se.setPaysrvAltAN("");
				} else {
					se.setPaysrvCode(getStype(type));
					se.setPaysrvAltAN(type);
				}
				se.setPaysrvAN("");
				se.setPaysrvKey("");
				se.setPaysrvAgent("");
				se.setExcon("");
				try {
					setteleServer.createSettles(se);
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String msg = "操作完成";
		String re = "{\"status\":\"" + 1 + "\",\"msg\":\"" + msg + "\"}";
		return re;
	}

	@POST
	@Path("importwxset")
	@Produces(MediaType.TEXT_PLAIN)
	// 注意这就是前面提到的Content-Type坑！
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// 提交的数据类型是application/form-data
	public String importwxset(@Multipart("fileToUpload") byte[] file) {
		try {
			InputStream sbs = new ByteArrayInputStream(file);
			ReadExcel re = new ReadExcel();
			List<List<String>> result = re.extractExcel(sbs);
			for (int i = 1; i < result.size(); i++) {
				List<String> op = result.get(i);
				String jcode = op.get(0);
				String code = op.get(1);
				String title = op.get(2);
				String tid = op.get(3);
				String paycode = op.get(4);
				String ratea = op.get(5);
				String rate = op.get(6);

				Merchant merchant = merchantServer.MerchantBycode(code);
				Settles se = new Settles();
				se.setContractNum(jcode);
				se.setMerchantid(merchant.getId());
				se.setEndtime("2017-12-31");
				se.setStarttime("2015-5-15");
				se.setRebate(rate);
				se.setRebatea(ratea);
				se.setTitle(title);
				se.setPaysrvCode(paycode);
				se.setPaysrvAltAN("");
				se.setPaysrvAN("");
				se.setPaysrvKey("");
				se.setPaysrvAgent("");
				se.setExcon("");

				try {
					setteleServer.createSettles(se);
					MerchantDevice MerchantDevice = new MerchantDevice();
					MerchantDevice.setMerchantid(se.getId());
					MerchantDevice.setPosnum(tid);
					merchantDeviceServer
							.createMerchantDevicedevice(MerchantDevice);
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String msg = "操作完成";
		String re = "{\"status\":\"" + 1 + "\",\"msg\":\"" + msg + "\"}";
		return re;
	}

	@GET
	@Path("getset")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getset(@QueryParam("userid") String mid) {
		try {
			Settles se = setteleServer.SettlesByid(mid);
			JSONObject da = new JSONObject();
			da.put("id", se.getId());
			da.put("num", se.getContractNum());
			da.put("title", se.getTitle());
			da.put("paycode", se.getPaysrvCode());
			da.put("payan", se.getPaysrvAN());
			da.put("paykey", se.getPaysrvKey());
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"" + 1 + "\",\"msg\":\"" + "初始化信息出错"
					+ "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("setsusp")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setsusp(@QueryParam("cid") String cid) {
		try {
			String msg = "注销成功!";
			String result = "1";
			Settles me = setteleServer.SettlesByid(cid);
			me.setStatus("0");
			setteleServer.suspendSettles(me);
			// disWebservice(me.getContractNum());
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			e.printStackTrace();
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("listmerchantset")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listmerchantset(String jsonStringContent,
			@QueryParam("st") String st, @QueryParam("li") String li) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			if (!jsonStringContent.equals("")) {
				condition.put("merchantid", jsonStringContent);
			}
			condition.put("status", "1");
			int s = 0;
			int l = 2;

			try {
				s = Integer.valueOf(st);
				l = Integer.valueOf(li);

			} catch (Exception e) {
			}
			List<Map<String, Object>> ml = setteleServer.listSettles(condition,
					s, l);
			JSONObject da = new JSONObject();

			JSONArray ja = new JSONArray(ml);

			da.put("total", setteleServer.totalSettles(condition));
			da.put("ne", s);
			da.put("con", ja);
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			return Response.ok().entity("").build();
		}
	}

	private void registerWebservice(String payload) {
		try {
			final CloseableHttpClient httpClient = getHttpsClient();
			final HttpPut httpPost = new HttpPut(webServiceURL + "/agreements");

			StringEntity stringEntity = new StringEntity(payload,
					ContentType.create("application/json", "UTF-8"));
			StringBuilder sb = new StringBuilder(payload);
			byte[] contentBytes = sb.toString().getBytes("UTF-8");
			byte[] keyBytes = Base64.decodeBase64(SHA1Key);
			byte[] signContent = new byte[contentBytes.length + keyBytes.length];
			System.arraycopy(contentBytes, 0, signContent, 0,
					contentBytes.length);
			System.arraycopy(keyBytes, 0, signContent, contentBytes.length,
					keyBytes.length);

			byte[] signature = DigestUtils.sha(signContent);
			String signatureString = Base64.encodeBase64String(signature);
			System.out.println(signatureString);

			httpPost.setHeader("Authorization", signatureString);
			httpPost.setEntity(stringEntity);
			ExecutorService threadPool = Executors.newSingleThreadExecutor();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					int count = 1;
					boolean isgo = true;
					while (isgo) {
						String re = "";
						try {
							re = httpClient.execute(httpPost,
									new ResponseHandler<String>() {
										public String handleResponse(
												HttpResponse httpResponse)
												throws ClientProtocolException,
												IOException {
											StatusLine statusLine = httpResponse
													.getStatusLine();
											if (logger.isLoggable(Level.INFO)) {
												logger.info(String
														.format("Receiving response for sending batch account envelopes letter:[%s]",
																statusLine
																		.toString()));
											}
											int sc = statusLine.getStatusCode();
											System.out.println(sc);
											return String.valueOf(sc);

										}
									});
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (re.equals("200") || re.equals("201"))
							isgo = false;
						count++;
						if (count > 10) {
							try {
								Thread.sleep(50000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
							}
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disWebservice(String agreementId) {
		try {
			final CloseableHttpClient httpClient = getHttpsClient();
			final HttpDelete httpDelete = new HttpDelete(webServiceURL
					+ "/agreements/" + agreementId);
			StringBuilder sb = new StringBuilder(agreementId);
			byte[] contentBytes = sb.toString().getBytes();
			byte[] keyBytes = Base64.decodeBase64(SHA1Key);
			byte[] signContent = new byte[contentBytes.length + keyBytes.length];
			System.arraycopy(contentBytes, 0, signContent, 0,
					contentBytes.length);
			System.arraycopy(keyBytes, 0, signContent, contentBytes.length,
					keyBytes.length);
			byte[] signature = DigestUtils.sha(signContent);
			String signatureString = Base64.encodeBase64String(signature);
			httpDelete.setHeader("Authorization", signatureString);
			ExecutorService threadPool = Executors.newSingleThreadExecutor();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					int count = 1;
					boolean isgo = true;
					while (isgo) {
						String re = "";
						try {
							re = httpClient.execute(httpDelete,
									new ResponseHandler<String>() {
										public String handleResponse(
												HttpResponse httpResponse)
												throws ClientProtocolException,
												IOException {
											StatusLine statusLine = httpResponse
													.getStatusLine();
											if (logger.isLoggable(Level.INFO)) {
												logger.info(String
														.format("Receiving response for sending batch account envelopes letter:[%s]",
																statusLine
																		.toString()));
											}
											int sc = statusLine.getStatusCode();
											System.out.println(sc);
											return String.valueOf(sc);

										}
									});
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (re.equals("200") || re.equals("201"))
							isgo = false;
						count++;
						if (count > 10) {
							try {
								Thread.sleep(50000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
							}
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerPosWebservice(String agreementId, String terminals,
			String terminal) {
		try {
			final CloseableHttpClient httpClient = getHttpsClient();
			final HttpPut httpPost = new HttpPut(webServiceURL + "/agreements/"
					+ agreementId + "/terminals");

			StringEntity stringEntity = new StringEntity(terminals,
					ContentType.create("application/json", "UTF-8"));
			StringBuilder sb = new StringBuilder(terminals);
			byte[] contentBytes = sb.toString().getBytes();
			byte[] keyBytes = Base64.decodeBase64(SHA1Key);

			byte[] signContent = new byte[contentBytes.length + keyBytes.length];
			System.arraycopy(contentBytes, 0, signContent, 0,
					contentBytes.length);
			System.arraycopy(keyBytes, 0, signContent, contentBytes.length,
					keyBytes.length);

			byte[] signature = DigestUtils.sha(signContent);
			String signatureString = Base64.encodeBase64String(signature);

			httpPost.setHeader("Authorization", signatureString);
			httpPost.setEntity(stringEntity);
			ExecutorService threadPool = Executors.newSingleThreadExecutor();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					int count = 1;
					boolean isgo = true;
					while (isgo) {
						String re = "";
						try {
							re = httpClient.execute(httpPost,
									new ResponseHandler<String>() {
										public String handleResponse(
												HttpResponse httpResponse)
												throws ClientProtocolException,
												IOException {
											StatusLine statusLine = httpResponse
													.getStatusLine();
											if (logger.isLoggable(Level.INFO)) {
												logger.info(String
														.format("Receiving response for sending batch account envelopes letter:[%s]",
																statusLine
																		.toString()));
											}
											int sc = statusLine.getStatusCode();
											System.out.println(sc);
											return String.valueOf(sc);

										}
									});
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (re.equals("200") || re.equals("201"))
							isgo = false;
						count++;
						if (count > 10) {
							try {
								Thread.sleep(50000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
							}
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disPosWebservice(String vendorsIdentifier, String terminal) {
		try {
			final CloseableHttpClient httpClient = getHttpsClient();
			final HttpDelete httpDelete = new HttpDelete(webServiceURL
					+ "/agreements/" + vendorsIdentifier + "/terminals/"
					+ terminal);
			StringBuilder sb = new StringBuilder(vendorsIdentifier)
					.append(terminal);
			byte[] contentBytes = sb.toString().getBytes();
			byte[] keyBytes = Base64.decodeBase64(SHA1Key);
			byte[] signContent = new byte[contentBytes.length + keyBytes.length];
			System.arraycopy(contentBytes, 0, signContent, 0,
					contentBytes.length);
			System.arraycopy(keyBytes, 0, signContent, contentBytes.length,
					keyBytes.length);
			byte[] signature = DigestUtils.sha(signContent);
			String signatureString = Base64.encodeBase64String(signature);
			httpDelete.setHeader("Authorization", signatureString);
			ExecutorService threadPool = Executors.newSingleThreadExecutor();
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					int count = 1;
					boolean isgo = true;
					while (isgo) {
						String re = "";
						try {
							re = httpClient.execute(httpDelete,
									new ResponseHandler<String>() {
										public String handleResponse(
												HttpResponse httpResponse)
												throws ClientProtocolException,
												IOException {
											StatusLine statusLine = httpResponse
													.getStatusLine();
											if (logger.isLoggable(Level.INFO)) {
												logger.info(String
														.format("Receiving response for sending batch account envelopes letter:[%s]",
																statusLine
																		.toString()));
											}
											int sc = statusLine.getStatusCode();
											System.out.println(sc);
											return String.valueOf(sc);

										}
									});
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (re.equals("200") || re.equals("201"))
							isgo = false;
						count++;
						if (count > 10) {
							try {
								Thread.sleep(50000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
							}
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SSLConnectionSocketFactory createSocketFactory()
			throws GeneralSecurityException {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				throw new CertificateException("limited.");

			}
		} }, new java.security.SecureRandom());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext);
		return sslsf;
	}

	private CloseableHttpClient getHttpsClient() {
		try {
			if (sslConnectionSocketFactory == null) {
				sslConnectionSocketFactory = createSocketFactory();
			}
			SSLConnectionSocketFactory sslsf = sslConnectionSocketFactory;
			CloseableHttpClient client = HttpClients.custom()
					.setSSLSocketFactory(sslsf).build();
			return client;

		} catch (Exception ex) {
			return null;
		}

	}

	private String getStype(String op) {
		try {
			op = "0" + op.substring(0, 2);
		} catch (Exception e) {
			op = "01" + op.substring(0, 1);
		}
		return op;
	}
}
