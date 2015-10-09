<%-- 
    Document   : home
    Created on : Sep 30, 2015, 1:12:53 PM
    Author     : jeremyongts92
--%>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="entity.Admin"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="protect-admin.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="/app/css/bootstrap.css">
        <link rel="stylesheet" type="text/css" href="/app/css/style.css">
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
                        <li class="active"><a href="/app/admin/home.jsp">Bootstrap <span class="sr-only">(current)</span></a></li>
                        <li><a href="/app/admin/delete-location.jsp">Delete Location data</a></li>

                    </ul>

                    <ul class="nav navbar-nav navbar-right">
                        <li><a>Welcome, <%=admin.getUsername() + " (admin)"%></a></li>
                        <li><a href="/app/logout.jsp">Logout</a></li>
                    </ul>
                </div><!-- /.navbar-collapse -->
            </div><!-- /.container-fluid -->
        </nav>
        <!--END OF NAV BAR-->


        <!--START OF CONTENT-->
        <div class="theme-container container">
            <div class="row">
                <div class="theme-div" style="width:37%">

                    <form action="/app/BootstrapAction" method="post" enctype="multipart/form-data">
                        Choose File to upload:<br/>
                        <div class="form-group">
                            <label for="exampleInputFile">File input</label>
                            <input type="file" name="zipFile" accept="application/zip" required>
                        </div>
                        <input type="radio" value="bootstrap" name="option" checked> Bootstrap<br>
                        <input type="radio" value="add-data" name="option"> Add additional data<br><br>
                        <input type="submit" class="btn btn-default" value="Upload">



                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">

                    <h1>Result</h1>


                    <%

                        HashMap<String, Integer> recordMap = (HashMap<String, Integer>) request.getAttribute("recordMap");
                        if (recordMap != null) {
                    %>
                    <%---app---%>
                    <h4><b> App</b> </h4>
                    <hr>
                    <%
                        out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                        out.println("<b>App</b>" + "</td></tr><tr><td>");
                        out.println("<b>Apps updated </b> " + "</td><td>" + recordMap.get("app-lookup.csv"));
                        out.println("</td></tr><tr><td>");
                        HashMap<Integer, String> appErrMap = (HashMap<Integer, String>) request.getAttribute("appErrMap");
                        Iterator<Integer> appiter = appErrMap.keySet().iterator();

                        out.println("<b>Number of rows with error </b> " + "</td><td>" + appErrMap.size() + "</td></tr>");
                        if (appErrMap != null && appErrMap.size() != 0) {
                            out.println("<tr><td><b>" + "Row");
                            out.println("</b></td><td><b>" + "Error Messages");
                            out.println("</b></td></tr>");
                            while (appiter.hasNext()) {
                                out.println("<tr><td>");
                                int rowWithErr = appiter.next();
                                String errAtThatRow = appErrMap.get(rowWithErr);
                                out.println(rowWithErr);
                                out.println("</td><td>");
                                out.println(errAtThatRow);
                                out.println("</td></tr>");
                            }
                        }
                        out.println("</table>");
                    %>

                    <p>
                    <P><p>
                        <%---user---%>
                    <h4><b> User</b> </h4>
                    <hr>


                    <%
                        out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                        out.println("<b>User</b>" + "</td></tr><tr><td>");
                        out.println("<b>User updated </b> " + "</td><td>" + recordMap.get("demographics.csv"));
                        out.println("</td></tr><tr><td>");
                        HashMap<Integer, String> userErrMap = (HashMap<Integer, String>) request.getAttribute("userErrMap");
                        Iterator<Integer> useriter = userErrMap.keySet().iterator();

                        out.println("<b>Number of rows with error </b> " + "</td><td>" + userErrMap.size() + "</td></tr>");
                        if (userErrMap != null && userErrMap.size() != 0) {
                            out.println("<tr><td><b>" + "Row");
                            out.println("</b></td><td><b>" + "Error Messages");
                            out.println("</b></td></tr>");
                            while (useriter.hasNext()) {
                                out.println("<tr><td>");
                                int rowWithErr = useriter.next();
                                String errAtThatRow = userErrMap.get(rowWithErr);
                                out.println(rowWithErr);
                                out.println("</td><td>");
                                out.println(errAtThatRow);
                                out.println("</td></tr>");
                            }
                        }
                        out.println("</table>");
                    %>

                    <P>
                        <%---location---%>
                    <h4><b> Location</b> </h4>
                    <hr>
                    <%
                        out.println("<table border=1px  class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                        out.println("<b>Location</b>" + "</td></tr><tr><td>");
                        out.println("<b>Location updated </b> " + "</td><td>" + recordMap.get("location-lookup.csv"));
                        out.println("</td></tr><tr><td>");
                        HashMap<Integer, String> locErrMap = (HashMap<Integer, String>) request.getAttribute("locErrMap");
                        Iterator<Integer> lociter = locErrMap.keySet().iterator();

                        out.println("<b>Number of rows with error </b> " + "</td><td>" + locErrMap.size() + "</td></tr>");
                        if (locErrMap != null && locErrMap.size() != 0) {
                            out.println("<tr><td><b>" + "Row");
                            out.println("</b></td><td><b>" + "Error Messages");
                            out.println("</b></td></tr>");
                            while (lociter.hasNext()) {
                                out.println("<tr><td>");
                                int rowWithErr = lociter.next();
                                String errAtThatRow = locErrMap.get(rowWithErr);
                                out.println(rowWithErr);
                                out.println("</td><td>");
                                out.println(errAtThatRow);
                                out.println("</td></tr>");
                            }
                        }
                        out.println("</table>");
                    %>
                    <P>
                        <%---appUsage---%>
                    <h4><b> AppUsage</b> </h4>
                    <hr>
                    <%
                        out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                        out.println("<b>AppUsage</b>" + "</td></tr><tr><td>");
                        out.println("<b>AppUsage updated </b> " + "</td><td>" + recordMap.get("app.csv"));
                        out.println("</td></tr><tr><td>");
                        HashMap<Integer, String> auErrMap = (HashMap<Integer, String>) request.getAttribute("auErrMap");
                        Iterator<Integer> auiter = auErrMap.keySet().iterator();

                        out.println("<b>Number of rows with error </b> " + "</td><td>" + auErrMap.size() + "</td></tr>");
                        if (auErrMap != null && auErrMap.size() != 0) {
                            out.println("<tr><td><b>" + "Row");
                            out.println("</b></td><td><b>" + "Error Messages");
                            out.println("</b></td></tr>");
                            while (auiter.hasNext()) {
                                out.println("<tr><td>");
                                int rowWithErr = auiter.next();
                                String errAtThatRow = auErrMap.get(rowWithErr);
                                out.println(rowWithErr);
                                out.println("</td><td>");
                                out.println(errAtThatRow);
                                out.println("</td></tr>");
                            }
                        }
                        out.println("</table>");
                    %>
                    <P>
                        <%---locationUsage---%>
                    <h4><b> LocationUsage</b> </h4>
                    <hr>
                    <%
                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                            out.println("<b>LocationUsage</b>" + "</td></tr><tr><td>");
                            out.println("<b>LocationUsage updated </b> " + "</td><td>" + recordMap.get("location.csv"));
                            out.println("</td></tr><tr><td>");
                            HashMap<Integer, String> luErrMap = (HashMap<Integer, String>) request.getAttribute("luErrMap");
                            Iterator<Integer> luiter = luErrMap.keySet().iterator();

                            out.println("<b>Number of rows with error </b> " + "</td><td>" + luErrMap.size() + "</td></tr>");
                            if (luErrMap != null && luErrMap.size() != 0) {
                                out.println("<tr><td><b>" + "Row");
                                out.println("</b></td><td><b>" + "Error Messages");
                                out.println("</b></td></tr>");
                                while (luiter.hasNext()) {
                                    out.println("<tr><td>");
                                    int rowWithErr = luiter.next();
                                    String errAtThatRow = luErrMap.get(rowWithErr);
                                    out.println(rowWithErr);
                                    out.println("</td><td>");
                                    out.println(errAtThatRow);
                                    out.println("</td></tr>");
                                }
                            }
                            out.println("</table>");

                    %>
                    <P>
                        <%---locationUsageDelete---%>
                    <h4><b> LocationUsageDelete</b> </h4>
                    <hr>
                    <%
                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                            out.println("<b>LocationUsage</b>" + "</td></tr><tr><td>");
                            out.println("<b>LocationUsage updated </b> " + "</td><td>" + recordMap.get("location-delete.csv"));
                            out.println("</td></tr><tr><td>");
                            HashMap<Integer, String> delErrMap = (HashMap<Integer, String>) request.getAttribute("delErrMap");
                            Iterator<Integer> deliter = delErrMap.keySet().iterator();

                            out.println("<b>Number of rows with error </b> " + "</td><td>" + delErrMap.size() + "</td></tr>");
                            if (delErrMap != null && delErrMap.size() != 0) {
                                out.println("<tr><td><b>" + "Row");
                                out.println("</b></td><td><b>" + "Error Messages");
                                out.println("</b></td></tr>");
                                while (deliter.hasNext()) {
                                    out.println("<tr><td>");
                                    int rowWithErr = deliter.next();
                                    String errAtThatRow = delErrMap.get(rowWithErr);
                                    out.println(rowWithErr);
                                    out.println("</td><td>");
                                    out.println(errAtThatRow);
                                    out.println("</td></tr>");
                                }
                            }
                            out.println("</table>");

                        } else {
                            out.println("You have not uploaded any files.");
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
