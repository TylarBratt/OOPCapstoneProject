package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Product;
import beans.User;

@WebServlet("/make-auction")

public class MakeAuctionController extends JSPController {

	public MakeAuctionController() {
		super("make-auction.jsp", true, false);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Product product = database.getProductWithID(Long.parseLong(req.getParameter("id")));
		req.setAttribute("product", product);
		req.setAttribute("productExtraHTML", "");
		
		super.doGet(req, resp);
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
			doGet(req,resp);
		
	}

}

