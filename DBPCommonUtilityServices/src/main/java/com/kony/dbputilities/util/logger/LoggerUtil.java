package com.kony.dbputilities.util.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.URLConstants;

public class LoggerUtil {

    private boolean isDebugModeEnabled = false;

    private Logger log;

    public LoggerUtil(Class<?> className) {
        isDebugModeEnabled = Boolean.parseBoolean(EnvironmentConfigurationsHandler.getValue(URLConstants.IS_DEBUG_MODE_ENABLED));
        log = LogManager.getLogger(className);
    }
    public void debug(String message) {
        isDebugModeEnabled = Boolean.parseBoolean(EnvironmentConfigurationsHandler.getValue(URLConstants.IS_DEBUG_MODE_ENABLED));
        if(isDebugModeEnabled) {
            log.error("Class: "+ log.getClass()+"\t Line no "+ new Exception().getStackTrace()[1].getLineNumber()+" \t "+message);
        }
    }

    public void error(String message) {
        log.error("Class: "+ log.getClass()+"\t Line no "+ new Exception().getStackTrace()[1].getLineNumber()+" \t "+message);
    }

    public void error(String string, Exception e) {
        log.error("Class: "+ log.getClass()+"\t Line no "+ new Exception().getStackTrace()[1].getLineNumber()+" \t "+string,e);
    }

    /**
     * @return the isDebugModeEnabled
     */
    public boolean isDebugModeEnabled() {
        return isDebugModeEnabled;
    }

}
