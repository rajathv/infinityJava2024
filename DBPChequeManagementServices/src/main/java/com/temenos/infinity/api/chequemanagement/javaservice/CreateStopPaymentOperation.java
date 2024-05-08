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
import com.temenos.infinity.api.chequemanagement.resource.api.CreateStopPaymentResource;

/**
 * 
 * @author smugesh
 * @version Java Service to create the stop payment request in order management
 *          micro services
 * 
 */
public class CreateStopPaymentOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CreateStopPaymentOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        try {
            CreateStopPaymentResource stopPaymentResource = DBPAPIAbstractFactoryImpl
                    .getResource(CreateStopPaymentResource.class);
            StopPayment stopPaymentDTO = constructPayload(request);

            Result result = stopPaymentResource.createStopPayment(stopPaymentDTO, request);
            return result;
        } catch (Exception e) {
            LOG.error("Unable to create order : " + e);
            return ErrorCodeEnum.ERR_26021.setErrorCode(new Result());
        }

    }

    public static StopPayment constructPayload(DataControllerRequest request) {

        StopPayment stopPaymentInput = new StopPayment();

        // Get input params from request object
        String accountId = request.getParameter("fromAccountNumber") != null ? request.getParameter("fromAccountNumber")
                : "";
        String validate = request.getParameter("validate") != null ? request.getParameter("validate") : "";
        String payeeName = request.getParameter("payeeName") != null ? request.getParameter("payeeName") : "";
        String checkNumber1 = request.getParameter("checkNumber1") != null ? request.getParameter("checkNumber1") : "";
        String checkNumber2 = request.getParameter("checkNumber2") != null ? request.getParameter("checkNumber2") : "";
        String checkReason = request.getParameter("checkReason") != null ? request.getParameter("checkReason") : "";
        String transactionsNotes = request.getParameter("transactionsNotes") != null
                ? request.getParameter("transactionsNotes") : "";
        String transactionType = request.getParameter("transactionType") != null
                ? request.getParameter("transactionType") : "";
        String checkDateOfIssue = request.getParameter("checkDateOfIssue") != null
                ? request.getParameter("checkDateOfIssue") : "";
        String amount = request.getParameter("amount") != null ? request.getParameter("amount") : "";

        stopPaymentInput.setFromAccountNumber(accountId);
        stopPaymentInput.setValidate(validate);
        stopPaymentInput.setPayeeName(payeeName);
        stopPaymentInput.setCheckNumber1(checkNumber1);
        stopPaymentInput.setCheckNumber2(checkNumber2);
        stopPaymentInput.setCheckReason(checkReason);
        stopPaymentInput.setTransactionsNotes(transactionsNotes);
        stopPaymentInput.setTransactionType(transactionType);
        stopPaymentInput.setCheckDateOfIssue(checkDateOfIssue);
        stopPaymentInput.setAmount(amount);
        return stopPaymentInput;
    }

}
