package com.temenos.infinity.api.docmanagement.acctstatement.javaservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.Constants;
import com.kony.dbputilities.util.HelperMethods;

public class GenerateCombinedStatementFile implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(GenerateCombinedStatementFile.class);
	public static final int UNIQUE_ID_LENGTH = 32;

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {

			String dateFormat = null;
			HashMap<String, String> inputParams = (HashMap<String, String>) inputArray[1];
			String userId = inputParams.get("userId");
			String fileType = inputParams.get("fileType");
			String accountsInfo = inputParams.get("accountsInfo");
			String fromDate = inputParams.get("fromDate");
			String toDate = inputParams.get("toDate");

			// TODO these should be updated
			// String currencyCode = inputParams.get("currencyCode");
			String currencyCode = "USD";
			// String bankName = inputParams.get("bankName");
			String bankName = "Infinity";

			// check for empty constant value
			String id = "";
			String currentDate = HelperMethods.getCurrentDate();
			currentDate = HelperMethods.convertDateFormat(currentDate, Constants.COMBINED_STATTEMENTS_DATE_FORMAT);
			String fileName = "Combined_Statement_" + fileType + "_" + currentDate + "." + fileType;
			String status = Constants.STATUS_INPROGRESS;

			HashMap<String, String> getDataInputParams = new HashMap<String, String>();
			getDataInputParams.put(Constants.$FILTER, "userId  eq '" + userId + "'" +   DBPUtilitiesConstants.AND + "legalEntityId" + DBPUtilitiesConstants.EQUAL + LegalEntityUtil.getLegalEntityIdFromSessionOrCache(dcRequest));
			Result getStatementFileDetails = HelperMethods.callApi(dcRequest, getDataInputParams,
					HelperMethods.getHeaders(dcRequest), URLConstants.ACCOUNTS_STATEMENT_FILES_GET);

			Dataset accountStatementset = getStatementFileDetails.getDatasetById("accountsstatementfiles");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("userId", userId);
			data.put("fileName", fileName);
			data.put("status", status);
			data.put("fileType", fileType);
			data.put("modifiedBy", userId);
			data.put("accountIds", accountsInfo);
			data.put("fromDate", fromDate);
			data.put("toDate", toDate);
			data.put("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(dcRequest));
			if (accountStatementset.getAllRecords().size() > 0) {
				LOG.info("File aleardy exists for user updating the existing row");
				id = accountStatementset.getRecord(0).getParamValueByName("id");
				data.put("id", id);
				data.put("failureMessage", "");
				data.put("lastmodifiedts", HelperMethods.getCurrentTimeStamp());
				Result updateStatementFiles = HelperMethods.callApi(dcRequest, data,
						HelperMethods.getHeaders(dcRequest), URLConstants.ACCOUNTS_STATEMENT_FILES_UPDATE);
				if (updateStatementFiles.getParamValueByName("opstatus") != null
						&& !updateStatementFiles.getParamValueByName("opstatus").toString().equalsIgnoreCase("0")) {
					LOG.error("Error while updating the combined statement with userId" + userId);
					result.addParam(new Param(ErrorCodeEnum.ERROR_CODE_KEY,
							String.valueOf(ErrorCodeEnum.ERR_28028.getErrorCode())));
					result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28028.getMessage()));
					updateFailureStatus(id, ErrorCodeEnum.ERR_28028.getMessage(), dcRequest);
					return result;
				}
			} else {
				id = CommonUtils.generateUniqueID(UNIQUE_ID_LENGTH);
				data.put("id", id);
				LOG.info("File doesnot exists for user so creating new row");
				Result createStatementFiles = HelperMethods.callApi(dcRequest, data,
						HelperMethods.getHeaders(dcRequest), URLConstants.ACCOUNTS_STATEMENT_FILES_CREATE);

				if (createStatementFiles.getParamValueByName("opstatus") != null
						&& !createStatementFiles.getParamValueByName("opstatus").toString().equalsIgnoreCase("0")) {
					LOG.error("Error while updating the row with userId" + userId);
					result.addParam(new Param(ErrorCodeEnum.ERROR_CODE_KEY,
							String.valueOf(ErrorCodeEnum.ERR_28029.getErrorCode())));
					result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28029.getMessage()));
					return result;
				}
			}
			Map<String, String> user = HelperMethods.getUserFromIdentityService(dcRequest);

			data.put("currencyCode", currencyCode);
			data.put("dateFormat", dateFormat);
			data.put("generatedBy", user.get("userName"));
			data.put("bankName", bankName);
			if (data.containsKey("failureMessage")) {
				data.remove("failureMessage");
			}
			if (data.containsKey("lastmodifiedts")) {
				data.remove("lastmodifiedts");
			}
			Map<String, String> userProfile = HelperMethods.getCustomerFromIdentityService(dcRequest);
			if (userProfile.containsKey("customerType")) {
				data.put("customerType", userProfile.get("customerType"));
			}
			String companyId = (String) dcRequest.getServicesManager().getIdentityHandler().getUserAttributes()
					.get("companyId");
			if (StringUtils.isNotBlank(companyId)) {
				data.put("companyId", companyId);
			}
			HashMap<String, String> pushEventData = new HashMap<String, String>();
			pushEventData.put("eventCode", "COMBINED_STATEMENT");
			pushEventData.put("eventData", data.toString());

			Result pushResult = HelperMethods.callApi(dcRequest, pushEventData, HelperMethods.getHeaders(dcRequest),
					URLConstants.PUSH_EVENT);

			if (pushResult.getParamValueByName("success") != null
					&& pushResult.getParamValueByName("success").equals("false")) {
				LOG.error("Error while creating event");
				result.addParam(ErrorCodeEnum.ERROR_CODE_KEY, pushResult.getParamValueByName("dbpErrCode"));
				result.addParam(ErrorCodeEnum.ERROR_MESSAGE_KEY, pushResult.getParamValueByName("dbpErrMsg"));
				updateFailureStatus(id, pushResult.getParamValueByName("dbpErrMsg"), dcRequest);
				return result;
			}

		} catch (Exception exception) {
			LOG.error("Error occured while generating combined statement", exception);
			result.addParam(ErrorCodeEnum.ERROR_CODE_KEY, String.valueOf(ErrorCodeEnum.ERR_28030.getErrorCode()));
			result.addParam(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28030.getMessage());
		}
		return result;
	}

	public static void updateFailureStatus(String fileId, String message, DataControllerRequest dcRequest) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", fileId);
		data.put("status", Constants.STATUS_FAIL);
		data.put("failureMessage", message);
		try {
			HelperMethods.callApi(dcRequest, data, HelperMethods.getHeaders(dcRequest),
					URLConstants.ACCOUNTS_STATEMENT_FILES_UPDATE);
		} catch (HttpCallException e) {
			LOG.error("Error while updating failure");
		}
	}

}
