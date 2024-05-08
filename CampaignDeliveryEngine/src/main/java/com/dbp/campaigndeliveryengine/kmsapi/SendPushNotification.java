package com.dbp.campaigndeliveryengine.kmsapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dbp.campaigndeliveryengine.dtoclasses.EventDTO;
import com.dbp.campaigndeliveryengine.utils.CampaignDeliveryEngineConstants;
import com.dbp.campaigndeliveryengine.utils.CampaignDeliveryEngineUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class SendPushNotification {
	private SendPushNotification() {

	}

	private static final Logger logger = LogManager.getLogger(SendPushNotification.class);

	public static JsonObject sendPushNotification(EventDTO event) throws Exception {
		JsonObject res = new JsonObject();
		Map<String, Object> inputparams = buildPayload(event);
		JsonObject logparams = CampaignDeliveryEngineUtils.buildlogparams();
		inputparams.put("logparams", logparams);
		if (inputparams.isEmpty())
			return res;
		logger.debug("inputparams:"+inputparams);
		try {
			res = new JsonParser()
					.parse(ResultToJSON.convert(CampaignDeliveryEngineUtils.callInternalService(inputparams,
							CampaignDeliveryEngineConstants.KMSINVOKESERVICE,
							CampaignDeliveryEngineConstants.SENDPUSHNOTIFICATIONOPERATION, null)))
					.getAsJsonObject();
		} catch (Exception e) {
			logger.debug("Exception occured in submitting mail", e);
		}
		logger.debug("Response for push:" + res);
		return res;

	}

	private static Map<String, Object> buildPayload(EventDTO event) {
		Map<String, Object> inputparams = new HashMap<>();
		JsonObject inputObj = new JsonObject();
		try {

			JsonObject messagerequest = new JsonObject();
			String ufid = event.getCustomerid();
			String text = event.getPushtext();
			String subject = event.getPushsubject();
			JsonObject messages = new JsonObject();
			JsonObject message = new JsonObject();
			JsonObject subscribers = new JsonObject();
			JsonObject platformspecificprops = new JsonObject();
			JsonObject subscriber = new JsonObject();
			JsonObject content = new JsonObject();
			content.addProperty(CampaignDeliveryEngineConstants.MIMETYPE, CampaignDeliveryEngineConstants.TEXT_PLAIN);
			content.addProperty(CampaignDeliveryEngineConstants.PRIORITYSERVICE, CampaignDeliveryEngineConstants.TRUE);
			content.addProperty(CampaignDeliveryEngineConstants.DATA, (text));

			platformspecificprops.addProperty(CampaignDeliveryEngineConstants.TITLE, (subject));
			subscriber.addProperty(CampaignDeliveryEngineConstants.UFID, ufid);
			subscribers.add(CampaignDeliveryEngineConstants.SUBSCRIBER, subscriber);
			message.add(CampaignDeliveryEngineConstants.CONTENT, content);
			message.addProperty(CampaignDeliveryEngineConstants.TYPE, CampaignDeliveryEngineConstants.PUSH);
			message.add(CampaignDeliveryEngineConstants.SUBSCRIBERS, subscribers);

			message.add(CampaignDeliveryEngineConstants.PLATFORMSPECIFICPROPS, platformspecificprops);
			messages.add(CampaignDeliveryEngineConstants.MESSAGE, message);
			messagerequest.add(CampaignDeliveryEngineConstants.MESSAGES, messages);
			messagerequest.addProperty(CampaignDeliveryEngineConstants.APPID,
					CampaignDeliveryEngineUtils.getConfigProperty(CampaignDeliveryEngineConstants.DBX_KMS_APPKEY));
			inputObj.add(CampaignDeliveryEngineConstants.MESSAGEREQUEST, messagerequest);
			inputparams.put(CampaignDeliveryEngineConstants.INPUTPARAMS, inputObj);
		} catch (Exception e) {
			logger.debug("Error in building payload", e);
		}
		return inputparams;
	}

}
