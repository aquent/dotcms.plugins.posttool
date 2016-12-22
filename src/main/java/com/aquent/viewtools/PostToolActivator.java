package com.aquent.viewtools;

import com.dotcms.repackage.org.osgi.framework.BundleContext;
import com.dotmarketing.osgi.GenericBundleActivator;

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
        
        //Registering the ViewTool service
        registerViewToolService(bundleContext, new PostToolInfo());
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        unregisterViewToolServices();
    }

}
