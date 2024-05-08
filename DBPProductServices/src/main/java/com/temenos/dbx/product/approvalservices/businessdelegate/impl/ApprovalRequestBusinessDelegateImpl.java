package com.temenos.dbx.product.approvalservices.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalRequestBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestCountDTO;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

/**
 * 
 * @author KH1755
 * @version 1.0 Extends the {@link ApprovalRequestBusinessDelegate}
 */
public class ApprovalRequestBusinessDelegateImpl implements ApprovalRequestBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(ApprovalRequestBusinessDelegateImpl.class);

	@Override
	public ApprovalRequestCountDTO getCounts(String customerId, String approveActionList, String createActionList) {

		ApprovalRequestCountDTO approvalRequestCountDTO = new ApprovalRequestCountDTO();

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_APPROVAL_REQUEST_COUNT_FETCH;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("_customerId", customerId);
		requestParams.put("_approveActionList", approveActionList);
		requestParams.put("_createActionList", createActionList);
		
		try {
			String fetchResponse = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
					operationName, requestParams, null, "");
			
			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			
			if(jsonRsponse.has("records")) {
			JSONArray countJsonArray =  jsonRsponse.getJSONArray("records");
			for(Object record : countJsonArray) {
				JSONObject recordObj = (JSONObject) record;
				String key = recordObj.getString("TransactionType");
				long count = recordObj.getLong("count");
				switch(key) {
				case "ACHTransactionsForMyApproval":
					approvalRequestCountDTO.setAchTransactionsForMyApproval(count);
					break;
				case "ACHFilesForMyApproval":
					approvalRequestCountDTO.setAchFilesForMyApproval(count);
					break;
				case "GeneralTransactionsForMyApproval":
					approvalRequestCountDTO.setGeneralTransactionsForMyApproval(count);
					break;
				case "myRequestsWaiting":
					approvalRequestCountDTO.setMyRequestsWaiting(count);
					break;
				case "myRequestsRejected":
					approvalRequestCountDTO.setMyRequestsRejected(count);
					break;
				case "myRequestsApproved":
					approvalRequestCountDTO.setMyRequestsApproved(count);
					break;
				}
			}
			}
			
			if(jsonRsponse.has("records1")) {
			JSONArray countJsonArray = jsonRsponse.getJSONArray("records1") ;
			for(Object record : countJsonArray) {
				JSONObject recordObj = (JSONObject) record;
				String key = recordObj.getString("TransactionType");
				long count = recordObj.getLong("count");
				switch(key) {
				case "ACHTransactionsForMyApproval":
					approvalRequestCountDTO.setAchTransactionsForMyApproval(count);
					break;
				case "ACHFilesForMyApproval":
					approvalRequestCountDTO.setAchFilesForMyApproval(count);
					break;
				case "GeneralTransactionsForMyApproval":
					approvalRequestCountDTO.setGeneralTransactionsForMyApproval(count);
					break;
				case "myRequestsWaiting":
					approvalRequestCountDTO.setMyRequestsWaiting(count);
					break;
				case "myRequestsRejected":
					approvalRequestCountDTO.setMyRequestsRejected(count);
					break;
				case "myRequestsApproved":
					approvalRequestCountDTO.setMyRequestsApproved(count);
					break;
				}
			}
			}
		}

		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the counts",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while fetching the counts",exp);
			return null;
		}

		return approvalRequestCountDTO;
	}

}