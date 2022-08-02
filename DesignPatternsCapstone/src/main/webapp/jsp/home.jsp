<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="java.util.List"
    import="beans.Auction"
    import="beans.Database"
    import="beans.User"%>

<!-- Initialize Data -->
<% Database db = (Database)request.getAttribute("db");
   User user = db.getUser((Long)request.getSession().getAttribute("user"));
   List<Auction> activeAuctions = db.getActiveAuctions(); %>
   
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Home - FleaBay</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
	</head>
	<body>
	
		<!-- Navbar -->
		<jsp:include page="navbar-logged-in.jsp">  
			<jsp:param name="active" value="home" />  
		</jsp:include>  
		
		<!-- Your Code Here -->
		<h2>Auctions</h2>
		
		<% if (activeAuctions.size() > 0) {
			for (Auction auction : activeAuctions) {%>
				<jsp:include page="auction.jsp">  
					<jsp:param name="id" value='<%= auction.id %>' />  
				</jsp:include>  
			<% }
		}
		else { %>
			<h4>No active auctions. <a href="account">Create one</a> to get the bidding started!</h4>
		<% } %>
		
	</body>
</html>

