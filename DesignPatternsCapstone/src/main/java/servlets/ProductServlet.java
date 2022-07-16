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
		String step = req.getParameter("id");
		long id = Long.parseLong(step);
		System.out.println(id);
		
		Product pr = database.getProductWithID(id);
		
		StringBuilder intro = new StringBuilder();
			intro.append("<h2>Product</h2>");
			System.out.println(pr.imagePath);
			
			//body.append("<img src=${pageContext.request.contextPath}/images/" + pr.imagePath + "alt=\"Product Image\" width=\"500\" height=\"500\">");
		StringBuilder body = new StringBuilder();
			body.append("<h2>" + pr.name + "</h2>");
			
		ResultSet rs = database.getBidswithPID(id);
		//i need some form of html list here in a string that I can add variables to and pass one giant list to product.html
		StringBuilder table = new StringBuilder();
				table.append("<table>" 
				+"	<tr>"
				+"  <th>Bid Id</th>"
				+"  <th>Ammount</th>"
				+"  <th>Bid Date</th>"
				+"  <th>User Name</th>"
				+"  </tr>");
		LocalDateTime startDate = null;
		int duration = 0;
		int startPrice = 0;
		int ownerId = 0;
		try {
			while (rs.next()) {
				int bidID = rs.getInt("id");
				int ammount = rs.getInt("ammount");
				Date bidDate = rs.getDate("date");
				startDate = (LocalDateTime) rs.getObject("start_date");
				duration = rs.getInt("duration_mins");
				startPrice = rs.getInt("start_price");
				String userName = rs.getString("username");
				ownerId = rs.getInt("owner_id");
				String maskedUser = replaceAll(userName);
				table.append("<tr>"
						+"	<td>" + bidID +"</td>" 
						+"	<td>" + ammount +"</td>"
						+"	<td>" + bidDate +"</td>"
						+"	<td>" + maskedUser +"</td></tr>");
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Error creating bid table");
		}
		//here we need to get the end date again.
		LocalDateTime finalDate = startDate.plusMinutes(duration);
		LocalDateTime currentDate = LocalDateTime.now();
	
		Duration dur = Duration.between(finalDate, currentDate);
		System.out.println(dur);
		long days = dur.toDays();
		long hours = (dur.toHours()) % 24;
		long minutes = (dur.toMinutes()) % 60;
		long seconds = (dur.toSeconds()) % 60;
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






}