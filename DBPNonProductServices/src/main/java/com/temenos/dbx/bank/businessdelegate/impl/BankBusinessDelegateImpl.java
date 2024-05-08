package com.temenos.dbx.bank.businessdelegate.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.temenos.dbx.bank.businessdelegate.api.BankBusinessDelegate;
import com.temenos.dbx.transaction.dto.BankDTO;

public class BankBusinessDelegateImpl implements BankBusinessDelegate {

	public static final String BANK_DATASET_NAME = "bankbranchview";
	private static final Logger LOG = LogManager.getLogger(BankBusinessDelegateImpl.class);

	@Override
	public BankDTO getBank() {
    	try {
    		Map<String, Object> inputParams = new HashMap<>();
        	inputParams.put(DBPUtilitiesConstants.FILTER,
    				DBPUtilitiesConstants.BRANCH_TYPE + DBPUtilitiesConstants.EQUAL + "MainBranch");
        	JsonObject bank = ServiceCallHelper.invokeServiceAndGetJson(inputParams, null, URLConstants.BANK_BRANCH_GET);
        	if(!HelperMethods.hasErrorOpstatus(bank)) {
				return getBankDTO(bank);
			}
    	} catch(Exception e) {
    		LOG.error("Error while getting branch info", e);
    	}
    	
    	return null;
	}
	
	private BankDTO getBankDTO(JsonObject bank) throws IOException {
		if(JSONUtil.hasKey(bank, getBankDatasetName())) {
			JsonArray bankDS = bank.getAsJsonArray(getBankDatasetName());
			if(bankDS.size() > 0) {
				return JSONUtils.parse(bankDS.get(0).toString(), BankDTO.class);
			}
		}
		return null;
	}

	public String getBankDatasetName() {
		return BANK_DATASET_NAME;
	}

}
