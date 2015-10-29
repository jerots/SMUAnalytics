<%-- 
    Document   : home
    Created on : Sep 30, 2015, 1:22:22 PM
    Author     : jeremyongts92
--%>

<%@page import="dao.UserDAO"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="entity.Breakdown"%>
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
        <%            User user = (User) session.getAttribute("user");
			String startdate = request.getParameter("startdate");
			if (startdate == null) {
				startdate = "";
			}
			String enddate = request.getParameter("enddate");
			if (enddate == null) {
				enddate = "";
			}


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
                        <!--<li class="active"><a href="#">Basic App Usage</a></li>-->
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Top-K App Usage <span class="caret"></span></a>
                            <ul class="dropdown-menu">
								<li><a href="top-kreport.jsp">Top-k most used apps (given a school)</a></li>
								<li><a href="top-kstudent.jsp">Top-k students with most app usage (given an app category)</a></li>
								<li><a href="top-kschool.jsp">Top-k schools with most app usage (given an app category)</a></li>
							</ul>
                        </li>
                        <li><a href="smartphoneOveruse.jsp">Smartphone Overuse</a></li>
                        <li><a href="heatmap.jsp">Smartphone Usage Heatmap</a></li>
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
        <div class="theme-container container">
            <div class="row">
                <div class="theme-div" style="width:37%">
                    <form action="BasicAppDiurnalAction">
                        <div class="form-group">
                            <label for="date">Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="startdate" name="startdate" value="<%=startdate%>" required>
                        </div>
                        <div class="form-group">
                            <label for="year">Year</label>
                            <select name="yearfilter" class="form-control">
                                <option value="NA">- Year Filter -</option>
                                <%
									UserDAO userDAO = new UserDAO();
									String yearFilter = request.getParameter("yearfilter");
									if (yearFilter == null) {
										yearFilter = "";
									}
									ArrayList<String> years = userDAO.getYears();
									for (String year : years) {
										String code = "";
										if (year.equals(yearFilter)) {
											code = "selected";
										}
										out.println("<option value='" + year + "' " + code + ">" + year + "</option>");

									}

                                %>

                            </select>
                        </div>
                        <div class="form-group">
                            <label for="gender">Gender</label>
                            <select name="genderfilter" class="form-control">
                                <option value="NA">- Gender Filter -</option>
                                <%                                    String genderFilter = request.getParameter("genderfilter");
									if (genderFilter == null) {
										genderFilter = "";
									}
									ArrayList<String> genders = userDAO.getGenders();
									for (String gender : genders) {
										String code = "";
										if (gender.equals(genderFilter)) {
											code = "selected";
										}
										out.println("<option value='" + gender + "' " + code + " >" + gender + "</option>");

									}

                                %>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="school">School</label>
                            <select name="schoolfilter" class="form-control">
                                <option value="NA">- School Filter -</option>
                                <%                                    String schoolFilter = request.getParameter("schoolfilter");
									if (schoolFilter == null) {
										schoolFilter = "";
									}
									ArrayList<String> schools = userDAO.getSchools();
									for (String school : schools) {
										String code = "";
										if (school.equals(schoolFilter)) {
											code = "selected";
										}
										out.println("<option value='" + school + "' " + code + ">" + school + "</option>");

									}

                                %>
                            </select>
                        </div>



                        <input type="submit" class="btn btn-default" value="Generate">
                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">
                    <%	ArrayList<String> errors = (ArrayList<String>) request.getAttribute("errors");

						//IF THERE IS ERROR, PRINT
						if (errors != null) {
							out.println("<h1 class=errorMsg>Error!</h1>");
							out.println("<ul>");
							for (String error : errors) {
								out.println("<li>" + error + "</li>");
							}
							out.println("</ul>");
							return;
						}
						ArrayList<HashMap<String, Breakdown>> breakdownList = (ArrayList<HashMap<String, Breakdown>>) request.getAttribute("result");

						if (breakdownList != null) {
							if (breakdownList.size() <= 0) {
								out.println("<h1>Result</h1>");
								out.println("Invalid Date. Please select your desired date again.");
								return;
							}
							out.println("<table class='table'>");
							out.println("<tr><th>Timing (24 hours)</th><th>Duration</th></tr>");

							for (HashMap<String, Breakdown> map : breakdownList) {

								if (map.get("period") != null) {
									out.println("<tr><td>" + map.get("period").getMessage() + "</td>");
								}
								if (map.get("duration") != null) {
									out.println("<td>" + map.get("duration").getMessage() + "</td></tr>");
								}
							}
							out.println("</table>");
						} else {
							//NO REPORT AND NO ERROR
							out.println("<h1>Result</h1>");
							out.println("You have not entered any input.");
						}
                    %>
                </div>

            </div>
        </div>
        <!--END OF CONTENT-->

        <%@include file="import-js.jsp" %>
    </body>
</html>
