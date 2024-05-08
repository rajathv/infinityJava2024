package com.kony.dbputilities.customersecurityservices.preprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.dbputilities.mfa.preprocessors.TransactionsMFAPreProcessor;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.postprocessors.ObjectServicesConstants;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;

public class CreateBulkBillPaymentsObjectServicePreProcessor
		implements ObjectServicePreProcessor, ObjectServicesConstants, ObjectProcessorTask {

	private static final Logger logger = LogManager.getLogger(CreateBulkBillPaymentsObjectServicePreProcessor.class);

	@Override
	public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager,
			FabricRequestChain requestChain) throws Exception {
		PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
		PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
		JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
		requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
		JsonObject customParams = new JsonObject();
		String statusId = PARAM_SID_EVENT_SUCCESS;
		String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
		String eventType = PARAM_MAKE_TRANSFER;
		String eventSubType = PARAM_BULK_BILL_PAYMENT;
		String producer = "";

		String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS, requestManager);

		if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
			statusId = "SID_EVENT_FAILURE";
		}

		logger.debug("ENABLE_EVENTS=" + enableEvents);

		if (!enableEvents.equals("") && !ObjectServiceHelperMethods.hasKey(responsePayload, "referenceId")
				&& enableEvents.equals("true") && ObjectServiceHelperMethods.hasKey(responsePayload, "state")) {
			EventsDispatcher.dispatch(requestManager, responseManager, eventType, eventSubType + "_MFA", producer,
					statusId, null, customerid, customParams);
		}

	}

	@Override
	public boolean process(FabricRequestManager requestManager, FabricResponseManager responseManager)
			throws Exception {
		boolean status = true;
		try {
			status = new TransactionsMFAPreProcessor().process(requestManager, responseManager);
			execute(requestManager, responseManager, null);
		} catch (Exception e) {
			logger.error("Exception occured in create bulk bill Pay pre processor", e);

		}
		return status;
	}

}
