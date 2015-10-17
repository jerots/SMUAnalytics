<%-- 
    Document   : home
    Created on : Sep 30, 2015, 1:22:22 PM
    Author     : jeremyongts92
--%>

<%@page import="java.util.TreeMap"%>
<%@page import="java.util.TreeSet"%>
<%@page import="dao.LocationDAO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="entity.LocationUsage"%>
<%@page import="java.util.ArrayList"%>
<%@page import="entity.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="protect-user.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
		<%@include file="import-css.jsp" %>
    </head>
    <body>
		<%			User user = (User) session.getAttribute("user");

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
					<!--<a class="navbar-brand" href="#"><%=user.getName() + " (student)"%></a>-->
				</div>

				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

					<ul class="nav navbar-nav">
						<li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Basic App Usage <span class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li><a href="student">Basic App (with demographic)</a></li>
                                <li><a href="basicapp-appcat.jsp">App category</a></li>
                                <li><a href="basicapp-diurnal.jsp">Diurnal Report</a></li>
                            </ul>
                        </li>
						<li><a href="#">Top-K App Usage <span class="sr-only">(current)</span></a></li>
						<li><a href="smartphoneOveruse.jsp">Smartphone Overuse</a></li>
						<li class="active"><a href="#">Smartphone Usage Heatmap</a></li>
						<li><a href="#">Social Activeness</a></li>

					</ul>

					<ul class="nav navbar-nav navbar-right">
						<li><a>Welcome, <%=user.getName() + " (student)"%></a></li>
						<li><a href="logout.jsp">Logout</a></li>
					</ul>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>
		<!--END OF NAV BAR-->
        <!--START OF CONTENT-->
		<%
			String floor = (String) request.getParameter("floor");
			String time = (String) request.getParameter("time");
			String date = (String) request.getParameter("date");
			String dateCode = "";
			if (date != null) {
				dateCode = "value='" + date + "'";
			}
			String timeCode = "";
			if (time != null) {
				timeCode = "value='" + time + "'";
			}

			//Scan all levels available
			LocationDAO locDAO = new LocationDAO();
			ArrayList<String> locationList = locDAO.retrieveAll();
			TreeSet<String> levelSet = new TreeSet<String>();
			//for each location in locationList
			System.out.println("LOCATIONLIST SIZE " + locationList.size());
			for (String location : locationList) {
				//Scan all basements
				String trimmed = location.replace("SMUSIS", "").toUpperCase();
				if (trimmed.length() > 1) {
					char type = trimmed.charAt(0);
					if (type == 'B') { //IF BASEMENT
						levelSet.add("B" + trimmed.charAt(1));
					} else if (type == 'L') { //IF LEVEL
						levelSet.add("L" + trimmed.charAt(1));
					}
				}

			}


		%>


		<div class="theme-container container">
			<div class="row">
				<div class="theme-div" style="width:37%">
					<form action="HeatmapAction" method="GET">
						<div class="form-group">
							<label for="date">Date</label>
							<input type="date" class="form-control" id="date" name="date" <%=dateCode%> required>
						</div>
						<div class="form-group">
							<label for="date">Time</label>
							<input type="text" class="form-control" id="time" name="time" <%=timeCode%> placeholder="HH:MM:SS (24h)" required>
						</div>
						<div class="form-group">
							<label for="startdate">Floor</label>
							<select class="form-control" name="floor">
								<%
									System.out.println("LEVEL SIZE " + levelSet.size());
									for (String level : levelSet) {
										String levelCode = "";
										if (level.equals(floor)) {
											levelCode = "selected";
										}
										out.println("<option value='" + level + "' " + levelCode + ">" + level + "</option>");
									}
								%>



								<!--<option value="B1">B1</option>-->
								<%
//									for (int i = 1; i <= 5; i++) {
//										out.println("<option value='L" + i + "'>L" + i + "</option>");
//									}
								%>
							</select>
						</div>

						<input type="submit" class="btn btn-default" value="Generate">
					</form>
				</div>
				<div class="theme-div theme-content" style="width:60%">
					<%						TreeMap<String, ArrayList<LocationUsage>> heatmap = (TreeMap<String, ArrayList<LocationUsage>>) request.getAttribute("heatmap");
						String error = (String) request.getAttribute("error");
						if (error != null) {

							out.println("<h1 style='color:red'>Error!</h1>");
							out.println("<h3 style='color:red'>" + error + "</h3>");

						} else if (heatmap == null) {
							out.println("<h1>Result</h1>");
							out.println("You have not uploaded any files.");

						} else {
							Iterator<String> iter = heatmap.keySet().iterator();
							out.println("<table class='table'>");
							out.println("<tr><th>Place</th><th>Density</th><th>Number of people using smartphone</th></tr>");
							while (iter.hasNext()) {
								String loc = iter.next();
								int numUsers = heatmap.get(loc).size();
								int density = 0;
								String desc = "0";
								if (numUsers == 0) {

								} else if (numUsers < 4) {
									density = 1;
									desc = "1 to 3";
								} else if (numUsers < 8) {
									density = 2;
									desc = "4 to 7";
								} else if (numUsers < 14) {
									density = 3;
									desc = "8 to 13";
								} else if (numUsers < 21) {
									density = 4;
									desc = "14 to 20";
								} else {
									density = 5;
									desc = "21 and more";
								}

								out.println("<tr>");
								out.println("<td>" + loc + "</td>");
								out.println("<td>" + density + "</td>");
								out.println("<td>" + desc + "</td>");

								out.println("</tr>");
							}

							out.println("</table>");

						}

					%>

				</div>

			</div>
		</div>
		<!--END OF CONTENT-->

		<%@include file="import-js.jsp" %>
    </body>
</html>
