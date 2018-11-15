package com.aquent.viewtools;

import com.dotmarketing.loggers.Log4jUtil;
import com.dotmarketing.osgi.GenericBundleActivator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.osgi.framework.BundleContext;

/**
 * Activator Class for the PostTool Plugin.
 *
 * @author cfalzone
 */
public class PostToolActivator extends GenericBundleActivator {
    private LoggerContext pluginLoggerContext;
  
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        // Setup Logger
        LoggerContext dotcmsLoggerContext = Log4jUtil.getLoggerContext();
        pluginLoggerContext = (LoggerContext) LogManager.getContext(this.getClass().getClassLoader(),
                                                                    false, dotcmsLoggerContext, dotcmsLoggerContext.getConfigLocation());

        //Initializing services...
        initializeServices(bundleContext);

        //Registering the ViewTool service
        registerViewToolService(bundleContext, new PostToolInfo());
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        unregisterViewToolServices();
        unpublishBundleServices();
        unregisterServices(bundleContext);
        Log4jUtil.shutdown(pluginLoggerContext);
    }

}
