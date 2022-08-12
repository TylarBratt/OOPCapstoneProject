<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>

<!-- Import java classes -->
<%@ page import="java.util.List" 
		 import="beans.User" 
		 import="beans.Product" 
		 import="beans.Auction" %>
		 

<!-- Initialize Data -->
<% User user = (User) request.getAttribute("user"); 
   List<Product> inventory = (List<Product>)request.getAttribute("inventory"); 
   List<Auction> inventoryAuctions = (List<Auction>)request.getAttribute("inventoryAuctions"); 
   
   List<Auction> participatingAuctions = (List<Auction>)request.getAttribute("participatingAuctions"); 
   List<Product> participatingAuctionProducts = (List<Product>)request.getAttribute("participatingAuctionProducts");
   List<String> participatingAuctionEndDates = (List<String>)request.getAttribute("participatingAuctionEndDates"); 
   List<String> participatingAuctionMessagesA = (List<String>)request.getAttribute("participatingAuctionMessagesA"); 
   List<String> participatingAuctionMessagesB = (List<String>)request.getAttribute("participatingAuctionMessagesB"); 
   
   List<Auction> startedAuctions = (List<Auction>)request.getAttribute("startedAuctions"); 
   List<Product> startedAuctionProducts = (List<Product>)request.getAttribute("startedAuctionProducts");
   List<String> startedAuctionEndDates = (List<String>)request.getAttribute("startedAuctionEndDates"); 
   List<String> startedAuctionMessages = (List<String>)request.getAttribute("startedAuctionMessages"); 
   
   %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Account Overview - FleaBay</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
	</head>
	<body>
	
		<!-- Navbar -->
		<jsp:include page="navbar-logged-in.jsp">  
			<jsp:param name="active" value="account" />  
		</jsp:include>  
		
		<h2>Account Overview</h2>
		
		<h3>Username</h3>
		<%= user.userName %>
		
		<h3>Credits</h3>
		<%= user.credits %>
		
		<!-- Inventory -->
		<h3>Inventory</h3>
		<% if (inventory.size() > 0) {
		   		for (int i = 0; i < inventory.size(); i++) {  %>
					<jsp:include page='<%= (inventoryAuctions.get(i) == null ? "product-icon-make-auction.jsp" : "product-icon.jsp")  %>'>
						<jsp:param name="name" value='<%= inventory.get(i).name %>' />
						<jsp:param name="image" value='<%= inventory.get(i).imagePath %>'/>
						<jsp:param name="productid" value='<%= inventory.get(i).id %>' />
					</jsp:include>
				<% }
		} else { %>
			<p>You have no items in your inventory.</p>
		<% } %>
		
		<!-- Participating Auctions -->
		<% if (participatingAuctions.size() > 0) { %>
			<h3>Auctions You Bid On</h3>
			<% for (int i = 0; i < participatingAuctions.size(); i++) {  
				Product product = participatingAuctionProducts.get(i); %>
					<jsp:include page="participating-auction.jsp">
						<jsp:param name="auction-id" value='<%= participatingAuctions.get(i).id %>' />
						<jsp:param name="image" value='<%= product.imagePath %>'/>
						<jsp:param name="end-date" value='<%= participatingAuctionEndDates.get(i) %>' />
						<jsp:param name="product-name" value='<%= product.name %>' />
						<jsp:param name="msg-a" value='<%= participatingAuctionMessagesA.get(i) %>' />
						<jsp:param name="msg-b" value='<%= participatingAuctionMessagesB.get(i) %>' />
					</jsp:include>
				<% }
		} %>
		
		<!-- Started Auctions -->
		<% if (startedAuctions.size() > 0) { %>
			<h3>Auctions You Started</h3>
			<% for (int i = 0; i < startedAuctions.size(); i++) {  
				Product product = startedAuctionProducts.get(i); %>
					<jsp:include page="started-auction.jsp">
						<jsp:param name="auction-id" value='<%= startedAuctions.get(i).id %>' />
						<jsp:param name="image" value='<%= product.imagePath %>'/>
						<jsp:param name="end-date" value='<%= startedAuctionEndDates.get(i) %>' />
						<jsp:param name="product-name" value='<%= product.name %>' />
						<jsp:param name="msg" value='<%= startedAuctionMessages.get(i) %>' />
						<jsp:param name="is-active" value='<%= startedAuctions.get(i).isActive ? "Y" : "N" %>' />
					</jsp:include>
				<% }
		} %>
		
		
		
	</body>
</html>

