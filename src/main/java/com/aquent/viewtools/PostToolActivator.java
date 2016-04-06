package com.aquent.viewtools;

import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import com.dotcms.repackage.org.osgi.framework.BundleContext;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Logger;

public class PostToolActivator extends GenericBundleActivator {

    @Override
    public void start ( BundleContext bundleContext ) throws Exception {

        //Initializing services...
        initializeServices( bundleContext );
        
        // Because OSGI doesn't know to import this class unless I do this:
        Socket s = SSLSocketFactory.getDefault().createSocket();
        Logger.debug(this, "Creted Socket: "+s);

        //Registering the ViewTool service
        registerViewToolService( bundleContext, new PostToolInfo() );
        
        // Test Https
        Logger.info(this, "=====================================");
        Logger.info(this, "Post Tool Test");
        Logger.info(this, "=====================================");
        String contentType = "application/x-www-form-urlencoded";
        String url = "https://www.google.com/";
        Map<String, String> params = new LinkedHashMap<String, String>();
        PostToolResponse r;
        try {
            NameValuePair[] query;
            if (params.size() > 0) {
                query = new NameValuePair[params.size()];
            } else {
                query = null;
            }

            int i = 0;
            for (String key : params.keySet()) {
                query[i] = new NameValuePair(key, params.get(key));
                i++;
            }
            HttpMethod m = null;

            try {
                HttpClient client = new HttpClient();

                // Encoding for UTF-8
                client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
                client.getParams().setParameter("http.protocol.content-charset", "UTF-8");

                m = new GetMethod(url);
                
                // Make sure we follow redirects
                m.getParams().setParameter("http.protocol.handle-redirects", true);

                // Set Content Type
                m.addRequestHeader("ContentType", contentType);

                if (query != null && query.length > 0) {
                    m.setQueryString(query);
                }

                client.executeMethod(m);
                r = new PostToolResponse(m.getStatusCode(), m.getResponseBodyAsString());
            } catch (Exception e) {
                Logger.error(this, "Exception posting to url: " + url, e);
                r = new PostToolResponse(PostTool.ERR_CODE_UNKNOWN_ERR, null);
            } finally {
                if (m != null) {
                    m.releaseConnection();
                }
            }
            Logger.info(this, "Response Code: "+r.getResponseCode());
            Logger.info(this, "Response Message: \n"+r.getResponse());
        } catch (Exception e) {
            Logger.error(this, "Post Tool Test Failed with Excpeiton", e);
        }
        Logger.info(this, "=====================================");
    }

    @Override
    public void stop ( BundleContext bundleContext ) throws Exception {
        unregisterViewToolServices();
    }

}
