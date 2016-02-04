package org.aves.transfer.imp;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.http.auth.AuthenticationException;
import org.aves.transfer.api.IAuthServer;
import org.aves.transfer.bean.Account;
import org.aves.transfer.json.JSONArray;
import org.aves.transfer.json.JSONObject;

@Path("api")
public class AuthResource {

	private static final Logger logger = Logger.getLogger(AuthResource.class
			.getName());

	private IAuthServer authServer;

	private String key = "erZ1f21sf";

	public AuthResource() {

	}

	public void setAuthServer(IAuthServer authServer) {
		this.authServer = authServer;
	}

	@GET
	@Path("veimg")
	public Response veimg() {
		try {

			VertifyCodetool vt = new VertifyCodetool();
			final Map<String, Object> resu = vt.geecode();
			String code = (String) resu.get("code");
			NewCookie cookie = new NewCookie(
					new Cookie("code", code, "/", null));
			return Response.ok(200)
					.header("Content-Type", "image/jpeg;Expires=0")
					.header("Pragma", "no-cache")
					.header("Cache-Control", "no-cache").cookie(cookie)
					.entity(new StreamingOutput() {
						@Override
						public void write(OutputStream outputStream)
								throws IOException {
							BufferedImage buffImg = (BufferedImage) resu
									.get("img");
							ImageIO.write(buffImg, "jpeg", outputStream);
						}
					}).build();
		} catch (Exception e) {
			String re = "{\"status\":\"4\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String jsonStringContent,
			@CookieParam("code") String code) {
		JSONObject js;
		try {
			String msg = "登录!";
			String result = "1";
			js = new JSONObject(jsonStringContent);
			String acct = js.optString("account", "");
			String pass = js.optString("password", "");
			String veryCode = js.optString("veryCode", "");

			Account acctd = authServer.AccountByAcct(acct);
			String repass = MD5tool.MD5(pass);
			if (acctd.getId() == null)
				throw new Exception("该账号不存在！");
			if (!acctd.getPassword().equals(repass))
				throw new Exception("密码错误！");
			if (!code.equals(veryCode.toUpperCase()))
				throw new Exception("验证码输入错误！");
			if (acctd.getAuth().equals("1"))
				result = "2";
			if (acctd.getAuth().equals("2"))
				result = "3";

			StringBuffer sb = new StringBuffer(acctd.getId());
			sb.append("*");
			sb.append(acctd.getAccount());
			sb.append("*");
			sb.append(acctd.getAuth());
			sb.append("*");
			sb.append(acctd.getAliasid());
			String auth = MD5tool.MD5(sb.toString() + key);
			NewCookie cookie = new NewCookie(new Cookie("pinfo", sb.toString(),
					"/", null));
			NewCookie cookied = new NewCookie(new Cookie("auth", auth, "/",
					null));
			NewCookie aname = new NewCookie(new Cookie("aname",
					HttpUtils.urlEncode(acctd.getAliasname(), "utf-8"), "/",
					null));

			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().cookie(cookie).cookie(cookied).cookie(aname)
					.entity(re).build();
		} catch (Exception e) {
			String re = "{\"status\":\"4\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@GET
	@Path("islogin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response islogin(@CookieParam("pinfo") String pinfo,
			@CookieParam("auth") String auth) {
		try {

			if (pinfo == null || auth == null)
				throw new AuthenticationException("登录信息丢失,请重新登录!");

			if (!MD5tool.MD5(pinfo + key).equals(auth))
				throw new AuthenticationException("登录信息异常,请重新登录!");
			String pin[] = pinfo.split("[*]");

			String pauth = pin[2];

			if (pauth.equals("1")) {

			} else if (pauth.equals("2")) {
				throw new AuthenticationException("重新登录!");
			} else {
				throw new AuthenticationException("重新登录!");
			}
			String re = "{\"status\":\"1\",\"msg\":\"'1'\"}";
			return Response.ok().entity(re).build();

		} catch (AuthenticationException e) {
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("createAcct")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAcct(String jsonStringContent) {
		JSONObject js;
		try {
			String msg = "创建成功!";
			String result = "1";
			js = new JSONObject(jsonStringContent);
			Account acct = new Account();
			acct.setAccount(js.optString("account", ""));
			acct.setAliasid(js.optString("aliasid", ""));
			acct.setAliasname(js.optString("aliasname", ""));
			acct.setAuth(js.optString("auth", ""));
			acct.setPassword(MD5tool.MD5("888888"));
			authServer.createAccount(acct);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("reset")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reset(String jsonStringContent) {
		try {
			String msg = "重置密码成功!";
			String result = "1";
			Account acct = authServer.AccountById(jsonStringContent);
			acct.setPassword(MD5tool.MD5("888888"));
			authServer.ResetAccount(acct);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("repass")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response repass(String jsonStringContent) {
		try {
			String msg = "修改密码成功!";
			String result = "1";
			JSONObject js = new JSONObject(jsonStringContent);
			Account acct = authServer.AccountById(js.optString("aid", ""));
			acct.setPassword(MD5tool.MD5(js.optString("pass", "")));
			authServer.ResetAccount(acct);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("suspend")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response suspend(String jsonStringContent) {
		try {
			String msg = "注销账号成功!";
			String result = "1";
			Account acct = authServer.AccountById(jsonStringContent);
			authServer.suspendAccount(acct);
			String re = "{\"status\":\"" + result + "\",\"msg\":\"" + msg
					+ "\"}";
			return Response.ok().entity(re).build();
		} catch (Exception e) {
			String re = "{\"status\":\"0\",\"msg\":\"" + e.getMessage() + "\"}";
			return Response.ok().entity(re).build();
		}
	}

	@POST
	@Path("listacct")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listacct(String jsonStringContent,
			@QueryParam("st") String st, @QueryParam("li") String li) {
		try {
			Map<String, String> condition = new HashMap<String, String>();
			if (!jsonStringContent.equals("")) {
				condition.put("merid", jsonStringContent);
			}
			int s = 0;
			int l = 2;
			try {
				s = Integer.valueOf(st);
				l = Integer.valueOf(li);
			} catch (Exception e) {
			}
			List<Map<String, Object>> ml = authServer.listAccounts(condition,
					s, l);
			JSONObject da = new JSONObject();
			JSONArray ja = new JSONArray(ml);
			da.put("total", authServer.totalAccounts(condition));
			da.put("ne", s);
			da.put("con", ja);
			return Response.ok().entity(da.toString()).build();
		} catch (Exception e) {
			return Response.ok().entity("").build();
		}
	}

	private String getString(Object ob) {
		try {
			return String.valueOf(ob);
		} catch (Exception e) {
			return "";
		}
	}

}
