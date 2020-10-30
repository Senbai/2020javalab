package cas.client.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.http.HttpRequest;

@WebFilter(urlPatterns = "*.do")
public class SingleSignOnFilter implements Filter {
	static String SSO_SERVER_URL = "http://localhost:8080/cas/login.do";
	static String SSO_VERIFY_URL = "http://localhost:8080/cas/verify.do";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		Cookie[] cookie = httpRequest.getCookies();
		boolean local_isLogin = false;
		if (cookie != null) {
			for (Cookie c : cookie) {
				if ("isLogin".equals(c.getName()) && "YES".equals(c.getValue())) {
					local_isLogin = true;
				}
			}
		}
		if (!local_isLogin) {
			String token = httpRequest.getParameter("token");
			if (token == null) {
				httpResponse.sendRedirect(SSO_SERVER_URL + "?" + "LOCAL_SERVICE=" + httpRequest.getRequestURL());
				return;
			}
			String verifyResult = httpRequest.getParameter("verify_result");
			if (verifyResult == null) {
				httpResponse.sendRedirect(SSO_VERIFY_URL + "?" + "LOCAL_SERVICE=" + httpRequest.getRequestURL() + "&token=" + token);
				return;
			}
			if (!"YES".equals(verifyResult)) {
				// token无效，交给认证中心，重新登陆
				httpResponse.sendRedirect(SSO_SERVER_URL + "?" + "LOCAL_SERVICE=" + httpRequest.getRequestURL());
				return;
			}
			// token有效，登录系统
			Cookie co = new Cookie("isLogin", "YES");
			httpResponse.addCookie(co);
			chain.doFilter(request, response);
			return;
		}
		// 系统未登录，则交给认证中心，重新登录。
		httpResponse.sendRedirect(SSO_SERVER_URL + "?" + "LOCAL_SERVICE=" + httpRequest.getRequestURL());
		return;
	}

	@Override
	public void destroy() {

	}
}
