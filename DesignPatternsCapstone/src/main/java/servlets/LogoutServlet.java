package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logout")

public class LogoutServlet extends HttpServlet {
	
	public static final String htmlPath = "html/logout.html";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Welcome to logout");
		//Clear the active user from the current session..
		req.getSession().setAttribute("user", null);
		//Redirect to login screen.
		resp.sendRedirect("login");
		
		
		
	}

}