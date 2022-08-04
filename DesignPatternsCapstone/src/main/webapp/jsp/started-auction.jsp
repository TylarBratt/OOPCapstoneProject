<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div class="participating-auction">
		
	<a href='auction-details?aid=<%= request.getParameter("auction-id") %>'>
		<img src="images?src=<%= request.getParameter("image") %>">
	</a>
	<p class="end-date">
		<%= request.getParameter("end-date") %>
	</p>
	<h3 class="product-name"><%= request.getParameter("product-name") %></h3>
	
	<!-- Message -->
	<p><%= request.getParameter("msg") %></p>
	
	<!-- Cancel Button -->
	<% if (request.getParameter("is-active").equals("Y")) { %>
		<form method=post>
			<input type=hidden name="cancel-auction" value='<%= request.getParameter("auction-id") %>' />
			<input type = "submit" class="button-link" value="Cancel Auction"/>
		</form>
	<% } %>
	
</div>