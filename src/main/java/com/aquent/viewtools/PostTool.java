package com.aquent.viewtools;

import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * This viewtool provides a method to make a simple post request and get the value.
 * @author Aquent, LLC. (cfalzone@aquent.com)
 */
public class PostTool implements ViewTool {

  private boolean inited = false;

  private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

  public static final int ERR_CODE_UNKNOWN_ERR = 888;
  public static final int ERR_CODE_NOT_INTED = 999;
  public static final int ERR_CODE_UNIMPLEMENTED_METHOD = 777;

  public static final String METHOD_PUT = "PUT";
  public static final String METHOD_POST = "POST";
  public static final String METHOD_GET = "GET";
  public static final String METHOD_HEAD = "HEAD";
  public static final String METHOD_DELETE = "DELETE";

  /**
   * Sets up the viewtool.  This viewtool should be application scoped.
   */
  @Override
  public void init(Object initData) {
    Logger.info(this, "Post Tool Viewtool Starting Up");

    // A flag to let the viewtool know we are good to go
    inited = true;

    Logger.info(this, "Post Tool Viewtool Started");
  }

  /**
   * Get a credentials object to send to authenticated requests.
   *
   * @param user - The username
   * @param pass - The password
   * @return A Credentials object that can be used in authenticated requests.
   */
  public Credentials createCreds(String user, String pass) {
    return new UsernamePasswordCredentials(user, pass);
  }

