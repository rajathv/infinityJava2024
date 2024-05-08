package com.temenos.infinity.api.wealthOrder.preandpostprocessors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) Builds the Result in the desired format for the Refinitiv service.
 * 
 * @author balaji.k.k
 */

public class getInstrumentMinimalPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(getInstrumentMinimalPreProcessor.class);

	@SuppressWarnings({ "rawtypes" })
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			if (request.getParameter(TemenosConstants.APPLICATION) != null
					&& (request.getParameter(TemenosConstants.APPLICATION).equalsIgnoreCase("DX")
							|| request.getParameter(TemenosConstants.APPLICATION).equalsIgnoreCase("DXMaster"))) {
				result.addOpstatusParam("0");
				result.addHttpStatusCodeParam("200");
				result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				return false;
				
			} else {
				//TransactTokenGenPreProcessor obj = new TransactTokenGenPreProcessor();
				//obj.execute(inputMap, request, response, result);
				return true;
			}

		} catch (Exception e) {
			LOG.error("Error in getInstrumentMinimalPreProcessor" + e);
			e.getMessage();
			return false;
		}
	}

}
