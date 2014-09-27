<%@ page import="bench.GoogleAuthHelper" %>
<%@ page import="bench.ShortenServlet" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>URL Shortener</title>

    <style>
        body {
            font-family: Verdana;
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
            font-size: 12;
            width: 20em;
        }
    </style>

</head>
<body>
<span style="font-family: Verdana">

<h3 class="message"> URL Shortener v1.0</h3>

<div class="login">
    <%
        /*
         * The GoogleAuthHelper handles all the heavy lifting, and contains all "secrets"
         * required for constructing a google login url.
         */
        final GoogleAuthHelper helper = new GoogleAuthHelper();
        final ShortenServlet servlet = new ShortenServlet();

        if (session.getAttribute("userinfo") == null && (request.getParameter("code") == null
                || request.getParameter("state") == null)) {

				/*
				 * initial visit to the page
				 */
            out.println("<a href='" + helper.buildLoginUrl()
                    + "'>Login with Google+</a><br><br>");

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
        if (session.getAttribute("userinfo") != null)
            servlet.PrintPreviousShorts(session, out);
    %>
</div>


        <form
                action="/shortener/result.jsp"
                name="shortenUrl">
            <input name="url" type="text"
                   placeholder="Paste a link to shorten it" value=""/>
            <input type="submit" value="Shorten"/>
        </form>

</span>

</body>
</html>
