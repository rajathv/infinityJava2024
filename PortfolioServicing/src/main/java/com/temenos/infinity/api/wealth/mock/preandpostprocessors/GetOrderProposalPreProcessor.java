package com.temenos.infinity.api.wealth.mock.preandpostprocessors;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author shreya.singh
 *
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

public class GetOrderProposalPreProcessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetOrderProposalPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			if (request.getParameter(TemenosConstants.WEALTH_CORE) != null
					&& request.getParameter(TemenosConstants.WEALTH_CORE).equalsIgnoreCase("Mock")) {
					                return true;

					 

					            } else {
					                result.addOpstatusParam("0");
					                result.addHttpStatusCodeParam("200");
					                result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
					                return false;
					            }

					 

					        } catch (Exception e) {
					            LOG.error("Error in GetOrderProposalPreProcessor" + e);
					            e.getMessage();
					            return false;
					        }
}}