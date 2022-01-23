<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gold-digger home</title>
</head>
<body>

	<jsp:useBean id="endpointLoader" scope="application"
		class="sk.golddigger.config.EndpointLoader"
		type="sk.golddigger.config.EndpointLoader">
	</jsp:useBean>

	<form
		action=<%="http://" + endpointLoader.getServerIpAddress() + ":8080/gold-digger/scheduler/switch"%>
		method="post">
		<button type="submit">Switch</button>
	</form>

</body>
</html>