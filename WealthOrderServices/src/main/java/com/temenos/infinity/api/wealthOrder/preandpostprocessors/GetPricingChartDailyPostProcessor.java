/**
 * 
 */
package com.temenos.infinity.api.wealthOrder.preandpostprocessors;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * (INFO) Returns the Result object constructed.
 * 
 * @author himaja.sridhar
 *
 */
public class GetPricingChartDailyPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		return result;
	}

}
