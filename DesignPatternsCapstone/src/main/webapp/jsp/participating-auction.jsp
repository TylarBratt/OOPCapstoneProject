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
	
	<!-- Message A -->
	<p><%= request.getParameter("msg-a") %></p>
	
	<!-- Message B -->
	<p><%= request.getParameter("msg-b") %></p>
	
</div>