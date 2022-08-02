<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<% String startingCredits = request.getServletContext().getInitParameter("startingCredits");
   String startingProductCount = request.getServletContext().getInitParameter("startingProductCount");
   String errorMsg = (String)request.getAttribute("err"); %>
   
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Signup - FleaBay</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
	</head>
	<body>
	
		<!-- Navbar -->
		<jsp:include page="navbar-logged-out.jsp">  
			<jsp:param name="active" value="signup" />  
		</jsp:include>  
		
		<!-- Your Code Here -->
		<h1>Create an Account</h1>
		<h3>It's easy and free!</h3>
		
		<p>New users will receive <%= startingCredits %> credits and <%= startingProductCount %> free products upon sign-up.</p>
		
		<form method="post">
			<label>Username:</label> <br><input type="text" name="username" id="title" size=20 maxlength=60 required><br>
			<label>Password:</label><br><input type="password" name="password" id="title" size=20 maxlength=60 required><br><br>
			<input type="submit" value="Create Account" id="signup">
		</form>
		
		<p style="color:red;"><%= errorMsg != null ? errorMsg : "" %></p>
	</body>
</html>

