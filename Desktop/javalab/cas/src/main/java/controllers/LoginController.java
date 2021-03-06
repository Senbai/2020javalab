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
import database.UserDao;
import domains.User;

@WebServlet("/login.do")
public class LoginController extends HttpServlet {
	/**
	 * 将子系统登录请求跳转到登录页面
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//是否已登录
		String LOCAL_SERVICE = req.getParameter("LOCAL_SERVICE");
		Cookie[] cookies = req.getCookies();
		String sid = null;
		if(cookies!=null) {
			for(Cookie c:cookies) {
				if("sid".equals(c.getName())) {
					sid = c.getValue();
				}
			}
		}
		if(sid!=null) {
			boolean flag = SessionMap.have(sid);
			if(flag){
				//签发JWT
				String token = "";
				HttpSession session = SessionMap.getSession(sid);
				String user_id = (String)session.getAttribute("user_id");
				// 储存
				Map<String, String> map = new HashMap<>();
				map.put("user_id", user_id + "");
				map.put("sid", sid);
				map.put(LOCAL_SERVICE, "IS_LOGIN");
				try {
					token = JwtUtils.createToken(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 4.跳转到子系统
				resp.sendRedirect(LOCAL_SERVICE+"?token="+token);
				return;
			}
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
		User user=null;
		try {
			user = UserDao.get(user_id);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 2.验证账号和密码，验证失败，则重新登录
		if (user==null) {
			// 用户账号或密码错误重新登录
			req.setAttribute("LOCAL_SERVICE", LOCAL_SERVICE);
			req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
			return;
		}else if(!user_pwd.equals(user.getPwd())){
			req.setAttribute("LOCAL_SERVICE", LOCAL_SERVICE);
			req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
		}
		// 3.登录成功，分配令牌
		HttpSession session = req.getSession();
		String sid = session.getId()+user_id;
		Cookie cookie =new Cookie("sid",sid);
		resp.addCookie(cookie);
		if(!SessionMap.have(sid)) {
			SessionMap.put(sid,session);
		}
		session.setAttribute("user_id", user_id);
		String token = "";
		// 储存
		Map<String, String> map = new HashMap<>();
		map.put("user_id", user_id + "");
		map.put("sid", sid);
		map.put(LOCAL_SERVICE, "IS_LOGIN");
		try {
			token = JwtUtils.createToken(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 4.跳转到子系统
		resp.sendRedirect(LOCAL_SERVICE+"?token="+token);
	}

}
