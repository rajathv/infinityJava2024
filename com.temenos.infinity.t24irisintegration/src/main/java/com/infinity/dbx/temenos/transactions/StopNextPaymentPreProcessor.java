package com.infinity.dbx.temenos.transactions;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePreProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class StopNextPaymentPreProcessor extends TemenosBasePreProcessor{
	
	private static final Logger logger = LogManager.getLogger(StopNextPaymentPreProcessor.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean execute(HashMap params, DataControllerRequest request,
            DataControllerResponse response,
            Result result) throws Exception {

        try {
        	logger.debug("In " + StopNextPaymentPreProcessor.class.getName());
            super.execute(params, request, response, result);
            String Id = (String) params.get(TransactionConstants.PAYMENT_ID);
            if(Id.isEmpty()) {
            	result.addOpstatusParam(0);
    			result.addHttpStatusCodeParam(200);
    			result.addErrMsgParam("Missing ID");
    			return Boolean.FALSE;
    		}
    		else {
    			int index = Id.indexOf(".");
                params.put(TransactionConstants.ACCOUNT_ID,Id.substring(0, index));
    		}     
            
        } catch (Exception e) {
        	logger.error("Exception in " + StopNextPaymentPreProcessor.class.getName(), e);
        }

        return Boolean.TRUE;
    }
}
