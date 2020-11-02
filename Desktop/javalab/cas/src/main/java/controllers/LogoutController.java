package controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cas.token.JwtUtils;

@WebServlet("/logout.do")
public class LogoutController extends HttpServlet {
	static String SSO_VERIFY_URL = "http://localhost:8080/cas/verify.do";
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
		String LOCAL_SERVICE = req.getParameter("LOCAL_SERVICE");
		HttpSession session = req.getSession();
		String user_id=(String)session.getAttribute("user_id");
		System.out.println(SessionMap.getSession(session.getId()+user_id).getId());
		SessionMap.invalidate(session.getId()+user_id);
		
		resp.sendRedirect(LOCAL_SERVICE);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
