<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>

<!-- Import java classes -->
<%@ page import="java.util.List" %>
		 
<!-- Initialize Data -->
<%  %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>My Account - FleaBay</title>
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

