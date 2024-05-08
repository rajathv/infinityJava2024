package com.kony.dbputilities.customersecurityservices;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class IsEmailLinkActive implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(IsEmailLinkActive.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {

		Result result = new Result();

		String id = dcRequest.getParameter("identifier");

		try {
			id = new String(java.util.Base64.getDecoder().decode(id));

			String[] strings = id.split("_");

			id = strings[0];

		} catch (Exception e) {
			LOG.error(e.getMessage());
		}

		if (StringUtils.isBlank(id)) {
			ErrorCodeEnum.ERR_12429.setErrorCode(result);
			result.addParam(new Param("status", Boolean.toString(false), MWConstants.STRING));
			return result;
		}

		Result existingRecord = HelperMethods.getActivationRecordByActivationId(id, dcRequest);
		String createdDate;
		boolean isLinkActive = false;
		PasswordHistoryManagement pm = new PasswordHistoryManagement(dcRequest, true);

		if (HelperMethods.hasRecords(existingRecord)) {
			createdDate = HelperMethods.getFieldValue(existingRecord, "createdts");
			if (createdDate.trim().isEmpty()) {
				ErrorCodeEnum.ERR_12429.setErrorCode(result);
				result.addParam(new Param("status", Boolean.toString(false), MWConstants.STRING));
				return result;
			} else {
				isLinkActive = HelperMethods.isDateInRange(createdDate, pm.getRecoveryEmailLinkValidity());
				if (isLinkActive) {
					HelperMethods.setSuccessMsg(DBPUtilitiesConstants.SUCCESS_MSG, result);
					result.addParam(new Param("status", "true", MWConstants.STRING));
				} else {
					ErrorCodeEnum.ERR_12429.setErrorCode(result);
					result.addParam(new Param("status", "false", MWConstants.STRING));
				}
				return result;
			}
		} else {
			ErrorCodeEnum.ERR_12429.setErrorCode(result);
			result.addParam(new Param("status", Boolean.toString(false), MWConstants.STRING));
			return result;
		}

	}

}
