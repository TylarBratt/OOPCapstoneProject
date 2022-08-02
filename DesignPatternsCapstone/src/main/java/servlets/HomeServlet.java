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

public class HomeServlet extends BaseServlet {

	public HomeServlet() {
		super("FleaBay - Home", true, true);
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
        		
        		//Calculate the ID for the for the new bid by adding 1 to the highest bid id in the database.
        		//TODO: Combine this all into a single SQL stored procedure, since a race condition for the ID can occur if two people try creating a bid at the same time.
        		int bidID = database.getnewID()+1;
               
        		//Get the value of the bid.
        		Integer bidAmount;
        		try {
        			bidAmount = Integer.valueOf(req.getParameter("newestBid"));
        		} catch (NumberFormatException e) {
        			//Error! Bid value was not set.
        			bidAmount = null;
        		}
        		
        		//Start building the parameter list that we'll use to display a message to the user.
        		LocalURLBuilder responseUrl = new LocalURLBuilder("home",req);
        		
        		//Make the bid and add the result to the parameters.
    			try {
					database.makeBid(auction, userID, bidAmount);
					responseUrl.addParam(BidResult.BID_SUCCESS.paramName);
				} catch (BidTooLowException e) {
					responseUrl.addParam(BidResult.BID_FAILED_TOO_LOW.paramName);
				} catch (InvalidInputException e) {
					responseUrl.addParam(BidResult.BID_FAILED_NO_VALUE.paramName);
				} catch (InvalidBidderException e) {
					responseUrl.addParam(BidResult.BID_FAILED_INVALID_BIDDER.paramName);
				} catch (InsufficientFundsException e) {
					responseUrl.addParam(BidResult.BID_FAILED_INSUFFICIENT_FUNDS.paramName);
				}
    			

    			responseUrl.addParam("auction-id", Long.toString(auction.id));
    			
    			//Reload the home page with response parameters.
				resp.sendRedirect(responseUrl.toString());
           } 
       	
    	}
    }
            


	@Override
	public String getBodyHTML(HttpServletRequest req) {
		//Obtain the auction index to display a special message on.
		Long targetAuctionID;
		try {
			targetAuctionID = Long.parseLong(req.getParameter("auction-id"));
		} catch (NumberFormatException e) {
			targetAuctionID = null;
		}

		//Retrieve the current time from the database. Used to calculate the time remaining for each auction.
		Timestamp currentTime = database.getCurrentTimestamp();
		
		StringBuilder body = new StringBuilder();
		body.append("<h2>Auctions</h2>");
		
		List<Auction> activeAuctions = database.getActiveAuctions();
		if (activeAuctions.size() > 0) {
			for (Auction auction : activeAuctions)
			{
				String bidSuccessText = "";
				String bidErrorText = "";
				boolean isTargetAuction = (targetAuctionID != null && auction.id == targetAuctionID);
				if (isTargetAuction) {
					//Generate the appropriate success/error message for this auction..
					if (req.getParameter(BidResult.BID_SUCCESS.paramName) != null)
						bidSuccessText = BidResult.BID_SUCCESS.displayMessage;
					else if (req.getParameter(BidResult.BID_FAILED_NO_VALUE.paramName) != null) 
						bidErrorText = BidResult.BID_FAILED_NO_VALUE.displayMessage;
					else if (req.getParameter(BidResult.BID_FAILED_INVALID_BIDDER.paramName) != null)
						bidErrorText = BidResult.BID_FAILED_INVALID_BIDDER.displayMessage;
					else if (req.getParameter(BidResult.BID_FAILED_TOO_LOW.paramName) != null)
						bidErrorText = BidResult.BID_FAILED_TOO_LOW.displayMessage;
					else if (req.getParameter(BidResult.BID_FAILED_INSUFFICIENT_FUNDS.paramName) != null)
						bidErrorText = BidResult.BID_FAILED_INSUFFICIENT_FUNDS.displayMessage;
				}
				
				
				Product product = database.getProductWithID(auction.productID);
				body.append(readFileText("html/auction.html", 
						product.name,
						product.imagePath,
						auction.id,
						auction.hasBid() ? "High Bid:" : "Starting Price:", 
						auction.hasBid() ? auction.getHighBidText() : auction.startPrice,
						auction.getTimeRemaining(currentTime),		
						bidErrorText, 
						bidSuccessText, 
						auction.id));
						
				
			}
			
		}
		else
			body.append("<h4>No active auctions. <a href=\"account\">Create one</a> to get the bidding started!</h4>");
		
		return body.toString();
	}



	@Override
	public String getActiveNavbarItem() {
		return "home";
	}

}