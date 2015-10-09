<%
	if (session.getAttribute("admin") == null){
		response.sendRedirect("/app/index.jsp");
		return;
	}
%>