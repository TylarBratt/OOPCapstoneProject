<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%!
String activeItem = "";
public String highlightIfActive(){
	
	if (activeItem.equalsIgnoreCase("itemName"))
		return "class=\"active\"";
	else return "";
}

public String addItem(String title, String path, boolean isFloatingRight){
	StringBuilder out = new StringBuilder();
	out.append("<li");
	if (isFloatingRight)
		out.append(" style=\"float:right\"");
	out.append(">");
	
	out.append("<a href=\"");
	out.append(path);
	out.append("\"");
	
	//Highlight if active
	if (path.equals(activeItem))
		out.append("class=\"active\"");
	
	out.append(">");
	out.append(title);
	out.append("</a></li>");
	return out.toString();
}
%>

<% activeItem = request.getParameter("active"); %>


<ul class="navbar">
	<%= addItem("Auctions", "home", false) %>
	<%= addItem("Account", "account", false) %>
	<%= addItem("Logout", "logout", true) %>
</ul>