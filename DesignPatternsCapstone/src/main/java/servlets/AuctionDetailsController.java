package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

import beans.Auction;
import beans.Bid;
import beans.BidResult;
import beans.Database;
import beans.DefaultProductInfo;
import beans.Product;
import beans.ProductIconHTMLAdapter;
import beans.Timespan;
import beans.LocalURLBuilder;
import beans.User;
import beans.navbar.LoggedInNavbar;
import beans.navbar.Navbar;
import beans.exception.BidTooLowException;
import beans.exception.InvalidBidderException;
import beans.exception.InvalidInputException;

@WebServlet("/auction-details")

public class AuctionDetailsServlet extends JSPController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuctionDetailsServlet() {
		super("auction-details.jsp", true, true);	
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
	public void initializeData(HttpServletRequest req) {
		String middle = req.getParameter("aid");
		long id = Long.valueOf(middle);
		Auction auction = database.getAuctionWithID(id);
		Product product = database.getProductWithID(auction.productID);
		
		StringBuilder table = new StringBuilder();
		List<Bid> bids = database.getBidsForAuction(id);
		if (bids.size() > 0) {	
			//Add a row of info for each bid..
			table.append("<table>");
			table.append("<tr><td>Amount</td><td>Time</td><td>User</td>");
			StringBuilder tableBody = new StringBuilder();
			for (Bid bid : bids) 
				tableBody.append("<tr>"
						+ "<td>"
						+ bid.amount
						+ "</td>"
						+ "<td>"
						+ bid.time
						+ "</td>"
						+ "<td>"
						+ database.getUser(bid.userID).getMaskedUserName()
						+ "</td>");
			
			table.append(tableBody.toString() + "</table>");
		}
		else
			table.append("<p>No Bids</p>");
		
		User user = database.getUser(auction.ownerID);
		
		
		req.setAttribute("Auction", auction);
		req.setAttribute("Product", product);
		req.setAttribute("Table", table.toString());
		req.setAttribute("Owner", user);
	}
	
	
}
        	
        	/*
        	@Override
	public String getBodyHTML(HttpServletRequest req) {
		//Get the auction ID from the URL parameter list
		long auctionID = Long.parseLong(req.getParameter("aid"));
		//Get the auction from the database using the auction ID.
		Auction auction = database.getAuctionWithID(auctionID);
		//Get the product being auctioned.
		Product product = database.getProductWithID(auction.productID);
		
		//Include product image
		String productImage = readFileText(new ProductIconHTMLAdapter(product));
		
		//Include table of bids
		StringBuilder table = new StringBuilder();
		List<Bid> bids = database.getBidsForAuction(auctionID);
		if (bids.size() > 0) {	
			//Add a row of info for each bid..
			StringBuilder tableBody = new StringBuilder();
			for (Bid bid : bids) 
				tableBody.append(readFileText("html/bid-table-row.html", 
						bid.amount, 
						bid.time, 
						database.getUser(bid.userID).getMaskedUserName()));
			
			table.append(readFileText("html/bid-table.html", tableBody));
		}
		else
			table.append("<p>No Bids</p>");
		
		
		return readFileText("html/auction-details.html", 
				productImage,
				table,
				auction.startPrice, 
				auction.startDate,
				auction.getEndDate(),
				auction.getTimeRemaining(database.getCurrentTimestamp()),
				database.getUser(auction.ownerID).userName);
	
	}
	

@Override
public String getActiveNavbarItem() {
	return "home";
}
*/



