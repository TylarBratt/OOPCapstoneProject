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

@WebServlet("/makeAuction")

public class MakeAuctionServlet extends BaseServlet {
	
	public MakeAuctionServlet(){
		super(true);
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//If user is not logged in, redirect to login screen.
		User user = (User)req.getSession().getAttribute("user");
		if (user == null)
			resp.sendRedirect(req.getContextPath()+"/login");
		else
			resp.getWriter().write(getHTMLString(req));
		
		
	}

	@Override
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		
		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(req));
	}
	
	public String getHTMLString(HttpServletRequest req) throws IOException {
		
	
		
		String productHTML = "";
		try {
			long productID = Long.parseLong(req.getParameter("id"));
			System.out.println("product id:"+productID);
			Product product = database.getProductWithID(productID);
			if (product != null) {
				productHTML = readFileText("html/product-list-item.html", product.imagePath, product.name, product.id);
			}
		} catch (NumberFormatException e) {
			System.out.println("Oops!");
			//TODO: Show error page if params are incorrect.String productHTML = "";
		}
		
		
		return readFileText("html/makeAuction.html", generateCSS(), productHTML);
	}
	
}