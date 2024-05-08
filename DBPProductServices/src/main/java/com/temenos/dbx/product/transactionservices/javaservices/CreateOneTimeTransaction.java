package com.temenos.dbx.product.transactionservices.javaservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.ErrorConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.transactionservices.resource.api.InterBankFundTransferResource;
import com.temenos.dbx.product.transactionservices.resource.api.InternationalFundTransferResource;
import com.temenos.dbx.product.transactionservices.resource.api.IntraBankFundTransferResource;

public class CreateOneTimeTransaction implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(CreateOneTimeTransaction.class);

	public static final int FILENAME_INDEX = 0;
	public static final int FILETYPE_INDEX = 1;
	public static final int FILECONTENTS_INDEX = 2;
	public static final int UNIQUE_ID_LENGTH = 32;
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		String referenceId = null;
		Result transactionResult = new Result();
		String operationName = null;
		HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
		try {
			HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String serviceDifferentiator = inputParams.get("serviceName");
			String serviceName = ServiceId.DBP_TRANSACTION_SERVICES;
			
			if (FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE.equalsIgnoreCase(serviceDifferentiator)) {
				operationName = OperationName.CREATE_INTERNATIONAL_FUND_TRANSACTION;
				transactionResult = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
						true);
			}            
	        else if (FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE.equalsIgnoreCase(serviceDifferentiator)) {
	        	operationName = OperationName.CREATE_INTER_BANK_FUND_TRANSACTION;
	        	transactionResult = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
						true);
	        }            
	        else if(FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE.equalsIgnoreCase(serviceDifferentiator)){
	        	operationName = OperationName.CREATE_INTRA_BANK_FUND_TRANSACTION;
	        	transactionResult = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
						true);
	        }
	        else {
	        	return ErrorCodeEnum.ERR_29021.setErrorCode(new Result());
	        }
			
		}
		catch (Exception e) {
			LOG.error("Error occured while invoking CreateOneTimeTransaction: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return transactionResult;
	}
}
