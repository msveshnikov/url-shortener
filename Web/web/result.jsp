<%@ page import="bench.ShortenServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="styles.css"/>

<html>
<head>
    <title>Result</title>
</head>

<body>
<font face="Verdana">
<h3 class="message"> Shorten result</h3>

<font size="2" color="green">
    Original URL: <%= request.getParameter("url") %>
    <br>
    <br>
    Short URL: <a href="<%= ShortenServlet.getShort(request.getParameter("url")) %>"><%= ShortenServlet.getShort(request.getParameter("url")) %></a>
</font>
</font>

</body>
</html>

