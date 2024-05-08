package com.temenos.infinity.api.wealth.tap.preandpostprocessors;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealth.tap.preandpostprocessors.GetOrderDetailsPreProcessor;
import com.temenos.infinity.api.wealthservices.tap.preandpostprocessors.TAPTokenGenPreProcessor;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class GetOrderDetailsPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetOrderDetailsPreProcessor.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {

			// added for sorting
			String orderBy = inputMap.get(TemenosConstants.ORDERBY) != null
					? inputMap.get(TemenosConstants.ORDERBY).toString()
					: "";
			String orderType = inputMap.get(TemenosConstants.SORTTYPE) != null
					? inputMap.get(TemenosConstants.SORTTYPE).toString().toLowerCase()
					: "";
			if (orderBy.length() > 0) {
				orderBy = orderBy.concat("%20").concat(orderType);
				inputMap.put(TemenosConstants.ORDERBY, orderBy);
			} else {
				inputMap.put(TemenosConstants.ORDERBY, "");
			}
			TAPTokenGenPreProcessor obj = new TAPTokenGenPreProcessor();
			obj.execute(inputMap, request, response, result);
			inputMap.put("minStatusE", "Cancelled");
			inputMap.put("maxStatusE", "Accounted");
			return true;
		} catch (Exception e) {
			LOG.error("Error in GetOrderDetailsPreProcessor" + e);
			e.getMessage();
		}
		return false;
	}

}
