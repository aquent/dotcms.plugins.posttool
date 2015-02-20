package com.aquent.viewtools;

import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.tools.view.tools.ViewTool;
import com.dotmarketing.util.Logger;

/**
 * 
 * @author Aquent, LLC. (cfalzone@aquent.com)
 *
 * This viewtool provides a method to make a simple post request and get the value
 * 
 */
public class PostTool implements ViewTool {

	private boolean inited = false;
		
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
	 * Sends a request to a url
	 * 
	 * @param url - The URL
	 * @param params - The Query String
	 * @param method - The Method (POST or GET)
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse send(String url, Map<String, String> params, String method) throws Exception {
		
		Logger.debug(this, "send(String) called with url="+url+", params="+params+", and method="+method);
		
		if(inited) {
			
			NameValuePair[] query;
			if(params.size() > 0) query = new NameValuePair[params.size()];
			else query = null;
			
			int i = 0;
			for(String key : params.keySet()) {
				query[i] = new NameValuePair(key, params.get(key));
				i++;
			}
			HttpMethod m = null;
			
	        try {
	            HttpClient client = new HttpClient();
	            
	            // Encoding for UTF-8
	            client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
				client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
				
				if(method.equalsIgnoreCase("POST")) m = new PostMethod(url);
				else m = new GetMethod(url);
				
				// Make sure we follow redirects
				m.getParams().setParameter("http.protocol.handle-redirects",true);
				
	            if(query != null && query.length > 0) m.setQueryString(query);
	            
	            client.executeMethod(m);
	            return new PostToolResponse(m.getStatusCode(), m.getResponseBodyAsString());
	        } catch (Exception e) {
	            Logger.error(this, "Exception posting to url: "+url, e);
	            return new PostToolResponse(888, null);
	        } finally {
	            if(method != null) {
	                m.releaseConnection();
	            }
	        }
	        
		}
		
		return new PostToolResponse(999, null);
	}
	
	/**
	 * Sends a request to a url
	 * 
	 * @param url - The URL
	 * @param params - The Query String
	 * @param method - The Method (POST or GET)
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse send(String url, String params, String method) throws Exception {
		
		Logger.debug(this, "send(String) called with url="+url+", params="+params+", and method="+method);
				
		Map<String, String> queryPairs = new LinkedHashMap<String, String>();
	    String[] pairs = params.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        queryPairs.put(
	        		URLDecoder.decode(pair.substring(0, idx), "UTF-8"), 
	        		URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    }
	    
	    return send(url, queryPairs, method);
	
	}
	
	/**
	 * Sends a get request to a url
	 * 
	 * @param url - The URL
	 * @param params - The Query String
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse send(String url, String params) throws Exception {
		return send(url, params, "GET");
	}
	
	/**
	 * Sends a get request to a url
	 * 
	 * @param url - The URL
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse send(String url) throws Exception {
		String params = "";
		if(url.contains("?")) {
			int idx = url.indexOf("?");
			params = url.substring(idx + 1);
			url = url.substring(0, idx);
		}
		return send(url, params, "GET");
	}
	
	
	
	/**
	 * Sends a get request to a url
	 * 
	 * @param url - The URL
	 * @param params - The Query String
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendGet(String url, String params) throws Exception {
		return send(url, params, "GET");
	}
	
	/**
	 * Sends a get request to a url
	 * 
	 * @param url - The URL
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendGet(String url) throws Exception {
		String params = "";
		if(url.contains("?")) {
			int idx = url.indexOf("?");
			params = url.substring(idx + 1);
			url = url.substring(0, idx);
		}
		return send(url, params, "GET");
	}
	
	/**
	 * Sends a get request to a url
	 * 
	 * @param url - The URL
	 * @param params - The Query String
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendGet(String url, Map<String, String> params) throws Exception {
		return send(url, params, "GET");
	}
	
	/**
	 * Sends a post request to a url
	 * 
	 * @param url - The URL
	 * @param params - The Query String
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendPost(String url, String params) throws Exception {
		return send(url, params, "POST");
	}
	
	/**
	 * Sends a post request to a url
	 * 
	 * @param url - The URL
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendPost(String url) throws Exception {
		String params = "";
		if(url.contains("?")) {
			int idx = url.indexOf("?");
			params = url.substring(idx + 1);
			url = url.substring(0, idx);
		}
		return send(url, params, "POST");
	}
	
	/**
	 * Sends a post request to a url
	 * 
	 * @param url - The URL
	 * @param params - The Query String
	 * @return A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendPost(String url, Map<String, String> params) throws Exception {
		return send(url, params, "POST");
	}

}
