package com.kony.task.datavalidation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.URLFinder;
import com.kony.memorymgmt.CardsManager;
import com.kony.utilities.Constants;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class ActivateCardAddTaskValidation implements ObjectProcessorTask {
		private static final Logger LOG = LogManager.getLogger(ActivateCardAddTaskValidation.class);

		@Override
		public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
				throws Exception {
	if (!HelperMethods.isDACEnabled()) {
		LOG.debug("data access control is disabled");
		return true;
	}
	JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
	if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
		JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
	
		CardsManager cardManager = new CardsManager(fabricRequestManager, fabricResponseManager);
		String cardType=HelperMethods.getCardType();
		if(cardType=="Debit")
		{
			requestPayload.addProperty("type", Constants.ManageDebitCard);
			requestPayload.addProperty("subtype", Constants.ActivateDebitCard);
		}
		else
		{
			requestPayload.addProperty("type", Constants.ManageCreditCard);
			requestPayload.addProperty("subtype", Constants.ActivateCreditCard);
		}
		
		fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
	}
	return true;
}
}


