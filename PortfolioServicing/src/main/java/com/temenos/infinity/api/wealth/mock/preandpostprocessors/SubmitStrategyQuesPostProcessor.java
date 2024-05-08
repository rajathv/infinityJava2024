package com.temenos.infinity.api.wealth.mock.preandpostprocessors;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

public class SubmitStrategyQuesPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(SubmitStrategyQuesPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {
			
			String portfolioId = request.getParameterValues(TemenosConstants.PORTFOLIOID)[0];
			
			if ((portfolioId.equalsIgnoreCase("100777-4")) ||(portfolioId.equalsIgnoreCase("100777-5"))) {
		    result.addParam("questionnaireHistoCode","question000-1");
			result.addParam("score", "28.0");
			result.addOpstatusParam("0");
			result.addHttpStatusCodeParam("200");
			result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
			
			}
			
			else  {
				
				LOG.error("Invalid request");
				result.addParam("status", "Failure");
				result.addParam("error", "Unauthorized Access");
				return result;
			}
			
		} catch (Exception e) {
			e.getMessage();
			LOG.error("Error while invoking SubmitStrategyQuesPostProcessor - "
					+ PortfolioWealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}
		return result;
	}

}