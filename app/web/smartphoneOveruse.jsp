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
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Top-K App Usage <span class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li><a href="topkapp.jsp">Top-k most used apps (given a school)</a></li>
                                <li><a href="topkstudent.jsp">Top-k students with most app usage (give an app category)</a></li>
                                <li><a href="topkschool.jsp">Top-k schools with most app usage (give an app category) </a></li>
                            </ul>
                        </li>
                        <li class="active"><a href="smartphoneOveruse.jsp">Smartphone Overuse</a></li>
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
        <%
            String time = (String) request.getAttribute("time");
            String date = (String) request.getAttribute("date");
            String dateCode = "";
            if (date != null) {
                dateCode = "value='" + date + "'";
            }
            String timeCode = "";
            if (time != null) {
                timeCode = "value='" + time + "'";
            }
        %>


        <div class="theme-container container">
            <div class="row">
                <div class="theme-div" style="width:37%">
                    <form action="SmartphoneOveruse" method="GET">
                        <div class="form-group">
                            <label for="date">Start Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="date" name="startDate" <%=dateCode%> required>
                        </div>
                        <div class="form-group">
                            <label for="date">End Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="date" name="endDate" <%=dateCode%> required>
                        </div>
                        

                        <input type="submit" class="btn btn-default" value="Generate">
                    </form>
                </div>

                <div class="theme-div theme-content" style="width:60%">
                    <%

                        TreeMap<String, String> result = (TreeMap<String, String>) request.getAttribute("result");

                        if (result != null) {
                            out.print("Overuse results: ");

                            out.print(result.get("overuse-index") + "<br>");

                            out.print("Usage results: ");
                            out.print(result.get("usage-category") + "<br>");
							out.print(result.get("usage-duration") + "<br>");

                            out.print("Gaming Usage results: ");
                            out.print(result.get("gaming-category") + "<br>");
							out.print(result.get("gaming-duration") + "<br>");

                            out.print("Access Frequency results: ");
                            out.print(result.get("accessfrequency-category") + "<br>");
							out.print(result.get("accessfrequency") + "<br>");

                        }


                    %>

                </div>

            </div>

        </div>
        <!--END OF CONTENT-->

        <%@include file="import-js.jsp" %>
    </body>
</html>
