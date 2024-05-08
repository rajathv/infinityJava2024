package com.dbp.campaigndeliveryengine.kmsapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dbp.campaigndeliveryengine.dtoclasses.CommunicationNodeDTO;
import com.dbp.campaigndeliveryengine.dtoclasses.EventDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.dbp.campaigndeliveryengine.utils.CampaignDeliveryEngineConstants;
import com.dbp.campaigndeliveryengine.utils.CampaignDeliveryEngineUtils;

public class SendSMS {
	private SendSMS() {

	}

	private static final Logger logger = LogManager.getLogger(SendSMS.class);

	public static JsonObject sendSMS(EventDTO event, CommunicationNodeDTO commdata) throws Exception {
		JsonObject res = new JsonObject();
		Map<String, Object> inputparams = buildPayload(event, commdata);
		JsonObject logparams = CampaignDeliveryEngineUtils.buildlogparams();
		inputparams.put("logparams", logparams);
		if (inputparams.isEmpty())
			return res;
		try {
			res = new JsonParser()
					.parse(ResultToJSON.convert(CampaignDeliveryEngineUtils.callInternalService(inputparams,
							CampaignDeliveryEngineConstants.KMSINVOKESERVICE,
							CampaignDeliveryEngineConstants.SENDSMSOPERATION, null)))
					.getAsJsonObject();

		} catch (Exception e) {
			logger.debug("Exception occured in submitting mail", e);
		}
		logger.debug("SMS Response:" + res);
		return res;
	}

	private static Map<String, Object> buildPayload(EventDTO event, CommunicationNodeDTO commdata) {
		Map<String, Object> inputparams = new HashMap<>();
		JsonObject inputObj = new JsonObject();
		try {
			JsonArray messagearray = new JsonArray();
			JsonObject smsservicerequest = new JsonObject();
			String text = "";
			text = event.getSmstext();
			JsonObject message = new JsonObject();
			JsonObject recipients = new JsonObject();
			JsonObject recipientTo = new JsonObject();
			JsonObject messages = new JsonObject();
			recipientTo.addProperty(CampaignDeliveryEngineConstants.MOBILE, commdata.getMobile());
			recipients.add(CampaignDeliveryEngineConstants.RECIPIENT, recipientTo);
			message.add(CampaignDeliveryEngineConstants.RECIPIENTS, recipients);
			message.addProperty(CampaignDeliveryEngineConstants.STARTTIMESTAMP, "0");
			message.addProperty(CampaignDeliveryEngineConstants.EXPIRYTIMESTAMP, "0");
			message.addProperty(CampaignDeliveryEngineConstants.PRIORITYSERVICE, "true");
			message.addProperty(CampaignDeliveryEngineConstants.CONTENT, (text));
			messages.add(CampaignDeliveryEngineConstants.MESSAGE, message);

			smsservicerequest.add(CampaignDeliveryEngineConstants.MESSAGES, messages);
			messagearray.add(messages);

			inputObj.add(CampaignDeliveryEngineConstants.SMSSERVICEREQUEST, smsservicerequest);
			inputparams.put(CampaignDeliveryEngineConstants.INPUTPARAMS, inputObj);
		} catch (Exception e) {
			logger.debug("Error occured in building payload");
		}
		return inputparams;
	}

}
