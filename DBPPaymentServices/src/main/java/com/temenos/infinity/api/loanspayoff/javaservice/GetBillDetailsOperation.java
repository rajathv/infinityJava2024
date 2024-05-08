package com.temenos.infinity.api.loanspayoff.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.loanspayoff.constants.ErrorCodeEnum;
import com.temenos.infinity.api.loanspayoff.resource.api.LoansPayoffResource;
import com.temenos.infinity.api.loanspayoff.resource.impl.LoansPayoffResourceImpl;

/**
 * 
 * @author suryaacharans
 * @version Java Service end point to create Loan Simulation for a given arrangement
 */
public class GetBillDetailsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(LoansPayoffResourceImpl.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        try {
            LoansPayoffResource billDetailsResource = DBPAPIAbstractFactoryImpl.getResource(LoansPayoffResource.class);
            String arrangementId = request.getParameter("arrangementId");
            String billType = request.getParameter("billType");
            String paymentDate = request.getParameter("paymentDate");
            request.addRequestParam_("flowType", "PostLogin");
            String backendToken = TokenUtils.getT24AuthToken(request);
            Result result = billDetailsResource.getBillDetails(arrangementId, billType, paymentDate, backendToken);
            return result;
        } catch (Exception e) {
            LOG.error(e);
            return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());

        }

    }

}
