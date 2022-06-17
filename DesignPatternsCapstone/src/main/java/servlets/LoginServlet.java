package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Common;
import beans.User;

@WebServlet("/login")

public class LoginServlet extends BaseServlet {
	
	public LoginServlet() {
		super(true);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//Write the page's HTML to the response.
		resp.getWriter().write(getHTMLString(req, null));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			session.setAttribute("user", user);
			
			//Redirect to the home page.
			resp.sendRedirect(req.getContextPath()+"/home");
		}
		else {
			//Reload the page with an error message..
			resp.getWriter().write(getHTMLString(req,"Username or password is incorrect."));
		}
	}
	
	public String getHTMLString(HttpServletRequest req, String errorMsg) throws IOException {
		
		//Read the html file and insert data for the specified arguments..
		return readFileText(getServletContext().getRealPath("html/login.html"),Common.appName, Common.appSlogan, errorMsg != null ? errorMsg : "");
	}
	
}