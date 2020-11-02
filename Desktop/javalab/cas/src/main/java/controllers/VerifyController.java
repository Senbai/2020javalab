package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cas.token.JwtUtils;

@WebServlet("/verify.do")
public class VerifyController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = req.getParameter("token");
		String LOCAL_SERVICE = req.getParameter("LOCAL_SERVICE");
		HttpSession session = req.getSession();
		Map<String, String> res=new HashMap<String, String>();
		// jwt验证
		try {
			 res = JwtUtils.verifyToken(token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String user_id =res.get("user_id");
		String sid = session.getId()+user_id;
		if(SessionMap.have(session.getId()+user_id)&&sid.equals(res.get("sid"))) {
			resp.sendRedirect(LOCAL_SERVICE+"?token="+token+"&verify_result=YES");
			return;
		}
		resp.sendRedirect(LOCAL_SERVICE+"?token="+token+"&verify_result=NO");
		return ;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
