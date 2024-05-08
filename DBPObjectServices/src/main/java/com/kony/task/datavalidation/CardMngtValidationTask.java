package com.kony.task.datavalidation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.URLFinder;
import com.kony.memorymgmt.CardsManager;
import com.kony.utilities.Constants;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class CardMngtValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(CardMngtValidationTask.class);

	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if (!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		CardsManager cardManager = new CardsManager(fabricRequestManager, fabricResponseManager);
		String cardType=HelperMethods.getCardType();
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			String requestBody=requestPayload.get("requestBody").getAsString();
			JSONObject jsonObject = new JSONObject(requestBody);
			String action=jsonObject.get("Action").toString();
			String updateCardSubType="";
			if ("Activate".equals(action)) {
				updateCardSubType =Constants.UnlockCard;
	    	 } else if ("Deactivate".equals(action)) {
	    		 updateCardSubType = Constants.LockCard;
	        } else if ("Cancel".equals(action)) {
	        	updateCardSubType = Constants.CancelCreditCard;
	        } else if ("Report Lost".equals(action)) {
	        	updateCardSubType = Constants.ReportLost;
	        } else if ("Replace".equals(action)) {
	        	updateCardSubType =Constants.ReplaceCard;
	        } else if ("Lock".equals(action)) {
	        	updateCardSubType =Constants.UnlockCard;
	        } else if ("Cancel Request".equals(action)) {
	        	updateCardSubType =Constants.CancelCreditCard;
	        } else if ("Replace Request".equals(action)) {
	        	updateCardSubType =Constants.ReplaceCard;
	        } else if ("PinChange".equals(action)) {
	        	updateCardSubType =Constants.ChangePIN;
	        } 
			
			if(cardType=="Debit")
			{
				requestPayload.addProperty("type", Constants.ManageDebitCard);
			}
			else
			{
				requestPayload.addProperty("type", Constants.ManageCreditCard);
			}
			requestPayload.addProperty("subtype", updateCardSubType);
			
			fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
		}
		return true;
	}
}
