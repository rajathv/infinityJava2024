package com.temenos.infinity.api.chequemanagement.resource.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.commonsutils.LogEvents;
import com.temenos.infinity.api.chequemanagement.businessdelegate.api.CreateChequeBookBusinessDelegate;
import com.temenos.infinity.api.chequemanagement.constants.ChequeManagementConstants;
import com.temenos.infinity.api.chequemanagement.constants.Constants;
import com.temenos.infinity.api.chequemanagement.dto.BBRequestDTO;
import com.temenos.infinity.api.chequemanagement.dto.ChequeBook;
import com.temenos.infinity.api.chequemanagement.dto.ChequeBookAction;
import com.temenos.infinity.api.chequemanagement.dto.ChequeBookStatusDTO;
import com.temenos.infinity.api.chequemanagement.resource.api.CreateChequeBookResource;
import com.temenos.infinity.api.chequemanagement.utils.AccountUtilities;
import com.temenos.infinity.api.chequemanagement.utils.ChequeManagementUtilities;
import com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler;

public class CreateChequeBookResourceImpl implements CreateChequeBookResource {

    private static final Logger LOG = LogManager.getLogger(CreateChequeBookResourceImpl.class);

    @Override
    public Result createChequeBook(ChequeBook chequeBook, DataControllerRequest request) {

        ChequeBook chequeBookOrder = new ChequeBook();
        AccountUtilities ac = new AccountUtilities();
        Result result = new Result();

        String validate = chequeBook.getValidate();
        String accountID = chequeBook.getAccountID();
        String chequeType = "";
        String chequeIssueId = chequeBook.getChequeIssueId() != "" ? chequeBook.getChequeIssueId() : "";
        if (StringUtils.isBlank(accountID)) {
            if (StringUtils.isNotBlank(chequeIssueId)) {
                String ChequeIssueIds[] = chequeIssueId.split("\\.");
                if (ChequeIssueIds != null && ChequeIssueIds.length > 0) {
                    accountID = ChequeIssueIds[1];
                    chequeType = ChequeIssueIds[0];
                    chequeBook.setAccountID(accountID);
                }
            }
        }
        
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String createdby = CustomerSession.getCustomerId(customer);
		
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("featureActionId", Constants.FEATURE_ACTION_ID);
		inputParams.put("accountId", chequeBook.getAccountID());
		inputParams.put("fees", chequeBook.getFees());
		inputParams.put("NoOfChequeBooks", chequeBook.getNumberOfChequeBooks());
		inputParams.put("isApprovalRequired",chequeBook.getSignatoryApprovalRequired());
		
        
        // For validate request , send back the dummy response
        if (StringUtils.isNotBlank(validate) && validate.equalsIgnoreCase("true")) {
        	try {
        	 String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getServerAppProperty("PAYMENT_BACKEND"); 
           	if ("MOCK".equalsIgnoreCase(PAYMENT_BACKEND)) {
           		HashMap<String, Object> headerParams = new HashMap<String, Object>();
        		HashMap<String, Object> in = new HashMap<String, Object>();
           		 String serviceName = "ChequeManagementMock";
                  String operationName = "validateCreateChequeMock";

                  Map<String, Object> requestParameters = (Map<String, Object>)  new HashMap<String, Object>();
//                  String response =  DBPServiceExecutorBuilder.builder().
//                          withServiceId(serviceName).
//                          withObjectId(null).
//                          withOperationId(operationName).
//                          withRequestParameters(requestParameters).
//                          withRequestHeaders(request.getHeaderMap()).
//                          withDataControllerRequest(request).
//                          build().getResponse();
                  
                   result = (CommonUtils.callIntegrationService(request, in, headerParams, serviceName, operationName,
   						true));
                   return result;
           	}
     	}
     	catch(Exception e) {
             LOG.error("Error in getting validate mock " + e);
         }



            if (StringUtils.isNotBlank(accountID)) {

                String customerId = "";
                try {
                    customerId = (String) request.getServicesManager().getIdentityHandler().getUserAttributes()
                            .get(ChequeManagementConstants.PARAM_CUSTOMER_ID);
                } catch (Exception e) {
                    LOG.error("Unable to fetch the customer id from session" + e);
                }

                if (StringUtils.isBlank(customerId))
                    return ErrorCodeEnum.ERR_26014.setErrorCode(new Result());

                if (!ac.validateInternalAccount(customerId, accountID)) {
                    return ErrorCodeEnum.ERR_26008.setErrorCode(new Result());
                }
                if (accountID.contains("-")) {
                    accountID = ChequeManagementUtilities.RemoveCompanyId(accountID);
                    chequeBook.setChequeIssueId(chequeType + "." + accountID);
                }

                try {
                    CreateChequeBookBusinessDelegate orderBusinessDelegate = DBPAPIAbstractFactoryImpl
                            .getBusinessDelegate(CreateChequeBookBusinessDelegate.class);
                    chequeBookOrder = orderBusinessDelegate.validateChequeBook(chequeBook, request);
                    if (StringUtils.isBlank(chequeBookOrder.getChequeIssueId())) {
                        String dbpErrCode = chequeBookOrder.getCode();
                        String dbpErrMessage = chequeBookOrder.getMessage();
                        if (StringUtils.isNotBlank(dbpErrMessage)) {
                            String msg = ErrorCodeEnum.ERR_26020.getMessage(dbpErrMessage);
                            return ErrorCodeEnum.ERR_26020.setErrorCode(new Result(), msg);
                        }
                        return ErrorCodeEnum.ERR_26016.setErrorCode(new Result());
                    }
                    if(StringUtils.isNotBlank(chequeBookOrder.getRecordVersion())) {
                    	String msg = ErrorCodeEnum.ERR_26020.getMessage("A cheque book request has already been placed, cannot place multiple requests");
                        return ErrorCodeEnum.ERR_26020.setErrorCode(new Result(), msg);
                    }
                    JSONObject chequeBookOrderDTO = new JSONObject(chequeBookOrder);
                    result = JSONToResult.convert(chequeBookOrderDTO.toString());
                    
                } catch (Exception e) {
                    LOG.error(e);
                    LOG.debug("Failed to validate cheque book request in OMS " + e);
                    return ErrorCodeEnum.ERR_26015.setErrorCode(new Result());
                }
                return result;
            }
        }

        try {

            CreateChequeBookBusinessDelegate orderBusinessDelegate = DBPAPIAbstractFactoryImpl
                    .getBusinessDelegate(CreateChequeBookBusinessDelegate.class);
            chequeBookOrder = orderBusinessDelegate.createChequeBook(chequeBook, request);
                if (StringUtils.isBlank(chequeBookOrder.getChequeIssueId())) {
    				if (StringUtils.isNotBlank(chequeBookOrder.getMessage())) {
    					String msg = ErrorCodeEnum.ERR_26010.getMessage(chequeBookOrder.getMessage());
    					return ErrorCodeEnum.ERR_26010.setErrorCode(new Result(), msg);
    				}
    				String msg = ErrorCodeEnum.ERR_26010.getMessage("");
    				return ErrorCodeEnum.ERR_26010.setErrorCode(new Result(),msg);
    			}
            JSONObject chequeBookOrderDTO = new JSONObject(chequeBookOrder);
            result = JSONToResult.convert(chequeBookOrderDTO.toString());
            
            inputParams.put("transactionId", chequeBookOrder.getChequeIssueId());
			try {
				LogEvents.pushAlertsForApprovalRequests(Constants.FEATURE_ACTION_ID, request, null, inputParams,
						null, null, null, CustomerSession.getCustomerName(customer), null);
			} catch (Exception e) {
				LOG.error("Failed at pushAlertsForApprovalRequests "+e);
			}

        } catch (Exception e) {
            LOG.error(e);
            LOG.debug("Failed to create cheque book request in OMS " + e);
            return ErrorCodeEnum.ERR_26011.setErrorCode(new Result());
        }
        return result;
    }

