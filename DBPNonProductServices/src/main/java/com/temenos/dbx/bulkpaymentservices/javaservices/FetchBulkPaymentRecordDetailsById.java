package com.temenos.dbx.bulkpaymentservices.javaservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentRecordDBOperations;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentRecordDTO;

public class FetchBulkPaymentRecordDetailsById implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(FetchBulkPaymentRecordDetailsById.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {


		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];
		
		Result result = null;
		try {
			BulkPaymentRecordDBOperations bulkPaymentRecordDBOperations = new BulkPaymentRecordDBOperations();

			String recordId = inputParams.get("recordId").toString();
			BulkPaymentRecordDTO recordDTO = new BulkPaymentRecordDTO();
			recordDTO = bulkPaymentRecordDBOperations.fetchBulkPaymentRecordDetailsById(recordId);
			result = JSONToResult.convert(new JSONObject(recordDTO).toString());
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking fetchOnGoingBulkPaymentRecord", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
	return result;
	}
	
}