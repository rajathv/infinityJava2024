package com.temenos.dbx.product.approvalservices.businessdelegate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApproversBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.FeatureActionBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FeatureActionDTO;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

/**
 * 
 * @author KH2387
 * @version 1.0 Extends the {@link ApproversBusinessDelegate}
 */
public class ApproversBusinessDelegateImpl implements ApproversBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(ApproversBusinessDelegate.class);

	/**
	 *  method to fetch approver Ids for a given actionId,accountId and organizationId
	 *  @param String companyId
	 *  @param String accountId
	 *  @param String actionId
	 *  return list of approvers
	 */
	@Override
	public List<String> getAccountActionApproverList(String contractId, String cif, String accountIds, String actionId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_GETACCOUNTACTIONAPPROVERLIST;

		HashMap<String, Object> requestParameters = new HashMap<>();
		HashMap<String, Object> requestHeaders = new HashMap<>();

		FeatureActionBusinessDelegate featureActionBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(FeatureActionBusinessDelegate.class);
		FeatureActionDTO featureActionDTO = featureActionBusinessDelegate.getFeatureActionById(actionId);

		requestParameters.put("_contractId", contractId);
		requestParameters.put("_cif", cif);
		requestParameters.put("_accountIds", accountIds);
		requestParameters.put("_approvalActionList", featureActionDTO.getApproveFeatureAction());
		requestParameters.put("_featureId", featureActionDTO.getFeatureId());

		try {
			String approversResponse = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
					operationName, requestParameters, requestHeaders, "");
			JSONObject approversResponseJson = new JSONObject(approversResponse);
			if (approversResponseJson.has(Constants.RECORDS)
					&& approversResponseJson.getJSONArray(Constants.RECORDS).length() > 0) {
				LOG.info("approvers fetched successfully");
				JSONArray approversArray = approversResponseJson.getJSONArray(Constants.RECORDS);
				List<String> approvers = new ArrayList<String>();
				for(int i=0;i<approversArray.length();i++) {
					approvers.add(approversArray.getJSONObject(i).getString("id"));
				}				
				return approvers;
			} else {
				LOG.error("Unable to fetch approvers list: with approversResponse"+approversResponse);
				return null;
			}
		} catch (JSONException e) {
			LOG.error("Failed to fetch approvers list ");
			
			
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at getAccountActionApproverList method: ");
			
			
			return null;
		}
	}

	@Override
	public List<String> getRequestApproversList(String requestId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_GETREQUESTAPPROVERS_PROC;

		HashMap<String, Object> requestParameters = new HashMap<>();

		requestParameters.put("_requestId", requestId);
		requestParameters.put("_status", "");
		
		try {
			String approversResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			JSONObject approversResponseJson = new JSONObject(approversResponse);
			if (approversResponseJson.has(Constants.RECORDS)
					&& approversResponseJson.getJSONArray(Constants.RECORDS).length() > 0) {
				LOG.info("approvers fetched successfully");
				JSONArray approversArray = approversResponseJson.getJSONArray(Constants.RECORDS);
				List<String> approvers = new ArrayList<String>();
				for(int i=0;i<approversArray.length();i++) {
					String FullName = approversArray.getJSONObject(i).getString(Constants.FirstName) + " " + approversArray.getJSONObject(i).getString(Constants.LastName);
					approvers.add(FullName);
				}				
				return approvers;
			} else {
				LOG.error("Unable to fetch approvers list: with approversResponse: "+approversResponse);
				return null;
			}
		} catch (JSONException e) {
			LOG.error("Failed to fetch approvers list ",e);
			
			
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at getRequestApproverList method: ",e);
			
			return null;
		}
	}
	
	@Override
	public List<String> getRequestActedApproversList(String requestId, String status) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_GETREQUESTAPPROVERS_PROC;

		HashMap<String, Object> requestParameters = new HashMap<>();

		requestParameters.put("_requestId", requestId);
		requestParameters.put("_status", status);
		
		try {
			String approversResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			JSONObject approversResponseJson = new JSONObject(approversResponse);
			if (approversResponseJson.has(Constants.RECORDS)
					&& approversResponseJson.getJSONArray(Constants.RECORDS).length() > 0) {
				LOG.info("approvers fetched successfully");
				JSONArray approversArray = approversResponseJson.getJSONArray(Constants.RECORDS);
				List<String> approvers = new ArrayList<String>();
				for(int i=0;i<approversArray.length();i++) {
					String FullName = approversArray.getJSONObject(i).getString(Constants.FirstName) + " " + approversArray.getJSONObject(i).getString(Constants.LastName);
					approvers.add(FullName);
				}				
				return approvers;
			} else {
				LOG.error("Unable to fetch approvers list: with approversResponse: "+approversResponse);
				return null;
			}
		} catch (JSONException e) {
			LOG.error("Failed to fetch approvers list ",e);
			
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at getRequestActedApproversList method: ",e);
			
			return null;
		}
	}
}