  /**
   * This is used to send a single string payload to a url.
   * Can be used to send JSON or XML to a url.
   * Only Supports POST and PUT methods.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param method - The Method (POST/PUT)
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendStringData(String url, String data, String method, String contentType, Credentials creds) {
    Logger.debug(this, "sendStringData called with url=" + url + ", data=" + data + ", and method=" + method
        + ", contentType = " + contentType);

    StringEntity entity;
    try {
      entity = new StringEntity(data, ContentType.create(contentType, "UTF-8"));
    } catch (Exception e) {
      Logger.error(this, "Exception creating RequestEntity for: " + url, e);
      return new PostToolResponse(ERR_CODE_UNKNOWN_ERR, null);
    }
    if (inited) {
      CloseableHttpClient client;

      // Authentication if passed in
      if (creds != null) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, creds);
        client = HttpClients.custom()
            .setDefaultCredentialsProvider(credsProvider)
            .build();
      } else {
        client = HttpClients.createDefault();
      }

      if (method.equalsIgnoreCase(METHOD_POST)) {
        HttpPost m = new HttpPost(url);
        m.setEntity(entity);
        try {
          CloseableHttpResponse r = client.execute(m);
          return new PostToolResponse(r.getStatusLine().getStatusCode(), EntityUtils.toString(r.getEntity(), "UTF-8"));
        } catch (Exception e) {
          Logger.error(this, "Exception posting to url: " + url, e);
          return new PostToolResponse(ERR_CODE_UNKNOWN_ERR, null);
        } finally {
          if (method != null) {
            m.releaseConnection();
          }
        }
      } else if (method.equalsIgnoreCase(METHOD_PUT)) {
        HttpPut m = new HttpPut(url);
        m.setEntity(entity);
        try {
          CloseableHttpResponse r = client.execute(m);
          return new PostToolResponse(r.getStatusLine().getStatusCode(), EntityUtils.toString(r.getEntity(), "UTF-8"));
        } catch (Exception e) {
          Logger.error(this, "Exception posting to url: " + url, e);
          return new PostToolResponse(ERR_CODE_UNKNOWN_ERR, null);
        } finally {
          if (method != null) {
            m.releaseConnection();
          }
        }
      } else {
        Logger.error(this, "Unimplemented Method: " + method);
        return new PostToolResponse(ERR_CODE_UNIMPLEMENTED_METHOD, null);
      }
    }

    return new PostToolResponse(ERR_CODE_NOT_INTED, null);
  }

  /**
   * This is used to send a single string payload to a url.
   * Can be used to send JSON or XML to a url.
   * Only Supports POST and PUT methods.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param method - The Method (POST/PUT)
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendStringData(String url, String data, String method, String contentType) {
    return sendStringData(url, data, method, contentType, null);
  }

  /**
   * This is used to send a single string payload to a url.
   * Can be used to send JSON or XML to a url.
   * Only supports PUT and POST Methods.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param method - The Method (POST/PUT)
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendStringData(String url, String data, String method, Credentials creds) {
    return sendStringData(url, data, method, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * This is used to send a single string payload to a url.
   * Can be used to send JSON or XML to a url.
   * Only supports PUT and POST Methods.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param method - The Method (POST/PUT)
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendStringData(String url, String data, String method) {
    return sendStringData(url, data, method, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * This is used to post a single string payload to a url.
   * Can be used to send JSON or XML to a url.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendStringData(String url, String data, Credentials creds) {
    return sendStringData(url, data, METHOD_POST, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * This is used to post a single string payload to a url.
   * Can be used to send JSON or XML to a url.
   *
   * @param url - The URL
   * @param data - The String Data
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendStringData(String url, String data) {
    return sendStringData(url, data, METHOD_POST, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse send(String url, Map<String, String> params, String method, String contentType, Credentials creds) {
    Logger.debug(this, "send(Map) called with url=" + url + ", and method=" + method
        + ", contentType = " + contentType);

    if (inited) {
      try {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        StringBuilder urlParamsSB = new StringBuilder();
        String appender = "?";
        if (url.contains("?")) {
          appender = "&";
        }
        for (Entry<String, String> e : params.entrySet()) {
          data.add(new BasicNameValuePair(e.getKey(), e.getValue()));
          urlParamsSB.append(appender + e.getKey() + "=" + e.getValue());
          appender = "&";
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, Charset.forName("UTF-8"));


        CloseableHttpClient client;

        // Authentication if passed in
        if (creds != null) {
          CredentialsProvider credsProvider = new BasicCredentialsProvider();
          credsProvider.setCredentials(AuthScope.ANY, creds);
          client = HttpClients.custom()
              .setDefaultCredentialsProvider(credsProvider)
              .build();
        } else {
          client = HttpClients.createDefault();
        }

        if (method.equalsIgnoreCase(METHOD_POST)) {
          HttpPost m = new HttpPost(url);
          m.setEntity(entity);
          CloseableHttpResponse r = client.execute(m);
          return new PostToolResponse(r.getStatusLine().getStatusCode(), EntityUtils.toString(r.getEntity(), "UTF-8"));
        } else if (method.equalsIgnoreCase(METHOD_PUT)) {
          HttpPut m = new HttpPut(url);
          m.setEntity(entity);
          CloseableHttpResponse r = client.execute(m);
          return new PostToolResponse(r.getStatusLine().getStatusCode(), EntityUtils.toString(r.getEntity(), "UTF-8"));
        } else if (method.equalsIgnoreCase(METHOD_HEAD)) {
          HttpHead m = new HttpHead(url + urlParamsSB.toString());
          CloseableHttpResponse r = client.execute(m);
          return new PostToolResponse(r.getStatusLine().getStatusCode(), EntityUtils.toString(r.getEntity(), "UTF-8"));
        } else if (method.equalsIgnoreCase(METHOD_DELETE)) {
          HttpDelete m = new HttpDelete(url + urlParamsSB.toString());
          CloseableHttpResponse r = client.execute(m);
          return new PostToolResponse(r.getStatusLine().getStatusCode(), EntityUtils.toString(r.getEntity(), "UTF-8"));
        } else if (method.equalsIgnoreCase(METHOD_GET)) {
          HttpGet m = new HttpGet(url + urlParamsSB.toString());
          CloseableHttpResponse r = client.execute(m);
          return new PostToolResponse(r.getStatusLine().getStatusCode(), EntityUtils.toString(r.getEntity(), "UTF-8"));
        } else {
          Logger.error(this, "Unimplemented Method: " + method);
          return new PostToolResponse(ERR_CODE_UNIMPLEMENTED_METHOD, null);
        }

      } catch (Exception e) {
        Logger.error(this, "Exception posting to url: " + url, e);
        return new PostToolResponse(ERR_CODE_UNKNOWN_ERR, null);
      }
    }

    return new PostToolResponse(ERR_CODE_NOT_INTED, null);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse send(String url, Map<String, String> params, String method, String contentType) {
    return send(url, params, method, contentType, null);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from URLDecoder.decode
   */
  public PostToolResponse send(String url, String params, String method, String contentType, Credentials creds) throws Exception {
    Logger.debug(this, "send(String) called with url=" + url + ", and method=" + method
        + ", contentType = " + contentType);

    Map<String, String> queryPairs = new LinkedHashMap<String, String>();
    if (params.length() > 0) {
      String[] pairs = params.split("&");
      for (String pair : pairs) {
        int idx = pair.indexOf("=");
        queryPairs.put(
            URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
            URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
      }
    }
    return send(url, queryPairs, method, contentType, creds);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   * @throws Exception from URLDecoder.decode
   */
  public PostToolResponse send(String url, String params, String method, String contentType) throws Exception {
    return send(url, params, method, contentType, null);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse send(String url, Map<String, String> params, String method, Credentials creds) {
    return send(url, params, method, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @return A PostToolResponse Object
   */
  public PostToolResponse send(String url, Map<String, String> params, String method) {
    return send(url, params, method, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @return A PostToolResponse Object
   * @param creds - A credentials object for authenticated requests.
   * @throws Exception from send
   */
  public PostToolResponse send(String url, String params, String method, Credentials creds) throws Exception {
    return send(url, params, method, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param method - The Method (POST/GET/PUT/HEAD/DELETE)
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse send(String url, String params, String method) throws Exception {
    return send(url, params, method, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse send(String url, Map<String, String> params, Credentials creds) {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   */
  public PostToolResponse send(String url, Map<String, String> params) {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse send(String url, String params, Credentials creds) throws Exception {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse send(String url, String params) throws Exception {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse send(String url, Credentials creds) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse send(String url) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendGet(String url, String params, String contentType, Credentials creds) throws Exception {
    return send(url, params, METHOD_GET, contentType, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @param contentType - The content type for this request
   * @throws Exception from send
   */
  public PostToolResponse sendGet(String url, String params, String contentType) throws Exception {
    return send(url, params, METHOD_GET, contentType, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendGet(String url, Map<String, String> params, String contentType, Credentials creds) {
    return send(url, params, METHOD_GET, contentType, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendGet(String url, Map<String, String> params, String contentType) {
    return send(url, params, METHOD_GET, contentType, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendGet(String url, String params, Credentials creds) throws Exception {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendGet(String url, String params) throws Exception {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendGet(String url, Map<String, String> params, Credentials creds) {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendGet(String url, Map<String, String> params) {
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendGet(String url, Credentials creds) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a get request to a url.
   *
   * @param url - The URL
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendGet(String url) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_GET, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Post a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse postStringData(String url, String data, String contentType, Credentials creds) {
    return sendStringData(url, data, METHOD_POST, contentType, creds);
  }

  /**
   * Post a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse postStringData(String url, String data, String contentType) {
    return sendStringData(url, data, METHOD_POST, contentType, null);
  }

  /**
   * Post a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse postStringData(String url, String data, Credentials creds) {
    return sendStringData(url, data, METHOD_POST, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Post a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @return A PostToolResponse Object
   */
  public PostToolResponse postStringData(String url, String data) {
    return sendStringData(url, data, METHOD_POST, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url, String params, String contentType, Credentials creds) throws Exception {
    return send(url, params, METHOD_POST, contentType, creds);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url, String params, String contentType) throws Exception {
    return send(url, params, METHOD_POST, contentType, null);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendPost(String url, Map<String, String> params, String contentType, Credentials creds) {
    return send(url, params, METHOD_POST, contentType, creds);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendPost(String url, Map<String, String> params, String contentType) {
    return send(url, params, METHOD_POST, contentType, null);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url, String params, Credentials creds) throws Exception {
    return send(url, params, METHOD_POST, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url, String params) throws Exception {
    return send(url, params, METHOD_POST, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url, Credentials creds) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_POST, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_POST, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url, Map<String, String> params, Credentials creds) throws Exception {
    return send(url, params, METHOD_POST, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a post request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPost(String url, Map<String, String> params) throws Exception {
    return send(url, params, METHOD_POST, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Put a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse putStringData(String url, String data, String contentType, Credentials creds) {
    return sendStringData(url, data, METHOD_PUT, contentType, creds);
  }

  /**
   * Put a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse putStringData(String url, String data, String contentType) {
    return sendStringData(url, data, METHOD_PUT, contentType, null);
  }

  /**
   * Put a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse putStringData(String url, String data, Credentials creds) {
    return sendStringData(url, data, METHOD_PUT, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Put a Single String payload to a url.
   * Used to send json or xml data.
   *
   * @param url - The URL
   * @param data - The String Data
   * @return A PostToolResponse Object
   */
  public PostToolResponse putStringData(String url, String data) {
    return sendStringData(url, data, METHOD_PUT, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPut(String url, String params, String contentType, Credentials creds) throws Exception {
    return send(url, params, METHOD_PUT, contentType, creds);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @param contentType - The content type for this request
   * @throws Exception from send
   */
  public PostToolResponse sendPut(String url, String params, String contentType) throws Exception {
    return send(url, params, METHOD_PUT, contentType, null);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendPut(String url, Map<String, String> params, String contentType, Credentials creds) {
    return send(url, params, METHOD_PUT, contentType, creds);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendPut(String url, Map<String, String> params, String contentType) {
    return send(url, params, METHOD_PUT, contentType, null);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPut(String url, String params, Credentials creds) throws Exception {
    return send(url, params, METHOD_PUT, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPut(String url, String params) throws Exception {
    return send(url, params, METHOD_PUT, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendPut(String url, Map<String, String> params, Credentials creds) {
    return send(url, params, METHOD_PUT, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendPut(String url, Map<String, String> params) {
    return send(url, params, METHOD_PUT, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPut(String url, Credentials creds) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_PUT, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a put request to a url.
   *
   * @param url - The URL
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendPut(String url) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_PUT, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendHead(String url, String params, String contentType, Credentials creds) throws Exception {
    return send(url, params, METHOD_HEAD, contentType, creds);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @param contentType - The content type for this request
   * @throws Exception from send
   */
  public PostToolResponse sendHead(String url, String params, String contentType) throws Exception {
    return send(url, params, METHOD_HEAD, contentType, null);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendHead(String url, Map<String, String> params, String contentType, Credentials creds) {
    return send(url, params, METHOD_HEAD, contentType, creds);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendHead(String url, Map<String, String> params, String contentType) {
    return send(url, params, METHOD_HEAD, contentType, null);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendHead(String url, String params, Credentials creds) throws Exception {
    return send(url, params, METHOD_HEAD, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendHead(String url, String params) throws Exception {
    return send(url, params, METHOD_HEAD, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param creds - A credentials object for authenticated requests.
   * @param params - The Query String
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendHead(String url, Map<String, String> params, Credentials creds) {
    return send(url, params, METHOD_HEAD, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendHead(String url, Map<String, String> params) {
    return send(url, params, METHOD_HEAD, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendHead(String url, Credentials creds) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_HEAD, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a head request to a url.
   *
   * @param url - The URL
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendHead(String url) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_HEAD, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendDelete(String url, String params, String contentType, Credentials creds) throws Exception {
    return send(url, params, METHOD_DELETE, contentType, creds);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @param contentType - The content type for this request
   * @throws Exception from send
   */
  public PostToolResponse sendDelete(String url, String params, String contentType) throws Exception {
    return send(url, params, METHOD_DELETE, contentType, null);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendDelete(String url, Map<String, String> params, String contentType, Credentials creds) {
    return send(url, params, METHOD_DELETE, contentType, creds);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param contentType - The content type for this request
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendDelete(String url, Map<String, String> params, String contentType) {
    return send(url, params, METHOD_DELETE, contentType, null);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendDelete(String url, String params, Credentials creds) throws Exception {
    return send(url, params, METHOD_DELETE, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendDelete(String url, String params) throws Exception {
    return send(url, params, METHOD_DELETE, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendDelete(String url, Map<String, String> params, Credentials creds) {
    return send(url, params, METHOD_DELETE, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param params - The Query String
   * @return A PostToolResponse Object
   */
  public PostToolResponse sendDelete(String url, Map<String, String> params) {
    return send(url, params, METHOD_DELETE, DEFAULT_CONTENT_TYPE, null);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @param creds - A credentials object for authenticated requests.
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendDelete(String url, Credentials creds) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_DELETE, DEFAULT_CONTENT_TYPE, creds);
  }

  /**
   * Sends a delete request to a url.
   *
   * @param url - The URL
   * @return A PostToolResponse Object
   * @throws Exception from send
   */
  public PostToolResponse sendDelete(String url) throws Exception {
    String params = "";
    if (url.contains("?")) {
      int idx = url.indexOf("?");
      params = url.substring(idx + 1);
      url = url.substring(0, idx);
    }
    return send(url, params, METHOD_DELETE, DEFAULT_CONTENT_TYPE, null);
  }
}
