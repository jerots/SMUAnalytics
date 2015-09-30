<%
	session.removeAttribute("user");
	session.removeAttribute("admin");
	
	response.sendRedirect("index.jsp");

%>