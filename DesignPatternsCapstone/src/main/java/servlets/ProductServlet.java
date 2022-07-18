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
import beans.BidResult;
import beans.Database;
import beans.DefaultProductInfo;
import beans.Product;
import beans.LocalURLBuilder;
import beans.User;
import beans.navbar.LoggedInNavbar;
import beans.navbar.Navbar;
import beans.exception.BidTooLowException;
import beans.exception.InvalidBidderException;
import beans.exception.InvalidInputException;

@WebServlet(
	name = "productDescription",
	urlPatterns = "/productDescription"
)

public class ProductServlet extends BaseServlet {

	public ProductServlet() {
		super("FleaBay - Product Description", "productDescription", true, true);
		
	}

	@Override
	public String getBodyHTML(HttpServletRequest req) {
		/*
		 * Product Description page
		 * We have passed a variable with the req that we need to save called with the value of product.id
		 * We need the following to construct a nice description page
		 * Name, Picture, All bids, bid, Start date, end date, start price,
		 * 
		 * new problem, We need to pass or grab the auction id right from the passed auction since there could be multiple auctions for the same product. 
		 * just do it,
		 * pass it through the same as id and add it to the sql statement.
		 */
		
		
		int ownerId = 0;
		boolean full = false;
		
	
		String step = req.getParameter("id");
		long id = Long.parseLong(step);
		System.out.println(id);
		
		Product pr = database.getProductWithID(id);
		
		StringBuilder intro = new StringBuilder();
			intro.append("<h2>Product</h2>");
			System.out.println(pr.imagePath);
		StringBuilder body = new StringBuilder();
			body.append("<h2>" + pr.name + "</h2>");
			//ok so this program has to be split into two seperate steps due to the sql statements required. If the first statement returns with no bids then the second statement runs.
			
			
			//this is the start of the first section
		ResultSet rs = database.getBidswithPID(id);
		
		boolean first = true;
		try {
			StringBuilder table = new StringBuilder();
			while (rs.next()) {
				if (first == true) {
					
					table.append("<table>" 
					+"	<tr>"
					+"  <th>Bid Id</th>"
					+"  <th>Ammount</th>"
					+"  <th>Bid Date</th>"
					+"  <th>User Name</th>"
					+"  </tr>");
					first = false;
				}
				full = true;
				int bidID = rs.getInt("id");
				int ammount = rs.getInt("ammount");
				Date bidDate = rs.getDate("date");
				int duration = rs.getInt("duration_mins");
				int startPrice = rs.getInt("start_price");
				String userName = rs.getString("username");
				ownerId = rs.getInt("owner_id");
				String maskedUser = replaceAll(userName);
				table.append("<tr>"
						+"	<td>" + bidID +"</td>" 
						+"	<td>" + ammount +"</td>"
						+"	<td>" + bidDate +"</td>"
						+"	<td>" + maskedUser +"</td></tr>");
				
		//here we need to get the end date again.
		Auction auction	= database.getActiveAuctionForProduct(pr.id);
		Date startDate = auction.startDate;
		
		Date finalDate = DateUtils.addMinutes(startDate, duration);
		Date currentDate = new Date();
	
		long toConvert = finalDate.getTime() - currentDate.getTime();
		toConvert = Math.max(toConvert, 0L);
		long days= (toConvert / (1000 * 60 * 60 * 24)) % 365;
		long hours = (toConvert / (1000 * 60 * 60)) % 24;
		long minutes = (toConvert / (1000 * 60))% 60;
		long seconds = (toConvert / 1000) % 60;
		
	
		System.out.println(startDate);
		System.out.println(currentDate);
		System.out.println(finalDate);
		User auctioneer = database.getUser(ownerId);
		String userId = replaceAll(auctioneer.userName);
			
				
		table.append("</table><h4> Starting Price: " + startPrice + "</h4>"
				+ "<h4> Start Date: " + startDate +"</h4>"
				+ "<h4> End Date: " + finalDate + "</h4>"
				+ "<h4> Days: " + days + " Hours: " + hours + " Minutes: " + minutes + " Seconds: " + seconds + "</h4>"
				+ "<h5> Auctioneer: " + userId + "</h5>");
		body.append(table.toString());
		rs.close();
		return readFileText("html/product-details.html", intro, pr.imagePath, body);
			}
			if (full == false) {
				body.append("<h4>No Bids</h4>");
				Auction auction = database.getActiveAuctionForProduct(pr.id);
				
				Date startDate = auction.startDate;
				Long temp2 = auction.durationMins;
				int duration = temp2.intValue();
				Date finalDate = DateUtils.addMinutes(startDate, duration);
				LocalDateTime now = LocalDateTime.now();
				Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
				
				long toConvert = finalDate.getTime() - currentDate.getTime();
				toConvert = Math.max(toConvert, 0L);
				long days= (toConvert / (1000 * 60 * 60 * 24)) % 365;
				long hours = (toConvert / (1000 * 60 * 60)) % 24;
				long minutes = (toConvert / (1000 * 60))% 60;
				long seconds = (toConvert / 1000) % 60;
				
				Long startPrice = auction.startPrice;
				User user = database.getUser(auction.ownerID);
				String userId = replaceAll(user.userName);
				table.append("</table><h4> Starting Price: " + startPrice + "</h4>"
				+ "<h4> Start Date: " + startDate +"</h4>"
				+ "<h4> End Date: " + finalDate + "</h4>"
				+ "<h4> Days: " + days + " Hours: " + hours + " Minutes: " + minutes + " Seconds: " + seconds + "</h4>"
				+ "<h5> Auctioneer: " + userId + "</h5>");
				body.append(table.toString());	
				
				
				
				
				
				return readFileText("html/product-details.html", intro, pr.imagePath, body);
			}
			}catch (SQLException e) {
				e.printStackTrace();
			}	
		//this is the end of the first section and the start of the second.
		
		
		
		
	
	return readFileText("html/product-details.html", intro, pr.imagePath, body);
	
		}
	
public String replaceAll(String string) {
	StringBuilder place = new StringBuilder();
	if (string.length()>2) {
		place.append(string.charAt(0));
		for (int i = 1; i < string.length() - 1; i++) {
			place.append("*");
		}
		place.append(string.charAt(string.length() - 1));
		return place.toString();
	}
	return string;
}

public int[] getDuration(Date startDate, Date finalDate) {
	int[] duration = new int[4];
	
	
	
	return duration;
}




}