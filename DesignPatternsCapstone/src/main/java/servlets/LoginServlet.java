package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Common;
import beans.LocalURLBuilder;
import beans.User;
import beans.navbar.LoggedInNavbar;
import beans.navbar.LoggedOutNavbar;
import beans.navbar.Navbar;

@WebServlet("/login")

public class LoginServlet extends BaseServlet {
	
	public LoginServlet() {
		super("FleaBay - Login", "login", true, false);
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		switch (action) {
			case "login":
				//Obtain the login credentials from the request. 
				String username = req.getParameter("username");
				String password = req.getParameter("password");
				
				//Try logging in with the provided credentials..
				User user = database.login(username,  password);
				
				// If the login was successful...
				if (user != null) {
					//Print a debug message to the console.
					System.out.println("Logged in as user: #"+user.id+" "+user.userName);
					
					//Store the user data to the session so we can remain logged in.
					HttpSession session = req.getSession();
					session.setAttribute("user", user.id);
					
					//Redirect to the home page.
					resp.sendRedirect(new LocalURLBuilder("home", req).toString());
					return;
				} else resp.sendRedirect(new LocalURLBuilder("login", req).addParam("invalid-credentials").toString());
				
				break;
			default:
				resp.sendRedirect(new LocalURLBuilder("login", req).addParam("request-failed").toString());
		}
		
		
		
			
			
		
	}
	

	@Override
	public String getBodyHTML(HttpServletRequest req) {
		
		String errorMsg = "";
		if (req.getParameter("invalid-credentials") != null)
			errorMsg = "Username or password is incorrect. Try again.";
		else if (req.getParameter("request-failed") != null)
			errorMsg = "Request failed. Try again.";
		
		// TODO Auto-generated method stub
		//Read the html file and insert data for the specified arguments..
		return readFileText("html/login.html",Common.appName, Common.appSlogan, errorMsg);
			
	}
	
	@Override
	public Navbar getNavbar(HttpServletRequest req) {
		//Always return the logged out navbar..
		return new LoggedOutNavbar();
	}
	
	
}