<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="beans.Product"
    import="beans.Database"%>

<% Product product = (Product)request.getAttribute("product");
   String extraHTML = (String)request.getAttribute("productExtraHTML"); %>
<div class="product">
	<h3 class="product-name"><%= product.name %></h3>
	<img class="product-image" src="images?src=<%= product.imagePath %>">
	<%= extraHTML %>
</div>