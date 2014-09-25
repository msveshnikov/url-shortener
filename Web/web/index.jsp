<%@ page import="bench.ShortenServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="styles.css"/>

<html>
<head>
    <title>URL Shortener</title>
</head>
<body>
<font face="Verdana">
<h3 class="message"> URL Shortener v1.0</h3>

<form
        action="result.jsp"
        name="shortenUrl" id="unAuthShortenForm">
    <input id="shorten_url" taborder="1" name="url" type="text" class="text"
           placeholder="Paste a link to shorten it" value="" autocomplete="off" autocorrect="off"
           autocapitalize="off"/>
    <input id="shorten_btn" type="submit" class="btn blue-btn square" value="Shorten"/>
</form>
</font>

</body>
</html>
