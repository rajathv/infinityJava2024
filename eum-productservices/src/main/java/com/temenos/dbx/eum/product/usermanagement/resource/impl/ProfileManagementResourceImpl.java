package com.temenos.dbx.eum.product.usermanagement.resource.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.kony.dbputilities.util.LegalEntityUtil;
import org.apache.commons.lang3.StringUtils;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.sessionmanager.SessionScope;
import com.kony.dbputilities.util.BundleConfigurationHandler;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPInputConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.IntegrationTemplateURLFinder;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.eum.dbputilities.customersecurityservices.PasswordHistoryManagement;
import com.kony.eum.dbputilities.util.ServiceCallHelper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.impl.BackendIdentifiersBackendDelegateimpl;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.impl.ProfileManagementBackendDelegateImpl;
import com.temenos.dbx.eum.product.usermanagement.businessdelegate.api.ProfileManagementBusinessDelegate;
import com.temenos.dbx.eum.product.usermanagement.resource.api.InfinityUserManagementResource;
import com.temenos.dbx.eum.product.usermanagement.resource.api.ProfileManagementResource;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.CustomerLegalEntityDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.MemberSearchBean;
import com.temenos.dbx.product.utils.CustomerUtils;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.InfinityConstants;

public class ProfileManagementResourceImpl implements ProfileManagementResource {

    private static LoggerUtil logger = new LoggerUtil(ProfileManagementResourceImpl.class);

    @Override
    public Result getCustomerForUserResponse(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        String legalEntityId = dcRequest.getParameter("legalEntityId");
        if(StringUtils.isBlank(legalEntityId)) {
        	legalEntityId = LegalEntityUtil.getCurrentLegalEntityIdFromCache(dcRequest);
        }
        String id = inputParams.get("Customer_id");
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(id);
        customerDTO.setCompanyLegalUnit(legalEntityId);
        Map<String, Object> headerMap = dcRequest.getHeaderMap();
        headerMap.put("companyid", legalEntityId);

        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        DBXResult dbxResult = businessDelegate.getCustomerForUserResponse(customerDTO, headerMap);

        if (dbxResult.getResponse() != null) {

            Record record = new Record();

            Map<String, String> map = (Map<String, String>) dbxResult.getResponse();

            for (Entry<String, String> entry : map.entrySet()) {
                record.addParam(entry.getKey(), entry.getValue());
            }

            Dataset dataset = new Dataset();
            dataset.setId(DTOConstants.CUSTOMER_TABLE);

            dataset.addRecord(record);

            result.addDataset(dataset);

            return result;
        }

        ErrorCodeEnum.ERR_10003.setErrorCode(result);
        return result;
    }

    @Override
    public Result getUserDetailsConcurrent(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        Result result = new Result();

        SessionScope.clear(dcRequest);

        Map<String, String> inputmap = HelperMethods.getInputParamMap(inputArray);
        String Username = inputmap.get("Username");
        String Customer_id = "";
        String coreCustomerID = "";
        String Bank_id = inputmap.get("Bank_id");

        Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(dcRequest);

        boolean IS_Integrated = IntegrationTemplateURLFinder.isIntegrated;

        if (!HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
            Customer_id = inputmap.get("Customer_id");
            if (StringUtils.isNotBlank(Customer_id)) {
                BackendIdentifiersBackendDelegateimpl backendDelegateimpl = new BackendIdentifiersBackendDelegateimpl();
                BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
                backendIdentifierDTO.setBackendId(Customer_id);
                if (IS_Integrated) {
                    backendIdentifierDTO
                            .setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
                } else {
                    backendIdentifierDTO.setBackendType(DTOConstants.CORE);
                }

                DBXResult dbxResult = backendDelegateimpl.get(backendIdentifierDTO, dcRequest.getHeaderMap());
                if (dbxResult.getResponse() != null) {
                    backendIdentifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
                    Customer_id = backendIdentifierDTO.getCustomer_id();
                    coreCustomerID = backendIdentifierDTO.getBackendId();
                }
            }
        }

        DBXResult dbxResult = new DBXResult();

        if (StringUtils.isBlank(Username) && StringUtils.isBlank(Customer_id)) {
            loggedInUserInfo = HelperMethods.getUserFromIdentityService(dcRequest);
            if (HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
                Username = loggedInUserInfo.get("UserName");
                Customer_id = loggedInUserInfo.get("Customer_id");
                Bank_id = loggedInUserInfo.get("Bank_id");
            }
        }

        if (StringUtils.isBlank(Customer_id)) {
            result.addParam(DBPUtilitiesConstants.VALIDATION_ERROR, "Unable to find userId");
            return result;
        }

        CustomerDTO customerDTO =
                new CustomerDTO();
        customerDTO.setId(Customer_id);
        customerDTO = (CustomerDTO) customerDTO.loadDTO();

        if (customerDTO == null) {
            result.addParam(DBPUtilitiesConstants.VALIDATION_ERROR, "Unable to find userId");
            return result;
        }

        if (StringUtils.isBlank(coreCustomerID)) {
            BackendIdentifiersBackendDelegateimpl backendDelegateimpl = new BackendIdentifiersBackendDelegateimpl();
            BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
            backendIdentifierDTO.setCustomer_id(Customer_id);
            if (IS_Integrated) {
                backendIdentifierDTO
                        .setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
            } else {
                backendIdentifierDTO.setBackendType(DTOConstants.CORE);
            }

            dbxResult = backendDelegateimpl.get(backendIdentifierDTO, dcRequest.getHeaderMap());
            if (dbxResult.getResponse() != null) {
                backendIdentifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
                coreCustomerID = backendIdentifierDTO.getBackendId();
            }
        }

        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        dbxResult = businessDelegate.getUserResponse(customerDTO, dcRequest.getHeaderMap());
        if (dbxResult.getResponse() != null) {
            JsonObject resultObject = (JsonObject) dbxResult.getResponse();
            JsonObject jsonObject = resultObject.has("customer") && resultObject.get("customer").isJsonArray()
                    && resultObject.get("customer").getAsJsonArray().size() > 0
                    && resultObject.get("customer").getAsJsonArray().get(0).isJsonObject()
                            ? resultObject.get("customer").getAsJsonArray().get(0).getAsJsonObject()
                            : new JsonObject();

            if (JSONUtil.isJsonNotNull(jsonObject) && JSONUtil.hasKey(jsonObject, "customers")
                    && jsonObject.get("customers").isJsonArray()
                    && JSONUtil.getJsonArrary(jsonObject, "customers").size() > 0) {

                JsonArray array = JSONUtil.getJsonArrary(jsonObject, "customers");
                for (JsonElement element : array) {
                    JsonObject coreCustomerObject =
                            element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
                    String coreCustomerId = coreCustomerObject.has("coreCustomerId")
                            ? coreCustomerObject.get("coreCustomerId").getAsString()
                            : "";
                    if (StringUtils.isNotBlank(coreCustomerId) && coreCustomerId.equalsIgnoreCase(coreCustomerID)) {
                        coreCustomerObject.addProperty("isPrimary", "true");
                    } else {
                        coreCustomerObject.addProperty("isPrimary", "false");
                    }
                }
            }
            result = JSONToResult.convert(resultObject.toString());
        }
        return result;
    }

