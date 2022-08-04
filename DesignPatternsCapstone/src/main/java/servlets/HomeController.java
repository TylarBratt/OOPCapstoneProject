package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

import beans.Auction;
import beans.BidResult;
import beans.Database;
import beans.DefaultProductInfo;
import beans.Product;
import beans.Timespan;
import beans.LocalURLBuilder;
import beans.User;
import beans.navbar.LoggedInNavbar;
import beans.navbar.Navbar;
import beans.exception.BidTooLowException;
import beans.exception.InsufficientFundsException;
import beans.exception.InvalidBidderException;
import beans.exception.InvalidInputException;

@WebServlet("/home")

public class HomeController extends JSPController {


	public HomeController() {
		super("home.jsp", true, true);
	}

	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//Get user ID from the current session.
		Long userID = (Long) req.getSession().getAttribute("user");

		//Redirect to login screen if user is not logged in.
        if (userID == null)
            resp.sendRedirect(req.getContextPath() + "/login");
        else {
        	String postAction = req.getParameter("action");
        	System.out.println("Post action: "+postAction);
        	if (postAction == null)
        		throw new RuntimeException("Post request recieved with no action parameter specified.");
        	else if (postAction.equals("bid")) {
        		//Start issuing a new bid...
        		
        		//Get the auction that is being bid on.
        		Auction auction = database.getAuctionWithID(Integer.valueOf(req.getParameter("id")));
        		
        		//Get the value of the bid.
        		Integer bidAmount;
        		try {
        			bidAmount = Integer.valueOf(req.getParameter("newestBid"));
        		} catch (NumberFormatException e) {
        			//Error! Bid value was not set.
        			bidAmount = null;
        		}
        		
        		
        		//Set the target auction ID attribute. This will let the JSP know which auction was just bid on.
        		req.setAttribute("targetAuctionID", auction.id);
        		//Make the bid and add the result to the parameters.
    			try {
					database.makeBid(auction, userID, bidAmount);
					req.setAttribute("bidSuccessMsg", "Bid success!");
				} catch (BidTooLowException e) {
					req.setAttribute("bidErrorMsg", "Bid too low. Try again.");
				} catch (InvalidInputException e) {
					req.setAttribute("bidErrorMsg", "You must enter a bid value!");
				} catch (InvalidBidderException e) {
					req.setAttribute("bidErrorMsg", "You are not allowed to bid on your own auctions.");
				} catch (InsufficientFundsException e) {
					req.setAttribute("bidErrorMsg", "Bid failed. Insufficient funds.");
				}
    			
				doGet(req,resp);
           } 
       	
    	}
    }



	@Override
	public void initializeData(HttpServletRequest req) {
		List<Auction> activeAuctions = database.getActiveAuctions();
		
		//Build the list of products for each active auction.
		List<Product> activeAuctionProducts = new ArrayList<>();
		for (Auction auction : activeAuctions) 
			activeAuctionProducts.add(database.getProductWithID(auction.productID));
		
		req.setAttribute("currentTime", database.getCurrentTimestamp());
		req.setAttribute("allActiveAuctions", activeAuctions);
		req.setAttribute("allActiveAuctionProducts", activeAuctionProducts);
		
	}
            



}