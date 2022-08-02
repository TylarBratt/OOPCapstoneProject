<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
	import="java.util.List"
    import="beans.Auction"
    import="beans.Database"
    import="beans.User"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Login - FleaBay</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
	</head>
	<body>
	
		<!-- Navbar -->
		<jsp:include page="navbar-logged-out.jsp">  
			<jsp:param name="active" value="login" />  
		</jsp:include>  
		
		<h1>Welcome to FleaBay</h1>
		<h3>Better than eBay - it's FleaBay!</h3>
		
		<% String errorMsg = (String)request.getAttribute("err"); %>
		<form method="post">
			<label>Username:</label><br>
			<input type="text" name="username" id="title" size=20 maxlength=60><br>
			<label>Password:</label><br>
			<input type="password" name="password" id="title" size=20 maxlength=60><br>
			<input type="hidden" name="action" value="login">
			<!-- Error Message -->
			<p style="color:red;"><%= errorMsg != null ? errorMsg : "" %></p>
			<input type="submit" value="Login" id="login">
			
		</form>
		
	</body>
</html>

