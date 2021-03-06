<%@ page import="bench.GoogleAuthHelper" %>
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
      <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
          (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
              m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
      ga('create', 'UA-33888137-1', 'auto');
      ga('send', 'pageview');
  </script>

</head>
<body>


<h3 class="message"> URL Shortener v1.5
</h3>

<div class="login">
    <%
        /*
         * The GoogleAuthHelper handles all the heavy lifting, and contains all "secrets"
         * required for constructing a google login url.
         */
        final bench.JspHelper helper = new bench.JspHelper(getServletConfig().getServletContext());
        final GoogleAuthHelper auth = new GoogleAuthHelper();
        GoogleAuthHelper.host = request.getHeader("host");

        if (session.getAttribute("userinfo") == null && (request.getParameter("code") == null
                || request.getParameter("state") == null)) {

            /*
             * initial visit to the page
             */
    %>
    <a href='<%= auth.buildLoginUrl() %>'>Login with Google+</a><br><br>
    <%
            helper.logger.info("Auth to Google+");
             /*
             * set the secure state token in session to be able to track what we sent to google
             */
            session.setAttribute("state", auth.getStateToken());

        } else if (request.getParameter("code") != null && request.getParameter("state") != null
                && request.getParameter("state").equals(session.getAttribute("state"))) {

            helper.logger.info("Auth succesful!");
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
            session.setAttribute("userinfo", auth.getUserInfoJson(request.getParameter("code")));
        }
    %>
</div>
<div class="history">
    <%
        Object userinfo = session.getAttribute("userinfo");
        if (userinfo != null) {
            String userId = JSONObject.fromObject(userinfo).getString("id");
            String name = JSONObject.fromObject(userinfo).getString("name");
            String picture = JSONObject.fromObject(userinfo).getString("picture");
    %>
    <img src="<%= picture %>" height="42" width="42">
    Welcome,  <%= name %> <br><br>
    <%
        if (helper.dao.isConnected()) {
            for (String url : helper.dao.historyByUserId(userId)) {
    %>
    <a href="<%= url %>"><%= url %>
    </a> <br>
    <%
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

    <br>

    <a href="http://iWatcher.tk">  iWatcher.tk  </a>

    <br>
<br>
<br>

<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
    <!-- my -->
    <ins class="adsbygoogle"
         style="display:inline-block;width:300px;height:600px"
         data-ad-client="ca-pub-1351673544337847"
         data-ad-slot="2591225138"></ins>
    <script>
        (adsbygoogle = window.adsbygoogle || []).push({});
    </script>

</body>
</html>
