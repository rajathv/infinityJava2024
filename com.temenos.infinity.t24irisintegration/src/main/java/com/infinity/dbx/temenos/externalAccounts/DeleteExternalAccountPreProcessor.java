package com.infinity.dbx.temenos.externalAccounts;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePreProcessor;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class DeleteExternalAccountPreProcessor extends TemenosBasePreProcessor implements ExternalAccountsConstants {

	private static final Logger logger = LogManager.getLogger(DeleteExternalAccountPreProcessor.class);

	public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {

		// Run TemenosBasePreProcessor
		super.execute(params, request, response, result);

		String beneficiaryId = CommonUtils.getParamValue(params, ID);
		beneficiaryId = StringUtils.isNotBlank(beneficiaryId) ? beneficiaryId : CommonUtils.getParamValue(params, "id");
		params.put(ID, beneficiaryId);
		if (!"".equalsIgnoreCase(beneficiaryId)) {
			return true;
		}

		String accountNumber = CommonUtils.getParamValue(params, ACCOUNT_NUMBER);
		String IBANNumber = CommonUtils.getParamValue(params, IBAN);
		TemenosUtils temenosUtils = TemenosUtils.getInstance();
		logger.error("account number " + accountNumber);
		if (!"".equalsIgnoreCase(IBANNumber)) {
			HashMap<String, ExternalAccount> domesticAccounts = temenosUtils
					.getExternalAccountsMapFromCache(TemenosConstants.SESSION_ATTRIB_OTHERBANK_ACCOUNT, request);
			logger.error("domestic accounts " + domesticAccounts);
			if (domesticAccounts != null && domesticAccounts.get(IBANNumber) != null) {
				ExternalAccount externalAccount = domesticAccounts.get(IBANNumber);
				if (externalAccount != null) {
					String Id = externalAccount.getId();
					params.put(ID, Id);
					return true;
				}
			}
		}
		if (!"".equalsIgnoreCase(accountNumber)) {
			HashMap<String, ExternalAccount> internationalAccounts = temenosUtils
					.getExternalAccountsMapFromCache(TemenosConstants.SESSION_ATTRIB_INTERNATIONAL_ACCOUNT, request);
			logger.error("international accounts " + internationalAccounts);
			if (internationalAccounts != null && internationalAccounts.get(accountNumber) != null) {
				ExternalAccount externalAccount = internationalAccounts.get(accountNumber);
				if (externalAccount != null) {
					String Id = externalAccount.getId();
					params.put(ID, Id);
					return true;
				}
			}
			HashMap<String, ExternalAccount> sameBankAccounts = temenosUtils
					.getExternalAccountsMapFromCache(TemenosConstants.SESSION_ATTRIB_DOMESTIC_ACCOUNT, request);
			logger.error("same bank accounts " + sameBankAccounts);
			ExternalAccount externalAccount = sameBankAccounts.get(accountNumber);
			if (externalAccount != null && externalAccount.getVersionNumber() != null) {
				String versionNumber = externalAccount.getVersionNumber();
				params.put(VERSION_NUMBER, versionNumber);
				String Id = externalAccount.getId();
				params.put(ID, Id);
				return true;
			}
			HashMap<String, ExternalAccount> domesticAccounts = temenosUtils
					.getExternalAccountsMapFromCache(TemenosConstants.SESSION_ATTRIB_OTHERBANK_ACCOUNT, request);
			logger.error("domestic accounts " + domesticAccounts);
			if (domesticAccounts != null && domesticAccounts.get(accountNumber) != null) {
				ExternalAccount domesticExternalAccount = domesticAccounts.get(accountNumber);
				if (domesticExternalAccount != null) {
					String Id = domesticExternalAccount.getId();
					params.put(ID, Id);
					return true;
				}
			}
		}
		result.addErrMsgParam("Something went wrong. Please try again later");
		return false;
	}

}
