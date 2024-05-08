package com.temenos.dbx.bulkpaymentservices.javaservices;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.temenos.dbx.product.commons.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentFileDBOperations;

public class FetchUploadedBulkPaymentFiles implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(FetchUploadedBulkPaymentFiles.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse)
			throws Exception {
		Result result;	
		
		try {
			BulkPaymentFileDBOperations bulkPaymentFileDBOperations = new BulkPaymentFileDBOperations();
			Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
			String customerId = CustomerSession.getCustomerId(customer);
			ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
			List<String> contracts = contractDelegate.fetchContractCustomers(customerId);
			contracts.add(CustomerSession.getCompanyId(customer));
			
			result = bulkPaymentFileDBOperations.fetchUploadedFiles(contracts, customerId);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking fetchUploadedFiles", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

}
