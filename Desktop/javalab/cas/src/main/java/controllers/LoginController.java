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

@WebServlet("/login.do")
public class LoginController extends HttpServlet {
	/**
	 * 将子系统登录请求跳转到登录页面
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//是否已登录
		HttpSession session = req.getSession();
		String LOCAL_SERVICE = req.getParameter("LOCAL_SERVICE");
		if(session.getAttribute("isLogin")!=null) {
			String token = session.getAttribute("token").toString();
			req.setAttribute("token", token);
			resp.sendRedirect(LOCAL_SERVICE+"?token="+token);
			return;
		}
		//未登录，跳转登录页面
		req.setAttribute("LOCAL_SERVICE", LOCAL_SERVICE);
		req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1.获取账号和密码
		String user_id = req.getParameter("id");
		String user_pwd = req.getParameter("pwd");
		String LOCAL_SERVICE = req.getParameter("LOCAL_SERVICE");

		// 2.验证账号和密码，验证失败，则重新登录
		if (false) {
			// 用户账号或密码错误重新登录
			req.setAttribute("LOCAL_SERVICE", LOCAL_SERVICE);
			req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
			return;
		}
		// 3.登录成功，分配令牌
		HttpSession session = req.getSession();
		session.setAttribute("isLogin", true);
		session.setAttribute("user_id", user_id);
		String token = "";
		// 储存
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user_id + "");
		// map.put("name", mobile);
		try {
			token = JwtUtils.createToken(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// jwt验证
		// Map<String, String> res = JwtUtils.verifyToken(token);
		
		// 4.跳转到子系统
		session.setAttribute("token", token);
		resp.sendRedirect(LOCAL_SERVICE+"?token="+token);
	}

}
