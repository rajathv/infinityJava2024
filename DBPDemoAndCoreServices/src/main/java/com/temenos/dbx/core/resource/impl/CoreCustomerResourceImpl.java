package com.temenos.dbx.core.resource.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infinity.dbx.dbp.jwt.auth.AuthConstants;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.IntegrationTemplateURLFinder;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.core.businessdelegate.api.CoreCustomerBusinessDelegate;
import com.temenos.dbx.core.businessdelegate.impl.CoreCustomerBusinessDelegateImpl;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.PartyDTO;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.product.usermanagement.resource.api.CoreCustomerResource;
import com.temenos.dbx.product.utils.CustomerUtils;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.DTOUtils;
import com.temenos.dbx.product.utils.InfinityConstants;

public class CoreCustomerResourceImpl implements CoreCustomerResource,com.temenos.dbx.eum.product.usermanagement.resource.api.CoreCustomerResource {

    private LoggerUtil logger;

    @Override
    public Result saveFromParty(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        logger = new LoggerUtil(CoreCustomerBusinessDelegateImpl.class);
        Result result = new Result();

        String CoreURL = URLFinder.getPathUrl(URLConstants.CORE_CUSTOMER_CREATE, dcRequest);
        PartyDTO partyDTO = getPartyDTOFromInput(HelperMethods.getInputParamMap(inputArray), dcRequest);

        CoreCustomerBusinessDelegate coreCustomerDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class)
                .getBusinessDelegate(CoreCustomerBusinessDelegate.class);

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        for (Entry<String, String> entry : inputParams.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        
        Result backendData = getBackendDataParty(dcRequest, partyDTO);
        String customer_Id = HelperMethods.getFieldValue(backendData, "Customer_id");
        String companyId = HelperMethods.getFieldValue(backendData, "CompanyId");
        
        map.put("companyId", companyId);
        DBXResult response = new DBXResult();
		if (StringUtils.isBlank(getBackendType(dcRequest, customer_Id))) {
			logger.error("CreateDbxPropect : Printing before hitting the T24 : " + map);
			// map = HelperMethods.addJWTAuthHeader(dcRequest, map,
			// AuthConstants.PRE_LOGIN_FLOW);
			map.put("Authorization",
					"eyJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJJUklTIiwic3ViIjoidGVzdGV1MDEiLCJyb2xlSWQiOiJJTkZJTklUWS5SRVRBSUwiLCJpc3MiOiJGYWJyaWMiLCJkYnhVc2VySWQiOiIxNDgxNjI2ODIyIiwiZXhwIjoxNjg3MjA0Mzg4LCJpYXQiOjE1ODcxOTI1ODgsInVzZXJJZCI6InRlc3RldTAxIiwiX2lzc21ldGEiOiJodHRwczovL2RieGludDMua29ueWxhYnMubmV0Ojg0NDMvc2VydmljZXMvVDI0SVNFeHRyYS9nZXRwdWJsaWNrZXkiLCJqdGkiOiIyYjc2MzA3ZS03NGUwLTQzZjYtYjZmNC03NzhiYWI0MWE1NjkifQ.fdgIW-2qnzTEOs1Mv4kOZOIIQ6qMYHc5rwwVadS6HQe8007nr3q2JPIx6-ppOnVv99q2q5B_FfZ3YD4hARQIKbNb99ELFj34juLG4BLOJ-NtQyrVj2mYlT-ouGawIA6Yv8Ub8umvkEO99Vg8NgG8u_s6RfwGKgfiMBC7x8XTK3U3WNeLttQ6rdEPWoNnlsmUuKfl1SAFq9yFMjWH6ux1a2_tNLZ55LxwrgurRfIUsV2XrA8hCnvcGZMYimePEAPQwmb8mOp0d7jmcC5-XQDF6moYki4Hdb1Zr7o0Lw7q7C3vO-RGT0RNXQfiI1o5IvjRnOM74RNriJ0Y8yxNynnYPg");
			logger.error("CreateDbxPropect : Printing after hitting the T24 : " + map);
			response = coreCustomerDelegate.saveCustomerFromParty(partyDTO, map, CoreURL);
			String id = (String) response.getResponse();
			if (StringUtils.isNotBlank(id)) {
				addBackendIdentifierTableEntry(dcRequest, partyDTO, id);
				result.addParam("success", "success");
				result.addParam(DTOConstants.CORECUSTOMERID, id);
				logger.debug("Core Customer creation is successful for id -> " + id);
				return result;
			}
		}else {
			ErrorCodeEnum.ERR_10216.setErrorCode(result);
	        return result;
		}

        result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
        result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
        logger.debug("Propect creation is failed");
        ErrorCodeEnum.ERR_10216.setErrorCode(result);
        return result;

    }
    
