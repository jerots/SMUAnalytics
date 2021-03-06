<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.TreeMap"%>
<%@page import="entity.Admin"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="protect-admin.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <%@include file="import-css.jsp" %>
    </head>
    <body>
        <%            
            Admin admin = (Admin) session.getAttribute("admin");
            String option = (String) request.getAttribute("option");
            String printer = (String) request.getAttribute("printer");
            String bootstrap = "";
            String addBatch = "";
            if (option != null) {
                if (option.equals("bootstrap")) {
                    bootstrap = "checked";
                } else {
                    addBatch = "checked";
                }
            } else {
                bootstrap = "checked";
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
                    <!--<a class="navbar-brand" href="#"><%=admin.getUsername() + " (admin)"%></a>-->
                </div>

                <!-- Collect the nav links, forms, and other content for toggling -->
                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

                    <ul class="nav navbar-nav">
                        <li class="active"><a href="admin">Bootstrap <span class="sr-only">(current)</span></a></li>
                        <li><a href="delete-location.jsp">Delete Location data</a></li>

                    </ul>

                    <ul class="nav navbar-nav navbar-right">
                        <li><a>Welcome, <%=admin.getUsername() + " (admin)"%></a></li>
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

                    <form action="BootstrapAction" method="post" enctype="multipart/form-data">
                        Choose File to upload:<br/>
                        <div class="form-group">
                            <label for="exampleInputFile">File input</label>
                            <input type="file" name="zipFile" accept="application/zip" required>
                        </div>
                        <input type="radio" value="bootstrap" name="option" <%=bootstrap%>> Bootstrap<br>
                        <input type="radio" value="add-data" name="option" <%=addBatch%>> Add additional data<br><br>
                        <input type="submit" class="btn btn-default" value="Upload">



                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">

                    <h1>Result</h1>


                    <%
                        String timeTaken = (String) request.getAttribute("timeTaken");
                        if (timeTaken != null && option.equals("bootstrap")) {
                            out.println("Bootstrap time taken: " + timeTaken + " seconds");
                        } else if (timeTaken != null) {
                            out.println("Additional Files time taken: " + timeTaken + " seconds");
                        }
                        TreeMap<String, Integer> recordMap = (TreeMap<String, Integer>) request.getAttribute("recordMap");
                        HashMap<String, Integer> delMap = (HashMap<String, Integer>) request.getAttribute("delMap");
                        if (recordMap != null) {
                            int appCount = recordMap.get("app-lookup.csv");
                            if (appCount >= 0) {
                    %>
                    <%---app---%>
                    <h4><b> app-lookup.csv</b> </h4>
                    <hr>
                    <%
                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                            out.println("<b>App</b>" + "</td></tr><tr><td>");
                            out.println("<b>Apps updated </b> " + "</td><td>" + appCount);
                            out.println("</td></tr><tr><td>");
                            TreeMap<Integer, String> appErrMap = (TreeMap<Integer, String>) request.getAttribute("appErrMap");
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
                        }
                        int demoCount = recordMap.get("demographics.csv");
                        if (demoCount >= 0) {
                    %>

                    <p>
                    <P><p>
                        <%---user---%>
                    <h4><b> demographics.csv</b> </h4>
                    <hr>


                    <%
                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                            out.println("<b>User</b>" + "</td></tr><tr><td>");
                            out.println("<b>User updated </b> " + "</td><td>" + demoCount);
                            out.println("</td></tr><tr><td>");
                            TreeMap<Integer, String> userErrMap = (TreeMap<Integer, String>) request.getAttribute("userErrMap");
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
                        }

                    %>

                    <P>
                        <%---location---%>
                        <%int locationCount = recordMap.get("location-lookup.csv");
                            if (locationCount >= 0) { %>    
                    <h4><b> location-lookup.csv</b> </h4>
                    <hr>
                    <%
                            out.println("<table border=1px  class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                            out.println("<b>Location</b>" + "</td></tr><tr><td>");
                            out.println("<b>Location updated </b> " + "</td><td>" + locationCount);
                            out.println("</td></tr><tr><td>");
                            TreeMap<Integer, String> locErrMap = (TreeMap<Integer, String>) request.getAttribute("locErrMap");
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
                        }

                        int appUsageCount = recordMap.get("app.csv");
                        if (appUsageCount >= 0) {
                    %>
                    <P>
                        <%---appUsage---%>
                    <h4><b> app.csv</b> </h4>
                    <hr>
                    <%
                            TreeMap<Integer, String> auErrMap = (TreeMap<Integer, String>) request.getAttribute("auErrMap");
                            Iterator<Integer> auiter = auErrMap.keySet().iterator();
                            int auErrorSize = auErrMap.size();

                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                            out.println("<b>AppUsage</b>" + "</td></tr><tr><td>");
                            out.println("<b>AppUsage updated </b> " + "</td><td>" + appUsageCount);
                            out.println("</td></tr><tr><td>");

                            out.println("<b>Number of rows with error </b> " + "</td><td>" + auErrorSize + "</td></tr>");
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
                        }

                    %>
                    <P>
                        <%---locationUsage---%>


                        <%  int locationUsageCount = recordMap.get("location.csv");
                            if (locationUsageCount >= 0) { %>
                    <h4><b> location.csv</b> </h4><hr>
                    <%
                                out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'>");
                                out.println("<b>LocationUsage</b>" + "</td></tr><tr><td>");
                                out.println("<b>LocationUsage updated </b> " + "</td><td>" + locationUsageCount);
                                out.println("</td></tr><tr><td>");
                                TreeMap<Integer, String> luErrMap = (TreeMap<Integer, String>) request.getAttribute("luErrMap");
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
                            }
                        }
                    %>
                    <P>
                        <%---locationUsageDelete---%>

                        <% if (delMap != null) {
                                int locationDeleteCount = delMap.get("location-delete.csv");
                                if (locationDeleteCount >= 0) {
                                    out.println("<h4><b> location-delete.csv</b> </h4><hr>");
                                    if (delMap.get("location-delete.csv") >= 0) {
                                        out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'><b>Location Delete</b></td></tr><tr><td>");
                                        out.println("<b>num-record-deleted</b></td><td>" + delMap.get("location-delete.csv") + "</td></tr>");
                                        out.println("<tr><td><b>num-record-not-found</b></td><td>" + delMap.get("deletenotfound") + "</td></tr></table>");

                                    }
                                }
                            }
                            if (recordMap == null && delMap == null && printer != null) {
                                out.println("<br><br><h4><b>You have not uploaded any files.</b></h4>");
                            }
                        %>
                </div>

            </div>
        </div>
        <!--END OF CONTENT-->
        <%@include file="import-js.jsp" %>
    </body>
</html>