    @Override
    public Result getUserResponseForAlerts(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {

        Result result = new Result();

        HashMap<String, Object> identityParam = new HashMap<String, Object>();
        Map<String, Object> identitytHeaders = new HashMap<>();
        String dbpAppKey = EnvironmentConfigurationsHandler.getValue("AC_DBP_APP_KEY");
        String dbpAppSecret = EnvironmentConfigurationsHandler.getValue("AC_DBP_APP_SECRET");
        String sharedSecret = EnvironmentConfigurationsHandler.getValue("AC_DBP_SHARED_SECRET");
        identitytHeaders.put("X-Kony-App-Key", dbpAppKey);
        identitytHeaders.put("X-Kony-App-Secret", dbpAppSecret);
        identitytHeaders.put("x-kony-dbp-api-access-token", sharedSecret);

        String authToken = "";

        if (StringUtils.isBlank(dbpAppKey) || StringUtils.isBlank(dbpAppSecret)
                || StringUtils.isBlank(sharedSecret)) {
            logger.error("Error while file fetching DBP_CORE_APPKEY or DBP_CORE_SECRET or DBP_CORE_SHARED_SECRET");
        }
        JsonObject identityResult = ServiceCallHelper.invokeServiceAndGetJson(identityParam, identitytHeaders,
                URLConstants.APILOGIN);
        if (identityResult.has("claimsToken")) {
            authToken = identityResult.get("claimsToken").getAsString();
        } else {
            logger.error("Error while file fetching auth Token in combined statements");
            return result;
        }

        if (StringUtils.isNotBlank(authToken)) {
            Map<String, Object> headers = dcRequest.getHeaderMap();
            headers.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION, authToken);
            headers.put("backendToken", authToken);
            Map<String, Object> inputParams = new HashMap<String, Object>();
            inputParams.put("backendToken", authToken);
            inputParams.put(InfinityConstants.legalEntityId,
                    dcRequest.getParameter(InfinityConstants.legalEntityId.toString()));
            dcRequest.addRequestParam_(InfinityConstants.legalEntityId,
                    dcRequest.getParameter(InfinityConstants.legalEntityId.toString()));
            inputParams.put(InfinityConstants.Customer_id,
                    dcRequest.getParameter(InfinityConstants.customerId.toString()));
            dcRequest.addRequestParam_(InfinityConstants.Customer_id,
                    dcRequest.getParameter(InfinityConstants.customerId.toString()));
            try {
                result = JSONToResult.convert(DBPServiceInvocationWrapper.invokePassThroughServiceAndGetString(
                        "DBPServices", null, "getCustomerCommunication",
                        inputParams, headers, dcRequest));
            } catch (Exception e) {
            	logger.error("Exception", e);
            }
        }

        if(result.getDatasetById("records")!= null) {
            result.getDatasetById("records").setId("customer");
            if(result.getDatasetById("customer").getAllRecords().size() >0) {
                Record record = result.getDatasetById("customer").getRecord(0);
                record.addParam("id", dcRequest.getParameter(InfinityConstants.customerId));
                record.addParam("FirstName", record.getParamValueByName("userfirstname"));
            }
        }
        
        return result;
    }

