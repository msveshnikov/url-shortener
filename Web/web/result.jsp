<%@ page import="bench.ShortenServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="shortener/styles.css"/>

<html>
<head>
    <title>Result</title>
</head>

<%

    final ShortenServlet servlet = new ShortenServlet();
    String shorturl = servlet.getShort(request.getParameter("url"), session);
%>

<body>
<span style="font-family: Verdana; ">
<h3 class="message"> Shorten result</h3>

<span style="color: green; ">
    Original URL: <%= request.getParameter("url") %>
    <br>
    <br>
    Short URL: <a href="<%= shorturl %>"><%= shorturl %>
</a>

    <a href="/shortener">Home page</a>

</span>
</span>

</body>
</html>

