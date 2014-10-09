<%@ page import="bench.GoogleAuthHelper" %>
<%@ page import="bench.ShortenHelper" %>
<%@ page import="net.sf.json.JSONObject" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  ~ Copyright (c) 2014 Thumbtack Technologies
  --%>

<html>
<head>
    <title>URL Shortener</title>

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

        .login a {
            display: block;
            border-style: solid;
            border-color: #bbb #888 #666 #aaa;
            border-width: 1px 2px 2px 1px;
            background: #cc2;
            color: #555;
            line-height: 2;
            text-align: center;
            text-decoration: none;
            font-weight: 900;
            width: 14em;
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
<body>


<h3 class="message"> URL Shortener v1.5
</h3>

<div class="login">
    <%! public static final String A_HREF = "<a href='";
        public static final String LOGIN_WITH_GOOGLE = "'>Login with Google+</a><br><br>";
    %><%
    /*
     * The GoogleAuthHelper handles all the heavy lifting, and contains all "secrets"
     * required for constructing a google login url.
     */
    final GoogleAuthHelper helper = new GoogleAuthHelper();
    final ShortenHelper shortener = new ShortenHelper();
    GoogleAuthHelper.host = request.getHeader("host");

    if (session.getAttribute("userinfo") == null && (request.getParameter("code") == null
            || request.getParameter("state") == null)) {

				/*
				 * initial visit to the page
				 */
        out.println(A_HREF + helper.buildLoginUrl() + LOGIN_WITH_GOOGLE);

				/*
				 * set the secure state token in session to be able to track what we sent to google
				 */
        session.setAttribute("state", helper.getStateToken());

    } else if (request.getParameter("code") != null && request.getParameter("state") != null
            && request.getParameter("state").equals(session.getAttribute("state"))) {

        session.removeAttribute("state");

				/*
				 * Executes after google redirects to the callback url.
				 * Please note that the state request parameter is for convenience to differentiate
				 * between authentication methods (ex. facebook oauth, google oauth, twitter, in-house).
				 *
				 * GoogleAuthHelper()#getUserInfoJson(String) method returns a String containing
				 * the json representation of the authenticated user's information.
				 * At this point you should parse and persist the info.
				 */
        session.setAttribute("userinfo", helper.getUserInfoJson(request.getParameter("code")));

    }
%>
</div>
<div class="history">
    <%
        final String GOOGLE_ID = "id";
        final String GOOGLE_NAME = "name";
        final String GOOGLE_PICTURE = "picture";

        Object userinfo = session.getAttribute("userinfo");
        if (userinfo != null) {
            String userId = JSONObject.fromObject(userinfo).getString(GOOGLE_ID);
            String name = JSONObject.fromObject(userinfo).getString(GOOGLE_NAME);
            String picture = JSONObject.fromObject(userinfo).getString(GOOGLE_PICTURE);

            out.println("<img src=\"" + picture + "\"  height=\"42\" width=\"42\">");
            out.println("Welcome, " + name + "<br><br>");
            if (shortener.dao.isConnected()) {
                for (String url : shortener.dao.historyByUserId(userId)) {
                    out.println("<a href='" + url + "'>" + url + "</a><br>");
                }
            }
        }
    %>
</div>


<form
        action="/shortener/result.jsp"
        name="shortenUrl">
    <input name="url" type="text"
           placeholder="Paste a link to shorten it" value=""/>
    <input type="submit" value="Shorten"/>
</form>

</body>
</html>
