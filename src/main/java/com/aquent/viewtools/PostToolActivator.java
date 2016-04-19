package com.aquent.viewtools;

import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

import com.dotcms.repackage.org.osgi.framework.BundleContext;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Logger;

/**
 * Activator Class for the PostTool Plugin.
 *
 * @author cfalzone
 */
public class PostToolActivator extends GenericBundleActivator {

    @Override
    public void start(BundleContext bundleContext) throws Exception {

        //Initializing services...
        initializeServices(bundleContext);

        // Because OSGI doesn't know to import this class unless I do this:
        Socket s = SSLSocketFactory.getDefault().createSocket();
        Logger.debug(this, "Creted Socket: " + s);

        //Registering the ViewTool service
        registerViewToolService(bundleContext, new PostToolInfo());
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        unregisterViewToolServices();
    }

}
