package com.temenos.infinity.api.accountsweeps.businessdelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.accountsweeps.backenddelegate.api.AccountSweepsBackendDelegate;
import com.temenos.infinity.api.accountsweeps.businessdelegate.api.AccountSweepsBusinessDelegate;
import com.temenos.infinity.api.accountsweeps.dto.AccountSweepsDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

/**
 * @author naveen.yerra
 */
public class AccountSweepsBusinessDelegateImpl implements AccountSweepsBusinessDelegate {

    private final AccountSweepsBackendDelegate sweepsBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(AccountSweepsBackendDelegate.class);
    private static final Logger LOG = LogManager.getLogger(AccountSweepsBusinessDelegateImpl.class);
    public AccountSweepsDTO getSweepByAccountId(String accountId) {
        return sweepsBackendDelegate.getSweepByAccountId(accountId);
    }
    public AccountSweepsDTO createSweep(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request){
        AccountSweepsDTO sweepsDTO=null;
        try {
            sweepsDTO = sweepsBackendDelegate.createSweepAtBackEnd(accountSweepsDTO,request);
        }
        catch (Exception e){
            LOG.error("Exception occured while creating record at backend",e);
        }
        return sweepsDTO;
    }

    @Override
    public List<AccountSweepsDTO> getAllAccountSweeps(Set<String> accounts) {
        return sweepsBackendDelegate.getAllSweepsFromBackend(accounts);
    }

    public AccountSweepsDTO deleteSweep(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request) {
       return sweepsBackendDelegate.deleteSweepAtBackEnd(accountSweepsDTO,request);

    }

    public AccountSweepsDTO editSweep(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request) {
        return sweepsBackendDelegate.editSweep(accountSweepsDTO, request);
    }

}
