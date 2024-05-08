package com.temenos.dbx.product.accountsstatement.javaservices;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.constants.Constants;

public class GetCombinedStatementDetails implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(GetCombinedStatementDetails.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			HashMap<String, String> inputParams = (HashMap<String, String>) inputArray[1];
			String userId = inputParams.get("userId");
			String customerId = HelperMethods.getCustomerIdFromSession(request);
			if(!userId.equals(customerId)) {
				LOG.error("Not Authorized user");
				return ErrorCodeEnum.ERR_12403.setErrorCode(new Result());
			}
			HashMap<String, String> data = new HashMap<String, String>();
			data.put(Constants.$FILTER, "userId  eq '" + userId + "'");
			Result getStatementData = HelperMethods.callApi(request, data, HelperMethods.getHeaders(request),
					URLConstants.ACCOUNTS_STATEMENT_FILES_GET);
			Dataset accountStatementset = getStatementData.getDatasetById("accountsstatementfiles");
			if (accountStatementset.getAllRecords().size() > 0) {
				LOG.info("Record is available for combined statement files");
				String generatedDate = accountStatementset.getRecord(0).getParamValueByName("lastmodifiedts");
				generatedDate = HelperMethods.convertDateFormat(generatedDate, "yyyy-MM-dd'T'hh:mm:ss'Z'");
				result.addParam("fileName", accountStatementset.getRecord(0).getParamValueByName("fileName"));
				result.addParam("status", accountStatementset.getRecord(0).getParamValueByName("status"));
				result.addParam("generatedDate", generatedDate);
				result.addParam("fileType", accountStatementset.getRecord(0).getParamValueByName("fileType"));
				result.addParam("fileId", accountStatementset.getRecord(0).getParamValueByName("id"));
			}
		} catch (Exception e) {
			LOG.error("Error while fetching combined statement details");
			result.addParam(
					new Param(ErrorCodeEnum.ERROR_CODE_KEY, String.valueOf(ErrorCodeEnum.ERR_28031.getErrorCode())));
			result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28031.getMessage()));

		}
		return result;
	}
}
