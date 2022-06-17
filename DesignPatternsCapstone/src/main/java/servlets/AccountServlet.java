package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Product;
import beans.User;

@WebServlet("/account")

public class AccountServlet extends BaseServlet {
	
	public AccountServlet(){
		super(true);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//If user is not logged in, redirect to login screen.
		User user = (User)req.getSession().getAttribute("user");
		if (user == null) 
			resp.sendRedirect(req.getContextPath()+"/login");
		else
			resp.getWriter().write(getHTML(req));
		
	}
	
	public String getHTML(HttpServletRequest req) throws IOException {
		
		User user = (User)req.getSession().getAttribute("user");
		
		//Get products html..
		StringBuffer products = new StringBuffer();
		
		
		
		for (Product product : database.getProductsOwnedByUser(user.id)) 
			products.append(readFileText(getServletContext().getRealPath("html/product-list-item.html"), product.imagePath, product.name));
		
		if (products.length() == 0)
			products.append("<p>You have no items in your inventory.</p>");
	
		
		
		

		return readFileText(getServletContext().getRealPath("html/account.html"), generateCSS(), products, user.userName, user.credits);
	}

	
}
