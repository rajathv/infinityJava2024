package com.kony.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class UpdateApprovalMatrixObjectServicePostProcessor
		implements ObjectServicePostProcessor, ObjectServicesConstants, ObjectProcessorTask {

	private static final Logger logger = LogManager.getLogger(UpdateApprovalMatrixObjectServicePostProcessor.class);

	@Override
	public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

		try {

			PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
			JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
			JsonObject customParams = new JsonObject();
			String opstatus = "";
			String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
			String eventType = PARAM_APPROVAL_MATRIX;
			String eventSubType = PARAM_UPDATE_APPROVAL_MATRIX;
			String producer = "ApprovalMatrix/operations/ApprovalMatrix/updateApprovalMatrix";
			String statusId = PARAM_SID_EVENT_FAILURE;
			String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
					requestManager);

			if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_OP_STATUS)) {
				opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
			}

			if (opstatus.equals("0") && !ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
				statusId = PARAM_SID_EVENT_SUCCESS;
			}

			logger.debug("ENABLE_EVENTS=" + enableEvents);

			if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {
				try {
					ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
							eventType, eventSubType, producer, statusId, null, customerid, customParams));
				} catch (Exception ex) {
					logger.error("Exception Occured while invoking objectServiceHelperMethods", ex);
				}
			}

		} catch (Exception ex) {
			logger.error("Exception occured in ObjectService PostProcessor while Updating Approval Matrix=", ex);
		}

	}

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			execute(fabricRequestManager, fabricResponseManager);
		} catch (Exception e) {
			logger.error("exception occured in execute method of objectservice", e);

		}
		return true;
	}

}
