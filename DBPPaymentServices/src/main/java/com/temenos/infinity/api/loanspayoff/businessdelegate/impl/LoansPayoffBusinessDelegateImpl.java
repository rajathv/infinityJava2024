package com.temenos.infinity.api.loanspayoff.businessdelegate.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.api.loanspayoff.backenddelegate.api.LoansPayoffAPIBackendDelegate;
import com.temenos.infinity.api.loanspayoff.businessdelegate.api.LoansPayoffBusinessDelegate;
import com.temenos.infinity.api.loanspayoff.constants.ErrorCodeEnum;
import com.temenos.infinity.api.loanspayoff.dto.BillDetails;
import com.temenos.infinity.api.loanspayoff.dto.LoanDTO;

public class LoansPayoffBusinessDelegateImpl implements LoansPayoffBusinessDelegate {
    private static final Logger LOG = LogManager.getLogger(LoansPayoffBusinessDelegateImpl.class);

    @Override
    public LoanDTO createSimulation(String arrangementId, String activityId, String productId, String effectiveDate,
            String backendToken) throws ApplicationException {

        try {
            LoansPayoffAPIBackendDelegate backendDelegate =
                    DBPAPIAbstractFactoryImpl.getBackendDelegate(LoansPayoffAPIBackendDelegate.class);
            LoanDTO output = backendDelegate.createSimulationT24(arrangementId, activityId, productId, effectiveDate,
                    backendToken);
            return output;
        } catch (Exception e) {
            LOG.error(e);
            throw new ApplicationException(ErrorCodeEnum.ERR_20400);
        }

    }

    @Override
    public BillDetails getBillDetails(String arrangementId, String billType, String paymentDate, String backendToken)
            throws ApplicationException {
        try {
            LoansPayoffAPIBackendDelegate backendDelegate =
                    DBPAPIAbstractFactoryImpl.getBackendDelegate(LoansPayoffAPIBackendDelegate.class);
            BillDetails output = backendDelegate.getBillDetailsT24(arrangementId, billType, paymentDate, backendToken);
            return output;
        } catch (Exception e) {
            LOG.error(e);
            throw new ApplicationException(ErrorCodeEnum.ERR_20400);
        }
    }
}