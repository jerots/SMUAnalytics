<%-- 
    Document   : home
    Created on : Sep 30, 2015, 1:12:53 PM
    Author     : jeremyongts92
--%>

<%@page import="entity.Admin"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/protect-admin.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
		<link rel="stylesheet" type="text/css" href="../css/bootstrap.css">
		<link rel="stylesheet" type="text/css" href="../css/style.css">
    </head>
    <body>
		<%			Admin admin = (Admin) session.getAttribute("admin");
		%>	
		<!--NAVBAR-->
		<nav class="navbar-static-top navbar-default">
			<div class="container-fluid">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<!--<a class="navbar-brand" href="#"><%=admin.getUsername() + " (admin)"%></a>-->
				</div>

				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

					<ul class="nav navbar-nav">
						<li><a href="#">Function</a></li>
						<li class="active"><a href="#">Bootstrap <span class="sr-only">(current)</span></a></li>
						<li><a href="#">Create</a></li>
						<li class="dropdown">
							<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Reports <span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="#">Action</a></li>
								<li><a href="#">Another action</a></li>
								<li><a href="#">Something else here</a></li>
								<li role="separator" class="divider"></li>
								<li><a href="#">Separated link</a></li>
								<li role="separator" class="divider"></li>
								<li><a href="#">One more separated link</a></li>
							</ul>
						</li>
					</ul>

					<ul class="nav navbar-nav navbar-right">
						<li><a>Welcome, <%=admin.getUsername() + " (admin)"%></a></li>
						<li><a href="../logout.jsp">Logout</a></li>
					</ul>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>
		<!--END OF NAV BAR-->


		<!--START OF CONTENT-->
		<div class="theme-container container">
			<div class="row">
				<div class="theme-div" style="width:37%">

					<form action="../BootstrapAction" method="post" enctype="multipart/form-data">
						Choose File to upload:<br/>
						<div class="form-group">
							<label for="exampleInputFile">File input</label>
							<input type="file" name="zipFile">
						</div>
						<input type="radio" value="bootstrap" name="option" checked> Bootstrap<br>
						<input type="radio" value="add-data" name="option"> Add additional data<br><br>
						<input type="submit" class="btn btn-default" value="Upload">



					</form>
				</div>
				<div class="theme-div theme-content" style="width:60%">

					<h1>Result</h1>
					You have not uploaded any files.
				</div>

			</div>
		</div>
		<!--END OF CONTENT-->
		<script src="../js/jquery-2.1.4.min.js"></script>
		<script src="../js/bootstrap.js"></script>
    </body>
</html>
