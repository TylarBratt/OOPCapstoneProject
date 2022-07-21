package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Common;
import beans.Database;
import beans.LocalURLBuilder;
import beans.User;
import beans.navbar.LoggedOutNavbar;
import beans.navbar.Navbar;

@WebServlet("/signup")

public class SignupServlet extends BaseServlet {

	public SignupServlet() {
		super("FleaBay - Create Account", true, false);
		// TODO Auto-generated constructor stub
	}

	public enum ErrorMessage {
		USERNAME_TAKEN("Username is taken. Try again."),
		MISSING_INFO("All fields must be filled.");
		
		ErrorMessage(String message){
			this.message = message;
		}
		public String message;
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		//Try creating a new user account with the provided credentials.
		User user = database.createAccount(username,  password);
		
		if (user != null) {
			System.out.println("Signed up as user: #"+user.id+" "+user.userName);
			
			//Store the user data to the session so we can remain logged in.
			HttpSession session = req.getSession();
			session.setAttribute("user", user.id);
			
			//If user already logged in, redirect to the home page.
			resp.sendRedirect(new LocalURLBuilder("home",req).toString());
		}
		else {
			//Reload the page with "err" parameter set to 1
			resp.sendRedirect(new LocalURLBuilder("signup",req).addParam("err").toString());
		}
	}
	
	@Override
	public Navbar getNavbar(HttpServletRequest req){
		return new LoggedOutNavbar();
	}

	@Override
	public String getBodyHTML(HttpServletRequest req) {
		//Check the parameters for an error condition..
		String errorMsg = "";
		
		if (req.getParameter("err") != null)
			errorMsg = "User is taken. Try again.";
		
	
		return readFileText("html/signup.html", Long.toString(Common.newUserCredits), Long.toString(Common.newUserProductCount), errorMsg);
	}

	@Override
	public String getActiveNavbarItem() {
		return "signup";
	}
	
}