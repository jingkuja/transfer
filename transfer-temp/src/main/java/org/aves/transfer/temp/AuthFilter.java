package org.aves.transfer.temp;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class AuthFilter implements ContainerResponseFilter {

	private static final Logger logger = Logger.getLogger(AuthFilter.class
			.getName());

	@Override
	public void filter(ContainerRequestContext requestCtx,
			ContainerResponseContext responseCtx) throws IOException {
		logger.info(requestCtx.getUriInfo().getPath());
		Map am = requestCtx.getCookies();
		for (Object key : am.keySet()) {
			Cookie cookie = (Cookie) am.get(key);
			logger.info("Key = " + key + ", Value = " + cookie.getValue());
		}
		responseCtx.setEntity("222");
		responseCtx.setStatus(201);
		return;

	}
}
