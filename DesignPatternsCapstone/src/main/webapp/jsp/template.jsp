<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>

<!-- Import java classes -->
<%@ page import="java.util.List"
		 import="beans.Database" 
		 import="beans.User" %>
		 
<!-- Initialize data -->
<% Database db = (Database)request.getAttribute("db");
   User user = db.getUser((Long)request.getSession().getAttribute("user")); %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Your Page Title</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
	</head>
	<body>
	
		<!-- Navbar -->
		<jsp:include page="navbar-logged-in.jsp">  
			<jsp:param name="active" value="login" />  
		</jsp:include>  
		
		<!-- Your Code Here -->
		
	</body>
</html>

