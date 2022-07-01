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
	
		return readFileText("html/account.html", user.userName, user.credits, products.toString());
	}

	
}
