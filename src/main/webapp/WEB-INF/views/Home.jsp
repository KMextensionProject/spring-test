<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Gold-digger home</title>
</head>
<style>
button {
  /* This extra margin represent roughly the same space as the space
     between the labels and their text fields */
  height:30px;
  width:180px;
}
</style>
<body>
	<%
     ServletContext sc = request.getServletContext();
     String ip = (String) sc.getAttribute("ip");
	%>

<h3>WELCOME TO YOUR GOLD DIGGER TOOL !</h3>

	<form
		action=<%="http://" + ip + ":8080/gold-digger/scheduler/switch"%>
		method="post">
		<br>Suspend/Resume the scheduler performing actions on your exchange account:<br>
		<button type="submit">SWITCH</button>
	</form>
	<br></br>

	<form
		action=<%="http://" + ip + ":8080/gold-digger/account/orders/excel"%>
		method="get">
		<br>Generate a report containing all filled orders for this year: <br>
		<button type="submit">GENERATE REPORT</button>
	</form>
	<br></br>

	<form
		action=<%="http://" + ip + ":8080/gold-digger/account/complexOverview"%>
		method="get">
		<br>Check out your account info and its current sate:<br>
		<button type="submit">ACCOUNT INFO</button>
	</form>
	<br></br>

	<form
		action=<%="http://" + ip + ":8080/gold-digger/market/complexOverview"%>
		method="get">
		<br>Check out the market info and its current state:<br>
		<button type="submit">MARKET INFO</button>
	</form>

</body>
</html>