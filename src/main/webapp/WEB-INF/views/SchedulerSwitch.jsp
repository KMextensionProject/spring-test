<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Gold-digger home</title>
</head>
<body>

	<%
     ServletContext sc = request.getServletContext();
     String ip = (String) sc.getAttribute("ip");
	%>

	<form
		action=<%="http://" + ip + ":8080/gold-digger/scheduler/switch"%>
		method="post">
		<button type="submit">Switch scheduler</button>
	</form>

</body>
</html>