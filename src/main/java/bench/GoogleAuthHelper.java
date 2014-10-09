/*
 * Copyright (c) 2014 Thumbtack Technologies
 */

package bench;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;

public final class GoogleAuthHelper {

    public static final String HTTP = "http://";
    private static final Collection<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email".split(";"));
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static String CALLBACK;
    public static String CLIENT_ID;
    public static String CLIENT_SECRET;
    public static String host = "localhost:8080"; // for jUnit
    private final GoogleAuthorizationCodeFlow flow;
    private String stateToken;

    /**
     * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE
     */
    public GoogleAuthHelper() {
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).build();
        generateStateToken();
    }

    /**
     * Callback URI that google will redirect to after successful authentication
     */
    private String callbackUri() {
        return HTTP + host + CALLBACK;
    }

    /**
     * Builds a login URL based on client ID, secret, callback URI, and scope
     */
    public String buildLoginUrl() {
        final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        return url.setRedirectUri(callbackUri()).setState(stateToken).build();
    }

    /**
     * Generates a secure state token
     */
    private void generateStateToken() {
        SecureRandom sr1 = new SecureRandom();
        stateToken = "google;" + sr1.nextInt();
    }

    /**
     * Accessor for state token
     */
    public String getStateToken() {
        return stateToken;
    }

    /**
     * Expects an Authentication Code, and makes an authenticated request for the user's profile information
     *
     * @param authCode authentication code provided by google
     * @return JSON formatted user profile information
     */
    public String getUserInfoJson(final String authCode) throws IOException {
        final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(callbackUri()).execute();
        final Credential credential = flow.createAndStoreCredential(response, null);
        final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
        // Make an authenticated request
        final GenericUrl url = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        return request.execute().parseAsString();
    }
}
