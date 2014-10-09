/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CouchDAOImpl implements ShortenerDAO, UsersDAO {

    public static final String COUCH_URL = "http://localhost:5984/"; //config
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";

    //fields
    public static final String COUCH_ID_FIELD = "_id";
    public static final String LONG_FIELD = "long";
    public static final String MY_ID_FIELD = "myid";
    public static final String SHORT_FIELD = "short";
    public static final String USERID_FIELD = "userid";

    //messages
    public static final String NO_SUCH_ID_FOUND = "No such ID found";
    public static final String GET_JSON_FAILED = "getJSON failed: ";
    public static final String POST_FAILED = "post failed: ";

    //couch semantics
    public static final String DESIGN_COUCHVIEW = "/_design/couchview/_view/";
    public static final String DESIGN = "_design/couchview";
    public static final String COUCH_ROWS = "rows";
    public static final String COUCH_KEY = "key";
    public static final String COUCH_VALUE = "value";
    public static final String COUCH_DOC = "doc";
    public static final String COUCH_VIEWS = "views";
    public static final String USERS_VIEW = "{\"userid\": {\"map\": \"function(doc) { emit(doc.userid, doc.short) } \"}}";
    public static final String USERS_FIND = "userid?key=";
    public static final String AUTOINC_VIEW = "{\"autoinc\": {\"map\": \"function(doc) { emit(doc.myid, null) } \"}}";
    public static final String AUTOINC_FIND = "autoinc?include_docs=true&key=";
    public static final String AUTOINC_MAX = "autoinc?startkey=2000000000&descending=true&limit=1";
    public static final String URL_QUOTE = "%22";

    private String dbname;
    private Boolean connected;

    public CouchDAOImpl(String dbname) {
        this.dbname = dbname;
        connected = true;
        try {
            createDatabase();
        } catch (Exception e) {
            connected = false;
        }
    }

    public Boolean isConnected() {
        return connected;
    }

    @Override
    public void savePair(String shortUrl, String longUrl) throws IOException {
        JSONObject doc = new JSONObject();
        doc.put(COUCH_ID_FIELD, shortUrl);
        doc.put(LONG_FIELD, longUrl);
        createDocument(doc);
    }

    @Override
    public void saveTriple(long id, String shortUrl, String longUrl) throws IOException {
        JSONObject doc = new JSONObject();
        doc.put(MY_ID_FIELD, id);
        doc.put(SHORT_FIELD, shortUrl);
        doc.put(LONG_FIELD, longUrl);
        createDocument(doc);
    }

    @Override
    public String findById(long id) throws IOException {
        JSONObject jsonObject = getJSON(COUCH_URL + dbname + DESIGN_COUCHVIEW + AUTOINC_FIND + id);
        if (jsonObject.getJSONArray(COUCH_ROWS).size() != 0)
            return jsonObject.getJSONArray(COUCH_ROWS).getJSONObject(0).getJSONObject(COUCH_DOC).getString(LONG_FIELD);
        else throw new IOException(NO_SUCH_ID_FOUND);
    }

    @Override
    public long findMaxId() {
        JSONObject jsonObject;
        try {
            jsonObject = getJSON(COUCH_URL + dbname + DESIGN_COUCHVIEW + AUTOINC_MAX);
            JSONArray rows = jsonObject.getJSONArray(COUCH_ROWS);
            return rows.getJSONObject(0).getInt(COUCH_KEY);
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public String findByShort(String shortUrl) throws IOException {
        JSONObject d = getDocument(shortUrl);
        return d.getString(LONG_FIELD);
    }

    private String quote(String s) {
        return URL_QUOTE + s + URL_QUOTE;
    }

    public List<String> historyByUserId(String userId) throws IOException {
        JSONObject result = getJSON(COUCH_URL + dbname + DESIGN_COUCHVIEW + USERS_FIND + quote(userId));
        JSONArray arr = result.getJSONArray(COUCH_ROWS);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(arr.getJSONObject(i).getString(COUCH_VALUE));
        }
        return list;
    }

    public void saveShort(String longUrl, String userId, String shortUrl) throws IOException {
        JSONObject doc = new JSONObject();
        doc.put(USERID_FIELD, userId);
        doc.put(SHORT_FIELD, shortUrl);
        doc.put(LONG_FIELD, longUrl);
        createDocument(doc);
    }

    public JSONObject getJSON(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", APPLICATION_JSON);
        HttpResponse response = httpclient.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException(GET_JSON_FAILED + response.getStatusLine());
        }
        String responseString = new BasicResponseHandler().handleResponse(response);
        return JSONObject.fromObject(responseString);
    }

    private JSONObject getDocument(String id) throws IOException {
        return getJSON(COUCH_URL + dbname + "/" + id);
    }

    private void createDocument(JSONObject doc) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = COUCH_URL + dbname;
        HttpPost post = new HttpPost(url);
        HttpEntity entity = new StringEntity(doc.toString());
        post.setEntity(entity);
        post.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        HttpResponse response = httpclient.execute(post);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) return;
        String responseString = new BasicResponseHandler().handleResponse(response);
        JSONObject result = JSONObject.fromObject(responseString);
        doc.put(COUCH_ID_FIELD, result.get(COUCH_ID_FIELD));
    }

    private void updateDocument(JSONObject doc, String id) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = COUCH_URL + dbname + "/" + id;
        HttpPut put = new HttpPut(url);
        HttpEntity entity = new StringEntity(doc.toString());
        put.setEntity(entity);
        put.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        HttpResponse response = httpclient.execute(put);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException(POST_FAILED + response.getStatusLine());
        }
    }

    void createView() throws IOException {
        JSONObject doc = new JSONObject();
        doc.put(COUCH_ID_FIELD, DESIGN);
        String str = AUTOINC_VIEW + "," + USERS_VIEW;
        doc.put(COUCH_VIEWS, str);
        createDocument(doc);
    }

    private void createDatabase() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = COUCH_URL + dbname + "/";
        httpclient.execute(new HttpPut(url));
        createView();
    }

}
