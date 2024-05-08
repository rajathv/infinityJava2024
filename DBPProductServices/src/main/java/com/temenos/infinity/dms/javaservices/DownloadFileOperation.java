package com.temenos.infinity.dms.javaservices;


import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.dms.resource.api.DMSResource;
import com.temenos.infinity.dms.resource.impl.DMSResourceImpl;

/**
 * 
 * @author TeamEverest
 * @version Java Service end point to login and download file
 * 
 */
public class DownloadFileOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(DMSResourceImpl.class);
	private static final int SIZE_OF_RANDOM_GENERATED_STRING = 10;

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try {
			DMSResource downloadResource = DBPAPIAbstractFactoryImpl.getResource(DMSResource.class);
			String documentId = request.getParameter("id");
			String revision = request.getParameter("revision");
			String auth_token = request.getHeader("X-Kony-Authorization");
			String operation = "download";

			Result result = downloadResource.loginAndDownload(documentId, revision, operation, auth_token);
			Result res = new Result();
			if (result.getParamValueByName("base64").length() > 0) {
				String fileId = HelperMethods.getUniqueNumericString(SIZE_OF_RANDOM_GENERATED_STRING);
				byte[] bytes = java.util.Base64.getMimeDecoder().decode(result.getParamValueByName("base64"));
//				request.getSession().setAttribute(fileId, bytes);
				MemoryManager.saveIntoCache(fileId, bytes, 120);
            	res.addParam("fileId", fileId);
				response.setStatusCode(HttpStatus.SC_OK);
				LOG.info("DownloadFileOperation -Succesfully constructed the File object");
			} else {
				LOG.error("DownloadFileOperation-DMS-No records were found with the given selection criteria");
				return ErrorCodeEnum.ERR_25001.setErrorCode(new Result()); //is this needed need to check
			}
			return res;
		}
		catch (Exception e) {
			LOG.error("DownloadFileOperation" + e);
			return ErrorCodeEnum.ERR_25001.setErrorCode(new Result());
		}
	}

}