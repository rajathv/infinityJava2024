package com.temenos.infinity.api.accountsweeps.resource.impl;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.api.accountsweeps.businessdelegate.api.AccountSweepsBusinessDelegate;
import com.temenos.infinity.api.accountsweeps.constants.Constants;
import com.temenos.infinity.api.accountsweeps.constants.ErrorCodeEnum;
import com.temenos.infinity.api.accountsweeps.dto.AccountSweepsDTO;
import com.temenos.infinity.api.accountsweeps.resource.api.AccountSweepsResource;
import com.temenos.infinity.api.srmstransactions.dto.SessionMap;
import com.temenos.infinity.api.srmstransactions.utils.MemoryManagerUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.temenos.infinity.api.accountsweeps.constants.Constants.*;

/**
 * @author naveen.yerra
 */
public class AccountSweepsResourceImpl implements AccountSweepsResource {

    private final AccountSweepsBusinessDelegate sweepsBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountSweepsBusinessDelegate.class);
    private final AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
    private static final LoggerUtil LOG = new LoggerUtil(AccountSweepsResourceImpl.class);

    AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountBusinessDelegate.class);

    public Result getSweepByAccountId(HashMap<String, Object> inputParams, DataControllerRequest controllerRequest) {
        Result result = new Result();
        String accountId = (String) inputParams.getOrDefault(ACCOUNT_ID, "");

        if(StringUtils.isBlank(accountId)) {
            return ErrorCodeEnum.ERR_3003.setErrorCode(result);
        }

        HashMap<String, Object> customer = (HashMap<String, Object>) CustomerSession.getCustomerMap(controllerRequest);
        String customerId = CustomerSession.getCustomerId(customer);

        if (!authorizationChecksBusinessDelegate.isOneOfMyAccounts(customerId, accountId)) {
            return ErrorCodeEnum.ERR_3008.setErrorCode(result);
        }
        if(featureActionIdCheckAccountLevel(customerId, Constants.ACCOUNT_SWEEP_VIEW, accountId,
                CustomerSession.IsCombinedUser(customer)))
            return ErrorCodeEnum. ERR_3012.setErrorCode(result);

        AccountSweepsDTO sweepsDTO = sweepsBusinessDelegate.getSweepByAccountId(accountId);

        if(StringUtils.isNotBlank(sweepsDTO.getErrorCode()) || StringUtils.isNotBlank(sweepsDTO.getErrorMessage())) {
            result = JSONToResult.convert(new JSONObject(sweepsDTO).toString());
            return result;
        }

        result = JSONToResult.convert(new JSONObject().put(ACCOUNT_SWEEP, new JSONObject(sweepsDTO)).toString());
        return result;
    }

    public Result createSweep(String methodID, Object[] inputArray, DataControllerRequest request,
                              DataControllerResponse response) {
        Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];
        Result result=new Result();
        AccountSweepsDTO accountSweepsDTO;
        try {
            AccountSweepsDTO sweepsDto = JSONUtils.parse(new JSONObject(inputParams).toString(), AccountSweepsDTO.class);

            Map<String, Object> customer = CustomerSession.getCustomerMap(request);
            String customerId = CustomerSession.getCustomerId(customer);
            if(customerId == null)
                return ErrorCodeEnum.ERR_3005.setErrorCode(result);
            if(validateInput(sweepsDto))
                return ErrorCodeEnum.ERR_3011.setErrorCode(result);


            // Validating accountId and account type against session info.
            if(validateAccount(sweepsDto.getPrimaryAccountNumber(), customerId, "Create", true)) {
                LOG.error("User is not Authorized for the account number used");
                return ErrorCodeEnum.ERR_3008.setErrorCode(result);
            }
            if(validateAccount(sweepsDto.getSecondaryAccountNumber(), customerId, "Create", false)) {
                LOG.error("User is not Authorized for the account number used");
                return ErrorCodeEnum.ERR_3008.setErrorCode(result);
            }

            if(accountStatusAndCurrencyCheck(sweepsDto.getPrimaryAccountNumber(),sweepsDto.getSecondaryAccountNumber(),customerId)) {
                LOG.error("Either Currency code of both accounts not matching or Account Status of both account is not Active or Closure Pending");
                return ErrorCodeEnum.ERR_3013.setErrorCode(result);
            }
            
            if(featureActionIdCheckAccountLevel(customerId, Constants.ACCOUNT_SWEEP_CREATE,
                    sweepsDto.getPrimaryAccountNumber(), CustomerSession.IsCombinedUser(customer)))
                return ErrorCodeEnum. ERR_3012.setErrorCode(result);

            if(validateDateAgainstBankDate(sweepsDto.getStartDate(), sweepsDto.getEndDate(), request))
                return ErrorCodeEnum.ERR_3010.setErrorCode(result);

            accountSweepsDTO = sweepsBusinessDelegate.createSweep(sweepsDto,request);
            accountSweepsDTO.setPrimaryAccountNumber(sweepsDto.getPrimaryAccountNumber());
            accountSweepsDTO.setSecondaryAccountNumber(sweepsDto.getSecondaryAccountNumber());

            JSONObject sweepObj = new JSONObject(accountSweepsDTO);
            result = JSONToResult.convert(sweepObj.toString());
        }
        catch (Exception e){
            LOG.error("Error occurred while creating sweep "+e);
            return ErrorCodeEnum.ERR_3007.setErrorCode(result);
        }
        return result;
    }

    @Override
    public Result getAccountSweeps(FilterDTO filterDTO, DataControllerRequest request) {

        Result result=new Result();
        JSONObject responseObj = new JSONObject();

        HashMap<String, Object> customer = (HashMap<String, Object>) CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);
        if(customerId == null)
            return ErrorCodeEnum.ERR_3005.setErrorCode(result);

        Set<String> accounts = getAccounts(customerId);
        if(accounts.size() == 0)
            return ErrorCodeEnum.ERR_3006.setErrorCode(result);

        List<AccountSweepsDTO> responseSweeps = sweepsBusinessDelegate.getAllAccountSweeps(accounts);

        if (responseSweeps == null) {
            LOG.error("Error occurred while fetching account sweeps from backend");
            return ErrorCodeEnum.ERR_3001.setErrorCode(result);
        }

        try {
            List<AccountSweepsDTO> filteredRecords = filterDTO.filter(responseSweeps);
            responseObj.put(ACCOUNT_SWEEP, filteredRecords);
            result = JSONToResult.convert(responseObj.toString());
        } catch (Exception e) {
            result.addErrMsgParam("Failed to fetch the records");
            LOG.error("Error occurred while fetching account sweeps from backend");
            return ErrorCodeEnum.ERR_3001.setErrorCode(result);
        }
        return result;
    }

    public Result deleteSweep(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws IOException {
        Result result = new Result();
        Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];

        AccountSweepsDTO sweepsDto = JSONUtils.parse(new JSONObject(inputParams).toString(), AccountSweepsDTO.class);

        AccountSweepsBusinessDelegate accountSweepsBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(AccountSweepsBusinessDelegate.class);

        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);
        if (StringUtils.isBlank(customerId)) {
            LOG.error("Failed to fetch Customer ID");
            return ErrorCodeEnum.ERR_3005.setErrorCode(result);
        }
        if(validateInput(sweepsDto))
            return ErrorCodeEnum.ERR_3011.setErrorCode(result);

        if(validateAccount(sweepsDto.getPrimaryAccountNumber(), customerId, "Delete", true)) {
            LOG.error("User is not Authorized for the account number used");
            return ErrorCodeEnum.ERR_3008.setErrorCode(result);
        }
        if(validateAccount(sweepsDto.getSecondaryAccountNumber(), customerId, "Delete", false)) {
            LOG.error("User is not Authorized for the account number used");
            return ErrorCodeEnum.ERR_3008.setErrorCode(result);
        }

        if(featureActionIdCheckAccountLevel(customerId, Constants.ACCOUNT_SWEEP_DELETE,
                sweepsDto.getPrimaryAccountNumber(), CustomerSession.IsCombinedUser(customer)))
            return ErrorCodeEnum. ERR_3012.setErrorCode(result);

        try {
            AccountSweepsDTO deleteSweep = accountSweepsBusinessDelegate.deleteSweep(sweepsDto, request);
            deleteSweep.setPrimaryAccountNumber(sweepsDto.getPrimaryAccountNumber());
            deleteSweep.setSecondaryAccountNumber(sweepsDto.getSecondaryAccountNumber());
            JSONObject sweepObj = new JSONObject(deleteSweep);
            result = JSONToResult.convert(sweepObj.toString());
        }
        catch(Exception e){
            LOG.error("Unable to delete account sweeps"+e);
            return ErrorCodeEnum.ERR_2000.setErrorCode(result);
        }

        return result;
    }

    public Result editSweep(Map<String, Object> inputParams, DataControllerRequest request) {

        Result result=new Result();
        AccountSweepsDTO accountSweepsDTO;
        try {
            AccountSweepsDTO sweepsDto = JSONUtils.parse(new JSONObject(inputParams).toString(), AccountSweepsDTO.class);

            Map<String, Object> customer = CustomerSession.getCustomerMap(request);
            String customerId = CustomerSession.getCustomerId(customer);
            if(customerId == null)
                return ErrorCodeEnum.ERR_3005.setErrorCode(result);
            if(validateInput(sweepsDto))
                return ErrorCodeEnum.ERR_3011.setErrorCode(result);

            String previousAccountnumber;
            AccountSweepsDTO getSweepById=sweepsBusinessDelegate.getSweepByAccountId(sweepsDto.getPrimaryAccountNumber());
            if(getSweepById.getSecondaryAccountNumber().equals(sweepsDto.getSecondaryAccountNumber())) {
                previousAccountnumber = "";
                if(validateAccount(sweepsDto.getSecondaryAccountNumber(), customerId, "Edit", true))
                    return ErrorCodeEnum.ERR_3008.setErrorCode(result);
            }
            else {
                previousAccountnumber = getSweepById.getSecondaryAccountNumber();
                 if(validateAccount(sweepsDto.getSecondaryAccountNumber(), customerId, "Edit", false))
                     return ErrorCodeEnum.ERR_3008.setErrorCode(result);
            }

            if(!getSweepById.getPrimaryAccountNumber().equals(sweepsDto.getPrimaryAccountNumber()))
                return ErrorCodeEnum.ERR_3008.setErrorCode(result);

            // Validating accountId and account type against session info.
            if(validateAccount(sweepsDto.getPrimaryAccountNumber(), customerId, "Edit", true)) {
                LOG.error("User is not Authorized for the account number used");
                return ErrorCodeEnum.ERR_3008.setErrorCode(result);
            }

            if(accountStatusAndCurrencyCheck(sweepsDto.getPrimaryAccountNumber(),sweepsDto.getSecondaryAccountNumber(),customerId)) {
                 LOG.error("Either Currency code of both accounts not matching or Account Status of both account is not Active or Closure Pending");
                 return ErrorCodeEnum.ERR_3013.setErrorCode(result);
            }

            if(featureActionIdCheckAccountLevel(customerId, Constants.ACCOUNT_SWEEP_EDIT,
                    sweepsDto.getPrimaryAccountNumber(), CustomerSession.IsCombinedUser(customer)))
                return ErrorCodeEnum. ERR_3012.setErrorCode(result);

            if(validateDateAgainstBankDate(sweepsDto.getStartDate(), sweepsDto.getEndDate(), request))
                return ErrorCodeEnum.ERR_3010.setErrorCode(result);

            accountSweepsDTO = sweepsBusinessDelegate.editSweep(sweepsDto,request);

            accountSweepsDTO.setSecondaryAccountNumber(sweepsDto.getSecondaryAccountNumber());
            accountSweepsDTO.setPrimaryAccountNumber(sweepsDto.getPrimaryAccountNumber());
            if(!previousAccountnumber.equals(""))
                accountSweepsDTO.setPreviousSecondaryAccountNumber(previousAccountnumber);

            JSONObject sweepObj = new JSONObject(accountSweepsDTO);
            result = JSONToResult.convert(sweepObj.toString());
        }
        catch (Exception e){
            LOG.error("Error occured while creating sweep "+e);
            return ErrorCodeEnum.ERR_3007.setErrorCode(result);
        }
        return result;
    }

    private boolean validateInput(AccountSweepsDTO sweepsDto) {
        String startDate = sweepsDto.getStartDate();

        if(StringUtils.isEmpty(startDate))
            return true;

        if(sweepsDto.getBelowSweepAmount().equals("") && sweepsDto.getAboveSweepAmount().equals(""))
            return true;

        try {
            double belowSweepAmount = Double.parseDouble(
                    StringUtils.isNotEmpty(sweepsDto.getBelowSweepAmount()) ? sweepsDto.getBelowSweepAmount() : "0.0");
            double aboveSweepAmount = Double.parseDouble(
                    StringUtils.isNotEmpty(sweepsDto.getAboveSweepAmount()) ? sweepsDto.getAboveSweepAmount() : "0.0");
            if(belowSweepAmount < 0.0 || aboveSweepAmount < 0.0  || (aboveSweepAmount!=0.0 && belowSweepAmount>aboveSweepAmount)) {
                return true;
            }
        }
        catch (Exception e) {
            LOG.error("Error occurred while parsing and validating amount");
            return true;
        }

        if (sweepsDto.getPrimaryAccountNumber().equals(sweepsDto.getSecondaryAccountNumber())) {
            LOG.error("From Account Number is same as To Account Number");
            return true;
        }

        return StringUtils.isEmpty(sweepsDto.getPrimaryAccountNumber()) || StringUtils.isEmpty(
                sweepsDto.getSecondaryAccountNumber());
    }

    private SessionMap getInternalAccountsFromSession(String customerId) {
        return (SessionMap) MemoryManagerUtils.retrieve(INTERNAL_BANK_ACCOUNTS + customerId);
    }

    private Set<String> getAccounts(String customerId) {
        SessionMap internalAccountsMap = getInternalAccountsFromSession(customerId);
        if (StringUtils.isNotBlank(internalAccountsMap.toString())) {
            return new JSONObject(internalAccountsMap.toString()).keySet();
        }
        return new HashSet<>();
    }

    private boolean validateAccount(String accountId, String customerId,String operationType,boolean isPrimaryAccount) {
        SessionMap accountMap = getInternalAccountsFromSession(customerId);
        if(!accountMap.hasKey(accountId))
            return true;
        String isSweepCreated=accountMap.getAttributeValueForKey(accountId, "isSweepCreated");
        if(isSweepCreated!=null) {
            if (operationType.equals("Create") && isSweepCreated.equals("true"))
                return true;
            if (operationType.equals("Delete")   && isSweepCreated.equals("false"))
                return true;
            if(operationType.equals("Edit")){
                if(isPrimaryAccount && isSweepCreated.equals("false"))
                    return true;
                if(!isPrimaryAccount && isSweepCreated.equals("true"))
                    return true;
            }
        }
        String accountType = accountMap.getAttributeValueForKey(accountId, "accountType");
        return !accountType.equalsIgnoreCase("Savings") && !accountType.equalsIgnoreCase("Checking");
    }

    private boolean validateDateAgainstBankDate(String startDate, String endDate, DataControllerRequest request) throws ParseException {

        JsonObject dateObj = new JsonObject();
        try {
            String date = DBPServiceExecutorBuilder.builder().
                    withServiceId("TransactionObjects").
                    withObjectId("BankDate").
                    withOperationId("getBankDate").
                    withDataControllerRequest(request).
                    withRequestParameters(new HashMap<>()).
                    build().getResponse();
            JsonParser parser = new JsonParser();
            dateObj = parser.parse(date).getAsJsonObject().getAsJsonArray("date").get(0).getAsJsonObject();
        } catch (Exception e) {
            LOG.error("Error occurred while fetching bank date");
        }

        String currentDate = dateObj.has("currentWorkingDate") ? dateObj.get("currentWorkingDate").getAsString() : null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date bankDate = sdf2.parse(currentDate);
        boolean isValid = true;
        Date Tempdate;
        if (StringUtils.isNotBlank(startDate)) {
            Tempdate = sdf.parse(startDate);
            isValid = Tempdate.compareTo(bankDate) >= 0;
        }
        if (isValid && StringUtils.isNotBlank(endDate)) {
            Tempdate = sdf.parse(endDate);
            isValid=(Tempdate.compareTo(sdf.parse(startDate)))>0;
        }
        return !isValid;

    }
    private boolean featureActionIdCheckAccountLevel (String customerId,String requiredActionId,String accountId,boolean isCombined){
        return !authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(customerId, requiredActionId, accountId, isCombined);
    }
    private boolean accountStatusAndCurrencyCheck(String primaryAccountNumber,String secondaryAccountNumber,String customerId){

        SessionMap accountMap = getInternalAccountsFromSession(customerId);
        String accountStatus1=accountMap.getAttributeValueForKey(primaryAccountNumber, "accountStatus");
        String accountStatus2=accountMap.getAttributeValueForKey(secondaryAccountNumber, "accountStatus");
        String currencyCode1=accountMap.getAttributeValueForKey(primaryAccountNumber, "currencyCode");
        String currencyCode2=accountMap.getAttributeValueForKey(secondaryAccountNumber, "currencyCode");

        if((!accountStatus1.equals("ACTIVE") && !accountStatus1.equals("CLOSURE_PENDING")) ||
                (!accountStatus2.equals("ACTIVE") && !accountStatus2.equals("CLOSURE_PENDING")))
            return true;

        return !currencyCode1.equals(currencyCode2);

    }

}
