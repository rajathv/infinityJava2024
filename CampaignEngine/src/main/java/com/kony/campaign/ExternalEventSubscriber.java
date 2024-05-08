package com.kony.campaign;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.kony.campaign.businessdelegate.api.CampaignBusinessDelegate;
import com.kony.campaign.common.CampaignConstants;
import com.kony.campaign.dto.EventDTO;
import com.konylabs.middleware.api.events.EventData;
import com.konylabs.middleware.api.events.EventSubscriber;
import com.konylabs.middleware.api.events.IntegrationEventSubscriber;

@IntegrationEventSubscriber(topics = { "/events/campaignengineexternalevent" })
public class ExternalEventSubscriber implements EventSubscriber{
	
	private static final Logger LOGGER = LogManager.getLogger(ExternalEventSubscriber.class);
	
	@Override
	public void onEvent(EventData eventData) {
		try {
		   if(LOGGER.isDebugEnabled()){			
			 LOGGER.debug("ExternalEventSubscriber invoked by subscribing in OnEvent");
			  LOGGER.debug("eventData.getData()" + eventData.getData());
			}
		    CampaignBusinessDelegate campaignBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			            .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(CampaignBusinessDelegate.class);
			  EventDTO event = new EventDTO();
			  JsonObject eventsJsonObj =  new JsonParser().parse(eventData.getData().toString()).getAsJsonObject();
			  eventsJsonObj = eventsJsonObj.get(CampaignConstants.PARAM_EVENTS).getAsJsonObject();
			  event.setEventId(eventsJsonObj.get(CampaignConstants.EXTERNAL_EVENT_CODE).getAsString());
			  JsonObject eventDataObj = eventsJsonObj.get(CampaignConstants.PARAM_EVENT_DATA).getAsJsonObject();
			  if(eventDataObj.has(CampaignConstants.REQ_CUSTOMER_DATA)){
				  JsonObject customerDataObj = eventDataObj.get(CampaignConstants.REQ_CUSTOMER_DATA).getAsJsonObject();
				  if(customerDataObj.has(CampaignConstants.REQ_CUSTOMER_ID)) {
				    event.setCoreCustId(customerDataObj.get(CampaignConstants.REQ_CUSTOMER_ID).getAsString());
				  }
			  }
			if(event.getCoreCustId() != null ) {
			  event.setEventData(eventDataObj);
			  campaignBusinessDelegate.getExternalCampaigns(event);
			}else {
				LOGGER.error("corecustomerID is manadatory");
			}
		} catch (JsonSyntaxException e) {
			LOGGER.error("Error while parsing event input " ,e);		
		}
		catch (Exception e) {
			LOGGER.error("Error while sending event" , e);
		}
	}

}
