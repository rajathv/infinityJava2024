package com.temenos.infinity.api.arrangements.resource.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.JSONUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.businessdelegate.api.ArrangementsBusinessDelegate;
import com.temenos.infinity.api.arrangements.config.ArrangementsAPIServices;
import com.temenos.infinity.api.arrangements.config.ServerConfigurations;
import com.temenos.infinity.api.arrangements.constants.ErrorCodeEnum;
import com.temenos.infinity.api.arrangements.constants.TemenosConstants;
import com.temenos.infinity.api.arrangements.dto.ArrangementsDTO;
import com.temenos.infinity.api.arrangements.resource.api.ArrangementsResource;
import com.temenos.infinity.api.arrangements.utils.ArrangementsUtils;
import com.temenos.infinity.api.arrangements.utils.CommonUtils;
import com.temenos.infinity.api.commons.config.InfinityServices;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.api.commons.invocation.Executor;

/**
 * 
 * @author smugesh
 * @version 1.0 Extends the {@link AccountsResource}
 */
public class ArrangementsResourceImpl implements ArrangementsResource {

    private static final Logger LOG = LogManager.getLogger(ArrangementsResourceImpl.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public Result getArrangementAccountsForAdmin(String backendUserId, String customerType, String customerID,
            String productLineId, String Account_id, String CompanyId, DataControllerRequest request, String authToken)
            throws ApplicationException {

        Result result = new Result();
        if (StringUtils.isBlank(authToken)) {
            return ErrorCodeEnum.ERR_20055.setErrorCode(new Result());
        }
        ArrangementsBusinessDelegate AccountsDelegateInstance = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ArrangementsBusinessDelegate.class);

        ArrangementsDTO inputPayloadDTO = new ArrangementsDTO();
        String username = request.getParameter(TemenosConstants.PARAM_USERNAME) != null
                ? request.getParameter(TemenosConstants.PARAM_USERNAME).toString()
                : "";

        List<ArrangementsDTO> accountsDTO = null;

        inputPayloadDTO.setCustomerType(customerType);
        inputPayloadDTO.setAccount_id(Account_id);
        inputPayloadDTO.setCustomerID(customerID);
        inputPayloadDTO.setUserName(username);
        try {
            if (StringUtils.isNotBlank(customerType)) {
                if (!customerType.equals("TYPE_ID_RETAIL") && !customerType.equals("TYPE_ID_PROSPECT")) {
                    accountsDTO = AccountsDelegateInstance.getBusinessUserArrangements(inputPayloadDTO, request);
                } else {
                    if (backendUserId.contains("-")) {
                        inputPayloadDTO.setBackendUserId(backendUserId);
                    } else {
                        CompanyId = StringUtils.isNotEmpty(CompanyId) ? CompanyId
                                : ServerConfigurations.AMS_COMPANYID.getValue();
                        inputPayloadDTO.setBackendUserId(CompanyId + "-" + backendUserId);
                    }
                    ArrayList resp = AccountsDelegateInstance.getArrangements(inputPayloadDTO, request, authToken, Boolean.TRUE);
                    accountsDTO = (List<ArrangementsDTO>) resp.get(1);
                    
                }
            } else {
                return ErrorCodeEnum.ERR_20047.setErrorCode(new Result());
            }
        } catch (Exception e) {
            LOG.error("Unable to fetch records from Backend" + e);
            throw new ApplicationException(ErrorCodeEnum.ERR_20041);
        }

        JSONArray accountsDTOArray = new JSONArray(accountsDTO);
        for (int i = 0; i < accountsDTOArray.length(); i++) {
            JSONObject arrangement = accountsDTOArray.getJSONObject(i);
            if (arrangement.has("account_id")) {
                String id = arrangement.getString("account_id");
                arrangement.put("Account_id", id);
                arrangement.remove("account_id");
            }
            if (arrangement.has("membership_id")) {
                String memberShipId = arrangement.getString("membership_id");
                if (StringUtils.isNotBlank(memberShipId)) {
                    arrangement.put("Membership_id", memberShipId);
                    arrangement.remove("membership_id");
                }
            }
            if (arrangement.has("taxId")) {
                String taxId = arrangement.getString("taxId");
                if (StringUtils.isNotBlank(taxId)) {
                    arrangement.put("TaxId", taxId);
                    arrangement.remove("taxId");
                }
            }
        }

        JSONObject responseObj = new JSONObject();
        responseObj.put("Accounts", accountsDTOArray);
        result = JSONToResult.convert(responseObj.toString());

        return result;
    }

    // Implementing the get Accounts method
    @SuppressWarnings({ "null", "unchecked", "rawtypes", "deprecation" })
	@Override
    public Result getArrangementAccounts(String backendUserId, String customerType, String customerID,
            String productLineId, String Account_id, String CompanyId, DataControllerRequest request, String authToken)
            throws ApplicationException {

        Result result = new Result();
        if (StringUtils.isBlank(authToken)) {
            return ErrorCodeEnum.ERR_20055.setErrorCode(new Result());
        }
        ArrangementsBusinessDelegate AccountsDelegateInstance = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ArrangementsBusinessDelegate.class);

