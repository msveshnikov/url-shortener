<%@ page import="bench.JspHelper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  ~ Copyright (c) 2014 Thumbtack Technologies
  --%>

<html>
<head>
    <title>Result</title>

    <style>
        body {
            font-family: monospace;
            font-size: 16px;
            margin: 1em;
        }

        .message {
            color: red;
            text-align: left;
        }

        .history a {
            display: block;
            border-style: solid;
            border-color: #bbb #888 #666 #aaa;
            border-width: 1px 2px 2px 1px;
            background: #6c6;
            color: #555;
            line-height: 2;
            text-align: center;
            text-decoration: none;
            font-weight: 700;
            font-size: 12px;
            width: 20em;
        }
    </style>
</head>

<%
    final JspHelper helper = new JspHelper(getServletConfig().getServletContext());
    String shorturl = helper.getShort(request.getParameter("url"), (String) session.getAttribute("userinfo"));
%>

<body>

<h3 class="message"> Shorten result</h3>

Original URL: <%= request.getParameter("url") %>
<br>
<br>

<div class="history">
    Short URL: <a href="<%= shorturl %>"><%= shorturl %>
</a>
</div>
<br><br>
<a href="/shortener">Home page</a>

</body>
</html>

