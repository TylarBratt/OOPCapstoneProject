package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Common;
import beans.Log;
import beans.User;
import dao.Database;

@WebServlet("/logout")

public class LogoutServlet extends HttpServlet {
	
	public static final String htmlPath = "html/logout.html";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Welcome to logout");
		//Clear the active user from the current session..
		req.getSession().setAttribute("user", null);
		//Redirect to login screen.
		resp.sendRedirect("login");
		
		
		
	}

}