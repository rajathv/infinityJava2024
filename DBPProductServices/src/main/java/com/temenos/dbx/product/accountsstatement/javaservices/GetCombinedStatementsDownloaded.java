package com.temenos.dbx.product.accountsstatement.javaservices;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.MWConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class GetCombinedStatementsDownloaded implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(DownloadCombinedStatementFile.class);
	
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		
		Result result = new Result();
		HashMap<String, String> inputParams = (HashMap<String, String>) inputArray[1];
		String fileType = inputParams.get("fileType");
		String fileName = inputParams.get("fileName");
		String fileId = inputParams.get("id");
		byte[] bytes = (byte[])MemoryManager.getFromCache(fileId);
		if (fileType.equals("pdf") || fileType.equals("application/pdf"))
			dcResponse.getHeaders().putAll(getCustomHeaders(fileName, "application/pdf"));
		else if (fileType.equals("csv") || fileType.equals("text/csv"))
			dcResponse.getHeaders().putAll(getCustomHeaders(fileName, "text/csv"));
		else if (fileType.equals("xls") || fileType.equals("xlsx") || fileType.equals("application/vnd.ms-excel"))
			dcResponse.getHeaders().putAll(
					getCustomHeaders(fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

		try {
			dcResponse.setAttribute(MWConstants.CHUNKED_RESULTS_IN_JSON,
					new BufferedHttpEntity(new ByteArrayEntity(bytes)));
		} catch (IOException exception) {
			LOG.error("Error while downloading file", exception);
			result.addParam(
					new Param(ErrorCodeEnum.ERROR_CODE_KEY, String.valueOf(ErrorCodeEnum.ERR_28027.getErrorCode())));
			result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28027.getMessage()));
			return result;
		}
		dcResponse.setStatusCode(HttpStatus.SC_OK);
		return result;
	}
	
	private Map<String, String> getCustomHeaders(String filename, String contentType) {
		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put(HttpHeaders.CONTENT_TYPE, contentType);
		customHeaders.put("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		return customHeaders;
	}

}
