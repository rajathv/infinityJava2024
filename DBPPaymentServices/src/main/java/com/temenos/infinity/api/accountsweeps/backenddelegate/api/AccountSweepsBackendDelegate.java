package com.temenos.infinity.api.accountsweeps.backenddelegate.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.accountsweeps.dto.AccountSweepsDTO;

import java.util.List;
import java.util.Set;

/**
 * @author naveen.yerra
 */
public interface AccountSweepsBackendDelegate extends BackendDelegate {
    AccountSweepsDTO getSweepByAccountId(String accountId);
    AccountSweepsDTO createSweepAtBackEnd (AccountSweepsDTO sweepsDTO, DataControllerRequest request);
    List<AccountSweepsDTO> getAllSweepsFromBackend(Set<String> accounts);

    AccountSweepsDTO editSweep(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request);

    AccountSweepsDTO deleteSweepAtBackEnd(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request);
}
