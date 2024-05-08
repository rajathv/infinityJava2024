package com.temenos.dbx.product.accountsstatement.javaservices;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.constants.Constants;

public class DownloadCombinedStatementFile implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(DownloadCombinedStatementFile.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {

		Result result = new Result();
		final int SIZE_OF_RANDOM_GENERATED_STRING = 10;

		HashMap<String, String> inputParams = (HashMap<String, String>) inputArray[1];
		String fileId = inputParams.get("fileId");

		String fileType = null;
		String fileName = null;

		HashMap<String, String> data = new HashMap<String, String>();

		data.put("id", fileId);
		data.put("fieldName", "fileContent");
		Result getStatementFileContent = HelperMethods.callApi(dcRequest, data, HelperMethods.getHeaders(dcRequest),
				URLConstants.ACCOUNTS_STATEMENT_FILES_BINARY_GET);

		HashMap<String, String> detailsInputdata = new HashMap<String, String>();
		detailsInputdata.put(Constants.$FILTER, "id  eq '" + fileId + "'");
		Result getStatementFileDetails = HelperMethods.callApi(dcRequest, detailsInputdata,
				HelperMethods.getHeaders(dcRequest), URLConstants.ACCOUNTS_STATEMENT_FILES_GET);

		Dataset accountStatementset = getStatementFileDetails.getDatasetById("accountsstatementfiles");
		if (accountStatementset.getAllRecords().size() > 0) {
			LOG.info("Record is available for accountsstatementfiles");
			fileName = accountStatementset.getRecord(0).getParamValueByName("fileName");
			fileType = accountStatementset.getRecord(0).getParamValueByName("fileType");
		} else {
			LOG.error("Error while fetching file name and file type accountStatementDetails");
			result.addParam(
					new Param(ErrorCodeEnum.ERROR_CODE_KEY, String.valueOf(ErrorCodeEnum.ERR_28026.getErrorCode())));
			result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28026.getMessage()));
			return result;
		}

		String base64 = getStatementFileContent.getParamValueByName("data");

		if (StringUtils.isBlank(base64)) {
			LOG.error("Error while fetching base64 file from accountsstatementfiles table");
			result.addParam(
					new Param(ErrorCodeEnum.ERROR_CODE_KEY, String.valueOf(ErrorCodeEnum.ERR_28026.getErrorCode())));
			result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28026.getMessage()));
			return result;
		}

		byte[] bytes = Base64.getMimeDecoder().decode(base64);
		
		String id = HelperMethods.getUniqueNumericString(SIZE_OF_RANDOM_GENERATED_STRING);
        MemoryManager.saveIntoCache(id, bytes, 120);
        result.addParam("id", id, DBPUtilitiesConstants.STRING_TYPE);
        result.addParam("fileType", fileType, DBPUtilitiesConstants.STRING_TYPE);
        result.addParam("fileName", fileName, DBPUtilitiesConstants.STRING_TYPE);
		dcResponse.setStatusCode(HttpStatus.SC_OK);
		return result;
	}


}