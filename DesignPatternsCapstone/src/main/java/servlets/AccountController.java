package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Auction;
import beans.Product;
import beans.User;

@WebServlet("/account")

public class AccountController extends JSPController {
	
	public AccountController(){
		super("account.jsp", true, true);
	}
	

	@Override
	public void initializeData(HttpServletRequest req) {
		User user = database.getUser((Long)req.getSession().getAttribute("user"));
		req.setAttribute("user", user);
		
		//Get products in user's inventory.
		List<Product> inventory = database.getProductsOwnedByUser(user.id);
		req.setAttribute("inventory", inventory);
		
		//Build the data lists needed to display the inventory.
		List<Auction> inventoryAuctions = new ArrayList<>();
		for (Product product : inventory) {
			Auction auction = database.getActiveAuctionForProduct(product.id);
			inventoryAuctions.add(auction);
		}
		req.setAttribute("inventoryAuctions", inventoryAuctions);
		
		
		//Initialize data for participating auctions...
		List<Auction> participatingAuctions = database.getParticipatingAuctions(user.id);
		List<Product> participatingAuctionProducts = new ArrayList<>();
		List<String> participatingAuctionEndDates = new ArrayList<>();
		List<String> participatingAuctionMessagesA = new ArrayList<>();
		List<String> participatingAuctionMessagesB = new ArrayList<>();
		
		for (Auction auction : participatingAuctions) {
			String messageA;
			String messageB;
			
			//Generate auction message line A.
			if (auction.isActive) {
				if (auction.highBidderID == user.id)
					messageA = "You're winning!";
				else
					messageA = "You were outbid!";
			} else {
				if (auction.highBidderID == user.id)
					messageA = "You won!";
				else
					messageA = "You didn't win.";
			}
			participatingAuctionMessagesA.add(messageA);
			
			//Generate auction message line B.
			if (auction.hasBid()) 
				messageB = "High Bid: "+auction.getHighBidText();
			else
				messageB = "";
			participatingAuctionMessagesB.add(messageB);
			
			//Generate auction end date text.
			participatingAuctionEndDates.add(auction.getTimeRemainingMessage(database.getCurrentTimestamp()));
			
			//Get the product for this auction.
			participatingAuctionProducts.add(database.getProductWithID(auction.productID));
		}
		
		req.setAttribute("participatingAuctions", participatingAuctions);
		req.setAttribute("participatingAuctionEndDates", participatingAuctionEndDates);
		req.setAttribute("participatingAuctionMessagesA", participatingAuctionMessagesA);
		req.setAttribute("participatingAuctionMessagesB", participatingAuctionMessagesB);
		req.setAttribute("participatingAuctionProducts", participatingAuctionProducts);
		
		//Initialize data for started auctions...
		List<Auction> startedAuctions = database.getStartedAuctions(user.id);
		List<Product> startedAuctionProducts = new ArrayList<>();
		List<String> startedAuctionEndDates = new ArrayList<>();
		List<String> startedAuctionMessages = new ArrayList<>();
		
		for (Auction auction : startedAuctions) {
			String message;
			
			//Generate auction message line A.
			if (auction.isActive) {
				//Show the current high bid (if there is one)..
				if (auction.hasBid()) 
					message = "High Bid: "+auction.getHighBidText();
				else
					message = "No Bids";
			}
			else {
				if (auction.hasBid()) 
					message= "Sold for: " + auction.getHighBidText();
				else
					message = "Didn't Sell";
			}
			startedAuctionMessages.add(message);
			
			//Generate end date text.
			startedAuctionEndDates.add(auction.getTimeRemainingMessage(database.getCurrentTimestamp()));
			
			//Get product for this auction.
			startedAuctionProducts.add(database.getProductWithID(auction.productID));
		}
		
		req.setAttribute("startedAuctions", startedAuctions);
		req.setAttribute("startedAuctionEndDates", startedAuctionEndDates);
		req.setAttribute("startedAuctionMessages", startedAuctionMessages);
		req.setAttribute("startedAuctionProducts", startedAuctionProducts);
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
		
		doGet(req,resp);
	}



	
	
}
