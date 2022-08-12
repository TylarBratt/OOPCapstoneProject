<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<%@ page isELIgnored="false" %>
<!-- Import java classes -->
<%@ page import="java.util.List"
	import="beans.Auction"
	import="beans.Product"
	import="beans.User"
	import="java.sql.Timestamp"
	import="org.apache.commons.lang3.time.DateUtils"
	import="java.sql.Date"
	import="beans.Bid"
	%>

<!-- Initialize Data -->
<%	Auction auction = (Auction)request.getAttribute("Auction");
	Product product = (Product) request.getAttribute("Product");
	String table = (String) request.getAttribute("Table");
	User auctioneer = (User) request.getAttribute("Owner");
	Timestamp endDate = auction.getEndDate();
	Timestamp now = new Timestamp(System.currentTimeMillis());
	List<Bid> bids = (List<Bid>) request.getAttribute("Bids");
	List<String> bidderNames = (List<String>) request.getAttribute("BidderNames");
%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Auction Details - FleaBay</title>
	<link rel="stylesheet"
		href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

	<!-- Navbar -->
	<jsp:include page="navbar-logged-in.jsp">
		<jsp:param name="active" value="login" />
	</jsp:include>

	<h2>Auction Details</h2>
	<jsp:include page="product-icon.jsp">
		<jsp:param name="active" value="login" />
		<jsp:param name="name" value="<%=product.name %>" />
		<jsp:param name="image" value="<%= product.imagePath%>" />
		<jsp:param name="extra" value="" />
	</jsp:include>
	
	<!-- List of bids -->
	<% if(bids.size() > 0) { %>
		<table>
			<!-- Table header -->
			<tr>
				<td>Amount</td>
				<td>Time</td>
				<td>User</td>
			</tr>
			
			<!-- Table rows -->
			<% for (int i = 0; i < bids.size(); i++) { %>
				<tr>
					<td> <%= bids.get(i).amount %> </td>
					<td> <%= bids.get(i).time %> </td>
					<td> <%= bidderNames.get(i) %> </td>
				</tr>
			<% } %>
		</table>
	<% } else { %>\
		<p>No Bids</p>
	<% } %>


	<c:out value="<%=table%>" escapeXml="false" />
	<h4>Starting price: </h4>
	<c:out value="<%=auction.startPrice %>" escapeXml="false" />
	<h4>Start Date: </h4>
	<c:out value="<%=auction.startDate %>" escapeXml="false" />
	<h4>End Date:  </h4>
	<c:out value="<%=auction.getEndDate() %>" escapeXml="false" />
	<h4>Time Remaining: </h4>
	<c:out value="<%=auction.getTimeRemaining(now) %>" escapeXml="false" />
	<h4>Auctioneer:  </h4>
	<c:out value="<%=auctioneer.userName %>" escapeXml="false" />

</body>
</html>