    @Override
    public Result createCustomer(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {
        Result result = new Result();
        LegalEntityUtil.addCompanyIDToHeaders(request);
        Map<String, String> map = HelperMethods.getInputParamMap(inputArray);
        CustomerDTO customerDTO = CustomerUtils.buildCustomerDTO(null, map, request);
        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        DBXResult dbxResult = businessDelegate.createCustomer(customerDTO, request.getHeaderMap());

        if (dbxResult.getResponse() != null) {
            result = JSONToResult.convert(((JsonObject) dbxResult.getResponse()).toString());
        } else {
            HelperMethods.addError(result, dbxResult);
        }
        return result;
    }

    @Override
    public Result getUserDetailsToAdmin(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {

        Result result = new Result();
        String adminId = HelperMethods.getAPIUserIdFromSession(request);

        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);

        try {
            LegalEntityUtil.addCompanyIDToHeaders(request);
            if (!HelperMethods.isAdmin(request, adminId)) {
                HelperMethods.setValidationMsg("logged in user is not admin", request, result);
            }
        } catch (HttpCallException e) {
            HelperMethods.setValidationMsg("logged in user is not admin", request, result);
            return result;
        }
        CustomerDTO customerDTO = new CustomerDTO();
        String customerID = getUserId(request, inputParams.get("accountNumber"));
        if (StringUtils.isBlank(customerID)) {
            HelperMethods.setValidationMsg("Invalid Account number", request, result);
            return result;
        }

        customerDTO.setId(customerID);
        ProfileManagementBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        DBXResult dbxResult = businessDelegate.getCustomerDetailsToAdmin(customerDTO, request.getHeaderMap());
        if (dbxResult.getResponse() == null) {
            HelperMethods.setValidationMsg("Unable to fetch user details", request, result);
            return result;
        }

        result = JSONToResult.convert(((JsonObject) dbxResult.getResponse()).toString());
        return result;
    }

    private String getUserId(DataControllerRequest dcRequest, String acctNum) {
        String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + acctNum;
        Result account;
        try {
            account = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.CUSTOMERACCOUNTSVIEW_GET);
            return HelperMethods.getFieldValue(account, "Customer_id");
        } catch (HttpCallException e) {
        	logger.error("Exception", e);
        }

        return null;
    }

    @Override
    public Result updateProfile(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        Result result = new Result();
        logger = new LoggerUtil(ProfileManagementResourceImpl.class);
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        String customerID = "";
        String legalEntityId = "";
        Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(dcRequest);
        if (!HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
            customerID = inputParams.get(DTOConstants.CUSTOMER_ID);
            legalEntityId = dcRequest.getParameter("legalEntityId"); 
            if (StringUtils.isEmpty(customerID)) {
                customerID = dcRequest.getParameter(DTOConstants.CUSTOMER_ID);
                legalEntityId = dcRequest.getParameter("legalEntityId"); 
            }

            if (StringUtils.isBlank(customerID)) {
                customerID = inputParams.get(DTOConstants.CORECUSTOMERID);
            }
        } else {
            customerID = HelperMethods.getCustomerIdFromSession(dcRequest);
            legalEntityId = dcRequest.getParameter("legalEntityId"); 
        }

        boolean isCoreCustomerIdPresent = false;
        DBXResult dbxResult = new DBXResult();

        boolean IS_Integrated = IntegrationTemplateURLFinder.isIntegrated;

        if (StringUtils.isNotBlank(customerID)) {

            BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
            backendIdentifierDTO.setBackendId(customerID);
            backendIdentifierDTO.setCompanyLegalUnit(legalEntityId);
            if (IS_Integrated) {
                backendIdentifierDTO
                        .setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
            } else {
                backendIdentifierDTO.setBackendType(DTOConstants.CORE);
            }

            try {
                dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
                        .get(backendIdentifierDTO, dcRequest.getHeaderMap());
            } catch (ApplicationException e) {
                logger.error("exception while fetching Backend Identifier", e);
            }
            if (dbxResult.getResponse() != null) {
                BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
                customerID = identifierDTO.getCustomer_id();
                isCoreCustomerIdPresent = true;
            } else {
                backendIdentifierDTO = new BackendIdentifierDTO();
                backendIdentifierDTO.setCustomer_id(customerID);
                if (IS_Integrated) {
                    backendIdentifierDTO
                            .setBackendType(IntegrationTemplateURLFinder.getBackendURL(InfinityConstants.BackendType));
                } else {
                    backendIdentifierDTO.setBackendType(DTOConstants.CORE);
                }

                try {
                    dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
                            .get(backendIdentifierDTO, dcRequest.getHeaderMap());
                } catch (ApplicationException e) {
                    logger.error("exception while fetching Backend Identifier", e);
                }
                if (dbxResult.getResponse() != null) {
                    BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
                    customerID = identifierDTO.getCustomer_id();
                    isCoreCustomerIdPresent = true;
                }
            }
        }
        
        CustomerDTO customerDto = new  CustomerDTO();
        
        if (StringUtils.isBlank(customerID) || customerDto.loadDTO(customerID) == null) {
            logger.debug("customer table entry is not found for given customerID "+ customerID);
            return result;
        }
        
        if (isCoreCustomerIdPresent) {
            inputParams.put("FirstName", null);
            inputParams.put("LastName", null);
            inputParams.put("dateOfBirth", null);
            inputParams.put("TaxId", null);
        }

        CustomerDTO customerDTO = CustomerUtils.buildCustomerDTOforUpdate(customerID, inputParams, dcRequest,
                isCoreCustomerIdPresent, IS_Integrated);

        result = new Result();
        LegalEntityUtil.addCompanyIDToHeaders(dcRequest);
        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        dbxResult = businessDelegate.updateCustomer(customerDTO, dcRequest.getHeaderMap());

        if (dbxResult.getResponse() != null) {
            JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
            result = JSONToResult.convert(jsonObject.toString());
        }

        return result;
    }

