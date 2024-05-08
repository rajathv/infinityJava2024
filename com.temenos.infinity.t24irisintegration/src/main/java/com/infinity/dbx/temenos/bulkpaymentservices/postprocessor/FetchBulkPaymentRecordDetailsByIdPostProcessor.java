package com.infinity.dbx.temenos.bulkpaymentservices.postprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.accounts.AccountsConstants;
import com.kony.dbx.BasePostProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class FetchBulkPaymentRecordDetailsByIdPostProcessor extends BasePostProcessor implements AccountsConstants  {

	private static final Logger logger = LogManager.getLogger(FetchBulkPaymentRecordDetailsByIdPostProcessor.class);
	
	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response) {
		try {
			result.addParam(new Param("recordId", request.getParameter("recordId")));
			if(result.getParamByName("bulkReference")!=null) {
			if(result.getParamByName("bulkReference").getValue()!=null) {
				if(result.getParamByName("description") == null){
					result.addParam(new Param("description", result.getParamByName("bulkReference").getValue()));
			}}}}
		catch(Exception e) {
			logger.error("Error occured while invoking post processor for fetchBulkPaymentRecordbyId: ", e);
			return null;
		}
		return result;
	}
}
