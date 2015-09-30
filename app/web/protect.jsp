<%
	if (session.getAttribute("admin") == null && session.getAttribute("user") == null){
		response.sendRedirect("../index.jsp");
	}
%>