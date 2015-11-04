<%-- 
    Document   : bootstrap-json
    Created on : Oct 10, 2015, 6:17:49 AM
    Author     : jeremyongts92
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form action="/app/json/update"  method="post" enctype="multipart/form-data">
			File:
			<input type="file" name="addbatch-file" /><br />
			<input type="text" name="token" />
			<!-- substitute the above value with a valid token -->
			<input type="submit" value="Additional File" />
		</form>
    </body>
</html>