        ArrangementsDTO inputPayloadDTO = new ArrangementsDTO();
        String username = request.getParameter(TemenosConstants.PARAM_USERNAME) != null
                ? request.getParameter(TemenosConstants.PARAM_USERNAME).toString()
                : "";

        List<ArrangementsDTO> accountsDTO = null;
        List <ArrangementsDTO> portfolioDTO = new ArrayList<ArrangementsDTO>();

        inputPayloadDTO.setCustomerType(customerType);
        inputPayloadDTO.setAccount_id(Account_id);
        inputPayloadDTO.setCustomerID(customerID);
        inputPayloadDTO.setUserName(username);
        String ArrangementResponse = null;
        try {
            if (StringUtils.isNotBlank(customerType) && customerType.equals("TYPE_ID_PROSPECT")) {
                if (backendUserId.contains("-")) {
                    inputPayloadDTO.setBackendUserId(backendUserId);
                } else {
                    CompanyId = StringUtils.isNotEmpty(CompanyId) ? CompanyId
                            : ServerConfigurations.AMS_COMPANYID.getValue();
                    inputPayloadDTO.setBackendUserId(CompanyId + "-" + backendUserId);
                }
                ArrayList resp = AccountsDelegateInstance.getArrangements(inputPayloadDTO, request, authToken,Boolean.FALSE);
                ArrangementResponse = (String) resp.get(0);
                accountsDTO = (List<ArrangementsDTO>) resp.get(1);
                int i=0;
                while(i<accountsDTO.size()) {
                	ArrangementsDTO portfolio = accountsDTO.get(i);
                	if(StringUtils.isNotBlank(portfolio.getAccountType())) {
                	if (portfolio.getAccountType().equals("Investment")) {
                		portfolioDTO.add(portfolio);
                		accountsDTO.remove(i);
                	}else i++;
                	}
                	else i++;
                }
            } else {
                boolean isImplicitBackendId = false;
                accountsDTO = new ArrayList<>();
                if (StringUtils.isBlank(CompanyId)) {
                    CompanyId = ServerConfigurations.AMS_COMPANYID.getValue();
                }
                String accountsString = "";
                JsonArray accountsjsonarray = new JsonArray();
                if (StringUtils.isNotBlank(backendUserId)) {
                    if (backendUserId.contains("-")) {
                        inputPayloadDTO.setBackendUserId(backendUserId);
                    } else {
                        CompanyId = StringUtils.isNotEmpty(CompanyId) ? CompanyId
                                : ServerConfigurations.AMS_COMPANYID.getValue();
                        inputPayloadDTO.setBackendUserId(CompanyId + "-" + backendUserId);
                    }
                    ArrayList resp =
                            AccountsDelegateInstance.getArrangements(inputPayloadDTO, request, authToken,Boolean.FALSE);
                    ArrangementResponse = (String) resp.get(0);
                    List<ArrangementsDTO> accountsDTO1 = (List<ArrangementsDTO>) resp.get(1);
                    int i=0;
                    while(i<accountsDTO1.size()) {
                    	ArrangementsDTO portfolio = accountsDTO1.get(i);
                    	if(StringUtils.isNotBlank(portfolio.getAccountType())) {
                    	if (portfolio.getAccountType().equals("Investment")) {
                    		portfolioDTO.add(portfolio);
                    		accountsDTO1.remove(i);
                    	}else i++;
                    	}
                    	else i++;
                    }
                    for (ArrangementsDTO arrangementsDTO : accountsDTO1) {
                        if (Boolean.parseBoolean(arrangementsDTO.getExternalIndicator())
                                || "Yes".equals(arrangementsDTO.getExternalIndicator())) {
                            accountsDTO.add(arrangementsDTO);
                        } else {
                            JsonObject json = new JsonObject();
                            json = getArrangementJson(arrangementsDTO);
                            if (json != null) {
                                json.addProperty("customerId", backendUserId);
                                accountsjsonarray.add(json);
                                accountsString += JSONUtil.getString(json, "accountId") + " ";
                            }
                        }
                    }
                }

                Map<String, ArrangementsDTO> implicitCIFAccountsInfo = new HashMap<String, ArrangementsDTO>();
                Map<String, String> newAcntCoreCustomerId = new HashMap<String, String>();
                HashSet<String> newAccountCoreCustomerIdList = new HashSet<String>();
                Map<String, Set> coreCustomerActions = new HashMap<String, Set>();
                if (StringUtils.isNotBlank(customerID)) {
                    Map<String, Object> inputParams = new HashMap<String, Object>();
                    inputParams.put("$filter", "customerId eq " + customerID + " and companyLegalUnit eq "+ CompanyId+ " and autoSyncAccounts eq 1");
                    request.addRequestParam_("$filter",
                            "customerId eq " + customerID + " and companyLegalUnit eq "+ CompanyId+ " and autoSyncAccounts eq 1");
                    String contractCustomersString = DBPServiceExecutorBuilder.builder()
                            .withServiceId(ArrangementsAPIServices.DBXDB_CONTRACT_CUSTOMERS.getServiceName())
                            .withOperationId(ArrangementsAPIServices.DBXDB_CONTRACT_CUSTOMERS.getOperationName())
                            .withRequestParameters(inputParams).withRequestHeaders(request.getHeaderMap())
                            .withDataControllerRequest(request)
                            .build().getResponse();
                    JsonObject contractCustomers =
                            new JsonParser().parse(contractCustomersString).getAsJsonObject();
                    Set<String> implicitCustomers = new HashSet<>();
                    if (contractCustomers != null && contractCustomers.has("contractcustomers")
                            && contractCustomers.get("contractcustomers") != null
                            && contractCustomers.get("contractcustomers").isJsonArray()
                            && contractCustomers.get("contractcustomers").getAsJsonArray().size() > 0) {
                        for (JsonElement jsonelement : contractCustomers.get("contractcustomers")
                                .getAsJsonArray()) {
                            implicitCustomers.add(
                                    jsonelement.getAsJsonObject().get("coreCustomerId").getAsString());
                        }
                        // if (StringUtils.isNotBlank(backendUserId) && implicitCustomers.contains(backendUserId)) {
                        // implicitCustomers.remove(backendUserId);
                        // isImplicitBackendId = true;
                        // }
                        LOG.error("Printing the implicit customer:" + implicitCustomers);
                    }

                    if (!implicitCustomers.isEmpty()) {
                        for (String coreCustomerId : implicitCustomers) {
                            inputPayloadDTO.setBackendUserId(CompanyId + "-" + coreCustomerId);
                            ArrayList resp =
                                    AccountsDelegateInstance.getArrangements(inputPayloadDTO, request, authToken,Boolean.TRUE);
                            ArrangementResponse = (String) resp.get(0);
                            List<ArrangementsDTO> accountsDTO1 = (List<ArrangementsDTO>) resp.get(1);
                            for (ArrangementsDTO arrangementsDTO : accountsDTO1) {
                                if (!Boolean.parseBoolean(arrangementsDTO.getExternalIndicator())
                                        || "False".equals(arrangementsDTO.getExternalIndicator())) {
                                    implicitCIFAccountsInfo.put(arrangementsDTO.getAccount_id(), arrangementsDTO);
                                    JsonObject json = new JsonObject();
                                    if (json != null) {
                                        json = getArrangementJson(arrangementsDTO);
                                        json.addProperty("customerId", coreCustomerId);
                                        accountsjsonarray.add(json);
                                    }
                                }
                            }
                        }
                    }
                    inputParams = new HashMap<String, Object>();
                    inputParams.put("accounts", accountsjsonarray.toString());
                    inputParams.put("customerId", customerID);
                    request.addRequestParam_("accounts", accountsjsonarray.toString());
                    request.addRequestParam_("customerId", customerID);
                    String validAccountsList = DBPServiceExecutorBuilder.builder()
                            .withServiceId(
                                    ArrangementsAPIServices.DBPPRODUCTSERVICES_NEWACCOUNTPROCESSING
                                            .getServiceName())
                            .withOperationId(
                                    ArrangementsAPIServices.DBPPRODUCTSERVICES_NEWACCOUNTPROCESSING
                                            .getOperationName())
                            .withRequestParameters(inputParams).withRequestHeaders(request.getHeaderMap())
                            .withDataControllerRequest(request)
                            .build().getResponse();
                    JsonObject dashBoardAccounts = new JsonParser().parse(validAccountsList).getAsJsonObject();
                    accountsString =
                            dashBoardAccounts.get("accounts") != null
                                    ? dashBoardAccounts.get("accounts").getAsString()
                                    : "";
                    String newAccounts = dashBoardAccounts.get("newAccounts") != null
                            ? dashBoardAccounts.get("newAccounts").getAsString()
                            : "";
                    if (StringUtils.isNotBlank(newAccounts)) {
                        String[] accountsInfo = newAccounts.split("\\|");
                        for (int i = 0; i < accountsInfo.length; i++) {
                            String[] singleAccountInfo = accountsInfo[i].split(":");
                            newAcntCoreCustomerId.put(singleAccountInfo[1], singleAccountInfo[0]);
                            newAccountCoreCustomerIdList.add(singleAccountInfo[0]);
                        }
                    }
                    coreCustomerActions =
                            fetchDefaultAccountActions(request, customerID, newAccountCoreCustomerIdList);
                }
                String[] validAccountsArray = accountsString.split(" ");
                StringBuilder bulkAccountsString = new StringBuilder();
                for (int i = 0; i < validAccountsArray.length; i++) {
                    if (implicitCIFAccountsInfo.containsKey(validAccountsArray[i]))
                        accountsDTO.add(implicitCIFAccountsInfo.get(validAccountsArray[i]));
                    else {
                        if (!bulkAccountsString.toString().isEmpty()) {
                            bulkAccountsString.append(" ");
                        }
                        bulkAccountsString.append(CompanyId).append("-");
                        bulkAccountsString.append(validAccountsArray[i]);
                    }
                }

                if (StringUtils.isNotBlank(bulkAccountsString.toString())) {
                    inputPayloadDTO.setAccount_id(bulkAccountsString.toString());
                    ArrangementsBusinessDelegate arrangementsBusinessDelegateImpl =
                            DBPAPIAbstractFactoryImpl.getBusinessDelegate(ArrangementsBusinessDelegate.class);
                    List<ArrangementsDTO> accountIdBasedArrangements =
                            arrangementsBusinessDelegateImpl.getArrangementBulkOverview(ArrangementResponse,inputPayloadDTO,
                                    request, authToken);
                    for (ArrangementsDTO dto : accountIdBasedArrangements) {
                        if (newAcntCoreCustomerId.containsKey(dto.getAccount_id())) {
                        	if(coreCustomerActions!=null && coreCustomerActions.get(newAcntCoreCustomerId.get(dto.getAccount_id()))!=null) {
                                dto.setActions(coreCustomerActions.get(newAcntCoreCustomerId.get(dto.getAccount_id()))
                                        .toString());
                        	}

                            dto.setIsNew("true");
                        } else {
                            dto.setIsNew("false");
                        }
                        accountsDTO.add(dto);
                    }
                }
            }
            accountsDTO.addAll(portfolioDTO);
            String mockMortgageResponse = ServerConfigurations.MOCK_MORTGAGE_RESPONSE.getValue();
            if(mockMortgageResponse != null && mockMortgageResponse.equalsIgnoreCase("Yes")) {
            	List<ArrangementsDTO> mortgageDTOs = getMortgages();
            	accountsDTO.addAll(mortgageDTOs);
            }
        } catch (Exception e) {
            LOG.error("Unable to fetch records from Backend", e);
            throw new ApplicationException(ErrorCodeEnum.ERR_20041);
        }

