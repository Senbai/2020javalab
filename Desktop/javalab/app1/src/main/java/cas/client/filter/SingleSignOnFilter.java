package cas.client.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

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
		Cookie[] cookies = httpRequest.getCookies();
		String token = null;
		if(cookies!=null) {
			for(Cookie c:cookies) {
				if(c.getName()=="cookie") {
					token = c.getValue();
				}
			}
		}
		if( httpRequest.getParameter("token")!=null) {
			token = httpRequest.getParameter("token");
		}
		if (token!=null) {
			String verifyResult = httpRequest.getParameter("verify_result");
			
			if (verifyResult == null) {
				//token 还未认证，先认证token是否有效
				httpResponse.sendRedirect(SSO_VERIFY_URL + "?" + "LOCAL_SERVICE=" + httpRequest.getRequestURL() + "&token=" + token);
				return;
			}
			if (!"YES".equals(verifyResult)) {
				// token无效，交给认证中心，重新登陆
				httpResponse.sendRedirect(SSO_SERVER_URL + "?" + "LOCAL_SERVICE=" + httpRequest.getRequestURL());
				return;
			}
			// token有效，登录系统
			Cookie c=new Cookie("token",token);
			httpResponse.addCookie(c);
			chain.doFilter(request, response);
			return;
		}
		else {
			//系统未登录，请求认证中心生成token登陆系统
			httpResponse.sendRedirect(SSO_SERVER_URL + "?" + "LOCAL_SERVICE=" + httpRequest.getRequestURL());
			return;
		}
	}

	@Override
	public void destroy() {

	}
}
