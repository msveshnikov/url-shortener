<%@page import="bench.GoogleAuthHelper" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" type="text/css" href="shortener/styles.css"/>

<html>
<head>
    <title>URL Shortener</title>

    <style>
        body {
            font-family: Verdana;
            margin: 1em;
        }

        .message { color: red; text-align: left; }

        .oauthDemo a {
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
            width: 10em;
        }

        .oauthDemo pre {
            background: #ccc;
        }

        .oauthDemo a:active {
            border-color: #666 #aaa #bbb #888;
            border-width: 2px 1px 1px 2px;
            color: #000;
        }

    </style>

</head>
<body>
<span style="font-family: Verdana; ">
<h3 class="message"> URL Shortener v1.0</h3>

    	<div class="oauthDemo">
            <%
                /*
                 * The GoogleAuthHelper handles all the heavy lifting, and contains all "secrets"
                 * required for constructing a google login url.
                 */
                final GoogleAuthHelper helper = new GoogleAuthHelper();

                if (request.getParameter("code") == null
                        || request.getParameter("state") == null) {

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

                    out.println("<pre>");
				/*
				 * Executes after google redirects to the callback url.
				 * Please note that the state request parameter is for convenience to differentiate
				 * between authentication methods (ex. facebook oauth, google oauth, twitter, in-house).
				 *
				 * GoogleAuthHelper()#getUserInfoJson(String) method returns a String containing
				 * the json representation of the authenticated user's information.
				 * At this point you should parse and persist the info.
				 */

                    out.println(helper.getUserInfoJson(request.getParameter("code")));

                    out.println("</pre>");
                }
            %>
        </div>

<form
        action="shortener/result.jsp"
        name="shortenUrl" id="unAuthShortenForm">
    <input id="shorten_url" name="url" type="text" class="text"
           placeholder="Paste a link to shorten it" value="" autocomplete="off"
            />
    <input id="shorten_btn" type="submit" class="btn blue-btn square" value="Shorten"/>
</form>
</span>

</body>
</html>
