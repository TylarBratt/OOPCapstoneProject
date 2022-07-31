<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Insert title here</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
	</head>
	<body>
	
		<!-- Include Navbar -->
		<jsp:include page="LoggedInNavbar.jsp">  
			<jsp:param name="active" value="home" />  
		</jsp:include>  
	
		<p><%= request.getParameter("msg") %></p>
	</body>
</html>