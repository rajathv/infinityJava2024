/**
 * 
 */
package com.temenos.infinity.api.wealthOrder.mockdata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;

/**
 *
 * (INFO) Prepares Result object from the mock data returned by the Util.
 * 
 * @author himaja.sridhar
 *
 */
public class GetDocumentsMock implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetDocumentsMock.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		JSONObject response1 = new JSONObject();

		try {
			JSONArray docData = new JSONArray();
			response1.put("documentDetails", docData);
			response1.put("opstatus", "0");
			response1.put("httpStatusCode", "200");
			return Utilities.constructResultFromJSONObject(response1);
		} catch (Exception e) {
			LOG.error("Caught exception at invoke of getDocumentsMock: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
	}

}
