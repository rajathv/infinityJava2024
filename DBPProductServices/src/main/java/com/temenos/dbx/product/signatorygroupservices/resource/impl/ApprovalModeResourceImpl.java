package com.temenos.dbx.product.signatorygroupservices.resource.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kony.dbputilities.util.LegalEntityUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.infinity.dbx.dbp.jwt.auth.AuthConstants;
import com.temenos.dbx.product.approvalmatrixservices.businessdelegate.api.ApprovalMatrixBusinessDelegate;
import com.temenos.dbx.product.approvalmatrixservices.businessdelegate.api.ApprovalRuleBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.contract.backenddelegate.api.ContractCoreCustomerBackendDelegate;
import com.temenos.dbx.product.contract.businessdelegate.api.ContractFeatureActionsBusinessDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.signatorygroupservices.businessdelegate.api.ApprovalModeBusinessDelegate;
import com.temenos.dbx.product.signatorygroupservices.businessdelegate.api.SignatoryGroupBusinessDelegate;
import com.temenos.dbx.product.signatorygroupservices.dto.ApprovalModeDTO;
import com.temenos.dbx.product.signatorygroupservices.dto.SignatoryGroupDTO;
import com.temenos.dbx.product.signatorygroupservices.resource.api.ApprovalModeResource;
import com.temenos.dbx.product.signatorygroupservices.resource.api.SignatoryGroupResource;

public class ApprovalModeResourceImpl implements ApprovalModeResource {

	private static final Logger LOG = LogManager.getLogger(ApprovalModeResourceImpl.class);

	@Override
	public Result fetchApprovalMode(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> headersMap = HelperMethods.addJWTAuthHeader(request, request.getHeaderMap(),
				AuthConstants.POST_LOGIN_FLOW);

		ApprovalModeBusinessDelegate approvalModeBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(ApprovalModeBusinessDelegate.class);

		String coreCustomerId = (inputParams.get("coreCustomerId")==null)?"":inputParams.get("coreCustomerId").toString();
		String contractId = (inputParams.get("contractId")==null)?"":inputParams.get("contractId").toString();
		
		if (StringUtils.isEmpty(contractId)) {
			return ErrorCodeEnum.ERR_21024.setErrorCode(new Result());
		}	
		
		if (StringUtils.isEmpty(coreCustomerId)) {
			return ErrorCodeEnum.ERR_21025.setErrorCode(new Result());
		}
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		boolean hasPermission = true;
		String userId = CustomerSession.getCustomerId(customer);
		/*if( !CustomerSession.IsAPIUser(customer) )
		{			
			hasPermission = approvalModeBusinessDelegate.checkForApprovalMatrixPermission(coreCustomerId,
					contractId, userId, headersMap, request);
		}*/
		if (hasPermission) {
			try {
				ApprovalModeDTO approvalDTO = approvalModeBusinessDelegate.fetchApprovalMode(coreCustomerId, contractId,
						userId, headersMap, request);
				if(approvalDTO == null) {
					LOG.error("Error while fetching approvalMode : ");
					return ErrorCodeEnum.ERR_21017.setErrorCode(new Result());
				}

				JSONObject responseObj = new JSONObject(approvalDTO);
			    responseObj.remove("deletedRecords");
			    responseObj.remove("updatedRecords");
				result = JSONToResult.convert(responseObj.toString());
				return result;
			} catch (Exception e) {
				LOG.error("Error while fetching approvalMode : " + e);
				return ErrorCodeEnum.ERR_21017.setErrorCode(new Result());
			}
		} else {
			return ErrorCodeEnum.ERR_21017.setErrorCode(new Result());
		}
	}

