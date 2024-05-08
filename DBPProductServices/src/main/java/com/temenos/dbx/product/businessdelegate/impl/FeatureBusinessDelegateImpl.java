package com.temenos.dbx.product.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.temenos.dbx.product.businessdelegate.api.FeatureBusinessDelegate;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.javaservice.AccountActionApprovalListGetServiceOperation;

/**
 * 
 * @author sowmya.vandanapu
 *
 */

public class FeatureBusinessDelegateImpl implements FeatureBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(FeatureBusinessDelegateImpl.class);

	/**
	 * @param featureDTO
	 * @param headersMap
	 * @return list of all features at FI Level
	 * @throws ApplicationException
	 * 
	 */
	@Override
	public DBXResult getFeatures(Map<String, Object> headersMap) throws ApplicationException {

		DBXResult result = new DBXResult();
		try {
			Map<String, Object> inputParams = new HashMap<>();
			inputParams.put(DBPUtilitiesConstants.SELECT,
					"id,name,description,Type_id,Status_id,DisplaySequence,isPrimary");
			inputParams.put(DBPUtilitiesConstants.ORDERBY, "name");
			JsonObject featuresJsonResponse = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
					URLConstants.FEATURE_GET);
			result.setResponse(featuresJsonResponse);
		} catch (Exception e) {
			LOG.error("Exception occured while fetching the features" + e.getMessage());
			result.setError(ErrorCodeEnum.ERR_10226);
		}

		return result;
	}

}
