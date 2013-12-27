package com.aquent.viewtools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.ParameterParser;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.velocity.tools.view.tools.ViewTool;
import org.omg.DynamicAny.NameValuePairSeqHelper;

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
	
	private final String USER_AGENT = "Mozilla/5.0";
	
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
	 * Send a Post request and returns a PostToolReponse Object
	 * 
	 * @param url		The url to post to
	 * @param params	A Map of the parameters
	 * @return			The PostToolResponse Object 
	 * @throws Exception
	 */
	public PostToolResponse sendPost(String url, Map<String, String> params) throws Exception {
		StringBuilder bob = new StringBuilder();
		for(String key : params.keySet()) {
			bob.append(key+"="+params.get(key)+"&");
		}
		
		String p = bob.toString();
		p = p.substring(0, p.length() - 1);
		
		return sendPost(url, p);
	}
	
	/**
	 * Send a Post request and returns a PostToolReponse Object
	 * 
	 * @param url		The url to post to
	 * @param params	A string of the params (do not start with ?)
	 * @return			The PostToolResponse
	 * @throws Exception
	 */
	public PostToolResponse sendPost(String url, String params) throws Exception {
		if(inited) {
			
			PostMethod method = null;
			
	        try {
	            HttpClient client = new HttpClient();
	            method = new PostMethod(url);
	            method.setQueryString(params);
	            client.executeMethod(method);
	            return new PostToolResponse(method.getStatusCode(), method.getResponseBodyAsString());
	        } catch (Exception e) {
	            Logger.error(this, "Exception posting to url: "+url, e);
	            return new PostToolResponse(888, null);
	        } finally {
	            if(method != null) {
	                method.releaseConnection();
	            }
	        }
	        
		}
		
		return new PostToolResponse(999, null);
	}
	
	/**
	 * Makes a get request to a url
	 * 
	 * @param url		The url
	 * @param params	A Map of the parameters
	 * @return			A PostToolResponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendGet(String url, Map<String, String> params) throws Exception {
		StringBuilder bob = new StringBuilder();
		for(String key : params.keySet()) {
			bob.append(key+"="+params.get(key)+"&");
		}
		
		String p = bob.toString();
		p = p.substring(0, p.length() - 1);
		
		return sendGet(url+"?"+p);
	}
	
	/**
	 * Makes a get request to a url
	 * 
	 * @param url		The url
	 * @return			A PostToolReponse Object
	 * @throws Exception
	 */
	public PostToolResponse sendGet(String url) throws Exception {
		if(inited) {
			GetMethod method = null;
	        
	        try {
	            HttpClient client = new HttpClient();
	            method = new GetMethod(url);
	            client.executeMethod(method);
	            return new PostToolResponse(method.getStatusCode(), method.getResponseBodyAsString());
	        } catch (Exception e) {
	            Logger.error(this, "Exception getting url: "+url, e);
	            return new PostToolResponse(888, null);
	        } finally {
	            if(method != null) {
	                method.releaseConnection();
	            }
	        }
	 
		}
		
		return new PostToolResponse(999, null);
	}

}
