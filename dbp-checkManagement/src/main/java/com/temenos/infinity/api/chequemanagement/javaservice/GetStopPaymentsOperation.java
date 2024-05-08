package com.temenos.infinity.api.chequemanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.chequemanagement.dto.StopPayment;
import com.temenos.infinity.api.chequemanagement.resource.api.GetStopPaymentResource;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class GetStopPaymentsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetStopPaymentsOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        try {
            GetStopPaymentResource stopPaymentResource = DBPAPIAbstractFactoryImpl
                    .getResource(GetStopPaymentResource.class);
            StopPayment stopPaymentInputDTO = new StopPayment();
            String accountID = request.getParameter("accountID") != null ? request.getParameter("accountID") : "";
            String offset = request.getParameter("offset") != null ? request.getParameter("offset") : "";
            String amount = request.getParameter("amount") != null ? request.getParameter("amount") : "";
            String sortBy = request.getParameter("sortBy") != null ? request.getParameter("sortBy") : "";
            String limit = request.getParameter("limit") != null ? request.getParameter("limit") : "";
            String order = request.getParameter("order") != null ? request.getParameter("order") : "";
            stopPaymentInputDTO.setAccountID(accountID);
            stopPaymentInputDTO.setOffset(offset);
            stopPaymentInputDTO.setSortBy(sortBy);
            stopPaymentInputDTO.setAmount(amount);
            stopPaymentInputDTO.setLimit(limit);
            stopPaymentInputDTO.setOrder(order);
            
            Result result = stopPaymentResource.getStopPaymentRequests(stopPaymentInputDTO, request);
            return result;
        } catch (Exception e) { 
            LOG.error("Unable to get Stop Payment Requests from OMS: "+e);
            return ErrorCodeEnum.ERR_26013.setErrorCode(new Result());  
        }

    }

}
