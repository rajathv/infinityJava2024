package com.temenos.dbx.product.payeeservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.payeeservices.resource.api.BulkPaymentsPayeeResource;

public class GetBankDetailsFromBICOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(GetBankDetailsFromBICOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result;

		try {			
			BulkPaymentsPayeeResource bulkPaymentsPayeeResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentsPayeeResource.class);
			result = bulkPaymentsPayeeResource.getBankDetailsFromBIC(methodID, inputArray, request, response);
		} catch (Exception e) {
			LOG.error("Error occured while fetching bank details: ", e);
			return ErrorCodeEnum.ERR_21260.setErrorCode(new Result());
		}

		return result;
	}
}
