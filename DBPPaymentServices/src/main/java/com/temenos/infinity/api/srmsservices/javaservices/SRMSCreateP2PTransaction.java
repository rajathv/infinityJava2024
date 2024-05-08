package com.temenos.infinity.api.srmsservices.javaservices;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.transactionservices.dto.P2PTransactionDTO;
import com.temenos.infinity.api.srmsservices.constants.SRMSConstants;
import com.temenos.infinity.api.srmstransactions.config.TransformSRMSRequest;

public class SRMSCreateP2PTransaction implements SRMSConstants, JavaService2 {

	private static final Logger LOG = LogManager.getLogger(SRMSCreateP2PTransaction.class);
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> requestparameters = (Map<String, Object>) inputArray[1];

		P2PTransactionDTO p2pTransactionDTO = null;

		// Convert DBPDto to SRMS DTO to filter out the unwanted fields to be
		// sent to SRMS
		TransformSRMSRequest transformObject = TransformSRMSRequest.getInstance();
		Map<String, Object> srmsInputParams = transformObject.P2PTransfer(requestparameters, request);
		Result result = new Result();
		try {
			LOG.debug("Inter bank Account Create Order Request :" + srmsInputParams.toString());
			String srmsresponse = Util.createOrder(srmsInputParams, request);
			LOG.debug("Inter bank Account Create Order Response :" + srmsresponse);
			p2pTransactionDTO = JSONUtils.parse(srmsresponse, P2PTransactionDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to create p2p transaction: ", e);
			return new Result();
		} catch (Exception e) {
			LOG.error("Caught exception at create p2p transaction: ", e);
			return new Result();
		}

		try {
			JSONObject resObj = new JSONObject(p2pTransactionDTO);
			String res = resObj.toString();
			result = JSONToResult.convert(res);

		} catch (Exception e) {
			LOG.error("Error occured while fetching the input params: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\"" + e.getMessage() + "\"}"));
			return result;
		}
		return result;
	}
}
