<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form action="/app/json/bootstrap"  method="post" enctype="multipart/form-data">
			File:
			<input type="file" name="bootstrap-file" /><br />
			<input type='text' name='token' value='eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0MDk3MTIxNTMsImlhdCI6MTQwOTcwODU1M30.h66rOPHh992gpEPtErfqBP3Hrfkh_nNxYwPG0gcAuCc' />
			<!-- substitute the above value with a valid token -->
			<input type="submit" value="Bootstrap" />
		</form>
    </body>
</html>
