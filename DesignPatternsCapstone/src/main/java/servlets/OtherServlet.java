package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Log;
import dao.Database;

@WebServlet("/other")

public class OtherServlet extends HttpServlet {
	
	Database database = null;
	@Override
	public void destroy() {
		//Shut down the database connection..
		database.shutdown();
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		//Initialize the database connection..
		database = new Database();
		
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//Return the default HTML page with a welcome message..
		String path = req.getServletContext().getRealPath("html/index.html");
		
		System.out.println("CONTEXT PATH: " +req.getRequestURI());
		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(path, "Welcome to Other!", null));
	}

	@Override
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getServletContext().getRealPath("html/index.html");
		String actionMsg = null;
		Long itemToEdit = null;
		
		//Check the action parameter to determine what type of action is being performed..
		switch(req.getParameter("action")) {
		case "log":
			//This is a regular log submission...
	    	String logTitle = req.getParameter("title");
			String logContent = req.getParameter("content");
			boolean result = database.addLog(logTitle, logContent);
			
			if (result)
				actionMsg = "Log submitted!";
			else
				actionMsg = "Submission failed. Try again.";
			
			break;
		case "delete":
			//We are deleting a log item.
			long itemToDelete = Long.parseLong(req.getParameter("index"));
			database.removeLog(itemToDelete);
			
			actionMsg = "Log removed!";
			break;
		case "edit":
			//We are starting the edit a log item.
			actionMsg = "Edit Log";
			itemToEdit = Long.parseLong(req.getParameter("index"));
			break;
		case "update":
			//We are done editing the log item and want to update the data..
			actionMsg = "Log Updated!";
			database.updateLog(Long.parseLong(req.getParameter("index")), 
					req.getParameter("title"), 
					req.getParameter("content"));
			
			break;
		}
		
		
		
		PrintWriter writer = resp.getWriter();
		writer.write(getHTMLString(path, actionMsg, itemToEdit));
	}
	
	public String getHTMLString(String filePath, String actionMsg, Long itemToEdit) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = null;
		StringBuffer buffer = new StringBuffer();
		while ((line=reader.readLine())!=null) {
			buffer.append(line);
		}
		
		reader.close();
		
		//Format html for log entries...
		StringBuilder logs = new StringBuilder();
		for (Log log : database.getLogs()) {
			
			logs.append("<article style=\"background-color:lightblue;margin: 8px;padding: 8px;\">");
			//Include the time stamp for this post..
			logs.append("<small>");
			logs.append(log.timestamp);
			logs.append("</small><br>");
			
			if (itemToEdit != null && log.id == itemToEdit) {
				//This item is being edited so show edit fields..
				logs.append("<form method=\"post\">");
				logs.append("<input type=\"text\" name=\"title\" id=\"title\" size=20 maxlength=60 value="+log.title+"><br>");
				logs.append("<input type=\"text\" name=\"content\" id=\"title\" size=60 maxlength=120 value="+log.content+"><br>");
				logs.append("<input type=\"hidden\" name=\"action\" value=\"update\">");
				logs.append("<input type=\"hidden\" name=\"index\" value="+log.id+">");
				logs.append("<input type=\"submit\" value=\"Done\" id=\"done\">");
				logs.append("</form>");
			}
			else {
				//Then this is a regular entry.
				
				logs.append("<b>"+log.title+"</b><br>");
				logs.append(log.content);
				
				logs.append("<form method=\"post\">");
				logs.append("<input type=\"hidden\" name=\"action\" value=\"delete\">");
				logs.append("<input type=\"hidden\" name=\"index\" value="+log.id+">");
				logs.append("<button type=\"submit\" name=\"Delete\" value=\"delete\">Delete</button>");
				logs.append("</form>");
			
				logs.append("<form method=\"post\">");
				logs.append("<input type=\"hidden\" name=\"action\" value=\"edit\">");
				logs.append("<input type=\"hidden\" name=\"index\" value="+log.id+">");
				logs.append("<button type=\"submit\" name=\"Edit\" value=\"edit\">Edit</button>");
				logs.append("</form>");
			}
			
			logs.append("</article>");
			
			
		}
		
		return MessageFormat.format(buffer.toString(), actionMsg != null ? actionMsg : "", logs);
	}
	
}