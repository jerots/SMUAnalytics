<%-- 
    Document   : addBatch
    Created on : 02-Oct-2015, 12:24:11
    Author     : Boyofthefuture
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form action="AddBatch" method="post" enctype="multipart/form-data">
           Choose File to add:<br/>
           <input type="file" name="zipFile"> <br/>
           <input type="submit" value="Upload">
        </form>
    </body>
</html>
