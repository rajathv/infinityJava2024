package com.temenos.infinity.api.wealthOrder.preandpostprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthOrder.config.WealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class GetUserFavouriteInstrumentsPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetUserFavouriteInstrumentsPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {
			String favInstrumentIds = result.getParamValueByName("favInstrumentIds").toString();
			if (favInstrumentIds != null && favInstrumentIds.length() > 0) {
				String[] favInstrumentIdsArr = favInstrumentIds.trim().split("@");
				String instrumentId = "";
				for (String s : favInstrumentIdsArr) {
					instrumentId = instrumentId + s.trim().split("~")[0].trim() + " " ;
				}
				result.addParam(TemenosConstants.INSTRUMENTID, instrumentId.trim());
				result.addParam("T24Favourite", "true");
				result.addParam("T24Instrumentids", instrumentId.trim());
			}

		} catch (Exception e) {

			LOG.error("Error while invoking OrderBlotterPostProcessor - "
					+ WealthAPIServices.WEALTH_GETUSERFAVOURITES.getOperationName() + "  : " + e);
		}

		return result;
	}
}
