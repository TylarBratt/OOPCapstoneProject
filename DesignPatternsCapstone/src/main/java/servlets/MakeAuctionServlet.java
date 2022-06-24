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
		super(true);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//If user is not logged in, redirect to login screen.
		User user = (User)req.getSession().getAttribute("user");
		if (user == null) {
			resp.sendRedirect(req.getContextPath()+"/login");
			return;
		}
			
		resp.getWriter().write(getHTMLString(req));
	}

	@Override
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//If user is not logged in, redirect to login screen.
		User user = (User)req.getSession().getAttribute("user");
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
			resp.sendRedirect(req.getContextPath()+"/account");
		else
			resp.getWriter().write(getHTMLString(req));
		
	}
	
	public String getHTMLString(HttpServletRequest req) throws IOException {

		StringBuilder body = new StringBuilder();
		
		body.append("<h1>Create An Auction</h1>");
		//Add the product image.
		long productID = Long.parseLong(req.getParameter("id"));
		Product product = database.getProductWithID(productID);
		if (product != null) 
			body.append(readFileText("html/product.html", product.imagePath, product.name, ""));
		

		//Add the make auction form..
		body.append(readFileText("html/make-auction.html", productID));
		
		
		return getHTML("FleaBay - Create Auction", new LoggedInNavbar(), "account",body.toString() );
	}
	
}