	@Override
	public Result executeChequeBookRequestAfterApproval(String methodId, Object[] inputArray,
			DataControllerRequest request, DataControllerResponse response) {
		
		Result result = new Result();
		try {
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);
			String customerId = CustomerSession.getCustomerId(customer);
			Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
			String featureActionId = request.getParameter("featureActionId") != null ? request.getParameter("featureActionId").toString() : null;
			String serviceRequestId = request.getParameter("serviceRequestId") != null ? request.getParameter("serviceRequestId").toString() : null;
			String requestId = request.getParameter("requestId") != null ? request.getParameter("requestId").toString() : "";
			String comments = inputParams.get("comments") != null ? inputParams.get("comments").toString() : "";
			String signatoryApproved =  inputParams.get("signatoryApproved") != null ? inputParams.get("signatoryApproved").toString() : "";
			
			ChequeBookAction chequeBookOrder = new ChequeBookAction();
			chequeBookOrder.setRequestId(serviceRequestId);
			chequeBookOrder.setComments(comments);
			chequeBookOrder.setSignatoryAction(signatoryApproved);
			
			CreateChequeBookBusinessDelegate chequeBookBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(CreateChequeBookBusinessDelegate.class);
			BBRequestDTO bbrequest = chequeBookBusinessDelegate.getAccountId(requestId);
			
			ChequeBookStatusDTO chequeBookDTO = new ChequeBookStatusDTO();
			chequeBookDTO.setAccountId(bbrequest.getAccountId());
			chequeBookDTO.setRequestId(requestId);
			chequeBookDTO.setFeatureActionID(featureActionId);
			chequeBookDTO.setCustomerId(customerId);
			
			chequeBookDTO = chequeBookBusinessDelegate.validateForApprovals(chequeBookDTO, request);
			
			if (chequeBookDTO == null) {
				LOG.error("Error occured while validating for approvals");
				return ErrorCodeEnum.ERR_26011.setErrorCode(new Result());
			}
			if (chequeBookDTO.getStatus().equalsIgnoreCase("sent")) {
				
				try {

		            chequeBookOrder = chequeBookBusinessDelegate.executeChequeBookRequestAfterApproval(chequeBookOrder, request);

		            JSONObject chequeBookOrderDTO = new JSONObject(chequeBookOrder);
		            result = JSONToResult.convert(chequeBookOrderDTO.toString());

		        } catch (Exception e) {
		            LOG.error(e);
		            LOG.debug("Failed to create cheque book request in OMS " + e);
		            return ErrorCodeEnum.ERR_26011.setErrorCode(new Result());
		        }
			} else if (chequeBookDTO.getStatus().equalsIgnoreCase("pending")) {
				//return pending status;
				
			}
			
			
			
		}
		catch (Exception e) {
			LOG.error("Caught exception at approve method: " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(result);
		}
		return result;
	}


}