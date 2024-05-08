/**
 * 
 */
package com.temenos.infinity.api.wealth.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealth.tap.preandpostprocessors.ComputeStrategyTAPOrchPreProcessor;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * @author himaja.sridhar
 *
 */
public class ModelConstraintDBUtil {

	private static final Logger LOG = LogManager.getLogger(ModelConstraintDBUtil.class);
	/**
	 * (INFO) Fetches the model Constraint if exists from the DB.
	 * 
	 * @param inputMap
	 * @return {@link JSONObject}
	 * @author 22952
	 * @return 
	 */

	public static String getModelConstraint(Map<String, Object> inputMap, DataControllerRequest request) {
		String portfolioCode = inputMap.get("portfolioCode").toString();
		String filter = null;
		JSONObject resultJson = new JSONObject();
		filter = "portfolioCode" + DBPUtilitiesConstants.EQUAL + portfolioCode;
		Map<String, String> input = new HashMap<>();
		input.put(DBPUtilitiesConstants.FILTER, filter);
		Result getResult = null;
		try {
			getResult = HelperMethods.callGetApi(request, input.get(DBPUtilitiesConstants.FILTER),
					HelperMethods.getHeaders(request), URLConstants.INF_WLTH_MODEL_CONSTRAINT_GET);
		} catch (HttpCallException e) {
			LOG.error("Unable to fetch data from the table.Error: ",e.getMessage());
		}
		if (HelperMethods.hasRecords(getResult)) {
			List<Dataset> dataset = getResult.getAllDatasets();
			List<Record> drecords = dataset.get(0).getAllRecords();
			resultJson = CommonUtils.convertRecordToJSONObject(drecords.get(0));
			String constraintId = resultJson.get("constraintId").toString();
			if (constraintId != null && constraintId.length() > 0) {
				String modelConstraintId = constraintId;
				return modelConstraintId;
			}

		}
		return null;
		
	}
	
	/**
	 * (INFO) Updates the model Constraint in the DB.
	 * 
	 * @param inputMap
	 * @return {@link JSONObject}
	 * @author 22952
	 * @return 
	 */

	public static String updateModelConstraint(String constraintId, DataControllerRequest request, Result result, Map<String, Object> inputMap) 
	{
		if (constraintId != null) {
			String constraintVal = result.getRecordById("body").getParamValueByName("id");
			inputMap.put("constraintId",constraintVal);
			inputMap.put("modelConstraintId", constraintVal);
			String portObj = result.getRecordById("body").getParamValueByName("portObject");
			inputMap.put("portfolioCode", portObj);
			inputMap.put(TemenosConstants.PORTFOLIOID, request.getParameter("portfolioId").toString());
			String customerId = HelperMethods.getCustomerIdFromSession(request);
			inputMap.put(TemenosConstants.CUSTOMERID, customerId);
			inputMap.put("modifiedby", customerId);
			try {
				HelperMethods.callApi(request, inputMap, HelperMethods.getHeaders(request),
						URLConstants.INF_WLTH_MODEL_CONSTRAINT_UPDATE);
			} catch (HttpCallException e) {
				LOG.error("Unable to update data in the table.Error: ",e.getMessage());
			}
			return constraintVal;
		}
		return null;
		
	}
	/**
	 * (INFO) Creates the model Constraint in the DB.
	 * 
	 * @param inputMap
	 * @return {@link JSONObject}
	 * @author 22952
	 * @return 
	 */

	public static String createModelConstraint(String createMessage,String constraintId, DataControllerRequest request, Result result, Map<String, Object> inputMap) 
	{
		result.addParam("message", createMessage);
		constraintId = result.getRecordById("body").getParamValueByName("id");
		inputMap.put("modelConstraintId", constraintId);
		String portObj = result.getRecordById("body").getParamValueByName("portObject");
		inputMap.put("constraintId", constraintId);
		inputMap.put("portfolioCode", portObj);
		inputMap.put(TemenosConstants.PORTFOLIOID, request.getParameter("portfolioId").toString());
		String customerId = HelperMethods.getCustomerIdFromSession(request);
		inputMap.put(TemenosConstants.CUSTOMERID, customerId);
		inputMap.put("createdby", customerId);
		inputMap.put("modifiedby", customerId);
		try {
			HelperMethods.callApi(request, inputMap, HelperMethods.getHeaders(request),
					URLConstants.INF_WLTH_MODEL_CONSTRAINT_CREATE);
		} catch (HttpCallException e) {
			LOG.error("Unable to add data to the table.Error: ",e.getMessage());
		}
		return constraintId;
		
	}
}
