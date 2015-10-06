<%
	session.removeAttribute("user");
	session.removeAttribute("admin");
	
	response.sendRedirect("/app/index.jsp");

%>