<%@page import="entity.LocationUsage"%>
<%@page import="entity.LocationUsage"%>
<%@page import="java.util.ArrayList"%>
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
            String macAdd = (String) request.getAttribute("macadd");
            String startdate = (String) request.getAttribute("startdate");
            String enddate = (String) request.getAttribute("enddate");
            String locationId = (String) request.getAttribute("locationid");
            String semanticPlace = (String) request.getAttribute("semanticplace");
            String startTime = (String) request.getAttribute("starttime");
            String endTime = (String) request.getAttribute("endtime");
            if (macAdd == null) {
                macAdd = "";
            }
            if (startdate == null) {
                startdate = "";
            }
            if (enddate == null) {
                enddate = "";
            }
            if (locationId == null) {
                locationId = "";
            }
            if (semanticPlace == null) {
                semanticPlace = "";
            }
            if (startTime == null) {
                startTime = "";
            }
            if (endTime == null) {
                endTime = "";
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
                        <li><a href="admin">Bootstrap <span class="sr-only">(current)</span></a></li>
                        <li class="active"><a href="delete-location.jsp">Delete Location data</a></li>

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

                    <form action="DeleteLocationAction" method="post">
                        <br/>
                        <div class="form-group">
                            <label for="startdate">Start Date</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="startdate" name="startdate" value="<%=startdate%>" required>
                        </div>
                        <div class="form-group">
                            <label for="starttime">Start Time (Optional)</label>
                            <input type="time" class="form-control" id="starttime" name="starttime" placeholder="HH:MM" value="<%=startTime%>">
                        </div>
                        <div class="form-group">
                            <label for="enddate">End Date (Optional)</label>
                            <input type="date" min="1970-01-01" max="2050-01-01" class="form-control" id="enddate" name="enddate" value="<%=enddate%>">
                        </div>
                        <div class="form-group">
                            <label for="endtime">End Time (Optional)</label>
                            <input type="time" class="form-control" id="endtime" name="endtime" placeholder="HH:MM" value="<%=endTime%>">
                        </div>
                        <div class="form-group">
                            <label for="macadd">Mac Address (Optional)</label>
                            <input type="text" class="form-control" id="macadd" name="macadd" placeholder="mac address" value="<%=macAdd%>">
                        </div>
                        <div class="form-group">
                            <label for="locationid">Location Id (Optional)</label>
                            <input type="number" class="form-control" id="locationid" name="locationid" placeholder = "location id" value="<%=locationId%>">
                        </div>
                        <div class="form-group">
                            <label for="semanticplace">Semantic Place (Optional)</label>
                            <input type="text" class="form-control" id="semanticplace" name="semanticplace" placeholder = "semantic place" value="<%=semanticPlace%>">
                        </div>

                        <input type="submit" class="btn btn-default" value="Delete location data">



                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">
                    <%      String errors = (String) request.getAttribute("errors");
                        ArrayList<LocationUsage> rowsDel = (ArrayList<LocationUsage>) request.getAttribute("deleted");
                        out.println("<h1>Result</h1><br>");
                        if (errors != null && errors.length() != 0) {
                            out.println("<h1 style='color:red'>Error!</h1>");
                            out.println("<h3 style='color:red'>" + errors + "</h3>");
                        } else if (rowsDel == null || rowsDel.size() == 0) {
                            out.println("You have not chosen anything to delete");
                        } else {
                            out.println(rowsDel.size() + " location usage records deleted.<br>");
                            out.println("<table border=1px class='table table-striped'><tr style='background-color:lightsalmon'>");
                            out.println("<td><b>Location Id</b></td><td><b>Mac Address</b></td><td><b>TimeStamp</b></td></tr>");
                            for (LocationUsage lu : rowsDel) {
                                out.println("<tr><td>" + lu.getLocation().getLocationId() + "</td><td>" + lu.getMacAddress() + "</td><td>" + lu.getTimestamp() + "</td></tr>");
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