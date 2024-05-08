package com.temenos.infinity.api.wealthOrder.tap.preandpostprocessors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.tap.preandpostprocessors.TAPTokenGenPreProcessor;
import com.temenos.infinity.api.wealthOrder.tap.preandpostprocessors.ConfirmOrdersPreProcessor;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class ConfirmOrdersPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(ConfirmOrdersPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			
			if (request.getParameter(TemenosConstants.CASES) != null) {
				request.addRequestParam_(TemenosConstants.CASES, request.getParameter(TemenosConstants.CASES));
			}
			
			String validate = request.getParameter(TemenosConstants.VALIDATEONLY) != null
					? request.getParameter(TemenosConstants.VALIDATEONLY).toString()
					: "";
			if (validate.equals("")) {
				return true;
			} else {
				result.addOpstatusParam("0");
				result.addHttpStatusCodeParam("200");
				result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error in ConfirmOrdersPreProcessor" + e);
			e.getMessage();
		}
		return false;
	}

}
