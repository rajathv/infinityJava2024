package com.temenos.infinity.tradefinanceservices.resource.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.LetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.dto.ApprovalRequestDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsActionDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.LetterOfCreditsResource;

public class LetterOfCreditsResourceImpl implements LetterOfCreditsResource{
	
	private static final Logger LOG = LogManager.getLogger(LetterOfCreditsResourceImpl.class);

	@Override
	public Result rejectLetterOfCredit(DataControllerRequest request) {
		 Result result = new Result();
	        try {

	            LetterOfCreditsBusinessDelegate letterOfCreditsBusinessDelegate = DBPAPIAbstractFactoryImpl
	                    .getBusinessDelegate(LetterOfCreditsBusinessDelegate.class);
	             LetterOfCreditsActionDTO letterOfCredit = letterOfCreditsBusinessDelegate.rejectLetterOfCredit(request);
	            JSONObject responseObj = new JSONObject(letterOfCredit);
	            result = JSONToResult.convert(responseObj.toString());

	        } catch (Exception e) {
	            LOG.error(e);
	            return ErrorCodeEnum.ERR_26021.setErrorCode(new Result());
	        }
	        return result;
	}

	@Override
	public Result withdrawLetterOfCredit(DataControllerRequest request) {
		Result result = new Result();
		String requestId = request.getParameter("requestId") != null ? request.getParameter("requestId") : "";
		if(StringUtils.isBlank(requestId)) {
			return ErrorCodeEnum.ERR_10503.setErrorCode(new Result());
		}
		try {
			LetterOfCreditsBusinessDelegate letterOfCreditsBusinessDelegate = DBPAPIAbstractFactoryImpl
                    .getBusinessDelegate(LetterOfCreditsBusinessDelegate.class);
			LetterOfCreditsActionDTO letterOfCredit = letterOfCreditsBusinessDelegate.withdrawLetterOfCredit(request);
			JSONObject letterOfCreditDto = new JSONObject(letterOfCredit);
			result = JSONToResult.convert(letterOfCreditDto.toString());
		}
		catch(Exception e) {
			LOG.error(e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result());
		}
		return result;
	}

	@Override
	public Result fetchLetterOfCreditDetails(DataControllerRequest request) {
		// TODO Auto-generated method stub
		Result result = new Result();
		String requestId = request.getParameter("transactionIds") != null ? request.getParameter("transactionIds") : "";
		if(StringUtils.isBlank(requestId)) {
			return ErrorCodeEnum.ERR_10503.setErrorCode(new Result());
		}
		try {
			LetterOfCreditsBusinessDelegate letterOfCreditsBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(LetterOfCreditsBusinessDelegate.class);
			List<ApprovalRequestDTO> letterOfCredit = letterOfCreditsBusinessDelegate.fetchLetterOfCreditDetails(request);
			JSONObject letterOfCreditDto = new JSONObject();
			letterOfCreditDto.put("ApprovalRequestDTO", letterOfCredit);
			result = JSONToResult.convert(letterOfCreditDto.toString());
		}
		catch(Exception e) {
			LOG.error(e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result());
		}
		return result;
	}

}
