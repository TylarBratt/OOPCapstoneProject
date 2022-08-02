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

public class SignupController extends JSPController {

	public SignupController() {
		super("signup.jsp", true, false);
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
			req.setAttribute("err", "User is taken. Try again.");
			doGet(req,resp);
		}
	}
	


	
}