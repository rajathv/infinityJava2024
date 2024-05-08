package com.temenos.dbx.product.transactionservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.temenos.dbx.product.transactionservices.resource.api.BulkWireFileResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.api.factory.ResourceFactory;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * 
 * @author KH2384
 * @version 1.0
 * Java Service end point to fetch the Bulk Wire File for a given file_ID
 */
public class DownloadBulkWireFileOperation implements JavaService2  {
	  private static final Logger LOG = LogManager.getLogger(DownloadBulkWireFileOperation.class);

	    @Override
	    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
	                         DataControllerResponse response) throws Exception {

	        Result result = new Result();
	        try {
	            //Initializing of DownloadBulkWireFileOperation through Abstract factory method
	        	BulkWireFileResource bulkWireResource = DBPAPIAbstractFactoryImpl.getInstance()
	                    .getFactoryInstance(ResourceFactory.class).getResource(BulkWireFileResource.class);
	            result  = bulkWireResource.downloadFileBulkWire(methodID, inputArray, request, response);
	        }
	        catch(Exception e) {
	        	LOG.error("Error occured while invoking DownloadBulkWireFileOperation: ", e);
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
	        }

	        return result;
	    }

	}