package com.temenos.infinity.api.wealth.preandpostprocessors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.preandpostprocessors.TransactTokenGenPreProcessor;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author vinayranjan.sharma
 *
 */

public class GetPersonalizedStrategyPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetPersonalizedStrategyPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			
			if (request.getParameter("wealthCoreFlag") != null
					&& request.getParameter("wealthCoreFlag").equalsIgnoreCase("true")) {
				result.addOpstatusParam("0");
				result.addHttpStatusCodeParam("200");
				result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				return false;
			} else {
				TransactTokenGenPreProcessor obj = new TransactTokenGenPreProcessor();
				obj.execute(inputMap, request, response, result);
				return true;
			}
		} catch (Exception e) {
			LOG.error("Error in GetPersonalizedStrategyPreProcessor-T24" + e);
			e.getMessage();
		}
		return false;
	}

}