    @Override
    public Result searchCustomer(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {

        Result processedResult = new Result();
        LegalEntityUtil.addCompanyIDToHeaders(dcRequest);
        // Sort validation
        if (StringUtils.isNotBlank(dcRequest.getParameter("_sortVariable"))
                && dcRequest.getParameter("_sortVariable").contains(" ")) {
            ErrorCodeEnum.ERR_10333.setErrorCode(processedResult);
            return processedResult;
        }
        if (StringUtils.isNotBlank(dcRequest.getParameter("_sortDirection"))
                && (dcRequest.getParameter("_sortDirection").contains(" ")
                        || (!dcRequest.getParameter("_sortDirection").equalsIgnoreCase("ASC")
                                && !dcRequest.getParameter("_sortDirection").equalsIgnoreCase("DESC")))) {
            ErrorCodeEnum.ERR_10334.setErrorCode(processedResult);
            return processedResult;
        }
        String legalEntityId = dcRequest.getParameter("_legalEntityId");
      
        MemberSearchBean memberSearchBean = new MemberSearchBean();
        memberSearchBean.setSearchType(dcRequest.getParameter("_searchType"));
        memberSearchBean.setMemberId(dcRequest.getParameter("_id"));
        memberSearchBean.setCustomerId(dcRequest.getParameter("_customerId"));
        memberSearchBean.setCustomerName(dcRequest.getParameter("_name"));
        memberSearchBean.setSsn(dcRequest.getParameter("_SSN"));
        memberSearchBean.setCustomerUsername(dcRequest.getParameter("_username"));
        memberSearchBean.setCustomerPhone(dcRequest.getParameter("_phone"));
        memberSearchBean.setCustomerEmail(dcRequest.getParameter("_email"));
        memberSearchBean.setIsStaffMember(dcRequest.getParameter("_IsStaffMember"));
        memberSearchBean.setCardorAccountnumber(dcRequest.getParameter("_cardorAccountnumber"));
        memberSearchBean.setTin(dcRequest.getParameter("_TIN"));
        memberSearchBean.setCustomerGroup(dcRequest.getParameter("_group"));
        memberSearchBean.setCustomerIDType(dcRequest.getParameter("_IDType"));
        memberSearchBean.setCustomerIDValue(dcRequest.getParameter("_IDValue"));
        memberSearchBean.setCustomerCompanyId(dcRequest.getParameter("_companyId"));
        memberSearchBean.setCustomerRequest(dcRequest.getParameter("_requestID"));
        memberSearchBean.setBranchIDS(dcRequest.getParameter("_branchIDS"));
        memberSearchBean.setProductIDS(dcRequest.getParameter("_productIDS"));
        memberSearchBean.setCityIDS(dcRequest.getParameter("_cityIDS"));
        memberSearchBean.setEntitlementIDS(dcRequest.getParameter("_entitlementIDS"));
        memberSearchBean.setGroupIDS(dcRequest.getParameter("_groupIDS"));
        memberSearchBean.setCustomerStatus(dcRequest.getParameter("_customerStatus"));
        memberSearchBean.setBeforeDate(dcRequest.getParameter("_before"));
        memberSearchBean.setAfterDate(dcRequest.getParameter("_after"));
        memberSearchBean.setSortVariable(dcRequest.getParameter("_sortVariable"));
        memberSearchBean.setSortDirection(dcRequest.getParameter("_sortDirection"));
        memberSearchBean.setPageOffset(dcRequest.getParameter("_pageOffset"));
        memberSearchBean.setPageSize(dcRequest.getParameter("_pageSize"));
        memberSearchBean.setDateOfBirth(dcRequest.getParameter("_dateOfBirth"));
        memberSearchBean.setCompanyLegalUnit(dcRequest.getParameter("_legalEntityId"));

        if (StringUtils.isBlank(memberSearchBean.getSearchType())) {
            ErrorCodeEnum.ERR_10334.setErrorCode(processedResult);
            return processedResult;

        }
        
        if(StringUtils.isBlank(legalEntityId)){ // ideally should throw error;but for now hardcoding some value
        	memberSearchBean.setCompanyLegalUnit(legalEntityId);
        }
        
        processedResult
                .addParam(new Param("SortVariable", memberSearchBean.getSortVariable(), Param.STRING_CONST));
        processedResult
                .addParam(new Param("SortDirection", memberSearchBean.getSortDirection(), Param.STRING_CONST));
        processedResult.addParam(
                new Param("PageOffset", String.valueOf(memberSearchBean.getPageOffset()), Param.STRING_CONST));
        processedResult.addParam(
                new Param("PageSize", String.valueOf(memberSearchBean.getPageSize()), Param.STRING_CONST));
        String id = dcRequest.getParameter("_id");
        String name = dcRequest.getParameter("_name");
        String SSN = dcRequest.getParameter("_SSN");
        String username = dcRequest.getParameter("_username");
        String phone = dcRequest.getParameter("_phone");
        String email = dcRequest.getParameter("_email");
        String accNo = dcRequest.getParameter("_cardorAccountnumber");
        String Tin = dcRequest.getParameter("_TIN");
        String IDType = dcRequest.getParameter("_IDType");
        String IDValue = dcRequest.getParameter("_IDValue");
        String CompanyId = dcRequest.getParameter("_companyId");
        String searchType = dcRequest.getParameter("_searchType");
        String dateOfBirth = dcRequest.getParameter("_dateOfBirth");
        String customerId = dcRequest.getParameter("_customerId");
        //legalEntityId = dcRequest.getParameter("_legalEntityId");
        /*if ((StringUtils.isBlank(id) || id.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(name) || name.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(dateOfBirth) || dateOfBirth.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(SSN) || SSN.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(username) || username.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(phone) || phone.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(email) || email.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(accNo) || accNo.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(Tin) || Tin.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(IDType) || IDType.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(IDValue) || IDValue.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(CompanyId) || CompanyId.equalsIgnoreCase("null"))
                && (StringUtils.isBlank(customerId) || customerId.equalsIgnoreCase("null"))
                && (searchType.equalsIgnoreCase("CUSTOMER_SEARCH"))
                || ((StringUtils.isBlank(legalEntityId) || legalEntityId.equalsIgnoreCase("null")))){
            processedResult.addParam(new Param("TotalResultsFound", "0", Param.INT_CONST));
            Dataset recordsDS = new Dataset();
            recordsDS.setId("records");
            processedResult.addDataset(recordsDS);
            return processedResult;
        }*/
       
        if((!(StringUtils.isBlank(legalEntityId) || legalEntityId.equalsIgnoreCase("null")))
        	&& (StringUtils.isBlank(id) || id.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(name) || name.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(dateOfBirth) || dateOfBirth.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(SSN) || SSN.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(username) || username.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(phone) || phone.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(email) || email.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(accNo) || accNo.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(Tin) || Tin.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(IDType) || IDType.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(IDValue) || IDValue.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(CompanyId) || CompanyId.equalsIgnoreCase("null"))
            && (StringUtils.isBlank(customerId) || customerId.equalsIgnoreCase("null"))
            && (searchType.equalsIgnoreCase("CUSTOMER_SEARCH"))){
        	ErrorCodeEnum.ERR_29041.setErrorCode(processedResult);
            return processedResult;
        	
        	
        }

        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);

        Map<String, String> configurations = BundleConfigurationHandler
                .fetchBundleConfigurations(BundleConfigurationHandler.BUDLENAME_C360, dcRequest);
        PasswordHistoryManagement pm = new PasswordHistoryManagement(dcRequest, true);
        DBXResult dbxResult =
                businessDelegate.searchCustomer(configurations, memberSearchBean, dcRequest.getHeaderMap(), pm);

        if (dbxResult.getResponse() != null) {
            JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
            Result result = JSONToResult.convert(jsonObject.toString());

            for (Param param : processedResult.getAllParams()) {
                result.addParam(param);
            }

            return result;
        } else {
            HelperMethods.addError(processedResult, dbxResult);
        }

        return processedResult;

    }

