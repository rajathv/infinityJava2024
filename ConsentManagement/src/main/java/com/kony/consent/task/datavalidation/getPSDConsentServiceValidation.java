package com.kony.consent.task.datavalidation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class getPSDConsentServiceValidation implements ObjectProcessorTask{
	private static final Logger LOG = LogManager.getLogger(UpdatePSDConsentServiceValidation.class);

	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		// TODO Auto-generated method stub
		String permission;
		List<String> featureActionIdList = new ArrayList<>();
        featureActionIdList.add("PSD2_TPP_CONSENT_VIEW");
        String PaymentBackend= EnvironmentConfigurationsHandler.getValue("PAYMENT_BACKEND");
		if("Mock".equalsIgnoreCase(PaymentBackend)) {
			return true;
		}
        permission =HelperMethods.getPermittedUserActionIds(fabricRequestManager,featureActionIdList);
        if(permission == null) {
        	JsonObject resPayload = null;
        	resPayload = ErrorCodeEnum.ERR_12001.setErrorCode(resPayload);
			fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
			return false;
        }
		return true;
    }
}
