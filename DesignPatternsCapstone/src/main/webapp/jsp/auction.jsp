<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.List"
    import="beans.Product"
    import="beans.Auction"
    import="beans.Database"
    import="java.sql.Timestamp"%>

<% List<Auction> activeAuctions = (List<Auction>) request.getAttribute("allActiveAuctions");
   List<Product> activeAuctionProducts = (List<Product>) request.getAttribute("allActiveAuctionProducts");
   Timestamp currentTime = (Timestamp) request.getAttribute("currentTime");
   int auctionIndex = Integer.parseInt(request.getParameter("id"));
   
   Auction auction = activeAuctions.get(auctionIndex);
   Product product = activeAuctionProducts.get(auctionIndex); 

   Long targetAuctionID = (Long)request.getAttribute("targetAuctionID");
   String bidSuccessMsg = (String)request.getAttribute("bidSuccessMsg");
   String bidErrorMsg = (String)request.getAttribute("bidErrorMsg"); %>

<div class="product">
	<h3 class="product-name"><%= product.name %></h3>
	<form action="auction-details" method="get">
	<input class="product-image" type="image" id="image" src='images?src=<%= product.imagePath %>'/>
	<input type="hidden" name="aid" value=<%= auction.id %> />
	</form>
	
	<!-- Auction details -->
	<form method=post>
		<h4><%= auction.hasBid() ? "High Bid:" : "Starting Price:" %></h4>
		<h5><%= auction.hasBid() ? auction.getHighBidText() : auction.startPrice %></h5>
		<h4>Time Remaining: </h4>
		<h5><%= auction.getTimeRemaining(currentTime) %></h5>
		
		<!-- Bid error/success messages -->
		<% if (targetAuctionID != null && auction.id == targetAuctionID) { %>
			<% if (bidErrorMsg != null) { %>
				<p style="color:red;"><%= bidErrorMsg %></p>
			<% } %>
			
			<% if (bidSuccessMsg != null) { %>
				<p style="color:green;"><%= bidSuccessMsg %></p>
			<% } %>
			
		<% } %>
		
		
		<input type="number" placeholder="Enter your bid" name="newestBid"/>
		<input type=hidden name="id" value=<%= auction.id %>><input type = "submit" name="action" value="bid"/>
	
	</form>
	
</div>