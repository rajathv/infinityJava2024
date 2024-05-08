package com.temenos.dbx.product.payeeservices.backenddelegate.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.InterBankPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.dto.InterBankPayeeBackendDTO;

/**
 * @author Subrahmanyam Yadav
 * @version 1.0
 * implements {@link InterBankPayeeBackendDelegate}
 */
public class InterBankPayeeBackendDelegateImpl implements InterBankPayeeBackendDelegate {
	
	private static final Logger LOG = LogManager.getLogger(InterBankPayeeBackendDelegateImpl.class);

	@Override
	public List<InterBankPayeeBackendDTO> fetchPayees(Set<String> payeeIds, 
			Map<String, Object> headerParams, DataControllerRequest dcRequest) {
		
        List<InterBankPayeeBackendDTO> backendPayeeDTOs = new ArrayList<>();

        String serviceName = ServiceId.BACKENDPAYEESERVICESORCH;
		String operationName = OperationName.BACKENDINTERBANKPAYEEGETORCH;
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
		if (CustomerSession.IsBusinessUser(customer)) {
			serviceName = "BackendPayeeServicesOrch";
			operationName = "backendInterBankPayeeGetOrch";
		} 
       
        Map<String, Object> requestParameters = new HashMap<String, Object>();
        requestParameters.put("id", String.join(",", payeeIds));
        requestParameters.put("loop_count", String.valueOf(payeeIds.size()));

        String payeeResponse = null;
        try {
            payeeResponse = DBPServiceExecutorBuilder.builder().
                    withServiceId(serviceName).
                    withObjectId(null).
                    withOperationId(operationName).
                    withRequestParameters(requestParameters).
                    withRequestHeaders(headerParams).
                    withDataControllerRequest(dcRequest).
                    build().getResponse();
            
            JSONObject responseObj = new JSONObject(payeeResponse);
			JSONArray records = responseObj.getJSONArray("LoopDataset");
			for(int i = 0; i < records.length(); i++) {
				List<InterBankPayeeBackendDTO> payeeDTOs = new ArrayList<InterBankPayeeBackendDTO>();
				responseObj = records.getJSONObject(i);
	            if(responseObj.has("errmsg") || responseObj.has("dbpErrMsg") || responseObj.has("errorMessage")) {
	                InterBankPayeeBackendDTO payee = new InterBankPayeeBackendDTO();
	                payee = JSONUtils.parse(responseObj.toString(), InterBankPayeeBackendDTO.class);
	                backendPayeeDTOs.add(payee);
	                return backendPayeeDTOs;
	            }
	            JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
	            if(jsonArray != null)
					payeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), InterBankPayeeBackendDTO.class);
				backendPayeeDTOs.addAll(payeeDTOs);
			}
			
        }
        catch(JSONException e) {
            LOG.error("Failed to fetch interbank payees: " + e);
            return null;
        }
        catch(Exception e) {
            LOG.error("Caught exception at fetchPayeesFromBackend: " + e);
            return null;
        }

        return backendPayeeDTOs;
	}

    @Override
    public String validateForApprovals( DataControllerRequest request, Map<String, Object> requestParameters) {
        String serviceName = ServiceId.DBP_APPROVAL_REQUEST_SERVICES;
        String operationName = OperationName.VALIDATE_FOR_APPROVALS;
        try {
            String response = DBPServiceExecutorBuilder.builder().
                    withServiceId(serviceName).
                    withObjectId(null).
                    withOperationId(operationName).
                    withRequestParameters(requestParameters).
                    withRequestHeaders(request.getHeaderMap()).
                    withDataControllerRequest(request).
                    build().getResponse();
            return response;
        } catch ( Exception exception ) {
            return null;
        }
    }

    @Override
    public String checkIfPayeeStatusInPending(DataControllerRequest request, Map<String, Object> requestParameters ) {
        String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_BBREQUEST_GET;
        try {
            String response = DBPServiceExecutorBuilder.builder().
                    withServiceId(serviceName).
                    withObjectId(null).
                    withOperationId(operationName).
                    withRequestParameters(requestParameters).
                    withRequestHeaders(request.getHeaderMap()).
                    withDataControllerRequest(request).
                    build().getResponse();
            return response;
        } catch ( Exception exception ) {
            return null;
        }
    }

    @Override
    public InterBankPayeeBackendDTO createPayee(InterBankPayeeBackendDTO interBankPayeeBackendDTO, Map<String, Object> headerParams, DataControllerRequest dcRequest) {
        String serviceName = ServiceId.INTER_BANK_PAYEE_LINE_OF_BUSINESS_SERVICE;
        String operationName = OperationName.INTER_BANK_PAYEE_BACKEND_CREATE;
		
        Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
		if (CustomerSession.IsBusinessUser(customer)) {
			serviceName = "InterBankPayeeLOB";
			operationName = "InterBankPayeeCreate";
		} 
		
        Map<String, Object> requestParameters;
        try {
            requestParameters = JSONUtils.parseAsMap(new JSONObject(interBankPayeeBackendDTO).toString(), String.class, Object.class);
        } catch (IOException e) {
            LOG.error("Error occurred while fetching the request params: " + e);
            return null;
        }
        
        requestParameters.put("IBAN", requestParameters.get("iban"));

        String createResponse = null;
        try {
            createResponse = DBPServiceExecutorBuilder.builder().
                    withServiceId(serviceName).
                    withObjectId(null).
                    withOperationId(operationName).
                    withRequestParameters(requestParameters).
                    withRequestHeaders(headerParams).
                    withDataControllerRequest(dcRequest).
                    build().getResponse();

            JSONObject response = new JSONObject(createResponse);
            interBankPayeeBackendDTO = JSONUtils.parse(response.toString(), InterBankPayeeBackendDTO.class);
        }
        catch (JSONException e) {
            LOG.error("Failed to create payee at payee table: " + e);
            return null;
        }
        catch (Exception e) {
            LOG.error("Caught exception at createPayeeAtBackend: " + e);
            return null;
        }

        return interBankPayeeBackendDTO;
    }
    
    @Override
    public InterBankPayeeBackendDTO deletePayee(InterBankPayeeBackendDTO interBankPayeeBackendDTO, Map<String, Object> headerParams, DataControllerRequest dcRequest) {
        String serviceName = ServiceId.INTER_BANK_PAYEE_LINE_OF_BUSINESS_SERVICE;
        String operationName = OperationName.INTER_BANK_PAYEE_BACKEND_DELETE;

        Map<String, Object> requestParameters;
        try {
            requestParameters = JSONUtils.parseAsMap(new JSONObject(interBankPayeeBackendDTO).toString(), String.class, Object.class);
        } catch (IOException e) {
            LOG.error("Error occurred while fetching the request params: " + e);
            return null;
        }
        
        requestParameters.put("IBAN", requestParameters.get("iban"));
        requestParameters.put("Id", requestParameters.get("id"));

        String deleteResponse = null;
        try {
            deleteResponse = DBPServiceExecutorBuilder.builder().
                    withServiceId(serviceName).
                    withObjectId(null).
                    withOperationId(operationName).
                    withRequestParameters(requestParameters).
                    withRequestHeaders(headerParams).
                    withDataControllerRequest(dcRequest).
                    build().getResponse();

            JSONObject responseObject = new JSONObject(deleteResponse);
            interBankPayeeBackendDTO = JSONUtils.parse(responseObject.toString(), InterBankPayeeBackendDTO.class);
        }
        catch (JSONException e) {
            LOG.error("Failed to delete payee from payee table: " + e);
            return null;
        }
        catch (Exception e) {
            LOG.error("Caught exception at deletePayeeAtBackend: " + e);
            return null;
        }

        return interBankPayeeBackendDTO;
    }
    
    @Override
    public InterBankPayeeBackendDTO editPayee(InterBankPayeeBackendDTO interBankPayeeBackendDTO, Map<String, Object> headerParams, DataControllerRequest dcRequest) {
        String serviceName = ServiceId.INTER_BANK_PAYEE_LINE_OF_BUSINESS_SERVICE;
        String operationName = OperationName.INTER_BANK_PAYEE_BACKEND_EDIT;

        Map<String, Object> requestParameters;
        try {
            requestParameters = JSONUtils.parseAsMap(new JSONObject(interBankPayeeBackendDTO).toString(), String.class, Object.class);
        } catch (IOException e) {
            LOG.error("Error occurred while fetching the request params: " + e);
            return null;
        }
        
        requestParameters.put("IBAN", requestParameters.get("iban"));
        requestParameters.put("Id", requestParameters.get("id"));

        String editResponse = null;
        try {
            editResponse = DBPServiceExecutorBuilder.builder().
                    withServiceId(serviceName).
                    withObjectId(null).
                    withOperationId(operationName).
                    withRequestParameters(requestParameters).
                    withRequestHeaders(headerParams).
                    withDataControllerRequest(dcRequest).
                    build().getResponse();

            JSONObject response = new JSONObject(editResponse);
            interBankPayeeBackendDTO = JSONUtils.parse(response.toString(), InterBankPayeeBackendDTO.class);
        }
        catch (JSONException e) {
            LOG.error("Failed to edit payee at payee table: " + e);
            return null;
        }
        catch (Exception e) {
            LOG.error("Caught exception at editPayeeAtBackend: " + e);
            return null;
        }

        return interBankPayeeBackendDTO;
    }

    @Override
	public boolean isValidIbanAndSwiftCode(String iban, String swiftcode, DataControllerRequest dcr) {
		return true;
	}
}
