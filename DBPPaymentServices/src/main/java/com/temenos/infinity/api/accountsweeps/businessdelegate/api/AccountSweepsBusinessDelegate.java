package com.temenos.infinity.api.accountsweeps.businessdelegate.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.accountsweeps.dto.AccountSweepsDTO;

import java.util.List;
import java.util.Set;

/**
 * @author naveen.yerra
 */
public interface AccountSweepsBusinessDelegate extends BusinessDelegate {
    AccountSweepsDTO getSweepByAccountId(String accountId);
    AccountSweepsDTO createSweep(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request);
    List<AccountSweepsDTO> getAllAccountSweeps(Set<String> accounts);
    AccountSweepsDTO editSweep(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request);
    AccountSweepsDTO deleteSweep(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request);
}
