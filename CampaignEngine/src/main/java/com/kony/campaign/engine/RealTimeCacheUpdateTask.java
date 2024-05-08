package com.kony.campaign.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.campaign.dto.Campaign;
import com.kony.campaign.dto.CampaignRequest;
import com.kony.campaign.dto.EventDTO;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class RealTimeCacheUpdateTask implements Callable<Object> {
	
	private static final Logger LOGGER = LogManager.getLogger(RealTimeCacheUpdateTask.class);
	
	private Result campaignResult;
	private List<Campaign> campaignList;
	private EventDTO cacheEvent;

	public RealTimeCacheUpdateTask(CampaignRequest creq, List<Campaign> campList,Result campaignResult) {
		super();
		this.campaignResult = campaignResult.getCopy();
		this.campaignList = new ArrayList<>(campList);	
		this.cacheEvent = getEvent(creq.getEvent());		
	}

	private EventDTO getEvent(EventDTO event) {
		EventDTO newEvent = new EventDTO();
		newEvent.setEventId(event.getEventId());
		newEvent.setCoreCustId(event.getCoreCustId());
		return newEvent;
	}

	@Override
	public Object call() throws Exception {		
		LOGGER.debug("cacheTask camp result is" +(campaignResult != null ? ResultToJSON.convert(campaignResult) : "null campaignResponse"));
		LOGGER.debug("cacheTask campaignList is " + campaignList);
		CampaignInternalBusinessDelegate.getCampaignsForCacheUpdateRealTime(
				cacheEvent, new ArrayList<Campaign>(campaignList), campaignResult);
		return true;
	}
	
}
