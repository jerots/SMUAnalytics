<%-- 
    Document   : index
    Created on : Sep 30, 2015, 12:04:42 PM
    Author     : jeremyongts92
--%>

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
	<!--<center>-->
		<div id='login-div'>
			<h1>SMUA Login</h1>
			<form>

				<div class="form-group">
					<label for="username">Username</label>
					<input type="text" class="form-control" id="username" placeholder="Username" required>
				</div>
				<div class="form-group">
					<label for="password">Password</label>
					<input type="password" class="form-control" id="password" placeholder="Password" required>
				</div>
				<center>
				<input class='btn btn-default' type='submit'>
				</center>

			</form>
		</div>
	<!--</center>-->


	<script src="css/bootstrap.js"></script>
</body>
</html>
