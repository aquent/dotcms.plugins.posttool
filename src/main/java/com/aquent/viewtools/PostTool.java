package com.aquent.viewtools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

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
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	 
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(params);
			wr.flush();
			wr.close();
	 
			int responseCode = con.getResponseCode();
	 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			return new PostToolResponse(responseCode, response.toString());
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
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
	 
			int responseCode = con.getResponseCode();
	 
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			return new PostToolResponse(responseCode, response.toString());
		}
		
		return new PostToolResponse(999, null);
	}

}
