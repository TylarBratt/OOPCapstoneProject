package servlets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Database;
import beans.HTMLAdapter;
import beans.navbar.LoggedInNavbar;
import beans.navbar.Navbar;

/**
 * 
 * A common interface for our servlets to inherit from. 
 * Simplifies common tasks such as database initialization and the loading of html files.
 *
 */
public abstract class JSPController extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	public final boolean isUsingDatabase;
	Database database = null; //Only initialized if isUsingDatabase is true.
	
	public final boolean isLoginRequired; //If true, the user will be automatically redirected to the login screen if not logged in.

	public final String jspPath;
	
	public JSPController(String jspPath, boolean isUsingDatabase, boolean isLoginRequired) {
		this.isUsingDatabase = isUsingDatabase;
		this.isLoginRequired = isLoginRequired;
		this.jspPath = jspPath;
	}
	
	@Override
	public void destroy() {
		//Shut down the database connection, if enabled..
		if (isUsingDatabase)
			database.shutdown();
		
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		//Initialize the database connection, if requested.
		if (isUsingDatabase) 
			database = new Database(getServletContext());
		
		super.init();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// If user is not logged in, redirect to login screen.
		boolean isLoggedIn = req.getSession().getAttribute("user") != null;
		if (isLoginRequired && !isLoggedIn) 
			resp.sendRedirect(req.getContextPath() + "/login");
		else {
			//Attach database attribute to the request.
			initializeData(req);
			
			//Forward the request to the associated JSP file.
			RequestDispatcher dispatcher = req.getRequestDispatcher("/jsp/"+jspPath);
	        dispatcher.forward(req, resp);
		}

	}
	
	/*
	 * This is where you initialize any data that you want passed into the JSP. 
	 * Data must be passed in as an attribute by calling req.setAttribute().
	 */
	public void initializeData(HttpServletRequest req) { }
	
	
}