<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>

<div class="product">
	<h3 class="product-name"><%= request.getParameter("name") %></h3>
	<img class="product-image" src="images?src=<%= request.getParameter("image") %>">
	<a class="button-link" href='make-auction?id=<%= request.getParameter("productid") %>'> Create Auction </a>
</div>