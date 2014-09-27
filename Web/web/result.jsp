<%@ page import="bench.ShortenServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <title>Result</title>

    <style>
        body {
            font-family: Verdana;
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
            font-size: 12;
            width: 20em;
        }
    </style>
</head>

<%

    final ShortenServlet servlet = new ShortenServlet();
    String shorturl = servlet.getShort(request.getParameter("url"), session);
%>

<body>
<span style="font-family: Verdana; ">
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


</span>

</body>
</html>

