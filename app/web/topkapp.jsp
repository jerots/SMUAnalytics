<%-- 
    Document   : topkschool
    Created on : Oct 22, 2015, 8:07:29 PM
    Author     : Shuwen
--%>

<%@page import="dao.Utility"%>
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
                                <li><a href="topkapp.jsp">Top-k most used apps (given a school)</a></li>
                                <li><a href="topkstudent.jsp">Top-k students with most app usage (give an app category)</a></li>
                                <li><a href="topkschool.jsp">Top-k schools with most app usage (give an app category) </a></li>
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
                    <form action="BasicAppCatAction">
                        <div class="form-group">
                            <label for="date">k</label>
                            <input type="text" class="form-control" id="k" name="k" value="<%=startdate%>">
                        </div>
                        <div class="form-group">
                            <label for="date">Start Date</label>
                            <input type="date" class="form-control" id="startdate" name="startdate" value="<%=startdate%>" required>
                        </div>
                        <div class="form-group">
                            <label for="date">End Date</label>
                            <input type="date" class="form-control" id="enddate" name="enddate" value="<%=enddate%>" required>
                        </div>
                            <%
                                ArrayList<String> categories = Utility.getCategories();

                            %>
                            <div class="form-group">
                                <label for="categories">Top K App</label>
                                <select class="form-control" name="school">
                                    <%                                    for (String s : categories) {
                                    %>
                                    <option class='form-control' id='school' name='<%= s%>' value='<%= s%>' ><%= s%></option>
                                    <%
                                        }
                                    %>
                                </select>

                            </div>



                        <input type="submit" class="btn btn-default" value="Generate">
                    </form>
                </div>
                <div class="theme-div theme-content" style="width:60%">
                               <!----
                
                ERRRROR STUFF
                --->
                </div>

            </div>
        </div>
        <!--END OF CONTENT-->

        <%@include file="import-js.jsp" %>
    </body>
</html>