	@Override
	public Result updateApprovalMode(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> headersMap = HelperMethods.addJWTAuthHeader(request, request.getHeaderMap(),
				AuthConstants.POST_LOGIN_FLOW);

		ApprovalModeBusinessDelegate approvalModeBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(ApprovalModeBusinessDelegate.class);
		
		ApprovalMatrixBusinessDelegate approvalMatrixBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(ApprovalMatrixBusinessDelegate.class);
		
		ContractCoreCustomerBackendDelegate contractCoreCustomerBD =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(ContractCoreCustomerBackendDelegate.class);

		String coreCustomerId = inputParams.get("coreCustomerId").toString();
		String contractId = inputParams.get("contractId").toString();

		String legalEntityId = null;
		try{
			legalEntityId = LegalEntityUtil.getLegalEntityForCifAndContract(coreCustomerId, contractId);
		} catch (ApplicationException ae){
			ae.getErrorCodeEnum().setErrorCode(new Result());
		}

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		
		boolean hasPermission = true;
		String userId = CustomerSession.getCustomerId(customer);
		if( !CustomerSession.IsAPIUser(customer) )
		{			
			hasPermission = approvalModeBusinessDelegate.checkForApprovalMatrixPermission(coreCustomerId,
					contractId, userId, headersMap, request);
		}
		Boolean isGroupLevel = false;
		int approvalMode =0;
		String isGlevel = inputParams.get("isGroupLevel").toString();
		if (isGlevel.equalsIgnoreCase("true") || isGlevel.equals("1")) {
			isGroupLevel = true;
			approvalMode = 1;
		}
		else {
			isGroupLevel = false;
			approvalMode = 0;
		}
		if (hasPermission) {
			try {
				 
				Map<String, Set<String>> contractCoreCustomerDetailsMap = contractCoreCustomerBD
						.getCoreCustomerAccountsFeaturesActions(contractId, coreCustomerId, request.getHeaderMap());

				Set<String> customerActions = contractCoreCustomerDetailsMap.get("actions");
				customerActions = approvalMatrixBusinessDelegate.getActionWithApproveFeatureAction(customerActions, request.getHeaderMap());

				approvalMatrixBusinessDelegate.callApprovalMatrixTemplateCleanupProc(contractId,
						customerActions.toArray(new String[0]), coreCustomerId, Constants.MAX_TRANSACTION_LIMIT.trim() + ","+ Constants.DAILY_LIMIT.trim() +"," + Constants.WEEKLY_LIMIT.trim() + "," + Constants.NON_MONETARY_LIMIT.trim() );
				
				approvalMatrixBusinessDelegate.createApprovalMatrixTemplateDefaultEntry(contractId,
						customerActions.toArray(new String[0]), coreCustomerId, approvalMode, legalEntityId);
				
				ApprovalModeDTO approvalDTO = approvalModeBusinessDelegate.updateApprovalMode(coreCustomerId,
						contractId, isGroupLevel, headersMap, request);

				// JSONArray rulesJSONArr = new JSONArray(approvalDTO);
				JSONObject responseObj = new JSONObject();
				if (approvalDTO != null && approvalDTO.getUpdatedRecords() > 0)
					responseObj.put("status", "Updated Successfully");
				else if (approvalDTO != null && approvalDTO.getUpdatedRecords() == 0)
					responseObj.put("status", "Created Successfully");
				result = JSONToResult.convert(responseObj.toString());
				return result;
			} catch (Exception e) {
				LOG.error("Error while fetching SavingsPotCategoriesDTO from SavingsPotBusinessDelegate : " + e);
				return new Result();
			}
		} else {
			return ErrorCodeEnum.ERR_21017.setErrorCode(new Result());
		}
	}

	@Override
	public Result deleteApprovalMode(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> headersMap = HelperMethods.addJWTAuthHeader(request, request.getHeaderMap(),
				AuthConstants.POST_LOGIN_FLOW);

		ApprovalModeBusinessDelegate approvalModeBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(ApprovalModeBusinessDelegate.class);

		String coreCustomerId = inputParams.get("coreCustomerId").toString();
		String contractId = inputParams.get("contractId").toString();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);
		boolean hasPermission = approvalModeBusinessDelegate.checkForApprovalMatrixPermission(coreCustomerId,
				contractId, userId, headersMap, request);
		if (hasPermission) {
			try {
				ApprovalModeDTO approvalDTO = approvalModeBusinessDelegate.deleteApprovalMode(coreCustomerId,
						contractId, headersMap, request);

				// JSONArray rulesJSONArr = new JSONArray(approvalDTO);
				JSONObject responseObj = new JSONObject();
				if (approvalDTO != null && approvalDTO.getDeletedRecords() > 0)
					responseObj.put("status", "Deleted Successfully");
				result = JSONToResult.convert(responseObj.toString());
				return result;
			} catch (Exception e) {
				LOG.error("Error while fetching SavingsPotCategoriesDTO from SavingsPotBusinessDelegate : " + e);
				return null;
			}
		} else {
			return ErrorCodeEnum.ERR_21017.setErrorCode(new Result());
		}
	}
	
}