    public boolean memberPresent(String memberId, Map<String, Object> headerMap) {
        String filter = "id" + DBPUtilitiesConstants.EQUAL + memberId;

        Map<String, Object> input = new HashMap<String, Object>();
        input.put(DBPUtilitiesConstants.FILTER, filter);
        JsonObject result =
                ServiceCallHelper.invokeServiceAndGetJson(input, headerMap, URLConstants.MEMBERSHIP_GET);

        if (result.has(DBPDatasetConstants.DATASET_MEMBERSHIP)) {
            JsonArray jsonArray = result.get(DBPDatasetConstants.DATASET_MEMBERSHIP).isJsonArray()
                    ? result.get(DBPDatasetConstants.DATASET_MEMBERSHIP).getAsJsonArray()
                    : new JsonArray();
            return jsonArray.size() > 0;
        }

        return false;
    }

    @Override
    public Result getBasicInformation(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        Result result = new Result();
        LegalEntityUtil.addCompanyIDToHeaders(dcRequest);
        CustomerDTO customerDTO = new CustomerDTO();

        customerDTO.setId(dcRequest.getParameter("Customer_id"));
        String legalEntityId = dcRequest.getParameter("legalEntityId");
        if(StringUtils.isBlank(legalEntityId)) { 
        	ErrorCodeEnum.ERR_29040.setErrorCode(result);
        }
        //customerDTO.setCompanyLegalUnit(legalEntityId);
        customerDTO = (CustomerDTO) customerDTO.loadDTO();
        boolean isCustomerSearch = false;

        if (customerDTO == null) {
            isCustomerSearch = true;
            customerDTO = new CustomerDTO();
            customerDTO.setId(dcRequest.getParameter("Customer_id"));
            customerDTO.setUserName(dcRequest.getParameter("userName"));
            customerDTO.setCompanyLegalUnit(dcRequest.getParameter("legalEntityId"));
        } else if (customerDTO.getId().equals(customerDTO.getUserName())
                || memberPresent(dcRequest.getParameter("Customer_id"), dcRequest.getHeaderMap())) {
            isCustomerSearch = true;
        }

        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        PasswordHistoryManagement pm = new PasswordHistoryManagement(dcRequest, true);
        Map<String, String> configurations = BundleConfigurationHandler
                .fetchBundleConfigurations(BundleConfigurationHandler.BUDLENAME_C360, dcRequest);
        customerDTO.setCompanyLegalUnit(legalEntityId);
        DBXResult dbxResult = businessDelegate.getBasicInformation(configurations, customerDTO,
                dcRequest.getHeaderMap(), isCustomerSearch, pm);
        if (dbxResult.getResponse() != null) {
            JsonObject jsonObject = (JsonObject) dbxResult.getResponse();

            result = JSONToResult.convert(jsonObject.toString());
        }

        return result;
    }

