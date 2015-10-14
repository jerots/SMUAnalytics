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
        <link rel="stylesheet" type="text/css" href="/app/css/bootstrap.css">
        <link rel="stylesheet" type="text/css" href="/app/css/style.css">
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
                        <li><a href="home.jsp">Basic App Usage</a></li>
                        <li><a href="#">Top-K App Usage <span class="sr-only">(current)</span></a></li>
                        <li><a href="#">Smartphone Overuse</a></li>
                        <li class="active"><a href="#">Smartphone Usage Heatmap</a></li>
                        <li><a href="#">Social Activeness</a></li>

                    </ul>

                    <ul class="nav navbar-nav navbar-right">
                        <li><a>Welcome, <%=user.getName() + " (student)"%></a></li>
                        <li><a href="/app/logout.jsp">Logout</a></li>
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
                    <form action="/app/SmartphoneOveruse" method="GET">
                        <div class="form-group">
                            <label for="date">Start Date</label>
                            <input type="date" class="form-control" id="date" name="startDate" <%=dateCode%> required>
                        </div>
                        <div class="form-group">
                            <label for="date">Start Time</label>
                            <input type="text" class="form-control" id="time" name="startTime" <%=timeCode%> placeholder="HH:MM:SS (24h)" required>
                        </div>
                        <div class="form-group">
                            <label for="date">End Date</label>
                            <input type="date" class="form-control" id="date" name="endDate" <%=dateCode%> required>
                        </div>
                        <div class="form-group">
                            <label for="date">End Time</label>
                            <input type="text" class="form-control" id="time" name="endTime" <%=timeCode%> placeholder="HH:MM:SS (24h)" required>
                        </div>

                        <input type="submit" class="btn btn-default" value="Generate">
                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">
                    <%

                        HashMap<String, String> result = (HashMap<String, String>) request.getAttribute("result");
                        
                        if (result != null) {
                            out.print("results: ");

                            out.print(result.get("overuseindex"));

                            out.print("results: ");
                            out.print(result.get("usage"));

                            out.print("results: ");
                            out.print(result.get("gaming"));

                        }
                    %>

                </div>

            </div>
        </div>
        <!--END OF CONTENT-->

        <script src="/app/js/jquery-2.1.4.min.js"></script>
        <script src="/app/js/bootstrap.js"></script>
    </body>
</html>