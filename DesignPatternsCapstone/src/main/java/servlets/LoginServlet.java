package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import beans.Log;
import beans.User;

@WebServlet("/login")

public class LoginServlet extends HttpServlet {
	
	public static final String htmlPath = "html/login.html";
	public enum ErrorMessage {
		INVALID_CREDENTIALS("Username or password is incorrect.");
		
		ErrorMessage(String message){
			this.message = message;
		}
		public String message;
	}
	
	Database database = null;
	@Override
	public void destroy() {
		//Shut down the database connection..
		database.shutdown();
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		//Initialize the database connection..
		database = new Database();
		
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//Return the default HTML page with a welcome message..
		//String path = req.getServletContext().getRealPath(htmlPath);
		
		//resp.sendRedirect(req.getContextPath()+"/other");
		
		
		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(req, null));
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		//Try logging in with the provided credentials..
		User user = database.login(username,  password);
		
		if (user != null) {
			System.out.println("Logged in as user: #"+user.id+" "+user.userName);
			
			//Store the user data to the session so we can remain logged in.
			HttpSession session = req.getSession();
			session.setAttribute("user", user);
			
			//Redirect to the home page.
			resp.sendRedirect(req.getContextPath()+"/home");
		}
		else {
			//Return the web page html
			 resp.getWriter().write(getHTMLString(req,ErrorMessage.INVALID_CREDENTIALS));
		}
		
		
	}
	
	public String getHTMLString(HttpServletRequest req, ErrorMessage error) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(req.getServletContext().getRealPath(htmlPath)));
		String line = null;
		StringBuffer buffer = new StringBuffer();
		while ((line=reader.readLine())!=null) 
			buffer.append(line);
		
		
		reader.close();
		
		
		String errorMsg = error != null ? error.message : " ";
		
		
		/**
		 * Params:
		 * 0 - App name
		 * 1 - Slogan
		 */
		return MessageFormat.format(buffer.toString(), Common.appName, Common.appSlogan, errorMsg);
	}
	
}