	private Result getBackendDataParty(DataControllerRequest request, PartyDTO partyDTO) {
		String filter = DTOConstants.BACKENDID + DBPUtilitiesConstants.EQUAL + partyDTO.getPartyId()
				+ DBPUtilitiesConstants.AND + DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL
				+ DTOConstants.PARTY;

		Result result = new Result();
		try {
			result = HelperMethods.callGetApi(request, filter, HelperMethods.getHeaders(request),
					URLConstants.BACKENDIDENTIFIER_GET);
		} catch (HttpCallException e) {
			logger.error("Caught exception while getting backend identifier: ", e);
		}
		return result;
	}
	
	private String getBackendType(DataControllerRequest request, String customerId) {
		String filter = DTOConstants.CUSTOMER_ID + DBPUtilitiesConstants.EQUAL + customerId
				+ DBPUtilitiesConstants.AND + DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL
				+ DTOConstants.T24;

		Result result = new Result();
		try {
			result = HelperMethods.callGetApi(request, filter, HelperMethods.getHeaders(request),
					URLConstants.BACKENDIDENTIFIER_GET);
		} catch (HttpCallException e) {
			logger.error("Caught exception while getting backend identifier: ", e);
		}
		return HelperMethods.getFieldValue(result, "BackendType");
	}
    
    private void addBackendIdentifierTableEntry(DataControllerRequest dcRequest, PartyDTO partyDTO, String coreCustomerId) {

        String filter = DTOConstants.BACKENDID + DBPUtilitiesConstants.EQUAL + partyDTO.getPartyId()
                + DBPUtilitiesConstants.AND + DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL
                + DTOConstants.PARTY;

        Result result = new Result();
        try {
            result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BACKENDIDENTIFIER_GET);
        } catch (HttpCallException e) {
            logger.error("Caught exception while getting backend identifier: ", e);
        }

