package com.temenos.dbx.product.approvalmatrixservices.businessdelegate.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.approvalmatrixservices.businessdelegate.api.ApprovalRuleBusinessDelegate;
import com.temenos.dbx.product.approvalmatrixservices.dto.ApprovalRuleDTO;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

/**
 * 
 * @author KH2387
 * @version 1.0 Implements the {@link ApprovalRuleBusinessDelegate}
 */
public class ApprovalRuleBusinessDelegateImpl implements ApprovalRuleBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(ApprovalRuleBusinessDelegateImpl.class);

	/**
	 * method to get the Approval rules
	 * 
	 * @return list of {@link ApprovalRuleDTO}
	 */
	@Override
	public List<ApprovalRuleDTO> getApprovalRules() {

		List<ApprovalRuleDTO> approvalRules = new ArrayList<>();

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_APPROVALRULE_GET;

		HashMap<String, Object> requestParameters = new HashMap<>();
		HashMap<String, Object> requestHeaders = new HashMap<>();

		String approvalRulesResponse = null;
		JSONArray records = null;
		try {
			approvalRulesResponse = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
					operationName, requestParameters, requestHeaders, "");
			JSONObject approvalRulesJSON = new JSONObject(approvalRulesResponse);
			records = approvalRulesJSON.getJSONArray("approvalrule");
		} catch (JSONException e) {
			LOG.error("Unable to fetch approvalrule: " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at getAllRules method: " + e);
			return null;
		}

		try {
			approvalRules = JSONUtils.parseAsList(records.toString(), ApprovalRuleDTO.class);
		} 
		catch (IOException e) {
			LOG.error("Caught exception while parsing list: " + e);
			return null;
		}
		catch(NullPointerException e) {
			LOG.error("NullPointer Exception for records: " + e);
			return null;
		}
		
		return approvalRules;
	}

}
