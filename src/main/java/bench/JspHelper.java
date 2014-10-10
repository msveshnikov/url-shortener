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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

public class JspHelper {
    public static final String GOOGLE_ID = "id";
    public static final String REST_SERVICE_FAILED = "REST service failed: ";
    public final UsersDAO dao;
    String DBNAME;
    String REST_COMMAND;
    String COUCH_URL;
    public static final Logger logger = LoggerFactory.getLogger(RestService.class);

    public JspHelper(ServletContext context) throws IOException {
        String resourceFileName = "/WEB-INF/config/rest.properties";
        InputStream is = context.getResourceAsStream(resourceFileName);
        Properties configuration = new Properties();
        configuration.load(is);
        DBNAME = configuration.getProperty("WEBDBNAME");
        REST_COMMAND = configuration.getProperty("REST_COMMAND");
        COUCH_URL = configuration.getProperty("COUCH_URL");

        GoogleAuthHelper.CALLBACK = configuration.getProperty("CALLBACK");
        GoogleAuthHelper.CLIENT_ID = configuration.getProperty("CLIENT_ID");
        GoogleAuthHelper.CLIENT_SECRET = configuration.getProperty("CLIENT_SECRET");
        dao = new CouchDAOImpl(DBNAME, COUCH_URL);
    }

    public String getShort(String url, String userinfo) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String encoded = URLEncoder.encode(url, "ASCII");
        String uri = GoogleAuthHelper.HTTP + GoogleAuthHelper.host + REST_COMMAND + encoded;
        logger.info("Call REST service url={}", uri);
        HttpGet get = new HttpGet(uri);
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            logger.error("Call REST failed, {} {}", response.getStatusLine().getStatusCode(), response.getStatusLine());
            throw new IOException(REST_SERVICE_FAILED + response.getStatusLine());
        }
        String shorturl = new BasicResponseHandler().handleResponse(response);
        if (userinfo != null && dao.isConnected())
            dao.saveShort(url, JSONObject.fromObject(userinfo).getString(GOOGLE_ID), shorturl);
        return shorturl;
    }
}













