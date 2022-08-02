<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="beans.Product"
    import="beans.Database"%>

<% Database database = (Database)request.getAttribute("db"); 
   Product product = database.getProductWithID(Long.parseLong(request.getParameter("id")));
   String extraHTML = request.getParameter("extra");
   if (extraHTML == null)
	   extraHTML = "";
%>
<div class="product">
	<h3 class="product-name"><%= product.name %></h3>
	<img class="product-image" src="images?src=<%= product.imagePath %>">
	<%= extraHTML %>
</div>