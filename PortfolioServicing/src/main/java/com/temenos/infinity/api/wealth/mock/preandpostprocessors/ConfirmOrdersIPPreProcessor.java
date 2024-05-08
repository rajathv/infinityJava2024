package com.temenos.infinity.api.wealth.mock.preandpostprocessors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */
public class ConfirmOrdersIPPreProcessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(ConfirmOrdersIPPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {

			if (request.getParameter(TemenosConstants.INF_WLTH_CORE) != null
					&& request.getParameter(TemenosConstants.INF_WLTH_CORE).equalsIgnoreCase("Mock")) {
				return true;

			} else {
				result.addOpstatusParam("0");
				result.addHttpStatusCodeParam("200");
				result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				return false;
			}

		} catch (Exception e) {
			LOG.error("Error in ConfirmChangeStratPreProcessor" + e);
			e.getMessage();
			return false;
		}
	}

}
