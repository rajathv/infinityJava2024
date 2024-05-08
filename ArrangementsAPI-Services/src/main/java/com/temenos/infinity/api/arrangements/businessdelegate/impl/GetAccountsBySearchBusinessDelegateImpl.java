package com.temenos.infinity.api.arrangements.businessdelegate.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.infinity.api.arrangements.backenddelegate.api.GetAccountsArrangementsExperienceAPIBackendDelegate;
import com.temenos.infinity.api.arrangements.businessdelegate.api.GetAccountsBySearchBusinessDelegate;
import com.temenos.infinity.api.arrangements.dto.ArrangementsDTO;
import com.temenos.infinity.api.commons.exception.ApplicationException;

public class GetAccountsBySearchBusinessDelegateImpl implements GetAccountsBySearchBusinessDelegate {
    private static final Logger LOG = LogManager.getLogger(GetAccountsBySearchBusinessDelegateImpl.class);

    @Override
    public List<ArrangementsDTO> getAccounts(String filterKey, String filterValue, String authToken)
            throws ApplicationException {

        List<ArrangementsDTO> arrangementsDTO = null;

        try {
            GetAccountsArrangementsExperienceAPIBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
                    .getBackendDelegate(GetAccountsArrangementsExperienceAPIBackendDelegate.class);

            arrangementsDTO = backendDelegate.getArrangements(filterKey, filterValue, authToken);
        } catch (ApplicationException e) {
            LOG.error(e.getMessage());
            throw e;
        }
        return arrangementsDTO;
    }

    @Override
    public JSONObject getAccountsByCoreCustomerIdSearch(String coreCustomerId, String authToken)
            throws ApplicationException {
        GetAccountsArrangementsExperienceAPIBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(GetAccountsArrangementsExperienceAPIBackendDelegate.class);
        return backendDelegate.getCoreCustomerArrangements(coreCustomerId, authToken);
    }

}