    @Override
    public Result getByUserName(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {

        Map<String, String> map = HelperMethods.getInputParamMap(inputArray);

        String userName = map.get("UserName");

        CustomerDTO customerDTO = new CustomerDTO();

        customerDTO.setUserName(userName);
        LegalEntityUtil.addCompanyIDToHeaders(request);
        ProfileManagementBackendDelegateImpl backendDelegateImpl = new ProfileManagementBackendDelegateImpl();

        DBXResult dbxResult = backendDelegateImpl.get(customerDTO, request.getHeaderMap());

        JsonObject jsonObject = (JsonObject) dbxResult.getResponse();

        return JSONToResult.convert(jsonObject.toString());
    }

    @Override
    public Result get(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {
        Map<String, String> map = HelperMethods.getInputParamMap(inputArray);

        String userName = map.get("UserName");

        CustomerDTO customerDTO = new CustomerDTO();

        customerDTO.setUserName(userName);
        LegalEntityUtil.addCompanyIDToHeaders(request);
        ProfileManagementBackendDelegateImpl backendDelegateImpl = new ProfileManagementBackendDelegateImpl();

        DBXResult dbxResult = backendDelegateImpl.get(customerDTO, request.getHeaderMap());

        JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
        JsonObject customer = new JsonObject();
        if (jsonObject.has("customer")) {
            JsonArray customers =
                    !jsonObject.get("customer").isJsonNull() ? jsonObject.get("customers").getAsJsonArray()
                            : new JsonArray();

            customer = customers.size() > 0 ? customers.get(0).getAsJsonObject() : new JsonObject();

            String customerTypeID =
                    customer.has("CustomerType_id") ? customer.get("CustomerType_id").getAsString() : "";

            boolean isCustomerPresent_in_dbxDB = false;

            String isCombinedUser = customer.has("isCombinedUser") && !customer.get("isCombinedUser").isJsonNull()
                    ? customer.get("isCombinedUser").getAsString()
                    : "false";

            if (HelperMethods.getBusinessUserTypes().contains(customerTypeID) || Boolean.parseBoolean(isCombinedUser)) {
                isCustomerPresent_in_dbxDB = true;
            }

            if (!isCustomerPresent_in_dbxDB) {
                ProfileManagementBusinessDelegate businessDelegate =
                        DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);

                dbxResult = businessDelegate.get(customerDTO, request.getHeaderMap());

                jsonObject = (JsonObject) dbxResult.getResponse();
            }
        }
        return JSONToResult.convert(jsonObject.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result checkifUserEnrolled(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse dcResponse) {
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        String customerlastname = (String) inputParams.get(DBPInputConstants.CUSTOMER_LAST_NAME);
        String ssn = (String) inputParams.get(DBPUtilitiesConstants.C_SSN);
        String dob = (String) inputParams.get(DBPUtilitiesConstants.C_DOB);

        if (StringUtils.isBlank(customerlastname) || StringUtils.isBlank(ssn) || StringUtils.isBlank(dob)) {
            ErrorCodeEnum.ERR_10183.setErrorCode(result);
            Param p = new Param(DBPUtilitiesConstants.VALIDATION_ERROR, "Please provide valid Details.",
                    DBPUtilitiesConstants.STRING_TYPE);
            result.addParam(p);

            return result;
        }
        CustomerDTO customerDTO = new CustomerDTO();

        customerDTO.setDateOfBirth(dob);
        customerDTO.setSsn(ssn);
        customerDTO.setLastName(customerlastname);

        ProfileManagementBusinessDelegate managementBusinessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        LegalEntityUtil.addCompanyIDToHeaders(request);
        DBXResult response = managementBusinessDelegate.checkifUserEnrolled(customerDTO, request.getHeaderMap());

        if (response.getResponse() != null) {

            JsonObject jsonObject = (JsonObject) response.getResponse();

            result = JSONToResult.convert(jsonObject.toString());
        }

        return result;
    }

    @Override
    public Object sendCustomerUnlockEmail(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse response) {
        Result result = new Result();
        LegalEntityUtil.addCompanyIDToHeaders(dcRequest);
        String isSuperAdmin = dcRequest.getParameter("isSuperAdmin");

        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        if (StringUtils.isBlank(isSuperAdmin)) {
            isSuperAdmin = inputParams.get("isSuperAdmin");
        }

        if (!"true".equals(isSuperAdmin)) {
            result.addParam(new Param("mailRequestSent", "false"));
            ErrorCodeEnum.ERR_10191.setErrorCode(result);
            return result;
        }

        String userName = dcRequest.getParameter("userName");
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setUserName(userName);

        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);

        DBXResult dbxResult = businessDelegate.sendCustomerUnlockEmail(customerDTO, dcRequest.getHeaderMap());

        if (dbxResult.getResponse() != null) {
            JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
            result = JSONToResult.convert(jsonObject.toString());
        }

        return result;
    }

    @Override
    public Result getAddressTypes(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {

        Result result = new Result();

        ProfileManagementBusinessDelegate businessDelegate =
                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
        LegalEntityUtil.addCompanyIDToHeaders(request);
        DBXResult dbxResult = businessDelegate.getAddressTypes(request.getHeaderMap());

        if (dbxResult.getResponse() != null) {
            JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
            result = JSONToResult.convert(jsonObject.toString());
        }
        return result;
       
    }

	@Override
	public Object createRetailContract(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws ApplicationException {
		InfinityUserManagementResource infinityUserManagementResource = DBPAPIAbstractFactoryImpl
				.getResource(InfinityUserManagementResource.class);
		return infinityUserManagementResource.createRetailContract(methodID, inputArray, request, response);
	}

	@Override
	public Result userIdSearchDetailed(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException {
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String userName = inputParams.get("userId");
			CustomerDTO customerDTO = new CustomerDTO();
			customerDTO.setUserName(userName);
			customerDTO = (CustomerDTO) customerDTO.loadDTO();
			BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
			backendIdentifierDTO.setCustomer_id(customerDTO.getId());
			backendIdentifierDTO.setBackendType("CORE");

			BackendIdentifiersBackendDelegateimpl backendDelegateimpl = new BackendIdentifiersBackendDelegateimpl();
			List<BackendIdentifierDTO> identifierDTOs = new ArrayList<>();

			DBXResult result = backendDelegateimpl.getList(backendIdentifierDTO, dcRequest.getHeaderMap());
			if (null != result && null != result.getResponse() && StringUtils.isBlank(result.getDbpErrMsg())
					&& StringUtils.isBlank(result.getDbpErrCode())) {
				identifierDTOs = (List<BackendIdentifierDTO>) result.getResponse();
			}
			CustomerLegalEntityDTO customerLegalEntityDTO = new CustomerLegalEntityDTO();
			customerLegalEntityDTO.setCustomer_id(customerDTO.getId());
			List<CustomerLegalEntityDTO> allCustomerDTOList = (List<CustomerLegalEntityDTO>) customerLegalEntityDTO.loadDTO();
			for (CustomerLegalEntityDTO customerEntityDTO : allCustomerDTOList) {
				String virtualUserId = customerEntityDTO.getCustomer_id();
				String virtualUserEntity = customerEntityDTO.getLegalEntityId();
				boolean isPresent = false;
				for (BackendIdentifierDTO backendDTO : identifierDTOs) {
					if (virtualUserEntity.equalsIgnoreCase(backendDTO.getCompanyLegalUnit())) {
						isPresent = true;
						break;
					}
				}
				if(!isPresent) {
					BackendIdentifierDTO virtualUserDTO = new BackendIdentifierDTO();
					virtualUserDTO.setBackendId(virtualUserId);
					virtualUserDTO.setCompanyLegalUnit(virtualUserEntity);
					identifierDTOs.add(virtualUserDTO);
				}
			}
			return getCustomerRecords(identifierDTOs, dcRequest,userName);

		} catch (Exception e) {
			logger.error("Error occured", e);
			throw new ApplicationException(ErrorCodeEnum.ERR_10379);

		}
	}
    
    @Override
   	public Result userIdSearch(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
   			DataControllerResponse dcResponse) throws ApplicationException {
   		Result result = new Result();
   		try {
   			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
   			String userName = inputParams.get("userId");
   			 ProfileManagementBusinessDelegate businessDelegate =
   		                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
                
   			 DBXResult dbxResult =
   		                businessDelegate.userIdSearch(userName, dcRequest.getHeaderMap());
   			  if (dbxResult.getResponse() != null) {
   		            JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
   		            result = JSONToResult.convert(jsonObject.toString());

   			  }
   		            
   			
   		} catch (Exception e) {
   			throw new ApplicationException(ErrorCodeEnum.ERR_10379);

   		}
   		return result;
   	}
    
	private Result getCustomerRecords(List<BackendIdentifierDTO> backendidentifiers, DataControllerRequest dcRequest,String userid) {

		JsonArray recordsArray = new JsonArray();
		ProfileManagementBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ProfileManagementBusinessDelegate.class);
		JsonObject processedResult = new JsonObject();
		if(!backendidentifiers.isEmpty()) {
		for (BackendIdentifierDTO backedDTO : backendidentifiers) {
			if (backedDTO == null)
				continue;
			MemberSearchBean memberSearchBean = new MemberSearchBean();
			memberSearchBean.setSearchType("CUSTOMER_SEARCH");
			memberSearchBean.setCustomerId(backedDTO.getBackendId());
			memberSearchBean.setSortVariable("name");
			memberSearchBean.setSortDirection("ASC");
			memberSearchBean.setPageOffset("0");
			memberSearchBean.setPageSize("100");
			memberSearchBean.setCompanyLegalUnit(backedDTO.getCompanyLegalUnit());
			

			Map<String, String> configurations = BundleConfigurationHandler
					.fetchBundleConfigurations(BundleConfigurationHandler.BUDLENAME_C360, dcRequest);
			 PasswordHistoryManagement pm = new PasswordHistoryManagement(dcRequest, true);
			DBXResult dbxResult = businessDelegate.searchCustomer(configurations, memberSearchBean,
					dcRequest.getHeaderMap(), pm);

			if (dbxResult.getResponse() != null) {
				JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
				JsonElement customerSearchRecords = jsonObject.get("records");
				if (customerSearchRecords != null && customerSearchRecords.isJsonArray()) {
					recordsArray.addAll(customerSearchRecords.getAsJsonArray());
				}
			}
		}
		}
		else {
			Map<String, String> configurations = BundleConfigurationHandler
					.fetchBundleConfigurations(BundleConfigurationHandler.BUDLENAME_C360, dcRequest);

			MemberSearchBean memberSearchBean = new MemberSearchBean();
			memberSearchBean.setSearchType("CUSTOMER_SEARCH");
			memberSearchBean.setCustomerUsername(userid);
			memberSearchBean.setSortVariable("name");
			memberSearchBean.setSortDirection("ASC");
			memberSearchBean.setPageOffset("0");
			memberSearchBean.setPageSize("100");
			// memberSearchBean.setCompanyLegalUnit(backedDTO.getCompanyLegalUnit());
			 PasswordHistoryManagement pm = new PasswordHistoryManagement(dcRequest, true);
			DBXResult dbxResult = businessDelegate.searchCustomer(configurations, memberSearchBean,
					dcRequest.getHeaderMap(), pm);
			if (dbxResult.getResponse() != null) {
				JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
				JsonElement customerSearchRecords = jsonObject.get("records");
				if (customerSearchRecords != null && customerSearchRecords.isJsonArray()) {
					recordsArray.addAll(customerSearchRecords.getAsJsonArray());
				}
			}
		}
		processedResult.add("records", recordsArray);
		return JSONToResult.convert(processedResult.toString());
	}

	@Override
	public Result customerLegalEntitiesGet(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse dcResponse) throws ApplicationException {
		
		Result result = new Result();

		Map<String, String> map = HelperMethods.getInputParamMap(inputArray);

		Iterator<String> iterator = request.getParameterNames();

		JsonObject inputObject = new JsonObject();

		while (iterator.hasNext()) {
			String key = iterator.next();
			if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key)))
					&& StringUtils.isNotBlank(request.getParameter(key))) {
				map.put(key, request.getParameter(key));
			}
		}

