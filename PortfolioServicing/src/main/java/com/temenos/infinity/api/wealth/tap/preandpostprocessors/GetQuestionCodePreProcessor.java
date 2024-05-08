package com.temenos.infinity.api.wealth.tap.preandpostprocessors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.tap.preandpostprocessors.TAPTokenGenPreProcessor;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class GetQuestionCodePreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetQuestionCodePreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		LOG.info("==========> GetQuestionCodePreProcessor - Begin");
		try {
			if (request.getParameter(TemenosConstants.WEALTH_CORE) != null
					&& (request.getParameter(TemenosConstants.WEALTH_CORE).equalsIgnoreCase("TAP,Refinitiv")
							|| request.getParameter(TemenosConstants.WEALTH_CORE).equalsIgnoreCase("TAP"))) {
				
				TAPTokenGenPreProcessor obj = new TAPTokenGenPreProcessor();
				obj.execute(inputMap, request, response, result);
				//inputMap.put(request.getParameter(TemenosConstants.DEFCODE),request.getParameter(TemenosConstants.DEFCODEVAL));
				//request.addRequestParam_(TemenosConstants.DEFCODE,TemenosConstants.DEFCODEVAL);
				//inputMap.put(TemenosConstants.STATUSE,TemenosConstants.STATUSVAL);
				//request.addRequestParam_(TemenosConstants.STATUSE,TemenosConstants.STATUSVAL);
				LOG.info("==========> GetQuestionCodePreProcessor - End");
				return true;
				
			} else {
				result.addOpstatusParam("0");
				result.addHttpStatusCodeParam("200");
				result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				return false;
			}
		} catch (Exception e) {
			LOG.error("==========> Error in GetMyStrategyPreProcessor-TAP" + e);
			e.getMessage();
		}
		return false;
	}

}
