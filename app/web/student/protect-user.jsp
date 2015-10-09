<%
	if (session.getAttribute("user") == null){
		response.sendRedirect("/app/index.jsp");
		return;
	}
%>