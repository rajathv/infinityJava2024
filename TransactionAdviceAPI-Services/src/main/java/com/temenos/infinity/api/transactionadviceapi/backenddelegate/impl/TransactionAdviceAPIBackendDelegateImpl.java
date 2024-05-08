package com.temenos.infinity.api.transactionadviceapi.backenddelegate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.infinity.api.commons.constants.FabricConstants;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.api.commons.invocation.Executor;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.transactionadviceapi.backenddelegate.api.TransactionAdviceAPIBackendDelegate;
import com.temenos.infinity.api.transactionadviceapi.config.ServerConfigurations;
import com.temenos.infinity.api.transactionadviceapi.config.TransactionAdviceAPIServices;
import com.temenos.infinity.api.transactionadviceapi.constants.ErrorCodeEnum;
import com.temenos.infinity.api.transactionadviceapi.dto.AutoFormCookie;
import com.temenos.infinity.api.transactionadviceapi.dto.AutoFormDownload;

public class TransactionAdviceAPIBackendDelegateImpl implements TransactionAdviceAPIBackendDelegate {
    private static final Logger LOG = LogManager.getLogger(TransactionAdviceAPIBackendDelegateImpl.class);

    @Override
    public AutoFormCookie login(String auth_token) {

        AutoFormCookie outputDto = new AutoFormCookie();
        try {
            Map<String, Object> inputMap = new HashMap<>();
            Map<String, Object> headerMap = new HashMap<>();
            inputMap.put("username", ServerConfigurations.AUTOFORM_USERNAME.getValue());
			inputMap.put("password", ServerConfigurations.AUTOFORM_PASSWORD.getValue());
            headerMap.put("X-XSRF-TOKEN", "");
            if (!StringUtils.isBlank(auth_token)) {
                LOG.debug("Using Auth Token from Param-Login");
                headerMap.put(FabricConstants.X_KONY_AUTHORIZATION_HEADER, auth_token);
            }
            String serviceResponse =
                    Executor.invokeService(TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_LOGIN, inputMap, headerMap);
            JSONObject serviceResponseJSON = Utilities.convertStringToJSON(serviceResponse);
            if (serviceResponseJSON == null)
                LOG.debug("failed");
            else {
                outputDto.setXsrftoken(serviceResponseJSON.getString("DM-XSRF-TOKEN"));
                outputDto.setJSESSIONID(serviceResponseJSON.getString("JSESSIONID"));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return outputDto;
    }

    @Override
    public byte[] download(String documentId, String revision, String xsrf, String jsessionid, String auth_token) {
        byte[] serviceResponse = null;
        try {
            Map<String, Object> inputMap = new HashMap<>();
            Map<String, Object> headerMap = new HashMap<>();
            inputMap.put("documentId", documentId);
            inputMap.put("revision", revision);
            headerMap.put("X-XSRF-TOKEN", xsrf);
            String cookie = "DM-XSRF-TOKEN=" + xsrf + "; " + "JSESSIONID=" + jsessionid;
            headerMap.put("COOKIE", cookie);
            if (!StringUtils.isBlank(auth_token)) {
                LOG.debug("Using Auth Token from Param-Download");
                headerMap.put(FabricConstants.X_KONY_AUTHORIZATION_HEADER, auth_token);
            }
            LOG.debug("Header=" + headerMap.toString());
            serviceResponse = Executor.invokePassThroughServiceAndGetBytes(
                    TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_DOWNLOAD, inputMap, headerMap);
            LOG.debug("ServiceResponse=" + serviceResponse);
            if (serviceResponse == null)
                LOG.error("failed");

        } catch (Exception e) {
            LOG.error(e);
        }
        return serviceResponse;

    }

    @Override
    public AutoFormDownload search(String cuk, String xsrf, String jsessionid, String auth_token)
            throws ApplicationException {
        // TODO Auto-generated method stub
        AutoFormDownload outputDto = new AutoFormDownload();
        try {
            Map<String, Object> inputMap = new HashMap<>();
            Map<String, Object> headerMap = new HashMap<>();
            String serviceResponse = new String();
            inputMap.put("cuk", cuk);
            headerMap.put("X-XSRF-TOKEN", xsrf);
            String cookie = "DM-XSRF-TOKEN=" + xsrf + "; " + "JSESSIONID=" + jsessionid;
            headerMap.put("COOKIE", cookie);
            if (!StringUtils.isBlank(auth_token)) {
                LOG.debug("Using Auth Token from Param-Search");
                headerMap.put(FabricConstants.X_KONY_AUTHORIZATION_HEADER, auth_token);
            }
            LOG.debug("SearchHeader=" + headerMap.toString());
            serviceResponse = Executor.invokePassThroughServiceAndGetString(
                    TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_SEARCH, inputMap, headerMap);
            LOG.debug("After call");
            LOG.debug("SearchServiceResponse=" + serviceResponse);
            if (serviceResponse == null || serviceResponse.length() == 0) {
                LOG.error("failed");
                LOG.debug("EmptyResponse");
                throw new ApplicationException(ErrorCodeEnum.ERR_20041);
            } else {
                LOG.debug("SearchServiceResponse=" + serviceResponse.substring(0, 10));
                JSONArray responseArray = new JSONArray(serviceResponse);
                int size = responseArray.length();
                while (size > 0) {
                    JSONObject obj = responseArray.getJSONObject(size - 1);
                    if (obj.has("fileProperties")) {
                        JSONObject properties = obj.getJSONObject("properties");
                        outputDto.setDocumentId(properties.get("id").toString());
                        outputDto.setRevision(properties.get("revision").toString());
                        return outputDto;
                    } else
                        size--;
                }
                throw new ApplicationException(ErrorCodeEnum.ERR_20041);
            }

        } catch (Exception e) {
            LOG.error(e);
            throw new ApplicationException(ErrorCodeEnum.ERR_20041);
        }
    }
}
