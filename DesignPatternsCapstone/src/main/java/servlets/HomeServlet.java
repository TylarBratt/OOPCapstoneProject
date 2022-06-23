package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Database;
import beans.DefaultProductInfo;
import beans.Product;
import beans.User;

@WebServlet("/home")

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

	public String getHTMLString(HttpServletRequest req) throws IOException {

		User user = (User) req.getSession().getAttribute("user");
		String loginMessage = "";
		String newMessage = null;
		String finalMessage = null;
		if (user != null) {
			int[] auctionids = null;
			loginMessage = "<h2>Greetings " + user.userName + "!</h2></br><h2>Auctions</h2></br><ul list-style: none>";
			ResultSet srs = database.getAuctions();
			newMessage = loginMessage;
			Boolean alpha = false;
			outer: for (int i = 0; srs.next(); i++) {
				String productName = srs.getString("product.name");
				req.setAttribute("product" + i, productName);
				int price = srs.getInt("bid.ammount");
				if (srs.getInt("bid.ammount") >= 1) {
					price = srs.getInt("bid.ammount");
				} else {
					price = srs.getInt("start_price");
				}

				int auction_id = srs.getInt("id");
				alpha = false;
				if (auctionids == null) {
					int beta = i;
					beta++;
					auctionids = new int[beta];
				}
				inner: if (Arrays.stream(auctionids).noneMatch(j -> j == auction_id)) {
					break inner;
				} else {
					continue outer;
				}
				auctionids[i] = auction_id;

				String intro = "<li><form action=\"BidServlet\" method=\"post\"><h3>Product Name: </h3><h4 name=productName>";
				String body1 = "</h4><input type=hidden name=\"productName\" value=";

				String body2 = "/><h3>Current highest Bid: </h3><h4>";
				// highest bid from auction goes here
				String body3 = "</h4><h3>Input your bid:</h3><input type=\"number\" name=\"newestBid\"/><input type=hidden name=\"location\"</li>\n";
				String body4 = "<input type=hidden name=\"id\" value=";
				String link = "><input type = \"submit\" value=\"Bid\"/>";
				newMessage = newMessage + intro + productName + body1 + productName + body2 + price + body3 + body4
						+ auction_id + link;
				System.out.println(newMessage);
			}
		}
		finalMessage = newMessage + "</ul>";
		System.out.println(finalMessage);

		return readFileText(htmlPath, finalMessage, "", generateCSS(), user.userName);
	}

}