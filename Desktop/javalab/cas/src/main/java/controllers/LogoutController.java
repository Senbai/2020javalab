package controllers;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cas.token.JwtUtils;

@WebServlet("/logout.do")
public class LogoutController extends HttpServlet {
	static String SSO_VERIFY_URL = "http://localhost:8080/cas/verify.do";
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String token = session.getAttribute("token").toString();
		String verifyResult = req.getParameter("verify_result");
		if (verifyResult == null) {
			resp.sendRedirect(SSO_VERIFY_URL + "?" + "LOCAL_SERVICE=" + req.getRequestURL() + "&token=" + token);
			return;
		}
		if (!"YES".equals(verifyResult)) {
			// token无效，交给认证中心，重新登陆
			resp.sendRedirect(SSO_VERIFY_URL + "?" + "LOCAL_SERVICE=" + req.getRequestURL());
			return;
		}
		//销毁全局会话
		session.invalidate();
		resp.sendRedirect("http://localhost:8080/app1/view.do");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
