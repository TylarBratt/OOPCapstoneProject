package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Auction;
import beans.Product;
import beans.User;
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
	
		StringBuilder auctions = new StringBuilder();
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
				endDateMsg.append("Ends: ");
			else
				endDateMsg.append("Ended: ");
			endDateMsg.append(auction.getEndDate());
			
			Product product = database.getProductWithID(auction.productID);
			if (product != null) 
				auctions.append(readFileText("html/participating-auction.html", product.imagePath, product.name, message, endDateMsg));
		}
		
		
		return readFileText("html/account.html", 
				user.userName, 
				user.credits, 
				products.toString(),
				auctions);
	}

	
}
