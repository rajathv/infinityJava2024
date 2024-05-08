package com.kony.campaign.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.campaign.CacheCampaignEvalutor;
import com.kony.campaign.CampaignEvalutor;
import com.kony.campaign.CampaignException;
import com.kony.campaign.InternalResponseProcessor;
import com.kony.campaign.JobCampaignEvalutor;
import com.kony.campaign.PreLoginCampaignEvalutor;
import com.kony.campaign.RealTimeCampaignEvalutor;
import com.kony.campaign.businessdelegate.api.CampaignBusinessDelegate;
import com.kony.campaign.common.CampaignConstants;
import com.kony.campaign.common.CampaignConstants.CampaignFilterTypes;
import com.kony.campaign.common.ErrorCodes;
import com.kony.campaign.dto.CampaignRequest;
import com.kony.campaign.dto.CampaignRequestType;
import com.kony.campaign.dto.EventDTO;
import com.kony.campaign.engine.CampaignProcessor;
import com.kony.campaign.engine.CampaignThreadPoolExecutor;
import com.kony.campaign.engine.DelayedRealTimeExecutorTask;
import com.kony.campaign.util.CampaignUtil;
import com.konylabs.middleware.dataobject.Result;

public class CampaignBusinessDelegateImpl implements CampaignBusinessDelegate{	
	
	private static final Logger LOGGER = LogManager.getLogger(CampaignBusinessDelegateImpl.class);

	@Override
	public Result getInternalCampaigns(EventDTO event) {
		CampaignRequest cr = null;
		if(event.getEventId().equals(CampaignConstants.PRELOGIN)) {
			 cr = new CampaignRequest(CampaignRequestType.PRELOGIN_EVENT,event,false);
			 cr.setCampaignFilter(CampaignFilterTypes.FILTER_ON_PLACEHOLDER.name());
			 cr.setCampaignEvalutor(new PreLoginCampaignEvalutor());
		}else { 
			CampaignEvalutor ce = null;
			if(StringUtils.isNotBlank(event.getPlaceholderCode())){
				String campFetchType = CampaignUtil.getOnlineCampaignResponsiveProperty();
				cr = new CampaignRequest(CampaignRequestType.ONLINE_PLACEHOLDER_EVENT,event,
											CampaignUtil.isCacheUpdateRequired(campFetchType));
				cr.setCampaignFilter(CampaignFilterTypes.FILTER_ON_PLACEHOLDER.name());
				ce = getCampaignEvalutor(campFetchType,true);
				cr.setCampaignEvalutor(ce);
			}
			if(ce == null) {
				return processOnlineNonPlaceHolderEvent(event);
			}	
		}
		cr.setCampaignCountInResponse(CampaignUtil.getIntServerProperty(CampaignConstants.CAMPAIGNS_CAROUSEL_NUMBER, 1));
		return CampaignProcessor.processCampaignRequest(cr);	
	}		
	
	@Override
	public Result getExternalCampaigns(EventDTO event) {		
		CampaignRequest cr =new CampaignRequest(CampaignRequestType.OFFLINE_EVENT,event,
										CampaignUtil.isCacheUpdateRequired());
		cr.setCampaignEvalutor(new RealTimeCampaignEvalutor());		
		return processOfflineCampainRequest(cr);
	}
	
	@Override
	public Result getCampaignsForJob() {
		CampaignRequest cr = new CampaignRequest(CampaignRequestType.JOB);
        cr.setCampaignEvalutor(new JobCampaignEvalutor());
        return processOfflineCampainRequest(cr);
	}
	
	private Result processOnlineNonPlaceHolderEvent(EventDTO event) {
		try {
			CampaignThreadPoolExecutor.execute(new DelayedRealTimeExecutorTask(event));
		} catch (Exception e) {
			LOGGER.error("Interupt while invoking DelayedRealTimeExecutor" , e);
		}
		return InternalResponseProcessor.getEmptyCampaignResult();
	}
	
	private Result processOfflineCampainRequest(CampaignRequest c) {
		Result res = CampaignProcessor.processCampaignRequest(c);
        if(res == null ||  res.getParamByName(CampaignConstants.DBP_ERROR_MESSAGE) != null ) {
        	Result resfinal = res != null ? res : new Result();
        	LOGGER.error("Error while processing external event "+ resfinal.getParamValueByName(CampaignConstants.DBP_ERROR_MESSAGE));
        	resfinal.addParam("success", Boolean.FALSE.toString());
        	return resfinal;
        }       
        res.addParam("success",Boolean.TRUE.toString());
        return res;
	}
		

	private CampaignEvalutor getCampaignEvalutor(String campaignFetchType, boolean isPlaceHolderPresent) {
		CampaignEvalutor ce = null;
		if(isPlaceHolderPresent) {
		    if(campaignFetchType.equalsIgnoreCase(CampaignConstants.CACHE)) {
		        	ce = new CacheCampaignEvalutor();
		    }else {
			  ce = new RealTimeCampaignEvalutor();
		    }
	    }		
		return ce;
	}

	@Override
	public boolean insertCustCompletedCampaigns(String userId, String campaignId) throws CampaignException {
		try {
			Map<String, Object> inputMap = new HashMap<>();	
			inputMap.put(CampaignConstants.PARAM_CUSTOMER_ID, userId);
			inputMap.put(CampaignConstants.PARAM_CAMPAIGN_ID, campaignId);
			Result dbRes = CampaignUtil.invokeService(CampaignConstants.CAMPAIGN_DB_SERVICE, 
					CampaignUtil.getDatabaseServiceNames(CampaignConstants.CUST_COMPLETED_CAMPAIGN_POST_OPERATION), inputMap);
			if(dbRes.getParamValueByName(CampaignConstants.ERRMSG) != null) {
				LOGGER.error(ErrorCodes.ERR_17012.getMessage() + " " + dbRes.getParamValueByName(CampaignConstants.ERRMSG));
				throw new CampaignException(ErrorCodes.ERR_17012.getMessage() + dbRes.getParamValueByName(CampaignConstants.ERRMSG),ErrorCodes.ERR_17012.getErrorCode());
			}
		} catch (Exception e) {
			LOGGER.error(ErrorCodes.ERR_17012.getMessage() , e);
			throw new CampaignException(ErrorCodes.ERR_17012.getMessage(),e,ErrorCodes.ERR_17012.getErrorCode());
		}
		return true;
	}
}
