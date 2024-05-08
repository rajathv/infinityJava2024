package com.temenos.infinity.tradefinanceservices.config;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.nimbusds.jose.JWSAlgorithm;
import com.temenos.infinity.api.commons.invocation.Executor;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.transact.tokenmanager.dto.BackendCertificate;
import com.temenos.infinity.transact.tokenmanager.exception.CertificateNotRegisteredException;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class TradeFinanceUtils {
    
    private static final Logger LOG = LogManager.getLogger(TradeFinanceUtils.class);

    private static class Holder {

        private static TradeFinanceUtils instance = new TradeFinanceUtils();
    }

    public static TradeFinanceUtils getInstance() {
        return Holder.instance;
    }

    private TradeFinanceUtils() {

    }
    
    public String generateDMSToken(DataControllerRequest request) throws Exception {
        String authToken = "";
        try {
        	Map<String, String> inputparamMap = new HashMap<>();
			inputparamMap.put("customerId",getUserAttributeFromIdentity(request, "customer_id"));
			authToken = TokenUtils.getDMSAuthToken(inputparamMap);
			
        } catch (CertificateNotRegisteredException e) {
            LOG.error("Certificate Not Registered" + e);
        } catch (Exception e) {
            LOG.error("Exception occured during generation of authToken " + e);
        }
        return authToken;

    }
    
    /**
     * Get User Attributes from Identity Session
     * 
     * @param request
     * @param attributeName
     */ 
    public static String getUserAttributeFromIdentity(DataControllerRequest request, String attribute) {
        try {
            if (request.getServicesManager() != null && request.getServicesManager().getIdentityHandler() != null) {
                Map<String, Object> userMap = request.getServicesManager().getIdentityHandler().getUserAttributes();
                if (userMap.get(attribute) != null) {
                    String attributeValue = userMap.get(attribute) + "";

                    return attributeValue;
                }

            }
        } catch (Exception e) {
            LOG.error("Error occurred while fetching user attributes " + e);
        }

        return "";
    }
    
    /**
     * Get Backend Certificate from DB
     * 
     * @param backendName
     */    
    public static BackendCertificate getCertFromDB(String backendName) {
        BackendCertificate backendCertificate = new BackendCertificate();
        Map<String, Object> inputMap = new HashMap<>();
        StringBuffer queryString = new StringBuffer();
        queryString.append("BackendName" + " ");
        queryString.append("eq" + " ");
        queryString.append(backendName);

        inputMap.put("$filter", queryString.toString());
        String backendCertResponse = null;
        try {
            backendCertResponse =
                    Executor.invokeService(TradeFinanceAPIServices.SERVICE_BACKEND_CERTIFICATE, inputMap, null);
            LOG.debug("Backend Certificate" + backendCertResponse);
        } catch (Exception e1) {
            LOG.error("Service call to dbxdb failed "+e1.toString());
        }
        JSONObject serviceResponseJSON = Utilities.convertStringToJSON(backendCertResponse);
        if (serviceResponseJSON == null) {
            LOG.error("EmptyResponse no backend certificate for MS");
        }
        if (serviceResponseJSON.has("backendcertificate")
                && serviceResponseJSON.getJSONArray("backendcertificate").length() > 0) {
            JSONObject certificateObj = serviceResponseJSON.getJSONArray("backendcertificate").getJSONObject(0);
            backendCertificate.setBackendName(certificateObj.getString("BackendName"));
            String certificateEncryptionKey = new String();
            try {
                certificateEncryptionKey = TradeFinanceServiceConfigurations.T24_PRIVATE_ENCRYPTION_KEY.getValue();
            } catch (Exception e) {
                LOG.error("Couldnt parse MS_PRIVATE_ENCRYPTION_KEY from environment "+ e.toString());

            }

            if (StringUtils.isNotBlank(certificateEncryptionKey))
                backendCertificate.setCertificateEncryptionKey(certificateEncryptionKey);
            backendCertificate.setCertificatePrivateKey(certificateObj.getString("CertPrivateKey"));
            backendCertificate.setCertificatePublicKey(certificateObj.getString("CertPublicKey"));
            backendCertificate.setGetPublicKeyServiceURL("/sample/service");
            backendCertificate.setId(certificateObj.getString("id"));
            backendCertificate.setJwsAlgorithm(JWSAlgorithm.RS256);

        }

        return backendCertificate;

    }
    

}
