package com.kony.task.datavalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.ConsentsManager;
import com.kony.memorymgmt.SessionMap;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class UpdatePSDConsentServiceValidation implements ObjectProcessorTask{
	private static final Logger LOG = LogManager.getLogger(UpdatePSDConsentServiceValidation.class);

	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		// TODO Auto-generated method stub
		ConsentsManager consentsManager = new ConsentsManager(fabricRequestManager, fabricResponseManager);
		SessionMap PSDConsentId = consentsManager.getPSDConsentFromSession(HelperMethods.getCustomerIdFromSession(fabricRequestManager));
		Map<String, String> PSDArrangementId = PSDConsentId.getValue("PSD_CONSENTS");
		if (null == PSDConsentId || PSDConsentId.isEmpty()) {
            LOG.debug("CDPConsentId is Null / Empty");
            return false;
        }
        LOG.debug("CDPConsentId: " + PSDConsentId.toString());
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			if (HelperMethods.isMFAVerify(requestPayload)) {
				return true;
			}
			
			if(PSDArrangementId.containsKey(requestPayload.get("arrangementId").getAsString())) {
				requestPayload.addProperty("arrangementId", PSDConsentId.getAttributeValueForKey("PSD_CONSENTS", requestPayload.get("arrangementId").getAsString()));
	        	fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
			}else {
				JsonObject resPayload = null;
	        	resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
				fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
				return false;
			}
			
			
			if (!HelperMethods.isDACEnabled()) {
				LOG.debug("data access control is disabled");
				return true;
			}
			
			String permission;
			List<String> featureActionIdList = new ArrayList<>();
	        featureActionIdList.add("PSD2_TPP_CONSENT_REVOKE");
	        permission =HelperMethods.getPermittedUserActionIds(fabricRequestManager,featureActionIdList);
	        if(permission == null) {
	        	JsonObject resPayload = null;
	        	resPayload = ErrorCodeEnum.ERR_12001.setErrorCode(resPayload);
				fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
				return false;
	        }
	    }
		return true;
    }
}
