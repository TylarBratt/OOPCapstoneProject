package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Database;
import beans.DefaultProductInfo;
import beans.Product;
import beans.User;

@WebServlet("/home")

public class HomeServlet extends BaseServlet {
	public static final String htmlPath = "html/home.html";
	
	public HomeServlet(){
		super(true);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//If user is not logged in, redirect to login screen.
		User user = (User)req.getSession().getAttribute("user");
		if (user == null)
			resp.sendRedirect(req.getContextPath()+"/login");
		else
			resp.getWriter().write(getHTMLString(req));
		
		
	}

	@Override
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		
		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(req));
	}
	
	public String getHTMLString(HttpServletRequest req) throws IOException {
		
		User user = (User)req.getSession().getAttribute("user");
		String loginMessage = "";
		if (user != null) {
			loginMessage = "<h2>Greetings "+user.userName+"!</h2>";
		}
		
		return readFileText(htmlPath, loginMessage, "", generateCSS(), user.userName);
	}
	
}