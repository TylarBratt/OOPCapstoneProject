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
	%>

<!-- Initialize Data -->
<%	Auction auction = (Auction)request.getAttribute("Auction");
	Product product = (Product) request.getAttribute("Product");
	String table = (String) request.getAttribute("Table");
	User auctioneer = (User) request.getAttribute("Owner");
	Timestamp endDate = auction.getEndDate();
	Timestamp now = new Timestamp(System.currentTimeMillis());

%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>My Account - FleaBay</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

	<!-- Navbar -->
	<jsp:include page="navbar-logged-in.jsp">
		<jsp:param name="active" value="login" />
	</jsp:include>

	<!-- Your Code Here -->
<h2>Auction Details</h2>
<jsp:include page="product-icon.jsp">
		<jsp:param name="active" value="login" />
		<jsp:param name="name" value="<%=product.name %>" />
		<jsp:param name="image" value="<%= product.imagePath%>" />
		<jsp:param name="extra" value="" />
	</jsp:include>


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

