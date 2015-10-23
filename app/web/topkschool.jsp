<%-- 
    Document   : top-kreport
    Created on : 17-Oct-2015, 00:06:45
    Author     : Boyofthefuture
--%>

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
                                <li><a href="topkstudent.jsp">Top-k students with most app usage (given an app category)</a></li>
                                <li><a href="topkschool">Top-k schools with most app usage (given an app category)</a></li>
                            </ul>
                        </li>
                        <li><a href="smartphoneOveruse.jsp">Smartphone Overuse</a></li>
                        <li><a href="heatmap.jsp">Smartphone Usage Heatmap</a></li>
                        <li><a href="activeness.jsp">Social Activeness</a></li>

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
            String select = request.getParameter("choice");
            String startDate = request.getParameter("startdate");
            String endDate = request.getParameter("enddate");

            String startDateInput = "";
            String endDateInput = "";
            if (startDate != null) {
                startDateInput = "value='" + startDate + "'";
            }
            if (endDate != null) {
                endDateInput = "value='" + endDate + "'";
            }
            ArrayList<String> categories = Utility.getCategories();
        %>


        <div class="theme-container container">
            <div class="row">
                <div class="theme-div" style="width:37%">
                    <form action="TopKServlet" method="POST">
                        <div class="form-group"></div>
                        <div class="form-group">
                            <label for="entries">Number of Top Results</label>
                            <input type="number" name="entries" min="1" max="10" value="3" class="form-control" id="entries">
                        </div>
                        <div class="form-group">
                            <label for="startdate">Start Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="startdate" name="startdate" <%=startDateInput%> required>
                        </div>
                        <div class="form-group">
                            <label for="enddate">End Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="enddate" name="enddate" <%=endDateInput%> required>
                        </div>
                        <div class="form-group">
                            <label for="choices"> Choice of App Categories </label>
                            <select class="form-control" name="choices">
                                <%
                                    for (String category : categories) {
                                        String categoryCode = "";
                                        if (category.equals(select)) {
                                            categoryCode = "selected";
                                        }
                                        out.println("<option value=" + category + " " + categoryCode + ">" + category + "</option>");
                                    }
                                %>
                            </select>
                        </div>
                        <input type="hidden" value="appschools" name="category">
                        <input type="submit" class="btn btn-default" value="Generate">
                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">
                    <h4><b> Top-K Report</b> </h4> <hr>
                    <%
                        ArrayList<HashMap<String, String>> values = (ArrayList<HashMap<String, String>>) request.getAttribute("catvalues");
                        String error = (String) request.getAttribute("error");
                        String errors = (String) request.getAttribute("errors");
                        if (error != null && error.length() > 0) {
                            out.println("<h1 style='color:red'>Error!</h1>");
                            out.println("<h3 style='color:red'>" + error + "</h3>");
                        } else if (values != null) { //Checks this because in case of category refresh.
                            out.println("<h1>Result</h1>");
                            //This prints the type of table
                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'>");

                            out.println("<td><b>Rank (for schools with most app usage)</b></td><td><b>School</b></td><td><b>App usage Time</b></td></tr>");
                            for (int i = 0; i < values.size(); i += 0) {
                                //This starts to retrieve and takes the values of each individual out. It will print based on what is stored.
                                HashMap<String, String> indiv = values.get(i);
                                int rank = Utility.parseInt(indiv.get("rank"));
                                //Gets ready the schools to add on.
                                String names = null;
                                //Gets the school to convert to its formal name
                                String name = indiv.get("school");
                                switch (name) {
                                    case "socsc":
                                        names = "Social Science";
                                        break;
                                    case "law":
                                        names = "Law";
                                        break;
                                    case "sis":
                                        names = "Information Systems";
                                        break;
                                    case "accountancy":
                                        names = "Accountancy";
                                        break;
                                    case "economics":
                                        names = "Economics";
                                        break;
                                    case "business":
                                        names = "Business";
                                        break;
                                }
                                String duration = indiv.get("duration");
                                out.println("<tr><td>" + rank + "</td>"); //Prints the Rank
                                Iterator<HashMap<String, String>> iter = values.iterator();
                                //Removes the first line that has already been stored so that it is always checking with later ranks
                                iter.next();
                                iter.remove();
                                while (iter.hasNext()) {
                                    HashMap<String, String> other = iter.next();
                                    if (Utility.parseInt(other.get("rank")) == rank) {
                                        names += ", " + other.get("name");
                                    } else {
                                        break; //breaks out of this while loop if the ranks are not equivalent anymore.
                                    }
                                    iter.remove();
                                }
                                out.println("<td>" + names + "</td>"); //Prints the concatenated Schoolnames
                                out.println("<td>" + duration + "</td></tr>"); //Prints the app usage time
                            }
                        }
                        if (errors != null && errors.length() > 0) {
                            out.println("<br>");
                            out.println("<h1 style='color:red'>Error!</h1>");
                            out.println("<h3 style='color:red'>" + error + "</h3>");
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