        JSONArray accountsDTOArray = new JSONArray(accountsDTO);

        JSONObject responseObj =
                new JSONObject();
        responseObj.put("Accounts", accountsDTOArray);
        result = JSONToResult.convert(responseObj.toString());
        LOG.error("Accounts  " + accountsDTOArray.toString());
        return result;
    }

    private JsonObject getArrangementJson(ArrangementsDTO arrangementsDTO) {
        JsonObject json = new JsonObject();
        if (StringUtils.isBlank(arrangementsDTO.getAccount_id())
                || StringUtils.isBlank(arrangementsDTO.getAccountType())
                || StringUtils.isBlank(arrangementsDTO.getAccountName())
                || StringUtils.isBlank(arrangementsDTO.getArrangementId())
                || StringUtils.isBlank(arrangementsDTO.getPartyRole())) {
            return null;
        }
        json.addProperty("accountId", arrangementsDTO.getAccount_id());
        json.addProperty("customerId", arrangementsDTO.getMembership_id());
        json.addProperty("accountType", arrangementsDTO.getAccountType());
        json.addProperty("accountName", arrangementsDTO.getAccountName());
        json.addProperty("arrangementId", arrangementsDTO.getArrangementId());
        json.addProperty("roleDisplayName", arrangementsDTO.getPartyRole());
        return json;
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    public Map<String, Set> fetchDefaultAccountActions(DataControllerRequest request, String customerId,
            HashSet<String> coreCustomerIdList) {
        Map<String, Object> inputParams = new HashMap<>();
        Map<String, Set> coreCustomerActions = new HashMap<>();
        try {
            for (String coreCustomerId : coreCustomerIdList) {
                inputParams = new HashMap<>();
                inputParams.put("_userId", customerId);
                inputParams.put("_coreCustomerId", coreCustomerId);
                request.addRequestParam_("_userId", customerId);
                request.addRequestParam_("_coreCustomerId", coreCustomerId);
                String defaultActionsString = DBPServiceExecutorBuilder.builder()
                        .withServiceId(
                                ArrangementsAPIServices.DBXDB_FETCH_DEFAULT_ACTIONS.getServiceName())
                        .withOperationId(
                                ArrangementsAPIServices.DBXDB_FETCH_DEFAULT_ACTIONS.getOperationName())
                        .withRequestParameters(inputParams).withRequestHeaders(request.getHeaderMap())
                        .withDataControllerRequest(request)
                        .build().getResponse();
                JsonObject defaultActionsJson = new JsonParser().parse(defaultActionsString).getAsJsonObject();
                String actions = defaultActionsJson.get("defaultAccountActions").getAsString();
                coreCustomerActions.put(coreCustomerId, new HashSet<>(Arrays.asList(StringUtils.split(actions, ","))));
            }
            return coreCustomerActions;
        } catch (Exception e) {
            LOG.error(e);
        }
        return coreCustomerActions;
    }

    // Implementing the get Account Overview method
    @SuppressWarnings("deprecation")
	@Override
    public Result getAccountOverview(String backendUserId, String customerType, String customerID, String productLineId,
            String Account_id, DataControllerRequest request, String authToken) throws ApplicationException {

        Result result = new Result();

        if (StringUtils.isBlank(authToken)) {
            return ErrorCodeEnum.ERR_20055.setErrorCode(new Result());
        }

        ArrangementsBusinessDelegate AccountsDelegateInstance = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ArrangementsBusinessDelegate.class);

        ArrangementsDTO inputPayloadDTO = new ArrangementsDTO();

        List<ArrangementsDTO> accountsDTO = null;
        inputPayloadDTO.setCustomerType(customerType);
        
        Map<String, Object> inputMap = new HashMap<>();
        Map<String, Object> headerMap = new HashMap<>();
        String mortgageString = null;
        if(Account_id.equalsIgnoreCase("MORT12345") || Account_id.equalsIgnoreCase("122877")|| Account_id.equalsIgnoreCase("122878")) {
            
            try {          mortgageString = Executor.invokePassThroughServiceAndGetString((InfinityServices)ArrangementsAPIServices.MOCKMORTGAGEMS_GETMORTGAGEDETAILS, inputMap, headerMap);
            LOG.error("AMS Response getDetails" + mortgageString);
          } catch (Exception e) {
            LOG.error("Unable to fetch Arrangements " + e);
          } 
          JSONArray mortgages = new JSONArray(mortgageString);
          List<ArrangementsDTO> mDTOs = new ArrayList<>();
          ArrangementsDTO mDTO = new ArrangementsDTO();
          JsonObject priAccHolder = new JsonObject();
          for(int i=0;i<mortgages.length();i++) {
        	  JSONObject mortgage = mortgages.getJSONObject(i);
        	  if(mortgage.get("accountID").equals(Account_id)) {
        		  String accHolderString = mortgage.get("accountHolder").toString();
        		  JsonObject accHolder = new JsonParser().parse(accHolderString).getAsJsonObject();
        		  priAccHolder.addProperty("username", accHolder.has("username") ? accHolder.get("username").getAsString() :"");
                  priAccHolder.addProperty("fullname", accHolder.has("fullname") ? accHolder.get("fullname").getAsString() :"");
                  mDTO.setAccountHolder(priAccHolder.toString());
        		  mDTO.setAccount_id(mortgage.has("accountID") ? mortgage.get("accountID").toString() : "");
        		  mDTO.setIBAN(mortgage.has("IBAN") ? mortgage.get("IBAN").toString() : "");
        		  mDTO.setOriginalAmount(mortgage.has("originalAmount") ? mortgage.get("originalAmount").toString() : "");
        		  mDTO.setNextPaymentAmount(mortgage.has("nextPaymentAmount") ? mortgage.get("nextPaymentAmount").toString() : "");
        		  mDTO.setPaymentDue(mortgage.has("NextPaymentDue") ? mortgage.get("NextPaymentDue").toString() : "");
        		  mDTO.setOutstandingBalance(mortgage.has("outstandingBalance") ? mortgage.get("outstandingBalance").toString() : "");
        		  mDTO.setPaidInstallmentsCount(mortgage.has("paidInstallmentsCount") ? mortgage.get("paidInstallmentsCount").toString() : "");
        		  mDTO.setOverDueInstallmentsCount(mortgage.has("overDueInstallmentsCount") ? mortgage.get("overDueInstallmentsCount").toString() : "");
        		  mDTO.setFutureInstallmentsCount(mortgage.has("futureInstallmentsCount") ? mortgage.get("futureInstallmentsCount").toString() : "");
        		  mDTO.setInterestPaidYTD(mortgage.has("interestPaidYTD") ? mortgage.get("interestPaidYTD").toString() : "");
        		  mDTO.setLastPaymentAmount(mortgage.has("lastPaymentAmount") ? mortgage.get("lastPaymentAmount").toString() : "");
        		  mDTO.setLastPaymentDate(mortgage.has("lastPaymentDate") ? mortgage.get("lastPaymentDate").toString() : "");
        		  mDTO.setRePaymentFrequency(mortgage.has("rePaymentFrequency") ? mortgage.get("rePaymentFrequency").toString() : "");
        		  mDTO.setInterestRate(mortgage.has("interestRate") ? mortgage.get("interestRate").toString() : "");
        		  mDTO.setSanctionedDate(mortgage.has("sanctionedDate") ? mortgage.get("sanctionedDate").toString() : "");
        		  mDTO.setMaturityDate(mortgage.has("maturityDate") ? mortgage.get("maturityDate").toString() : "");
        		  mDTO.setTermAmount(mortgage.has("sanctionedAmount") ? mortgage.get("sanctionedAmount").toString() : "");
        		  mDTO.setAccountType(mortgage.has("accountType") ? mortgage.get("accountType").toString() : "");
        		  mDTO.setTypeDescription(mortgage.has("accountType") ? mortgage.get("accountType").toString() : "");

        		  mDTO.setAccountName(mortgage.has("accountName") ? mortgage.get("accountName").toString() : "");
        		  mDTO.setArrangementId(mortgage.has("arrangementId") ? mortgage.get("arrangementId").toString() : "");
        		  mDTO.setBankname(mortgage.has("bankName") ? mortgage.get("bankName").toString() : "");
        		  mDTO.setCompanyCode(mortgage.has("companyId") ? mortgage.get("companyId").toString() : "");
        		  mDTO.setCurrencyCode(mortgage.has("currencyCode") ? mortgage.get("currencyCode").toString() : "");
        		  mDTO.setOpeningDate(mortgage.has("openingDate") ? mortgage.get("openingDate").toString() : "");
        		  mDTO.setDisplayName(mortgage.has("displayName") ? mortgage.get("displayName").toString() : "");
        		  mDTO.setExternalIndicator(mortgage.has("externalIndicator") ? mortgage.get("externalIndicator").toString() : "");
        		  mDTO.setNickName(mortgage.has("nickName") ? mortgage.get("nickName").toString() : "");
        		  mDTO.setPrincipalValue(mortgage.has("principalValue") ? mortgage.get("principalValue").toString() : "");
        		  mDTO.setProcessingTime(mortgage.has("processingTime") ? mortgage.get("processingTime").toString() : "");
        		  mDTO.setProduct(mortgage.has("productId") ? mortgage.get("productId").toString() : "");
        		  mDTO.setStatusDesc(mortgage.has("statusDesc") ? mortgage.get("statusDesc").toString() : "");
        		  mDTO.setSupportBillPay(mortgage.has("supportBillPay") ? mortgage.get("supportBillPay").toString() : "");
        		  mDTO.setSupportChecks(mortgage.has("supportChecks") ? mortgage.get("supportChecks").toString() : "");
        		  mDTO.setSupportTransferFrom(mortgage.has("supportTransferFrom") ? mortgage.get("supportTransferFrom").toString() : "");
        		  mDTO.setSupportTransferTo(mortgage.has("supportTransferTo") ? mortgage.get("supportTransferTo").toString() : "");
        		  mDTO.setAvailableBalance(mortgage.has("availableBalance") ? Double.valueOf(mortgage.get("availableBalance").toString()).doubleValue() : 0.0);
        		  mDTO.setCurrentBalance(mortgage.has("currentBalance") ? Double.valueOf(mortgage.get("currentBalance").toString()).doubleValue() : 0.0);
        		  mDTO.setDividendLastPaidAmount(mortgage.has("dividendLastPaidAmount") ? Double.valueOf(mortgage.get("dividendLastPaidAmount").toString()).doubleValue() : 0.0);
        		  mDTO.setDividendPaidYTD(mortgage.has("dividendPaidYTD") ? Double.valueOf(mortgage.get("dividendPaidYTD").toString()).doubleValue() : 0.0);
        		  mDTO.setPendingDeposit(mortgage.has("pendingDeposit") ? Double.valueOf(mortgage.get("pendingDeposit").toString()).doubleValue() : 0.0);
        		  mDTO.setPendingWithdrawal(mortgage.has("pendingWithdrawal") ? Double.valueOf(mortgage.get("pendingWithdrawal").toString()).doubleValue() : 0.0);
        		  mDTOs.add(mDTO);
        	  }
          }
        	JSONArray accountsDTOArray = new JSONArray(mDTOs);

            JSONObject responseObj = new JSONObject();
            responseObj.put("Accounts", accountsDTOArray);
            result = JSONToResult.convert(responseObj.toString());
            return result;
            }
          

       // String CompanyId = ArrangementsUtils.getUserAttributeFromIdentity(request, TemenosConstants.COMPANYID);
        String CompanyId = CommonUtils.getCompanyId(request);
        try {
            if (StringUtils.isNotBlank(customerType)) {
                    String AccIdWithCompany = StringUtils.EMPTY;
                    String featureActionsInCache = (String)MemoryManager.getFromCache(
        					DBPUtilitiesConstants.ACCOUNTS_POSTLOGIN_CACHE_KEY + CompanyId);
                    LOG.error("featureActionsInCache" + featureActionsInCache);  
                    try {
                        if (Account_id.contains("-")) {
                            AccIdWithCompany = Account_id;
                        } else {
                            AccIdWithCompany = ArrangementsUtils.getAccountIdWithCompanyFromCache(Account_id, request);
                            
                            
                            
                        }
                    } catch (Exception e) {
                        LOG.error("Unable to fetch account id with company from cache" + e);
                        return ErrorCodeEnum.ERR_20056.setErrorCode(new Result());
                    }
                    if (StringUtils.isNotBlank(AccIdWithCompany))
                        inputPayloadDTO.setAccount_id(AccIdWithCompany);
                    if (StringUtils.isNotBlank(backendUserId)){
                        if (backendUserId.contains("-")) {
                            inputPayloadDTO.setBackendUserId(backendUserId);
                        } else {
                            CompanyId = StringUtils.isNotEmpty(CompanyId) ? CompanyId
                                    : ServerConfigurations.AMS_COMPANYID.getValue();
                            inputPayloadDTO.setBackendUserId(CompanyId + "-" + backendUserId);
                        }
                    }
                    accountsDTO = AccountsDelegateInstance.getArrangementOverview(inputPayloadDTO, request, authToken);
            } else {
                return ErrorCodeEnum.ERR_20047.setErrorCode(new Result());
            }
        } catch (Exception e) {
            LOG.error("Unable to fetch records " + e);
            throw new ApplicationException(ErrorCodeEnum.ERR_20041);
        }
        JSONArray accountsDTOArray = new JSONArray(accountsDTO);

        JSONObject responseObj = new JSONObject();
        responseObj.put("Accounts", accountsDTOArray);
        result = JSONToResult.convert(responseObj.toString());
        return result;
    }

    @Override
    public Result getArrangementPreviewAccounts(String did, String userName, String backendUserId, String customerType,
            String productLine, String customerID, DataControllerRequest request, String authToken)
            throws ApplicationException {
        Result result = new Result();
        ArrangementsDTO inputPayloadDTO = new ArrangementsDTO();
        if (StringUtils.isBlank(authToken)) {
            return ErrorCodeEnum.ERR_20055.setErrorCode(new Result());
        }
        ArrangementsBusinessDelegate AccountsDelegateInstance = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ArrangementsBusinessDelegate.class);

        List<ArrangementsDTO> accountsDTO = null;
        if (StringUtils.isNotBlank(did) && StringUtils.isNotBlank(userName)) {
            inputPayloadDTO.setBackendUserId(backendUserId);
            inputPayloadDTO.setDeviceId(did);
            inputPayloadDTO.setCustomerID(customerID);
            accountsDTO = AccountsDelegateInstance.getBusinessUserArrangementPreview(inputPayloadDTO, request);
        }
        if (accountsDTO == null || accountsDTO.size() == 0) {
            JSONArray accountsDTOArray = new JSONArray();
            JSONObject responseObj = new JSONObject();
            responseObj.put("Accounts", accountsDTOArray);
            result = JSONToResult.convert(responseObj.toString());
            result.getAllDatasets().get(0).setId("records");
            return result;
        } else {
            String CompanyId = ArrangementsUtils.getUserAttributeFromIdentity(request, TemenosConstants.COMPANYID);
            try {
                CompanyId = StringUtils.isNotEmpty(CompanyId) ? CompanyId
                        : ServerConfigurations.AMS_COMPANYID.getValue();
            } catch (Exception e) {
                LOG.error("Error while fetching companyId");
            }
            String ARRANGEMENTS_BACKEND = ServerConfigurations.ARRANGEMENTS_BACKEND.getValueIfExists();
            if (ARRANGEMENTS_BACKEND.equals("t24")) {
            	try {
            		HashMap<String, Object> headerParams = new HashMap<String, Object>();
            		HashMap<String, Object> inputParams = new HashMap<String, Object>();
            		inputParams.put("loginUserId","PreLogin-"+customerID);
            		request.addRequestParam_("loginUserId","PreLogin-"+customerID);
    				String accounts = DBPServiceExecutorBuilder.builder()
                            .withServiceId("ArrangementT24ISAccounts")
                            .withOperationId("getAccountsByCoreCustomerIdList")
                            .withRequestParameters(inputParams).withRequestHeaders(headerParams)
                            .withDataControllerRequest(request).build().getResponse();
    				JSONObject accJson = new JSONObject(accounts);
    				JSONArray accountsRes = accJson.getJSONArray("Accounts");
    				JSONObject records = new JSONObject();
    				records.put("records", accountsRes);
    				return JSONToResult.convert(records.toString());
                } catch (Exception e) {
                	LOG.error("Error in resource layer::",e);
                    throw new ApplicationException(ErrorCodeEnum.ERR_20041);
                }
        	}
            else {
            result = getArrangementAccounts(backendUserId, customerType, customerID, productLine, null, CompanyId,
                    request, authToken);
            result.getAllDatasets().get(0).setId("records");
            return result;
            }
        }
    }
    private List<ArrangementsDTO> getMortgages() {
        Map<String, Object> inputMap = new HashMap<>();
        Map<String, Object> headerMap = new HashMap<>();
        String mortgageString = null;
        try {          mortgageString = Executor.invokePassThroughServiceAndGetString((InfinityServices)ArrangementsAPIServices.MOCKMORTGAGEMS_FETCHMORTGAGEACCOUNT, inputMap, headerMap);
          LOG.error("AMS Response" + mortgageString);
        } catch (Exception e) {
          LOG.error("Unable to fetch Arrangements " + e);
        } 

        List<ArrangementsDTO> mortgagesDTOs = new ArrayList<>();
        JSONArray mortgages = new JSONArray(mortgageString);
        for(int i=0;i<mortgages.length();i++) {
        JSONObject mortgage = mortgages.getJSONObject(i);
        ArrangementsDTO mortgageDTO = new ArrangementsDTO();
        String companyId = mortgage.has("company") ? mortgage.getJSONObject("company").getString("companyReference") : "";
        String aid = mortgage.has("linkedReference") ? mortgage.getString("linkedReference") : "";
        String accountId = aid.replace(companyId + "-", "");
        mortgageDTO.setAccount_id(accountId);
        mortgageDTO.setAccountType("mortgageFacility");
        mortgageDTO.setTypeDescription("mortgageFacility");
        mortgageDTO.setCompanyCode(companyId);
        mortgageDTO.setCurrencyCode(mortgage.has("currency") ? mortgage.getString("currency") : "");
        mortgageDTO.setProductGroup(mortgage.has("productGroup") ? mortgage.getString("productGroup") : "");
        mortgageDTO.setProduct(mortgage.has("product") ? mortgage.getString("product") : "");
        mortgageDTO.setArrangementId(accountId);
        mortgageDTO.setServiceType(mortgage.has("productDescription") ? mortgage.getString("productDescription") : "");
        mortgageDTO.setAccountName(mortgage.has("shortTitle") ? mortgage.getString("shortTitle") : "");
        mortgageDTO.setPartyRole("Owner");
        mortgageDTO.setOutstandingBalance(mortgage.has("outstandingBalance") ? mortgage.getString("outstandingBalance") : "");
        mortgageDTO.setProcessingTime(mortgage.has("processDate") ? mortgage.getString("processDate"):"");
        mortgageDTO.setNickName(mortgage.has("shortTitle") ? mortgage.getString("shortTitle") : "");
        mortgagesDTOs.add(mortgageDTO);
        }
        return mortgagesDTOs;
      }

    @Override
    public Result getUserDetailsFromDBX(String userName, DataControllerRequest request) throws ApplicationException {
        Result result = new Result();
        ArrangementsDTO inputPayloadDTO = new ArrangementsDTO();

        ArrangementsBusinessDelegate AccountsDelegateInstance = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ArrangementsBusinessDelegate.class);

        List<ArrangementsDTO> accountsDTO = null;
        inputPayloadDTO.setUserName(userName);
        accountsDTO = AccountsDelegateInstance.getUserDetailsFromDBX(inputPayloadDTO, request);
        JSONArray accountsDTOArray = new JSONArray(accountsDTO);

        JSONObject responseObj = new JSONObject();
        responseObj.put("Accounts", accountsDTOArray);
        result = JSONToResult.convert(responseObj.toString());
        result.getAllDatasets().get(0).setId("records");
        return result;
    }

    @Override
    public Result getAccountDetailsForCombinedStatements(String accountID, String customerType, String authToken,
            String companyId, DataControllerRequest request)
            throws Exception {

        ArrangementsBusinessDelegate AccountsDelegateInstance = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ArrangementsBusinessDelegate.class);

        String AccIdWithCompanyId = accountID;
        if (accountID != null && !accountID.contains("-")) {
            if (StringUtils.isNotBlank(companyId)) {
                AccIdWithCompanyId = companyId + "-" + accountID;
            } else {
                try {
                    AccIdWithCompanyId = ArrangementsUtils.getAccountIdWithCompanyFromCache(accountID, request);
                } catch (Exception e) {
                    LOG.error("Unable to get account with company id from cache " + e);
                    return ErrorCodeEnum.ERR_20046.setErrorCode(new Result());
                }
            }

        }
        JSONObject accounDetails = new JSONObject();
        if (!customerType.equals("TYPE_ID_RETAIL") && !customerType.equals("TYPE_ID_PROSPECT")) {
            accounDetails = AccountsDelegateInstance.getAccountDetailForCombinedStatementsfromT24(accountID,
                    customerType, authToken);

        } else {
            accounDetails = AccountsDelegateInstance.getAccountDetailForCombinedStatements(AccIdWithCompanyId,
                    customerType, authToken);
        }
        Result result = JSONToResult.convert(accounDetails.toString());
        return result;
    }

	@Override
	public Result getSimulationResults(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		ArrangementsBusinessDelegate SimulatedResultsDelegateInstance = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ArrangementsBusinessDelegate.class);
		Result simultedResults = SimulatedResultsDelegateInstance.getSimulatedResults(methodID, inputArray, request, response);
		return simultedResults;
	}

}
