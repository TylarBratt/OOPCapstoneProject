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

import beans.Auction;
import beans.Database;
import beans.DefaultProductInfo;
import beans.Product;
import beans.User;
import beans.navbar.LoggedInNavbar;

@WebServlet("/make-auction")

public class MakeAuctionServlet extends BaseServlet {
	
	public MakeAuctionServlet(){
		super("Flea Bay - Create Auction", true, true);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//If user is not logged in, redirect to login screen.
		User user = database.getUser((Long)req.getSession().getAttribute("user"));
		if (user == null) {
			resp.sendRedirect(req.getContextPath()+"/login");
			return;
		}
				
		//Obtain the parameters for creating the auction..
		long productID = Long.parseLong(req.getParameter("product"));
		long startingBid = Long.parseLong(req.getParameter("price"));
		long durationMins = Long.parseLong(req.getParameter("duration"));
		
		//Create an auction in the database and return a user.
		boolean success = database.createAuction(user.id, productID, startingBid, durationMins);
		if (success)
			resp.sendRedirect(req.getContextPath()+"/home");
		else
			resp.getWriter().write(getHTML(req));
		
	}
	
	@Override
	public String getBodyHTML(HttpServletRequest req) {
		StringBuilder body = new StringBuilder();
		
		body.append("");
		//Add the product image.
		long productID = Long.parseLong(req.getParameter("id"));
		Product product = database.getProductWithID(productID);
		if (product != null) 
			body.append(readFileText("html/auction.html", product.imagePath, product.name, ""));
		

		//Add the make auction form..
		return readFileText("html/make-auction.html", productID, body.toString());
	}

	@Override
	public String getActiveNavbarItem() {
		return "make-auction";
	}
	
}