		String customerId = map.get(InfinityConstants.customerId);
		
		Map<String, String> loggedInUserInfo = HelperMethods.getCustomerFromAPIDBPIdentityService(request);
		if (HelperMethods.isPSD2Agent(request)) {
			inputObject.addProperty(InfinityConstants.isSuperAdmin, "true");
		} else if (HelperMethods.isAuthenticationCheckRequiredForService(loggedInUserInfo)) {
			Set<String> userPermissions = SessionScope.getAllPermissionsFromIdentityScope(request);
			if (!userPermissions.contains("USER_MANAGEMENT")) {
				ErrorCodeEnum.ERR_10051.setErrorCode(result);
				return result;
			}

			inputObject.addProperty(InfinityConstants.isSuperAdmin, "false");
		} else {
			inputObject.addProperty(InfinityConstants.isSuperAdmin, "true");
		}
		
		 ProfileManagementBusinessDelegate businessDelegate =
	                DBPAPIAbstractFactoryImpl.getBusinessDelegate(ProfileManagementBusinessDelegate.class);

	        Map<String, String> configurations = BundleConfigurationHandler
	                .fetchBundleConfigurations(BundleConfigurationHandler.BUDLENAME_C360, request);

	        CustomerDTO customerDTO = new CustomerDTO();
	        customerDTO.setId(customerId);
	        if(!CommonUtils.validInput(customerId))
	        {
	        	throw new ApplicationException(ErrorCodeEnum.ERR_10050);
	        }
	        DBXResult dbxResult =
	                businessDelegate.customerLegalEntitiesGet(customerDTO, request.getHeaderMap());
	        if (dbxResult.getResponse() != null) {
	            JsonObject jsonObject = (JsonObject) dbxResult.getResponse();
	            result = JSONToResult.convert(jsonObject.toString());

	            return result;
	        } 

		return result;
	}
	
}