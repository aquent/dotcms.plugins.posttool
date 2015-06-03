package com.aquent.viewtools;

import org.apache.commons.httpclient.HttpClient;

import com.dotcms.repackage.org.osgi.framework.BundleContext;
import com.dotmarketing.osgi.GenericBundleActivator;

public class PostToolActivator extends GenericBundleActivator {

    @Override
    public void start ( BundleContext bundleContext ) throws Exception {

        //Initializing services...
        initializeServices( bundleContext );
        
        HttpClient client = new HttpClient();

        //Registering the ViewTool service
        registerViewToolService( bundleContext, new PostToolInfo() );
    }

    @Override
    public void stop ( BundleContext bundleContext ) throws Exception {
        unregisterViewToolServices();
    }

}
