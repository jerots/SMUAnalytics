<%
	if (session.getAttribute("user") == null){
		response.sendRedirect("../index.jsp");
		return;
	}
%>