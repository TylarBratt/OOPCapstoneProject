package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;

import beans.Auction;
import beans.Database;
import beans.DefaultProductInfo;
import beans.Product;
import beans.User;
import beans.navbar.LoggedInNavbar;

@WebServlet(
	name = "home",
	urlPatterns = "/home"
)

public class HomeServlet extends BaseServlet {

	public static final String htmlPath = "html/home.html";

	public HomeServlet() {
		super(true);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// If user is not logged in, redirect to login screen.
		User user = (User) req.getSession().getAttribute("user");
		if (user == null)
			resp.sendRedirect(req.getContextPath() + "/login");
		else
			resp.getWriter().write(getHTMLString(req));

	}

	@Override

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(req));
	}

	public String getHTMLString(HttpServletRequest req) {

		User user = (User) req.getSession().getAttribute("user");

		StringBuilder newMessage = new StringBuilder();
		
		

		newMessage.append("<h2>Auctions</h2>");
		
		List<Auction> auctions = database.getAuctions();
		if (auctions.size() > 0) {
			newMessage.append("<ul list-style: none>");
			
			//For each result in the result set.
			for (Auction auction : database.getAuctions())
			{//here we collect information to calculate date
				Date date = auction.getDate();
				long min = auction.getDurationMins();
				Date finalDate = DateUtils.addMinutes(date, (int) min);
				LocalDateTime now = LocalDateTime.now();
				Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
				//here we actually calculate the date but it is in milliseconds or something
				long toConvert = finalDate.getTime() - currentDate.getTime();
				//here we convert the milliseconds into viewable time increments.
				long Days= (toConvert / (1000 * 60 * 60 * 24)) % 365;
				long Hours = (toConvert / (1000 * 60 * 60)) % 24;
				long Minutes = (toConvert / (1000 * 60))% 60;
				long Seconds = (toConvert / 1000) % 60;
				
						
				String intro = "<li><form action=\"BidServlet\" method=\"post\">";
				String body1 = "<input type=hidden name=\"productName\" value=";
				//product name goes here
				String body2 = "/><h4>"+ (auction.hasBid()? "Highest Bid:":"Starting Price:")+"</h4><h5>";
				// highest bid from auction goes here
				//the lower line adds the time
				String body3 = "</h5><h4>Time Remaining: </h4><h5>" + "Days: " + Long.toString(Days) + ", Hours: " + Long.toString(Hours) + ", Minutes: "+ Long.toString(Minutes) + ", Seconds: " + Long.toString(Seconds) + "</h5><input type=\"number\" placeholder=\"Enter your bid\" name=\"newestBid\"/><input type=hidden name=\"location\"</li>\n";
				//the lower line is used to add the auction id.
				String body4 = "<input type=hidden name=\"id\" value=";
				String link = "><input type = \"submit\" value=\"Bid\"/></form>";
				String productInfo = intro + body1 + auction.productName + body2 + auction.getCurrentPrice() + body3 + body4
						+ Long.toString(auction.id) + link;
				
				Product product = database.getProductWithID(auction.productID);
				if (product != null) 
					newMessage.append(readFileText("html/product.html", product.imagePath, product.name, productInfo));
				
			}
			
			newMessage.append("</ul>");
		}
		else
			newMessage.append("<h3>None</h3>");
		
		
		
		//Print the message for debugging purposes.
		//System.out.println(finalMessage);

		return readFileText(htmlPath, newMessage.toString(), new LoggedInNavbar().getHTML("home"), generateCSS(), user.userName);
	}

}