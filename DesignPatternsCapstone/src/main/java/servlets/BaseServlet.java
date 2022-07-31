package servlets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

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
public abstract class BaseServlet extends HttpServlet{
	public final boolean isUsingDatabase;
	Database database = null; //Only initialized if isUsingDatabase is true.
	
	public final boolean isLoginRequired; //If true, the user will be automatically redirected to the login screen if not logged in.
	public final String title;
	
	public BaseServlet(String title, boolean isUsingDatabase, boolean isLoginRequired) {
		this.title = title;
		this.isUsingDatabase = isUsingDatabase;
		this.isLoginRequired = isLoginRequired;
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
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// If user is not logged in, redirect to login screen.
		boolean isLoggedIn = req.getSession().getAttribute("user") != null;
		if (isLoginRequired && !isLoggedIn) {
			resp.sendRedirect(req.getContextPath() + "/login");
		}
		else {
			//Write the html to the response..
			resp.getWriter().write(getHTML(req));
		}

	}
	
	
	public String readFileText(String path, Object ...arguments) {
		try {
			//Create a new buffered reader to load the file with.
			BufferedReader reader = new BufferedReader(new FileReader(getServletContext().getRealPath(path)));
			StringBuffer out = new StringBuffer();
			String line = null;
			while ((line=reader.readLine())!=null) 
				out.append(line);
			
			reader.close();
			
			//If there are additional arguments passed in, include them. Otherwise return the raw string.
			if (arguments.length > 0)
				return MessageFormat.format(out.toString(), arguments);
			else
				return out.toString();
		} catch (IOException e) {
			e.printStackTrace();
			//Stop the program immediately an error occurs.
			throw new RuntimeException("Error reading file text!");
		}
	}
	
	public String readFileText(HTMLAdapter htmlAdapter) {
		return readFileText(htmlAdapter.getFilePath(), htmlAdapter.getArguments().toArray());
	}
	
	public String generateCSS() {
		return "<style>"+readFileText("/css/style.css")+"</style>";
	}
	
	
	
	public String getHTML(HttpServletRequest req) {
		
		//Generate a basic html page from base template..
		return readFileText("html/base.html", title, generateCSS(), getNavbar(req).getHTML(getActiveNavbarItem()), getBodyHTML(req));
	}
	public abstract String getBodyHTML(HttpServletRequest req);
	
	
	public Navbar getNavbar(HttpServletRequest req) {
		return new LoggedInNavbar();
	}
	
	public abstract String getActiveNavbarItem();
	
}
