package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.User;

@WebServlet("")

public class MainServlet extends HttpServlet {
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = (User)req.getSession().getAttribute("user");
		
		if (user != null) {
			//If user already logged in, redirect to the home page.
			resp.sendRedirect(req.getContextPath()+"/home");
		}
		else {
			//Otherwise redirect to the login screen.
			resp.sendRedirect(req.getContextPath()+"/login");
		}
	}
}