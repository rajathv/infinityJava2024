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

public class UploadBWTemplateFileOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(CreateBulkBillPayTransactionOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response)
	{
		
		Result result = new Result();
		try {
			BulkWiresResource uploadBWfiles = DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class).getResource(BulkWiresResource.class);
			result = uploadBWfiles.uploadBWTemplateFile(methodID, inputArray, request, response);
		}
		catch(Exception exp) {
			LOG.error("Exception occured while uploading bulk wire file",exp);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

        return result;
	}
}