        String id = HelperMethods.getFieldValue(result, "Customer_id");
        String companyId = HelperMethods.getFieldValue(result, "CompanyId");
        if (StringUtils.isEmpty(id))
            return;
        BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setId(UUID.randomUUID().toString());
        backendIdentifierDTO.setCustomer_id(id);
        backendIdentifierDTO.setBackendId(coreCustomerId);
        backendIdentifierDTO.setCompanyId(companyId);
        backendIdentifierDTO.setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
        backendIdentifierDTO.setIdentifier_name(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendCustomerIdentifierName));
        backendIdentifierDTO.setSequenceNumber("1");
        backendIdentifierDTO.setIsNew(true);
        Map<String, Object> input = DTOUtils.getParameterMap(backendIdentifierDTO, true);
        backendIdentifierDTO.persist(input, new HashMap<String, Object>());
        
        backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setId(UUID.randomUUID().toString());
        backendIdentifierDTO.setCustomer_id(id);
        backendIdentifierDTO.setBackendId(coreCustomerId);
        backendIdentifierDTO.setCompanyId(companyId);
        backendIdentifierDTO.setBackendType(IntegrationTemplateURLFinder.getBackendURL(DTOConstants.CORE));
        backendIdentifierDTO.setIdentifier_name(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendCustomerIdentifierName));
        backendIdentifierDTO.setSequenceNumber("1");
        backendIdentifierDTO.setIsNew(true);
        input = DTOUtils.getParameterMap(backendIdentifierDTO, true);
        backendIdentifierDTO.persist(input, new HashMap<String, Object>());

        backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setId(UUID.randomUUID().toString());
        backendIdentifierDTO.setCustomer_id(id);
        backendIdentifierDTO.setBackendId(coreCustomerId);
        backendIdentifierDTO.setCompanyId(companyId);
        backendIdentifierDTO.setBackendType(IntegrationTemplateURLFinder.getBackendURL(DTOConstants.CORE));
        backendIdentifierDTO.setIdentifier_name(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendCustomerIdentifierName));
        backendIdentifierDTO.setSequenceNumber("1");
        backendIdentifierDTO.setIsNew(true);
        input = DTOUtils.getParameterMap(backendIdentifierDTO, true);
        backendIdentifierDTO.persist(input, new HashMap<String, Object>());
    }


    private PartyDTO getPartyDTOFromInput(Map<String, String> inputParamMap, DataControllerRequest dcRequest) {
        String partyEventData = inputParamMap.get(DTOConstants.PARTYEVENTDATA);
        if (StringUtils.isBlank(partyEventData)) {
            partyEventData = dcRequest.getParameter(DTOConstants.PARTYEVENTDATA);
        }

        if (StringUtils.isNotBlank(partyEventData)) {

            JsonObject partyEventDataJson = new JsonParser().parse(partyEventData).getAsJsonObject();
            String partyID = null;
            if (partyEventDataJson.has(DTOConstants.PARTY_ID)
                    && !partyEventDataJson.get(DTOConstants.PARTY_ID).isJsonNull()) {

                partyID = partyEventDataJson.get(DTOConstants.PARTY_ID).getAsString();

                if (StringUtils.isNotBlank(partyID)) {

                    PartyDTO partyDTO = new PartyDTO();

                    partyDTO.loadFromJson(partyEventDataJson);
                    logger.debug("partyDTO for Party Serivce is : " + partyDTO.toStringJson().toString());

                    return partyDTO;
                }
            }
        }

        return new PartyDTO();
    }

    @Override
    public Result updateFromParty(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {

        logger = new LoggerUtil(CoreCustomerBusinessDelegateImpl.class);

        PartyDTO partyDTO = getPartyDTOFromInput(HelperMethods.getInputParamMap(inputArray), dcRequest);

        String CoreURL = URLFinder.getPathUrl(URLConstants.CORE_CUSTOMER_CREATE, dcRequest);

        Result result = new Result();
        if (partyDTO == null || StringUtils.isBlank(partyDTO.getPartyId())) {
            ErrorCodeEnum.ERR_10209.setErrorCode(result);
            return result;
        }

        String filter =
                DTOConstants.BACKENDID + DBPUtilitiesConstants.EQUAL + partyDTO.getPartyId() + DBPUtilitiesConstants.AND
                        + DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL + DTOConstants.PARTY;
        try {
            result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BACKENDIDENTIFIER_GET);
        } catch (HttpCallException e) {
            logger.error("Caught exception while getting backend identifier: ", e);
        }

        String companyId = HelperMethods.getFieldValue(result, "CompanyId");

        String customerID = null;

        if (!HelperMethods.hasRecords(result)) {
            customerID = partyDTO.getPartyId();
        }
        else {
            customerID = HelperMethods.getFieldValue(result, "Customer_id");
        }

        filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customerID + DBPUtilitiesConstants.AND
                + DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL + IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType);
        try {
            result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BACKENDIDENTIFIER_GET);
        } catch (HttpCallException e) {

            logger.error("Caught exception while getting backend identifier: ", e);
        }

        if (HelperMethods.hasRecords(result)) {
            customerID = HelperMethods.getFieldValue(result, DTOConstants.BACKENDID);
            partyDTO.setPartyId(customerID);
        } else {
            result = new Result();
            logger.debug("Core Customer identifier information not available for customer " + customerID);
            ErrorCodeEnum.ERR_10211.setErrorCode(result);
            return result;
        }

        CoreCustomerBusinessDelegate coreCustomerDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class)
                .getBusinessDelegate(CoreCustomerBusinessDelegate.class);

        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(companyId))
            map.put("companyId", companyId);

        map = HelperMethods.addJWTAuthHeader(dcRequest, map, AuthConstants.PRE_LOGIN_FLOW);
        DBXResult response = coreCustomerDelegate.updateCustomerfromParty(partyDTO, map, CoreURL);

        String id = (String) response.getResponse();

        result = new Result();
        if (StringUtils.isNotBlank(id)) {
            result.addParam("success", "success");
            result.addParam(DTOConstants.CORECUSTOMERID, id);
            logger.debug("Core Customer update is successful for id -> " + id);
            return result;
        }

        result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
        result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
        logger.debug("Core Customer update is failed");
        ErrorCodeEnum.ERR_10217.setErrorCode(result);
        return result;
    }

    @Override
    public Result get(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result saveFromDBX(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        logger = new LoggerUtil(CoreCustomerBusinessDelegateImpl.class);
        Result result = new Result();

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        for (Entry<String, String> entry : inputParams.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        String customerId = inputParams.get(InfinityConstants.id);
        String companyId = inputParams.get(InfinityConstants.companyId);

        if (StringUtils.isBlank(customerId)) {
            result = new Result();
            logger.debug("Prospect creation is failed");
            ErrorCodeEnum.ERR_10216.setErrorCode(result);
            return result;
        }

        if (StringUtils.isBlank(companyId)) {
            result = new Result();
            logger.debug("Prospect creation is failed");
            ErrorCodeEnum.ERR_10216.setErrorCode(result, "companyId is blank");
            return result;
        }

        String CoreURL = URLFinder.getPathUrl(URLConstants.CORE_CUSTOMER_CREATE, dcRequest);

        CustomerDTO customerDTO = CustomerUtils.buildCustomerDTO(HelperMethods.getNumericId() + "",
                HelperMethods.getInputParamMap(inputArray), dcRequest);

        CoreCustomerBusinessDelegate coreCustomerDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class)
                .getBusinessDelegate(CoreCustomerBusinessDelegate.class);

        map = HelperMethods.addJWTAuthHeader(dcRequest, map, AuthConstants.PRE_LOGIN_FLOW);
        DBXResult response = coreCustomerDelegate.saveCustomerFromDBX(customerDTO, map, CoreURL);

        String id = (String) response.getResponse();

        if (StringUtils.isNotBlank(id)) {
            addBackendIdentifierTableEntry(id, customerId, dcRequest, companyId);
            result.addParam("success", "success");
            result.addParam(DTOConstants.CORECUSTOMERID, id);
            logger.debug("Core Customer creation is successful for id -> " + id);
            return result;
        }

        result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
        result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
        logger.debug("Propect creation is failed");
        ErrorCodeEnum.ERR_10216.setErrorCode(result);
        return result;
    }

    private void addBackendIdentifierTableEntry(String coreCustomerId, String id, DataControllerRequest dcRequest,
            String companyId) {

        BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setId(UUID.randomUUID().toString());
        backendIdentifierDTO.setCustomer_id(id);
        backendIdentifierDTO.setBackendId(coreCustomerId);
        backendIdentifierDTO.setCompanyId(companyId);
        backendIdentifierDTO.setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
        backendIdentifierDTO.setIdentifier_name(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendCustomerIdentifierName));
        backendIdentifierDTO.setSequenceNumber("1");
        backendIdentifierDTO.setIsNew(true);
        Map<String, Object> input = DTOUtils.getParameterMap(backendIdentifierDTO, true);
        backendIdentifierDTO.persist(input, new HashMap<String, Object>());

    }

    @Override
    public Result updateFromDBX(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        logger = new LoggerUtil(CoreCustomerBusinessDelegateImpl.class);

        String id = HelperMethods.getInputParamMap(inputArray).get(DTOConstants.ID);

        if (StringUtils.isBlank(id)) {
            id = dcRequest.getParameter(DTOConstants.ID);
        }

        String coreCustomerID = "";
        String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + id + DBPUtilitiesConstants.AND
                + DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL + IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType);

        Result result = new Result();
        try {
            result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.BACKENDIDENTIFIER_GET);
        } catch (HttpCallException e) {

            logger.error("Caught exception while getting backend identifier: ", e);
        }

        String companyId = null;
        if (HelperMethods.hasRecords(result)) {
            coreCustomerID = HelperMethods.getFieldValue(result, DTOConstants.BACKENDID);
            companyId = HelperMethods.getFieldValue(result, "CompanyId");
        } else {
            result = new Result();
            logger.debug("Core Customer identifier information not available for customer " + id);
            ErrorCodeEnum.ERR_10211.setErrorCode(result);
            return result;
        }

        CustomerDTO customerDTO =
                CustomerUtils.buildCustomerDTO(id, HelperMethods.getInputParamMap(inputArray), dcRequest);

        String CoreURL = URLFinder.getPathUrl(URLConstants.CORE_CUSTOMER_CREATE, dcRequest);

        result = new Result();
        if (customerDTO == null) {
            ErrorCodeEnum.ERR_10209.setErrorCode(result);
            return result;
        }

        CoreCustomerBusinessDelegate coreCustomerDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class)
                .getBusinessDelegate(CoreCustomerBusinessDelegate.class);

        Map<String, Object> map = new HashMap<String, Object>();
        map = HelperMethods.addJWTAuthHeader(dcRequest, map, AuthConstants.PRE_LOGIN_FLOW);
        customerDTO.setId(coreCustomerID);

        map.put("companyId", companyId);
        
            

        DBXResult response = coreCustomerDelegate.updateCustomerfromDBX(customerDTO, map, CoreURL);

        id = (String) response.getResponse();

        result = new Result();
        if (StringUtils.isNotBlank(id)) {
            result.addParam("success", "success");
            result.addParam(DTOConstants.CORECUSTOMERID, id);
            logger.debug("Core Customer update is successful for id -> " + id);
            return result;
        }

        result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
        result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
        logger.debug("Core Customer update is failed");
        ErrorCodeEnum.ERR_10217.setErrorCode(result);
        return result;
    }
    
    @Override
    public Result activateCustomer(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        logger = new LoggerUtil(CoreCustomerBusinessDelegateImpl.class);

        String customerId = HelperMethods.getInputParamMap(inputArray).get(InfinityConstants.customerId);
        String id ="";
        String companyId = null;
        BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
        backendIdentifierDTO.setCustomer_id(customerId);
        if(IntegrationTemplateURLFinder.isIntegrated) {
            backendIdentifierDTO.setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
        }
        else {
            backendIdentifierDTO.setBackendType(DTOConstants.CORE);
        }
        DBXResult dbxResult = new DBXResult();
        try {
            dbxResult  = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
                    .get(backendIdentifierDTO, dcRequest.getHeaderMap());
        } catch (ApplicationException e) {
            logger.error("exception while fetching Backend Identifier", e);
        }
        if (dbxResult.getResponse() != null) {
            BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
            id = identifierDTO.getBackendId();
            companyId = identifierDTO.getCompanyId();
        }
        
        
        String CoreURL = URLFinder.getPathUrl(URLConstants.CORE_CUSTOMER_CREATE, dcRequest);

        CoreCustomerBusinessDelegate coreCustomerDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class)
                .getBusinessDelegate(CoreCustomerBusinessDelegate.class);
        
        Map<String, Object> map = new HashMap<String, Object>();
        map = HelperMethods.addJWTAuthHeader(dcRequest, map, AuthConstants.PRE_LOGIN_FLOW);
        CustomerDTO customerDTO =new CustomerDTO();
        customerDTO .setId(id);

        map.put("companyId", companyId);
        
        DBXResult response = coreCustomerDelegate.activateCustomer(customerDTO, map, CoreURL);

        return new Result();
    }

}
