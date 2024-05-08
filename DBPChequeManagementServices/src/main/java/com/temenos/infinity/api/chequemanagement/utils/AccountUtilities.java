package com.temenos.infinity.api.chequemanagement.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.temenos.infinity.api.chequemanagement.dto.SessionMap;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class AccountUtilities {

    private static final Logger LOG = LogManager.getLogger(AccountUtilities.class);
    private FabricRequestManager fabricRequestManager = null;
    private String customerId = null;
    private static final String INTERNAL_BANK_ACCOUNTS = "INTERNAL_BANK_ACCOUNTS";

    public boolean validateInternalAccount(String customerId, String accountNumber) {
        if (StringUtils.isBlank(accountNumber)) {
            return false;
        }
        SessionMap internalAccountsMap = getInternalBankAccountsFromSession(customerId);

        if (null == internalAccountsMap || internalAccountsMap.isEmpty()) {
            LOG.debug("validateInternalAccount - internalAccountsMap Null / Empty");
            return false;
        }

        LOG.debug("validateInternalAccount: " + internalAccountsMap.toString());
        return internalAccountsMap.hasKey(accountNumber);
    }

    public SessionMap getInternalBankAccountsFromSession(String customerId) { 
        SessionMap internalAccntsMap = (SessionMap) MemoryManagerUtils
                .retrieve(AccountUtilities.INTERNAL_BANK_ACCOUNTS + customerId);
        return internalAccntsMap;
    }

}
