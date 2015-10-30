<%-- 
    Document   : home
    Created on : Sep 30, 2015, 1:22:22 PM
    Author     : jeremyongts92
--%>

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
        <%			User user = (User) session.getAttribute("user");
            String startdate = request.getParameter("startdate");
            if (startdate == null) {
                startdate = "";
            }
            String enddate = request.getParameter("enddate");
            if (enddate == null) {
                enddate = "";
            }
            String filter1 = request.getParameter("filter1");
            if (filter1 == null) {
                filter1 = "";
            }
            String filter2 = request.getParameter("filter2");
            if (filter2 == null) {
                filter2 = "";
            }
            String filter3 = request.getParameter("filter3");
            if (filter3 == null) {
                filter3 = "";
            }

            if (filter1.equals("gender")) {
                String gender1 = "selected";
            }
            boolean school1 = filter1.equals("school");
            boolean year1 = filter1.equals("year");
            boolean gender2 = filter2.equals("gender");
            boolean school2 = filter2.equals("school");
            boolean year2 = filter2.equals("year");
            boolean gender3 = filter3.equals("gender");
            boolean school3 = filter3.equals("school");
            boolean year3 = filter3.equals("year");
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
                    <form action="BasicAppAction">
                        <div class="form-group">
                            <label for="date">Start Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="startdate" name="startdate" value="<%=startdate%>" required>
                        </div>
                        <div class="form-group">
                            <label for="date">End Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="enddate" name="enddate" value="<%=enddate%>" required>
                        </div>

                        <%
                            //GENERATE OPTIONS FOR DEMO
                            String[] types = {"gender", "school", "year"};
                            out.println("<div class='form-group'>");
                            out.println("<label for='filter1'>Filter by demographic</label>");
                            out.println("(if no demographic is selected, general report will be generated)");
                            out.println("<select name='filter1' class='form-control'>");
                            out.println("<option value=''>- Filter 1 -</option>");
                            for (String type : types) {
                                if (filter1.equals(type)) {
                                    out.println("<option value=" + type + " selected>" + type + "</option>");
                                } else {
                                    out.println("<option value=" + type + ">" + type + "</option>");
                                }
                            }
                            out.println("</select>");
                            out.println("</div>");

                            out.println("<div class='form-group'>");
                            out.println("<select name='filter2' class='form-control'>");
                            out.println("<option value=''>- Filter 2 -</option>");
                            for (String type : types) {
                                if (filter2.equals(type)) {
                                    out.println("<option value=" + type + " selected>" + type + "</option>");
                                } else {
                                    out.println("<option value=" + type + ">" + type + "</option>");
                                }
                            }
                            out.println("</select>");
                            out.println("</div>");

                            out.println("<div class='form-group'>");
                            out.println("<select name='filter3' class='form-control'>");
                            out.println("<option value=''>- Filter 3 -</option>");
                            for (String type : types) {
                                if (filter3.equals(type)) {
                                    out.println("<option value=" + type + " selected>" + type + "</option>");
                                } else {
                                    out.println("<option value=" + type + ">" + type + "</option>");
                                }
                            }
                            out.println("</select>");
                            out.println("</div>");


                        %>



                        <input type="submit" class="btn btn-default" value="Generate">
                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">
                    <h4><b> Basic App(with Demographic)</b> </h4> <hr>
                    <%						ArrayList<String> errors = (ArrayList<String>) request.getAttribute("errors");

                        //IF THERE IS ERROR, PRINT
                        if (errors != null) {
                            out.println("<h1 class=errorMsg>Error!</h1>");
                            out.println("<ul>");
                            for (String error : errors) {
                                out.println("<li>" + error + "</li>");
                            }
                            out.println("</ul>");

                        } else {

                            Breakdown breakdown = (Breakdown) request.getAttribute("result");

                            //IF NO ERROR, GENERATE OUTPUT
                            if (breakdown != null) {
                                //GENERATE REPORT WITHOUT DEMOGRAPHIC
                                if (request.getAttribute("withDemo") == null) {

                                    ArrayList<HashMap<String, Breakdown>> resultList = breakdown.getBreakdown();

                                    out.println("<table class='table'>");
                                    out.println("<tr><th>Intense Users</th><th>Normal Users</th><th>Mild Users</th></tr>");
                                    HashMap<String, Breakdown> intenseMap = resultList.get(0);
                                    HashMap<String, Breakdown> normalMap = resultList.get(1);
                                    HashMap<String, Breakdown> mildMap = resultList.get(2);
                                    out.println("<tr><td>Count: " + intenseMap.get("intense-count") + "</td><td>Count: " + normalMap.get("normal-count") + "</td><td>Count: " + mildMap.get("mild-count") + "</td></tr>");
                                    out.println("<tr><th>" + intenseMap.get("intense-percent") + "% of total</th><th>" + normalMap.get("normal-percent") + "% of total</th><th>" + mildMap.get("mild-percent") + "% of total</th></th>");
                                    out.println("</table>");

                                } else {
                                    //GENERATE REPORT BY DEMOGRAPHIC
                                    String[] demoArr = (String[]) request.getAttribute("demoArr");
                                    int demoCount = demoArr.length;
                                    out.println("<table class='table'>");
                                    if (demoCount > 0) {
                                        ArrayList<HashMap<String, Breakdown>> tier1 = breakdown.getBreakdown();
                                        String demo1 = demoArr[0];
                                        for (HashMap<String, Breakdown> map1 : tier1) {
                                            out.println("<tr class=active><th>" + demo1 + ": " + map1.get(demo1) + "</th><th>Count: " + map1.get("count") + "</th><th>" + map1.get("percent").getMessage() + "% of all " + demo1 + "</th></tr>");

                                            if (demoCount == 1) {
                                                ArrayList<HashMap<String, Breakdown>> report1 = map1.get("breakdown").getBreakdown();
                                                HashMap<String, Breakdown> intenseMap = report1.get(0);
                                                HashMap<String, Breakdown> normalMap = report1.get(1);
                                                HashMap<String, Breakdown> mildMap = report1.get(2);
                                                out.println("<tr style='color:green'><td>");
                                                out.println("intense users: " + intenseMap.get("intense-count") + " (" + intenseMap.get("intense-percent") + "%)<br>"
                                                        + "normal users: " + normalMap.get("normal-count") + " (" + normalMap.get("normal-percent") + "%)<br>"
                                                        + "mild users: " + mildMap.get("mild-count") + " (" + mildMap.get("mild-percent") + "%)<br>");
                                                out.println("</td></tr>");

                                            }

                                            if (demoCount > 1) {
                                                String demo2 = demoArr[1];
                                                ArrayList<HashMap<String, Breakdown>> tier2 = map1.get("breakdown").getBreakdown();
                                                for (HashMap<String, Breakdown> map2 : tier2) {
//												out.println("<tr style='color:blue'><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
//														+ "" + demo2 + ": " + map2.get(demo2) + "</td><td>Count: " + map2.get("count") + "</td><td>" + map2.get("percent").getMessage() + "%</td></tr>");
                                                    out.println("<tr style='color:green'><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
                                                            + "" + demo2 + ": " + map2.get(demo2) + " (count: " + map2.get("count") + " , " + map2.get("percent") + "%)</td><td>");
                                                    if (demoCount == 2) {

                                                        ArrayList<HashMap<String, Breakdown>> report2 = map2.get("breakdown").getBreakdown();
                                                        HashMap<String, Breakdown> intenseMap = report2.get(0);
                                                        HashMap<String, Breakdown> normalMap = report2.get(1);
                                                        HashMap<String, Breakdown> mildMap = report2.get(2);
                                                        out.println("intense users: " + intenseMap.get("intense-count") + " (" + intenseMap.get("intense-percent") + "%)<br>"
                                                                + "normal users: " + normalMap.get("normal-count") + " (" + normalMap.get("normal-percent") + "%)<br>"
                                                                + "mild users: " + mildMap.get("mild-count") + " (" + mildMap.get("mild-percent") + "%)<br>");

                                                    }
                                                    out.println("</td><td></td></tr>");

                                                    if (demoCount > 2) {
                                                        String demo3 = demoArr[2];
                                                        ArrayList<HashMap<String, Breakdown>> tier3 = map2.get("breakdown").getBreakdown();
                                                        for (HashMap<String, Breakdown> map3 : tier3) {
                                                            ArrayList<HashMap<String, Breakdown>> report3 = map3.get("breakdown").getBreakdown();
                                                            HashMap<String, Breakdown> intenseMap = report3.get(0);
                                                            HashMap<String, Breakdown> normalMap = report3.get(1);
                                                            HashMap<String, Breakdown> mildMap = report3.get(2);
//														out.println("<tr style='color:green'><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
//																+ "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
//																+ "<i>" + demo3 + ": " + map3.get(demo3) + "</i></td><td><i>Count: " + map3.get("count") + "</i></td><td><i>" + map3.get("percent").getMessage() + "%</i></td></tr>");
                                                            out.println("<tr style='color:blue'><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
                                                                    + "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
                                                                    + "" + demo3 + ": " + map3.get(demo3) + " (count: " + map3.get("count") + " , " + map3.get("percent") + "%)</td><td>"
                                                                    + "intense users: " + intenseMap.get("intense-count") + " (" + intenseMap.get("intense-percent") + "%)<br>"
                                                                    + "normal users: " + normalMap.get("normal-count") + " (" + normalMap.get("normal-percent") + "%)<br>"
                                                                    + "mild users: " + mildMap.get("mild-count") + " (" + mildMap.get("mild-percent") + "%)<br>"
                                                                    + "</td><td></td></tr>");

//														out.println("<tr><td></td><td>intense users: "+ intenseMap.get("intense-count") +" ("+ intenseMap.get("intense-percent") +"%)</td>"
//																+ "<td></td></tr>");
                                                        }

                                                    }

                                                }
                                            }

                                        }
                                    }
                                    out.println("</table>");
                                }

                            } else {
                                //NO REPORT AND NO ERROR
                                out.println("<h1>Result</h1>");
                                out.println("You have not entered any input.");
                            }
                        }
                    %>
                </div>

            </div>
        </div>
        <!--END OF CONTENT-->

        <%@include file="import-js.jsp" %>
    </body>
</html>
