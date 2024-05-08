package com.temenos.dbx.product.transactionservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.transactionservices.resource.api.BulkWiresResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
* @version 1.0
* Java Service end point to fetch all the Bulk Wire Template Line Items
*/
public class GetBulkWireTemplateLineItems implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(GetBulkWireTemplateLineItems.class);

		@Override
		public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
				DataControllerResponse response) throws Exception {

			Result result = new Result();
			try {
				//Initializing of BulkWireFileResource through Abstract factory method
				BulkWiresResource bulkWiresResource = DBPAPIAbstractFactoryImpl.getInstance()
						.getFactoryInstance(ResourceFactory.class).getResource(BulkWiresResource.class);

				result  = bulkWiresResource.getBulkWireTemplateLineItems(methodID, inputArray, request, response);
			}
			catch(Exception e) {
				LOG.error("Caught exception at invoke of GetWireFileLineItems: ", e);
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}

			return result;
		}
}
