<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Create Auction - FleaBay</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
	</head>
	<body>
	
		<!-- Navbar -->
		<jsp:include page="navbar-logged-in.jsp">  
			<jsp:param name="active" value="home" />  
		</jsp:include>  
	
		
		
		<h1>Create An Auction</h1>
		
		<!-- Product Icon -->
		<jsp:include page="product-icon.jsp">  
			<jsp:param name="id" value='<%= request.getParameter("id") %>' /> 	 
		</jsp:include> 
		
		<!-- TODO add product image here -->
		<form method="post">
			<label>Starting Price (credits):</label><input type="number" name="price"  size=20 maxlength=60 required><br>
			<label>Auction Duration (minutes):</label><input type="number" name="duration" size=20 maxlength=60 required><br>
			<input type="hidden" name="product" value=<%= request.getParameter("id") %>>
			<input type="submit" value="Start Auction" id="makeAuction">
		</form>
		
		
	</body>
</html>