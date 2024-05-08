package com.kony.dbputilities.transservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.dbutil.QueryFormer;
import com.kony.dbputilities.dbutil.SqlQueryEnum;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.sortutil.SortRecordByParamValue;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetSentP2PTransactions implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetSentP2PTransactions.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        Map<String, String> user = HelperMethods.getUserFromIdentityService(dcRequest);

        if (preProcess(inputParams, dcRequest, result, user)) {
            String url = URLConstants.ACCOUNT_TRANSACTION_PROC;
            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest), url);
        }
        if (!HelperMethods.hasError(result)) {
            if (!HelperMethods.hasRecords(result)) {
                result.addDataset(new Dataset());
            }
            result.getAllDatasets().get(0).setId("accountransactionview");
        }
        if (HelperMethods.hasRecords(result)) {
            postProcess(dcRequest, inputParams, result);
        }
        return result;
    }

    private void postProcess(DataControllerRequest dcRequest, Map<String, String> inputParams, Result result)
            throws HttpCallException {
        Map<String, String> accountTypes = getAccountTypes(dcRequest, inputParams.get("countryCode"));
        String accountId = inputParams.get("accountID");
        List<Record> transactions = result.getAllDatasets().get(0).getAllRecords();
        for (Record transaction : transactions) {
            updateAmount(transaction, accountId);
            updateFromAccountDetails(dcRequest, transaction, accountTypes);
            updateToAccountDetails(dcRequest, transaction, accountTypes);
            updatePayeeNickName(dcRequest, transaction);
            updatePayPersonDetails(dcRequest, transaction);
            updateDateFormat(transaction);
        }
        if ("nickName".equalsIgnoreCase(inputParams.get("sortBy"))) {
            List<Record> sortedList = sortBy(transactions, "name", inputParams.get("order"));
            String id = result.getAllDatasets().get(0).getId();
            Dataset ds = new Dataset(id);
            ds.addAllRecords(sortedList);
            result.addDataset(ds);
        }
    }

    private List<Record> sortBy(List<Record> transactions, String fieldName, String order) {
        boolean asc = true;
        List<Record> mutable = new ArrayList<>();
        mutable.addAll(transactions);
        if (StringUtils.isNotBlank(order) && "desc".equals(order)) {
            asc = false;
        }
        Collections.sort(mutable, new SortRecordByParamValue(fieldName, asc));
        return mutable;
    }

    private void updateDateFormat(Record transaction) {
        String scheduledDate = HelperMethods.getFieldValue(transaction, "scheduledDate");
        String transactionDate = HelperMethods.getFieldValue(transaction, "transactionDate");
        try {
            if (StringUtils.isNotBlank(scheduledDate)) {
                transaction.addParam(new Param("scheduledDate",
                        HelperMethods.convertDateFormat(scheduledDate, "yyyy-MM-dd'T'hh:mm:ss'Z'"), "String"));
            }
            if (StringUtils.isNotBlank(transactionDate)) {
                transaction.addParam(new Param("transactionDate",
                        HelperMethods.convertDateFormat(transactionDate, "yyyy-MM-dd'T'hh:mm:ssXXX"), "String"));
            }
        } catch (Exception e) {
        }
    }

    private void updatePayeeNickName(DataControllerRequest dcRequest, Record transaction) throws HttpCallException {
        String payeeId = HelperMethods.getFieldValue(transaction, "Payee_Id");
        if (StringUtils.isNotBlank(payeeId)) {
            String filter = "id" + DBPUtilitiesConstants.EQUAL + payeeId;
            Result payee = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.PAYEE_GET);
            if (HelperMethods.hasRecords(payee)) {
                transaction.addParam(new Param("payeeNickName", HelperMethods.getFieldValue(payee, "nickName"),
                        DBPUtilitiesConstants.STRING_TYPE));
            }
        }
    }

    private void updatePayPersonDetails(DataControllerRequest dcRequest, Record transaction) {
        try {
            String payPersonId = HelperMethods.getFieldValue(transaction, "Person_Id");
            if (StringUtils.isNotBlank(payPersonId)) {

                String filter = "id" + DBPUtilitiesConstants.EQUAL + payPersonId;
                Result payPerson = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                        URLConstants.PAYPERSON_GET);
                if (HelperMethods.hasRecords(payPerson)) {
                    Record person = payPerson.getAllDatasets().get(0).getRecord(0);
                    transaction.addParam(new Param("phone", HelperMethods.getFieldValue(person, "phone"),
                            DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("name", HelperMethods.getFieldValue(person, "name"),
                            DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("email", HelperMethods.getFieldValue(person, "email"),
                            DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("firstName", HelperMethods.getFieldValue(person, "firstName"),
                            DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("nickName", HelperMethods.getFieldValue(person, "nickName"),
                            DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("secondaryEmail",
                            HelperMethods.getFieldValue(person, "secondaryEmail"), DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("secondaryEmail2",
                            HelperMethods.getFieldValue(person, "secondaryEmail2"), DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("secondoryPhoneNumber",
                            HelperMethods.getFieldValue(person, "secondoryPhoneNumber"),
                            DBPUtilitiesConstants.STRING_TYPE));
                    transaction.addParam(new Param("secondaryPhoneNumber2",
                            HelperMethods.getFieldValue(person, "secondaryPhoneNumber2"),
                            DBPUtilitiesConstants.STRING_TYPE));
                }
            }
        } catch (HttpCallException e) {
            LOG.error(e.getMessage());
        }
    }

    private Map<String, String> getAccountTypes(DataControllerRequest dcRequest, String countryCode)
            throws HttpCallException {
        String filter = null;
        if (StringUtils.isNotBlank(countryCode)) {
            filter = "countryCode" + DBPUtilitiesConstants.EQUAL + countryCode;
        }
        Result accountTypes = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                URLConstants.ACCOUNTTYPE_GET);
        Map<String, String> accountTypeMap = new HashMap<>();
        List<Record> types = accountTypes.getAllDatasets().get(0).getAllRecords();
        for (Record type : types) {
            accountTypeMap.put(HelperMethods.getFieldValue(type, "TypeID"),
                    HelperMethods.getFieldValue(type, "TypeDescription"));
        }
        return accountTypeMap;
    }

    private void updateToAccountDetails(DataControllerRequest dcRequest, Record transaction,
            Map<String, String> accountTypes) throws HttpCallException {
        String toAccountNum = HelperMethods.getFieldValue(transaction, "toAccountNumber");
        if (StringUtils.isNotBlank(toAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + toAccountNum;
            Result toAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            String typeId = HelperMethods.getFieldValue(toAccount, "Type_id");
            transaction
                    .addParam(new Param("toAccountType", accountTypes.get(typeId), DBPUtilitiesConstants.STRING_TYPE));
            String accountName = HelperMethods.getFieldValue(toAccount, "accountName");
            transaction.addParam(new Param("toAccountName", accountName, DBPUtilitiesConstants.STRING_TYPE));
        }
    }

    private void updateFromAccountDetails(DataControllerRequest dcRequest, Record transaction,
            Map<String, String> accountTypes) throws HttpCallException {
        String toAccountNum = HelperMethods.getFieldValue(transaction, "fromAccountNumber");
        if (StringUtils.isNotBlank(toAccountNum)) {
            String filter = "Account_id" + DBPUtilitiesConstants.EQUAL + toAccountNum;
            Result toAccount = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
                    URLConstants.ACCOUNTS_GET);
            String typeId = HelperMethods.getFieldValue(toAccount, "Type_id");
            transaction.addParam(
                    new Param("fromAccountType", accountTypes.get(typeId), DBPUtilitiesConstants.STRING_TYPE));
            String accountName = HelperMethods.getFieldValue(toAccount, "accountName");
            String nickName = HelperMethods.getFieldValue(toAccount, "nickName");
            transaction.addParam(new Param("fromAccountName", accountName, DBPUtilitiesConstants.STRING_TYPE));
            transaction.addParam(new Param("fromAccountNickName", nickName, DBPUtilitiesConstants.STRING_TYPE));
        }
    }

    private void updateAmount(Record transaction, String accountId) {
        String account1 = HelperMethods.getFieldValue(transaction, "fromAccountNumber");
        String amount = HelperMethods.getFieldValue(transaction, "amount");
        String fee = HelperMethods.getFieldValue(transaction, "fee");
        if (StringUtils.isNotBlank(fee)) {
            amount = new BigDecimal(amount).subtract(new BigDecimal(fee)).toPlainString();
        }
        if (StringUtils.isNotBlank(accountId) && accountId.equals(account1)) {
            amount = "-" + amount;
        }
        transaction.addParam(new Param("amount", amount, DBPUtilitiesConstants.STRING_TYPE));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean preProcess(Map inputParams, DataControllerRequest dcRequest, Result result,
            Map<String, String> user) {
        inputParams.put("countryCode", user.get("countryCode"));
        String sortBy = (String) inputParams.get(DBPUtilitiesConstants.SORTBY);
        String order = (String) inputParams.get(DBPUtilitiesConstants.ORDER);
        String offset = (String) inputParams.get("offset");
        String limit = (String) inputParams.get("limit");
        String jdbcUrl=QueryFormer.getDBType(dcRequest);
        
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "createdDate";
        }
        if (StringUtils.isBlank(order)) {
            order = "desc";
        }
        StringBuilder filter = new StringBuilder();

        String userId = user.get("user_id");
        filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_GetTranTypeDetails_ByCustomerId").getQuery().replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2", userId));
        
        /*if (HelperMethods.isBusinessUserType(user.get("customerType"))) {
			
			 * filter.append("select t1.*,tt.description as transactionType from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".transaction t1 left join " + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
			 * dcRequest) + ".transactiontype tt on (t1.Type_id = tt.id)");
			 * filter.append("where (t1.FromAccountNumber in (select account_id from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".customeraccounts where Customer_id = '" + userId + "'))");
			 
        	filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_GetTranTypeDetails_ByCustomerId").getQuery().replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2", userId));
        } else {
			
			 * filter.append("select t1.*,tt.description as transactionType from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".transaction t1 left join " + URLFinder.getPathUrl(URLConstants.SCHEMA_NAME,
			 * dcRequest) + ".transactiontype tt on (t1.Type_id = tt.id)");
			 * filter.append("where (t1.FromAccountNumber in (select account_id from " +
			 * URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest) +
			 * ".accounts where User_id = '" + userId + "'))");
			 
        	filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_GetTranTypeDetails_ByUserId").getQuery().replace("?1", URLFinder.getPathUrl(URLConstants.SCHEMA_NAME, dcRequest)).replace("?2", userId));
        }*/

		/* filter.append(" and tt.description = 'P2P'"); */
        if (!"nickName".equalsIgnoreCase(sortBy)) {
            filter.append(" order by " + sortBy + " " + order);
        }

        if (StringUtils.isNotBlank(offset) && StringUtils.isNotBlank(limit)) {
        	filter.append(SqlQueryEnum.valueOf(jdbcUrl+"_Limit").getQuery().
        			replace("?1", offset).replace("?2", limit));
        }

        inputParams.put("transactions_query", filter.toString());

        return true;
    }
}
