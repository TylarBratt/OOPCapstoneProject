package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("")

/**
 * 
 * This is the first landing page the user should see when visiting the site.
 * The only purpose of this servlet is to redirect the user to either the login or home
 * screen depending on their login status.
 *
 */
public class MainServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//Obtain the user account from the current session.
		Long userID = ((Long)req.getSession().getAttribute("user"));
		
		//If a logged-in user exists for this session, redirect to the home page. Otherwise redirect to the login screen..
		if (userID != null) 
			resp.sendRedirect(req.getContextPath()+"/home");
		else 
			resp.sendRedirect(req.getContextPath()+"/login");
	}
}