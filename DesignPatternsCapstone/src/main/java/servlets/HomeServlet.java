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

import beans.Common;
import beans.Log;
import beans.User;
import dao.Database;

@WebServlet("/home")

public class HomeServlet extends HttpServlet {
	public static final String htmlPath = "html/home.html";
	
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
		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(req));
	}

	@Override
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		
		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(req));
	}
	
	public String getHTMLString(HttpServletRequest req) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(req.getServletContext().getRealPath(htmlPath)));
		String line = null;
		StringBuffer buffer = new StringBuffer();
		while ((line=reader.readLine())!=null) 
			buffer.append(line);
		
		
		reader.close();
		
		
		User user = (User)req.getSession().getAttribute("user");
		String loginMessage = "";
		if (user != null) {
			loginMessage = "<h2>Greetings "+user.userName+"!</h2>";
		}
		/**
		 * Params:
		 * 0 - App name
		 * 1 - Slogan
		 */
		return MessageFormat.format(buffer.toString(), loginMessage);
	}
	
}