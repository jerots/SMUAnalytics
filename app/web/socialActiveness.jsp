<%-- 
    Document   : top-kreport
    Created on : 17-Oct-2015, 00:06:45
    Author     : Boyofthefuture
--%>

<%@page import="entity.Breakdown"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="dao.Utility"%>
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
            String macAdd = "value='" + user.getMacAddress() + "'";
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
                                <li><a href="student">Usage Time</a></li>
                                <li><a href="basicapp-appcat.jsp">App Category</a></li>
                                <li><a href="basicapp-diurnal.jsp">Diurnal Pattern</a></li>
                            </ul>
                        </li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Top-K App Usage <span class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li><a href="top-kreport.jsp">Most used apps</a></li>
                                <li><a href="top-kstudent.jsp">Students with most app usage</a></li>
                                <li><a href="top-kschool.jsp">Schools with most app usage</a></li>
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
        <%
            String date = request.getParameter("date");

            String dateInput = "";
            if (date != null) {
                dateInput = "value='" + date + "'";
            }
        %>


        <div class="theme-container container">
            <div class="row">
                <div class="theme-div" style="width:37%">
                    <form action="SocialActivenessAction" method="POST">
                        <div class="form-group"></div>
                        <div class="form-group">
                            <label for="startdate">Date</label>
                            <input type="date" class="form-control" id="date" name="date" <%=dateInput%> required>
                        </div>
                        <input type="hidden" <%=macAdd%> name="macadd">
                        <input type="submit" class="btn btn-default" value="Generate">
                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">
                    <h4><b> Social Activeness Report</b> </h4> <hr>
                    <%
                        HashMap<String, Breakdown> resultsMap = (HashMap<String, Breakdown>) request.getAttribute("results");
                        String errors = (String) request.getAttribute("errors");
                        if (errors != null && errors.length() > 0) {
                            out.println("<h1 style='color:red'>Error!</h1>");
                            out.println("<h3 style='color:red'>" + errors + "</h3>");
                        } else if (resultsMap != null) { //Checks this because in case of category refresh.
                            String msg = resultsMap.get("total-social-app-usage-duration").getMessage();
                            int num = Utility.parseInt(msg);
                            out.println("<h1>Result</h1><br>");
                            //This prints the type of table
                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'>");
                            //Note that the value will never get larger than 0 so it is fine to keep iterating without adding.
                            out.println("<td colspan='2'><b>App Breakdown of Social Activeness</b></td></tr><tr><td>Total Usage Time (for Social apps)</td>");
                            out.println("<td>" + num + "</td></tr></table><br><br>");
                            if (num != 0) {
                                out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'><b>Individual Social App Usage</b></td></tr>");
                                ArrayList<HashMap<String, Breakdown>> arrList = resultsMap.get("individual-social-app-usage").getBreakdown();
                                for (HashMap<String, Breakdown> appItems : arrList) {
                                    out.println("<tr><td>" + appItems.get("app-name").getMessage() + "</td>");
                                    out.println("<td>" + appItems.get("percent").getMessage() + "%</td></tr>");
                                }
                                out.println("</table><br>");
                            }
                            out.println("<br><table border=1px class='table table-striped'><tr style='background-color:lightsalmon'><td colspan='2'><b>");
                            out.println("Physical Breakdown for Social Activeness</b></td></tr><tr><td>");
                            out.println("Total Time Spent in SIS</td><td>" + resultsMap.get("total-time-spent-in-sis").getMessage() + "</td></tr>");
                            out.println("<tr><td> Group Percent </td><td>" + resultsMap.get("group-percent").getMessage() + "%</td></tr>");
                            out.println("<tr><td> Solo Percent </td><td>" + resultsMap.get("solo-percent").getMessage() + "%</td></tr></table>");
                        } else {
                            out.println("<h1>Result</h1>");
                            out.println("You have not entered any input.");
                        }

                    %>
                    <p>
                    <P><p>
                </div>

            </div>
        </div>
        <!--END OF CONTENT-->

        <%@include file="import-js.jsp" %>
    </body>
</html>
