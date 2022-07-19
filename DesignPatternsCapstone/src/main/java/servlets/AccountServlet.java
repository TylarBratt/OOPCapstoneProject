package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Auction;
import beans.BidResult;
import beans.LocalURLBuilder;
import beans.Product;
import beans.User;
import beans.exception.BidTooLowException;
import beans.exception.InsufficientFundsException;
import beans.exception.InvalidBidderException;
import beans.exception.InvalidInputException;
import beans.navbar.LoggedInNavbar;

@WebServlet("/account")

public class AccountServlet extends BaseServlet {
	
	public AccountServlet(){
		super("FleaBay - Account Overview", "account", true, true);
	}
	
	
	

	public String getBodyHTML(HttpServletRequest req) {
		
		//Fetch the updated user info.
		User user = database.getUser((Long)req.getSession().getAttribute("user"));
		
		
		//Build the html for each product in the user's inventory..
		StringBuffer products = new StringBuffer();
		for (Product product : database.getProductsOwnedByUser(user.id)) {
			String controls = "";
			Auction auction = database.getActiveAuctionForProduct(product.id);

			//Add 'create auction' button if this product is not currently for auction.
			if (auction == null)
				controls = readFileText("html/product-make-auction-button.html",  product.id);
			
			products.append(readFileText("html/product.html", product.imagePath, product.name, controls));
		}
		
		//If there are no products to display, display a friendly message.
		if (products.length() == 0)
			products.append("<p>You have no items in your inventory.</p>");
	
		StringBuilder participatingAuctions = new StringBuilder();
		for (Auction auction : database.getParticipatingAuctions(user.id)) {
			
			StringBuilder message = new StringBuilder();
			
			//Get a status message describing whether this auction is winning/losing.
			message.append("<p>");
			if (auction.isActive) {
				if (auction.highBidderID == user.id)
					message.append("You're winning!");
				else
					message.append("You were outbid!");
			} else {
				if (auction.highBidderID == user.id)
					message.append("You won!");
				else
					message.append("You didn't win.");
			}
			message.append("</p>");
			
			//Show the current high bid (if there is one)..
			if (auction.hasBid()) {
				message.append("<p>");
				message.append("High Bid: ");
				message.append(auction.getHighBidText());
				message.append("</p>");
			}
			
			StringBuilder endDateMsg = new StringBuilder();
			//SHow the end date.
			if (auction.isActive)
				endDateMsg.append("Ends ");
			else
				endDateMsg.append("Ended ");
			endDateMsg.append(auction.getEndDate());
			
			Product product = database.getProductWithID(auction.productID);
			if (product != null) 
				participatingAuctions.append(readFileText("html/participating-auction.html", product.imagePath, product.name, message, endDateMsg));
		}
		
		StringBuilder startedAuctions = new StringBuilder();
		for (Auction auction : database.getStartedAuctions(user.id)) {
			StringBuilder endDateMsg = new StringBuilder();
			//SHow the end date.
			if (auction.isActive)
				endDateMsg.append("Ends ");
			else
				endDateMsg.append("Ended ");
			
			endDateMsg.append(auction.getEndDate());
			
			
			StringBuilder message = new StringBuilder();
			
			message.append("<p>");
			if (auction.isActive) {
				//Show the current high bid (if there is one)..
				if (auction.hasBid()) {
					
					message.append("High Bid: ");
					message.append(auction.getHighBidText());
				}
				else
					message.append("No Bids");
			}
			else {
				if (auction.hasBid()) {
					message.append("Sold for: ");
					message.append(auction.getHighBidText());
				}
				else
					message.append("Didn't Sell");
			}
			
			message.append("</p>");
			

			//Include a cancel button if the auction is still active.
			if (auction.isActive) 
				message.append(readFileText("html/cancel-auction-button.html", auction.id));
			
			
			
			
			
			
			Product product = database.getProductWithID(auction.productID);
			if (product != null) 
				startedAuctions.append(readFileText("html/participating-auction.html", product.imagePath, product.name, message, endDateMsg));
		}
		
		
		return readFileText("html/account.html", 
				user.userName, 
				user.credits, 
				products.toString(),
				participatingAuctions,
				startedAuctions);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Long auctionToCancelID;
		try{
			auctionToCancelID = Long.parseLong(req.getParameter("cancel-auction"));
		} catch (NumberFormatException e) {
			auctionToCancelID = null;
		}
		
		System.out.println("Auction to cancel: "+auctionToCancelID);
		
		if (auctionToCancelID != null)
			database.cancelAuction(auctionToCancelID);
		
		//Start building the parameter list that we'll use to display a message to the user.
		LocalURLBuilder responseUrl = new LocalURLBuilder("account",req);
		

		//Reload the home page with response parameters.
		resp.sendRedirect(responseUrl.toString());
	}
	
	
}
