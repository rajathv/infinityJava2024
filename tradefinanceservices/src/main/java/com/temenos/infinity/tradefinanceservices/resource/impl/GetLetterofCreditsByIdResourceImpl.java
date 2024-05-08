package com.temenos.infinity.tradefinanceservices.resource.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
//import com.kony.dbputilities.util.ErrorCodeEnum;
import  com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetLetterOfCreditsByIDBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.Constants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.GetLetterofCreditsByIdResource;

public class GetLetterofCreditsByIdResourceImpl implements GetLetterofCreditsByIdResource {
	private static final Logger LOG = LogManager.getLogger(GetLetterofCreditsByIdResourceImpl.class);

	public Result getLetterOfCreditsByID(LetterOfCreditsDTO letterOfCredit, DataControllerRequest request) {
		LetterOfCreditsDTO letterOfCredits;
		Result result = new Result();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);	   
		boolean isCombinedUser = CustomerSession.IsCombinedUser(customer);
		String featureActionId = Constants.GET_LOC_FEATURE_ACTION_ID;
		if (StringUtils.isBlank(customerId))
            return ErrorCodeEnum.ERR_26014.setErrorCode(new Result());   
		
        AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);		     
		
                           
        
        List<String> requiredActionIds = Arrays.asList(Constants.GET_LOC_FEATURE_ACTION_ID);
		String features = CustomerSession.getPermittedActionIds(request, requiredActionIds);

		if (features == null) {
			return ErrorCodeEnum.ERR_10117.setErrorCode(new Result());
		}
		
		 try {

	            GetLetterOfCreditsByIDBusinessDelegate requestBusinessDelegate = DBPAPIAbstractFactoryImpl
	                    .getBusinessDelegate(GetLetterOfCreditsByIDBusinessDelegate.class);
	            
				letterOfCredits = requestBusinessDelegate.getLetterOfCreditsByID(letterOfCredit, request);  
				
				if (letterOfCredits == null) {
					LOG.error("Error occurred while fetching letter of credits from backend");
				}
				if(StringUtils.isBlank(customerId) || !customerId.equalsIgnoreCase(letterOfCredits.getCustomerId())) {
					return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
				}
				if(letterOfCredits.getErrorCode()!=null) {
					return ErrorCodeEnum.ERRTF_29057.setErrorCode(new Result(),letterOfCredits.getErrorMessage());
				}
	            JSONObject responseObj = new JSONObject();
	            List<LetterOfCreditsDTO> LOC = new ArrayList<>();
	            LOC.add(letterOfCredits);
	            responseObj.put("LetterOfCredits", LOC);
	            result = JSONToResult.convert(responseObj.toString());

	        } catch (Exception e) {
	            LOG.error(e);
	            LOG.debug("Failed to fetch letter of credits from OMS " + e);
	            return ErrorCodeEnum.ERRTF_29053.setErrorCode(new Result());
	        }
		return result;
	}
}
