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

public class LoginController extends JSPController {
	
	public LoginController() {
		super("login.jsp", true, false);
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
				} else {
					//Add an error msg
					req.setAttribute("err", "Username or password is incorrect. Try again.");
					doGet(req,resp);
				}
				
				break;
			default:
				req.setAttribute("err", "Request failed. Try again.");
				doGet(req,resp);
		}
		
		
		
			
			
		
	}
	
	
	
}