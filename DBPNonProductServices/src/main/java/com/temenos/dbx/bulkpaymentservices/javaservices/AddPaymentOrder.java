package com.temenos.dbx.bulkpaymentservices.javaservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentPODBOperations;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentRecordDBOperations;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentPODTO;

public class AddPaymentOrder implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(AddPaymentOrder.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {

			@SuppressWarnings("unchecked")
			Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
			Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);

			String confirmationNumber = String.valueOf(HelperMethods.getIdUsingCurrentTimeStamp());
			inputParams.put("paymentOrderId", confirmationNumber);
			inputParams.put("createdBy", CustomerSession.getCustomerId(customer));
			inputParams.put("companyId", CustomerSession.getCompanyId(customer));
			inputParams.put("status", TransactionStatusEnum.NEWLY_ADDED.getStatus());
			
			BulkPaymentPODBOperations bulkPaymentPODBOperations = new BulkPaymentPODBOperations();
			BulkPaymentPODTO inputObj = JSONUtils.parse(new JSONObject(inputParams).toString(), BulkPaymentPODTO.class);
			BulkPaymentPODTO outputObj = bulkPaymentPODBOperations.createBulkPaymentPOEntry(inputObj);
			
			if(outputObj != null && outputObj.getDbpErrMsg() == null && outputObj.getDbpErrorCode() == null) {
				BulkPaymentRecordDBOperations operations = new BulkPaymentRecordDBOperations();
				operations.updateBulkPaymentRecordAmountAndTotalTransactions(outputObj.getRecordId(),
						outputObj.getAmount(),new Double(1));
			}

			JSONObject resultObject = new JSONObject(outputObj);
			result = JSONToResult.convert(resultObject.toString());
			result.addParam(new Param("paymentOrderId", confirmationNumber, MWConstants.STRING));
						
		} catch (Exception e) {
			LOG.error("Error occured while invoking createBulkPaymentPOEntry at Backend: ", e);
			return ErrorCodeEnum.ERR_21210.setErrorCode(new Result());
		}
		return result;
	}

}
