package com.infinity.dbx.temenos.externalAccounts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbx.BasePostProcessor;
import com.kony.dbx.util.CommonUtils;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetAllDomesticBankAccountPostProcessor extends BasePostProcessor implements ExternalAccountsConstants{
	private static final Logger logger = LogManager.getLogger(GetOtherBankAccountPostProcessor.class);
	@Override
	  public Result execute(Result result, DataControllerRequest request,
	      DataControllerResponse response) throws Exception {
		
		String errmsg = CommonUtils.getParamValue(result, PARAM_ERR_MSG);
		if(!"".equalsIgnoreCase(errmsg) && errmsg.contains("No records")) {
			return TemenosUtils.getEmptyResult(DS_EXTERNAL_ACCOUNTS);
		}
		
		Dataset externalAccounts = result.getDatasetById(DS_EXTERNAL_ACCOUNTS);
		List<Record> sepaRecords = new ArrayList<Record>();
		
		if(externalAccounts != null) {			
			// Get all records
			List<Record> records = externalAccounts.getAllRecords();
			ListIterator<Record> iter = records.listIterator();
			
			SimpleDateFormat t24Format = new SimpleDateFormat(T24_FORMAT);
			SimpleDateFormat dbxDateFormat = new SimpleDateFormat(Constants.DATE_YYYYMMDD);
			
			HashMap<String, ExternalAccount> sepaBeneficiaries = new HashMap<String, ExternalAccount>();

			while(iter.hasNext()) {
				
				// Get the next record from the iterator
				Record record = iter.next();
				
				String  transactionType = CommonUtils.getParamValue(record, TRANSACTION_TYPE);
				String createdOn = CommonUtils.getParamValue(record, CREATED_ON);
				String accountNumber = CommonUtils.getParamValue(record, ACCOUNT_NUMBER);

				if(TRANSACTION_TYPE_BCIB.equalsIgnoreCase(transactionType) || TRANSACTION_TYPE_ACIB.equalsIgnoreCase(transactionType)) {
					
					if(!"".equalsIgnoreCase(createdOn)) {
						Date date = new Date();
						date = (Date) t24Format.parse(createdOn);
						
						record.getParamByName(CREATED_ON).setValue(dbxDateFormat.format(date));
					}
					record.addStringParam(IBAN, accountNumber);
					record.addParam(new Param(IS_INTERNATIONAL_ACCOUNT, Constants.FALSE, Constants.PARAM_DATATYPE_STRING));
					if(TRANSACTION_TYPE_ACIB.equalsIgnoreCase(transactionType)) {
						record.addParam(new Param(IS_SAMEBANK_ACCOUNT, Constants.TRUE, Constants.PARAM_DATATYPE_STRING));
					}else {
						record.addParam(new Param(IS_SAMEBANK_ACCOUNT, Constants.FALSE, Constants.PARAM_DATATYPE_STRING));
					}
					record.addParam(new Param(IS_VERIFIED, Constants.TRUE, Constants.PARAM_DATATYPE_STRING));
					
					sepaRecords.add(record);
					
					ExternalAccount externalAccount = TemenosUtils.copyToExtrenalAccount(ExternalAccount.class, record);
					sepaBeneficiaries.put(externalAccount.getAccountNumber(), externalAccount);
				}
			}
			
			Gson gson = new Gson();

			String gsonBeneficiaries = gson.toJson(sepaBeneficiaries);		
			
			TemenosUtils temenosUtils = TemenosUtils.getInstance();
			
			temenosUtils.insertIntoSession(TemenosConstants.SESSION_ATTRIB_OTHERBANK_ACCOUNT, gsonBeneficiaries, request);
			
		}
		Result finalResult = new Result();
		Dataset domesticDS = new Dataset(DS_EXTERNAL_ACCOUNTS);
		domesticDS.addAllRecords(sepaRecords);
		finalResult.addDataset(domesticDS);
		
		return finalResult;
	}

}
