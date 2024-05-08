package com.kony.AdminConsole.PreAndPostProcessor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.AdminConsole.Utilities.ServiceConfig;
import com.kony.AdminConsole.Utilities.URLConstants;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AdminConsole_PreLogin implements DataPreProcessor2 {

    private static final String AUTH_TOKEN_PARAM_KEY = "Auth_Token";
    private static final String AUTH_TOKEN_UPDATED_TIME_KEY = "TokenUpdatedTime";

    private static final Logger LOG = LogManager.getLogger(AdminConsole_PreLogin.class);

    private static final SimpleDateFormat IDENTITY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

    @SuppressWarnings("rawtypes")
    @Override
    public boolean execute(HashMap inputArgs, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance, Result result) throws Exception {

        try {
        	Calendar currentTimeStamp = Calendar.getInstance();
            if (ServiceConfig.getValue(AUTH_TOKEN_PARAM_KEY).equals("null")
                    || ServiceConfig.getValue(AUTH_TOKEN_UPDATED_TIME_KEY).equals("null")) {
                ServiceConfig.setValue(AUTH_TOKEN_UPDATED_TIME_KEY,
                        String.valueOf(IDENTITY_DATE_FORMAT.format(currentTimeStamp.getTime())));
                String backendToken = AdminUtil.getAdminToken();
                requestInstance.getHeaderMap().put("backendToken", backendToken);
                ServiceConfig.setValue("Auth_Token", backendToken);
            } else {
                Date parsedTime = IDENTITY_DATE_FORMAT.parse(ServiceConfig.getValue(AUTH_TOKEN_UPDATED_TIME_KEY));
                Calendar TokenUpdatedTime = Calendar.getInstance();
                TokenUpdatedTime.setTime(parsedTime);
                currentTimeStamp.add(Calendar.MINUTE, (-1) * Integer
                        .parseInt(ServiceConfig.getValueFromRunTime(URLConstants.TIME_OUT_IN_MINS, requestInstance)));
                if (TokenUpdatedTime.before(currentTimeStamp)) {
                	String backendToken = AdminUtil.getAdminToken();
                    requestInstance.getHeaderMap().put("backendToken", backendToken);
                    ServiceConfig.setValue(AUTH_TOKEN_PARAM_KEY, backendToken);
                    ServiceConfig.setValue(AUTH_TOKEN_UPDATED_TIME_KEY,
                            String.valueOf(IDENTITY_DATE_FORMAT.format(Calendar.getInstance().getTime())));
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("Unhandled Exception. Returning false from pre-processor", e);
            ErrorCodeEnum.ERR_12000.setErrorCode(result);
        }
        return false;
    }
}
