/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.net.URLEncoder;

public class ShortenHelper {
    public final static String DBNAME = "users";//config
    public static final String REST_COMMAND = "/shorten?url=";//config

    public static final String GOOGLE_ID = "id";
    public static final String GOOGLE_NAME = "name";
    public static final String GOOGLE_PICTURE = "picture";
    public static final String REST_SERVICE_FAILED = "REST service failed: ";

    private final UsersDAO dao = new CouchDAOImpl(DBNAME);

    public String getShort(String url, String userinfo) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String encoded = URLEncoder.encode(url, "ASCII");
        HttpGet get = new HttpGet(GoogleAuthHelper.HTTP + GoogleAuthHelper.host + REST_COMMAND + encoded);
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw new IOException(REST_SERVICE_FAILED + response.getStatusLine());
        String shorturl = new BasicResponseHandler().handleResponse(response);
        if (userinfo != null && dao.isConnected())
            dao.saveShort(url, JSONObject.fromObject(userinfo).getString(GOOGLE_ID), shorturl);
        return shorturl;
    }

    public void PrintPreviousShorts(String userinfo, JspWriter out) throws IOException {
        String userId = JSONObject.fromObject(userinfo).getString(GOOGLE_ID);
        String name = JSONObject.fromObject(userinfo).getString(GOOGLE_NAME);
        String picture = JSONObject.fromObject(userinfo).getString(GOOGLE_PICTURE);
        // TODO: to JSP
        out.println("<img src=\"" + picture + "\"  height=\"42\" width=\"42\">");
        out.println("Welcome, " + name + "<br><br>");
        if (dao.isConnected()) {
            for (String url : dao.historyByUserId(userId)) {
                out.println("<a href='" + url + "'>" + url + "</a><br>");
            }
        }
    }
}













