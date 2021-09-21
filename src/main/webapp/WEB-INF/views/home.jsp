<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Toby</title>
</head>
<body>
<h1>
	Hello Toby!
</h1>

<P>  ${user.getName()} </P>
<P>  ${user.getPassword()} </P>
<P>  ${user.getId()} 조회 성공. </P>
</body>
</html>
