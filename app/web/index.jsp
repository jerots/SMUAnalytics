<%-- 
    Document   : index
    Created on : Sep 30, 2015, 12:04:42 PM
    Author     : jeremyongts92
--%>

<%@page import="java.sql.SQLException"%>
<%@page import="dao.InitDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
		<link rel="stylesheet" type="text/css" href="css/bootstrap.css">
		<link rel="stylesheet" type="text/css" href="css/index.css">
    </head>
    <body>
		<!--LOGIN BOX-->
		<div id='login-div'>
			<h1>SMUA Login</h1>
			<form method='POST' action='LoginAction'>
				<%

					//InitDAO.createTable();

					String username = (String) request.getAttribute("username");
					if (username == null) {
						username = "";
					}
					String error = (String) request.getAttribute("error");
					if (error != null) {
						out.println("<h4 class='errorMsg'>" + error + "</h4>");
					}
				%>
				<div class="form-group">
					<label for="username">Username</label>
					<input type="text" class="form-control" id="username" value='<%=username%>' name='username' placeholder="Username" required>
				</div>
				<div class="form-group">
					<label for="password">Password</label>
					<input type="password" class="form-control" name='password' id="password" placeholder="Password" required>
				</div>
				<center>
					<input class='btn btn-default' type='submit'>
				</center>

			</form>
		</div>
		<!--END OF LOGIN BOX-->

		<script src="js/jquery-2.1.4.min"></script>
		<script src="js/bootstrap.js"></script>
	</body>
</html>
