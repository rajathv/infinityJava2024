package com.temenos.infinity.tradefinanceservices.resource.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
//import com.kony.dbputilities.util.ErrorCodeEnum;
import  com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetLetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.Constants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.GetLetterOfCreditsResource;

/**
 *
 */
public class GetLetterOfCreditsResourceImpl implements GetLetterOfCreditsResource {

    private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsResourceImpl.class);

	@SuppressWarnings("null")
	@Override
	public Result getLetterOfCredits(Object[] inputArray, LetterOfCreditsDTO letterOfCreditsDTO,
			DataControllerRequest request) {

        List<LetterOfCreditsDTO> letterOfCredits = null;
        Result result = new Result();       
      
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);
		boolean isCombinedUser = CustomerSession.IsCombinedUser(customer);
		String featureActionId = Constants.GET_LOC_FEATURE_ACTION_ID;
		if (StringUtils.isBlank(customerId))
			return ErrorCodeEnum.ERR_26014.setErrorCode(new Result());

		AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

		List<String> requiredActionIds = Arrays.asList(Constants.GET_LOC_FEATURE_ACTION_ID);
		String features = CustomerSession.getPermittedActionIds(request, requiredActionIds);

		if (features == null) {
			return ErrorCodeEnum.ERR_10117.setErrorCode(new Result());
		}

		try {
			FilterDTO filterDTO = null;
			@SuppressWarnings("unchecked")
			Map<String, Object> inputParamsMap = (HashMap<String, Object>) inputArray[1];

			try {
				filterDTO = JSONUtils.parse(new JSONObject(inputParamsMap).toString(), FilterDTO.class);
			} catch (IOException e) {
				LOG.error("Exception occurred while fetching params: ", e);
			}

			GetLetterOfCreditsBusinessDelegate orderBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(GetLetterOfCreditsBusinessDelegate.class);
			letterOfCredits = orderBusinessDelegate.getLetterOfCredits(letterOfCreditsDTO, request);

			if (letterOfCredits == null) {
				LOG.error("Error occurred while fetching letter of credits from backend");
			}
			if (letterOfCredits.size() > 0 && StringUtils.isNotBlank(letterOfCredits.get(0).getErrorMessage())) {
				LOG.error("Error occurred while fetching letter of credits from backend");
			}

			List<LetterOfCreditsDTO> filter_paymentTerms =  new ArrayList<LetterOfCreditsDTO>();
			List<LetterOfCreditsDTO> resultFilter = new ArrayList<LetterOfCreditsDTO>();
			String fromDateFilter = inputParamsMap.get("fromDateFilter") != null ? inputParamsMap.get("fromDateFilter").toString() : null;
			String toDateFilter   = inputParamsMap.get("toDateFilter") != null ? inputParamsMap.get("toDateFilter").toString() : null; 
	        String filterbyparam[] = request.getParameter("filterByParam")!=null ? request.getParameter("filterByParam").split(",") : null;
	        String filterbyvalue[] = request.getParameter("filterByValue")!=null ? request.getParameter("filterByValue").split(",") : null; 
			
			if(StringUtils.isNotEmpty(request.getParameter("filterByParam")) && request.getParameter("filterByParam").contains("paymentTerms")  && StringUtils.isNotEmpty(request.getParameter("filterByValue")) && filterbyparam!=null && filterbyvalue!=null && filterbyparam.length>0 && filterbyvalue.length>0 && filterbyparam.length == filterbyvalue.length){
				for (LetterOfCreditsDTO ob : letterOfCredits) {
					int add_param = 0;
					Map<String, Object> objMap = JSONUtils.parseAsMap(new JSONObject(ob).toString(), String.class,Object.class);
					for(int k=0;k<filterbyparam.length;k++) {
						if (objMap.containsKey("paymentTerms") && objMap.get("paymentTerms") != null
								&& StringUtils.isNotEmpty(objMap.get("paymentTerms").toString()) 
								&& objMap.get("paymentTerms").toString().equalsIgnoreCase(filterbyvalue[k]) 
								&& filterbyparam[k].equalsIgnoreCase("paymentTerms")) {
							add_param++;
						}	
					}
					if(add_param > 0) {
						filter_paymentTerms.add(ob);
					}					
			    }
				letterOfCredits = filter_paymentTerms;
			}
			
			List<LetterOfCreditsDTO> filteredLOC = filterDTO.filter(letterOfCredits);
			        
			if (StringUtils.isNotBlank(fromDateFilter) && StringUtils.isNotBlank(toDateFilter)) {
				try {
//					String addTime = "T00:00:00Z";
					String fromValue = fromDateFilter;
					String toValue =   toDateFilter;
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Date startDate = (Date) formatter.parse(fromValue);
					Date endDate = (Date) formatter.parse(toValue);
					for (LetterOfCreditsDTO ob : filteredLOC) {
						Map<String, Object> objMap = JSONUtils.parseAsMap(new JSONObject(ob).toString(), String.class,
								Object.class);
						if (objMap.containsKey("lcCreatedOn") && objMap.get("lcCreatedOn") != null
								&& StringUtils.isNotEmpty(objMap.get("lcCreatedOn").toString())) {
							String LOCIssueDateValue = objMap.get("lcCreatedOn").toString();
							Date LOCIssueDate = (Date) formatter.parse(LOCIssueDateValue);
							Boolean isIssueDateAvailable = LOCIssueDate.getTime() >= startDate.getTime()
									&& LOCIssueDate.getTime() <= endDate.getTime();
							if (isIssueDateAvailable) {
								resultFilter.add(ob);
							}
							isIssueDateAvailable = false;
						}
					}
						JSONArray LOCFiles = new JSONArray(resultFilter);
						JSONObject responseObj = new JSONObject();
						responseObj.put("LetterOfCredits", LOCFiles);
						result = JSONToResult.convert(responseObj.toString());
						return result;
				} catch (Exception e) {
					LOG.error(e);
					LOG.debug("No letter of credits available on the range" + e);
					return ErrorCodeEnum.ERRTF_29050.setErrorCode(new Result());
				}
			}
			JSONArray LOCFiles = new JSONArray(filteredLOC);
			JSONObject responseObj = new JSONObject();
			responseObj.put("LetterOfCredits", LOCFiles);
			result = JSONToResult.convert(responseObj.toString());

        } catch (Exception e) {
            LOG.error(e);
            LOG.debug("Failed to fetch letter of credits from OMS " + e);
            return ErrorCodeEnum.ERRTF_29053.setErrorCode(new Result());
        }
        return result;
    }
    

    	

}
