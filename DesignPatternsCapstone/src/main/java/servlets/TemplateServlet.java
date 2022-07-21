package servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

import beans.Auction;
import beans.DefaultProductInfo;
import beans.LocalURLBuilder;
import beans.Product;
import beans.navbar.LoggedOutNavbar;
import beans.navbar.Navbar;



/**
 * A basic servlet template. 
 * Duplicate this to add a new webpage to the site.
 */
@WebServlet("/template") //<-- The path that this servlet is located at. The URL will be www.yoursite.com/template.

public class TemplateServlet extends BaseServlet {

	public TemplateServlet() {
		/*
		 * Base Servlet Constructor
		 * 1. String : The title that will be displayed at the top of the webpage.
		 * 2. String : The servlet name. This should match the name used in your Navbar (See LoggedInNavbar class).
		 * 3. Boolean : Does this servlet need database access? If TRUE, database connection will be automatically initialized and made available through the "database" member.
		 * 4. Boolean : Is login required to access this page? If TRUE, this servlet will automatically redirect users to the login screen if they don't have an active login session.
		 */
		super("Title", true, false);
	}

	
	/**
	 * With the new BaseServlet design, we no longer have to implement the doGet() method. 
	 * The base class will handle this for us and automatically apply the appropriate page styling and navbar. 
	 * The only data we have to provide is the body HTML, which is implemented by the method below.
	 * 
	 * Note: Do not make any changes to the database from here. Updates to the database should only be done via POST requests in doPost().
	 */
	@Override
	public String getBodyHTML(HttpServletRequest req) {
		/* To display a special state such as an error/success message, pass the
		 * data in via URL paramaters (Ex: mysite.com/template?errorMsg=InvalidBid)
		 * and extract it from the HttpServletRequest.
		 * (See doPost() below for an example of how to add parameters to a URL.)
		 */
		
		// Example: Extract an integer from a URL parameter.
		Integer itemID;
		try {
			itemID = Integer.parseInt(req.getParameter("item-id"));
		} catch (NumberFormatException e) {
			itemID = null;
		}
		
		
		//Start building the body HTML..
		StringBuilder bodyHTML = new StringBuilder();
		
		//Display a list of items using HTML loaded from an external file.
		for (DefaultProductInfo product : DefaultProductInfo.values()){
			
			//Load the item html from file and populate it with the necessary values.
			String itemHTML = readFileText("html/product.html", 
											product.path, 
											product.description, 
											"Any html works here");
			
			bodyHTML.append(itemHTML);
		}


		return bodyHTML.toString();
	}

	

	/**
	 * This method is responsible for processing any POST requests.
	 * Requests that change the contents of the database should only be handled through this method and not getBodyHTML().
	 * */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * Example: Reload the same page with additional paramaters in its URL in order to display a custom message.
		 * This is typically done to display the result of an action performed by the user (ie. make bid, start auction, etc)
		 */
		
		LocalURLBuilder url = new LocalURLBuilder("template",req);
		
		boolean isSuccess = true;
		if (isSuccess)
			url.addParam("result","message");
		else
			url.addParam("result","failed");
		
		//Reload the home page with response parameters.
		resp.sendRedirect(url.toString());
         
       	
    	
    }


	@Override
	public String getActiveNavbarItem() {
		return "template";
	}
	
	/**
	 * By default, this page will use the LoggedInNavbar.
	 * If you want this page to have a different navbar, uncomment the method below and return
	 * your own Navbar implementation instead.
	 */
	/*
	@Override
	public Navbar getNavbar(HttpServletRequest req) {
		//Always return the logged out navbar..
		return new LoggedOutNavbar();
	}
	*/
            


	
}