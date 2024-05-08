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
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentRecordDBOperations;

public class FetchOnGoingBulkPayments implements JavaService2 {

	 private static final Logger LOG = LogManager.getLogger(FetchOnGoingBulkPayments.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result;
			try {
				BulkPaymentRecordDBOperations bulkPaymentRecordDBOperations = new BulkPaymentRecordDBOperations();
				Map<String, Object> customer = CustomerSession.getCustomerMap(request);
				String createdby = CustomerSession.getCustomerId(customer);
				
				ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
				List<String> contracts = contractDelegate.fetchContractCustomers(createdby);
				contracts.add(CustomerSession.getCompanyId(customer));
				
				String batchMode = request.getParameter("batchMode");
				result = bulkPaymentRecordDBOperations.fetchOnGoingBulkPaymentRecords(contracts, batchMode, createdby);
			}
			catch(Exception e) {
				LOG.error("Error occured while invoking fetchOnGoingBulkPaymentRecord", e);
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
		return result;
	}

}
