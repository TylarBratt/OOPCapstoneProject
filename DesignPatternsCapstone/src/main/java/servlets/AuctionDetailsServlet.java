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
import beans.Timespan;
import beans.LocalURLBuilder;
import beans.User;
import beans.navbar.LoggedInNavbar;
import beans.navbar.Navbar;
import beans.exception.BidTooLowException;
import beans.exception.InvalidBidderException;
import beans.exception.InvalidInputException;

@WebServlet("/auction-details")

public class AuctionDetailsServlet extends BaseServlet {

	public AuctionDetailsServlet() {
		super("FleaBay - Auction Details", true, true);
		
	}

	@Override
	public String getBodyHTML(HttpServletRequest req) {
		long auctionID = Long.parseLong(req.getParameter("aid"));
		Auction auction = database.getAuctionWithID(auctionID);
		Product product = database.getProductWithID(auction.productID);
		
		//Include product image
		String productImage = readFileText("html/product.html", product.imagePath, product.name, "");
		
		StringBuilder table = new StringBuilder();
		List<Bid> bids = database.getBidsForAuction(auctionID);
		if (bids.size() > 0) {
			table.append("<table>");
			
			//Add the table header first..
			table.append(readFileText("html/bid-table-header.html")); 
				
			//Add a row of info for each bid..
			for (Bid bid : bids) 
				table.append(readFileText("html/bid-table-row.html", 
						bid.amount, 
						bid.time, 
						database.getUser(bid.userID).getMaskedUserName()));
			
			table.append("</table>");
			
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




}