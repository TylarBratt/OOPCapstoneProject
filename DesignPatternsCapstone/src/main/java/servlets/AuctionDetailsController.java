package servlets;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import beans.Auction;
import beans.Bid;
import beans.Product;
import beans.User;

@WebServlet("/auction-details")

public class AuctionDetailsController extends JSPController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuctionDetailsController() {
		super("auction-details.jsp", true, true);	
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
		
	
		User user = database.getUser(auction.ownerID);
		
		List<Bid> bids = database.getBidsForAuction(id);
		
		//Build list of masked usernames for each bid..
		List<String> bidderNames = new ArrayList<>();
		for (Bid bid : bids) {
			if (bid != null)
				bidderNames.add(database.getUser(bid.userID).getMaskedUserName());
			else
				bidderNames.add(null);
		}
		
		req.setAttribute("Bids", bids);
		req.setAttribute("BidderNames", bidderNames);
		req.setAttribute("Auction", auction);
		req.setAttribute("Product", product);
		req.setAttribute("Owner", user);
	}
}
        



