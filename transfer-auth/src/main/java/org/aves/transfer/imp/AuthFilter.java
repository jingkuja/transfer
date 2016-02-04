package org.aves.transfer.imp;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

import org.apache.http.auth.AuthenticationException;

@Provider
@PreMatching
public class AuthFilter implements ContainerResponseFilter {

	private static final Logger logger = Logger.getLogger(AuthFilter.class
			.getName());

	private String key = "erZ1f21sf";

	@Override
	public void filter(ContainerRequestContext requestCtx,
			ContainerResponseContext responseCtx) throws IOException {
		try {
			String path = requestCtx.getUriInfo().getPath();
			if (path.contains("api/agreements"))
				return;

			String pinfo = null;
			String auth = null;
			Map am = requestCtx.getCookies();
			for (Object key : am.keySet()) {
				Cookie cookie = (Cookie) am.get(key);
				if (key.equals("pinfo"))
					pinfo = cookie.getValue();
				if (key.equals("auth"))
					auth = cookie.getValue();
			}
			if (pinfo == null || auth == null)
				throw new AuthenticationException("登录信息丢失，请重新登录！");

			if (!MD5tool.MD5(pinfo + key).equals(auth))
				throw new AuthenticationException("登录信息异常，请重新登录！");
			String pin[] = pinfo.split("[*]");

			String pauth = pin[2];

			if (pauth.equals("1")) {

			} else if (pauth.equals("2")) {
				if (!path.contains("api/clistbills")) {
					throw new AuthenticationException("用户无权限访问该页面！");
				}
			} else {
				responseCtx.setEntity("用户信息认证异常，请重新登录！");
			}

		} catch (AuthenticationException e) {
			responseCtx.setEntity(e.getMessage());
			responseCtx.setStatus(401);
			return;
		} catch (Exception e) {
			responseCtx.setEntity("用户信息认证异常，请重新登录！");
			responseCtx.setStatus(401);
			return;
		}

	}
}
