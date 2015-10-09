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
						<li><a href="home.jsp">Bootstrap <span class="sr-only">(current)</span></a></li>
						<li class="active"><a href="delete-location.jsp">Delete Location data</a></li>

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

					<form action="../DeleteLocationAction" method="post" enctype="multipart/form-data">
						<br/>

						<div class="form-group">
							<label for="macadd">MAC Address</label>
							<input type="text" class="form-control" id="macadd" name="macadd" placeholder="mac address" required>
						</div>
						<div class="form-group">
							<label for="startDate">Start Date</label>
							<input type="date" class="form-control" id="startDate" name="startdate" required>
						</div>
						<div class="form-group">
							<label for="endDate">End Date</label>
							<input type="date" class="form-control" id="endDate" name="enddate" required>
						</div>

						<input type="submit" class="btn btn-default" value="Delete location data">



					</form>
				</div>
				<div class="theme-div theme-content" style="width:60%">

					<h1>Result</h1>
					You have not chosen anything to delete.
				</div>

			</div>
		</div>
		<!--END OF CONTENT-->
		<script src="../js/jquery-2.1.4.min.js"></script>
		<script src="../js/bootstrap.js"></script>
    </body>
</html>

<!--macAdd = Utility.parseString(macAdd);
            if (macAdd == null) {
                unsuccessful.add("mac add cannot be blank");
                err = true;
            }
            if (!Utility.checkHexadecimal(macAdd)) {
                unsuccessful.add("invalid mac address");
                err = true;
            }-->