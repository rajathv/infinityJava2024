package com.temenos.infinity.api.docmanagement.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class DownloadAttachmentValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(DownloadAttachmentValidationTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if (!isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		LOG.debug("data access control is enabled");
		String userId = null;
		String fileID = null;
		boolean status = true;
		if (StringUtils.isNotBlank(fabricRequestManager.getQueryParamsHandler().getParameter("fileID"))
				&& StringUtils.isNotBlank(fabricRequestManager.getQueryParamsHandler().getParameter("Auth_Token"))) {
			userId = fabricRequestManager.getQueryParamsHandler().getParameter("customerId");
			fileID = fabricRequestManager.getQueryParamsHandler().getParameter("fileID");
			LOG.debug("userId from client:" + userId);
			
				JsonObject attachments = new JsonObject();
				Map<String, String> dataMapFilter = new HashMap<String, String>();
				Map<String, String> headersMap = new HashMap<String, String>();
				if (isDMSIntegrationEnabled()) {
					dataMapFilter.put("userId", userId);
					attachments = HelperMethods.callApiJson(fabricRequestManager, dataMapFilter, headersMap,
							URLConstants.DOCUMENT_STORAGE_SEARCH);
				} else {
					dataMapFilter.put(DBPUtilitiesConstants.FILTER, "paymentFileID" + DBPUtilitiesConstants.EQUAL + fileID);
					dataMapFilter.put(DBPUtilitiesConstants.SELECT, "paymentFileID");
					attachments = HelperMethods.callApiJson(fabricRequestManager, dataMapFilter, headersMap,
							URLConstants.PAYMENT_FILES_GET);
				}
				if (attachments.isJsonNull()
						|| (attachments.has("opstatus") && !attachments.get("opstatus").getAsString().equals("0"))) {
					status = false;
					LOG.debug("Attachments are null or returned opstatus is non-zero");
				} else if (attachments.has("opstatus") && attachments.get("opstatus").getAsString().equals("0")) {
					if (isDMSIntegrationEnabled()) {
						String documentsList = attachments.get("documentsList").toString();
						if (StringUtils.isBlank(documentsList)) {
							status = false;
							LOG.debug("File with given fileID is not available in retrieved attachments with opstatus as zero");
						}
					} else {
						JsonArray paymentFiles = attachments.get("paymentfiles").getAsJsonArray();
						if (paymentFiles.size() == 0) {
							status = false;
							LOG.debug("File with given fileID is not available in retrieved attachments with opstatus as zero");
						}
					}
				}
			
		} else {
			status = false;
			LOG.debug("One or more required input parameters are missing - Auth token, customerId, fileID");
		}
		if (!status) {
			JsonObject resPayload = null;
			if (HelperMethods.isJsonNotNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
				resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
			}
			resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
			fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
		}
		return status;
	}

	public boolean isDACEnabled() {
		ServicesManager serviceManager;
		try {
			serviceManager = ServicesManagerHelper.getServicesManager();
			ConfigurableParametersHelper configurableParametersHelper = serviceManager
					.getConfigurableParametersHelper();
			String isDacEnabled = configurableParametersHelper.getServerProperty("DAC_ENABLED");
			return StringUtils.isBlank(isDacEnabled)
					|| BooleanUtils.toBoolean(configurableParametersHelper.getServerProperty("DAC_ENABLED"));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return true;
	}

	public boolean isDMSIntegrationEnabled() {
		ServicesManager serviceManager;
		try {
			serviceManager = ServicesManagerHelper.getServicesManager();
			ConfigurableParametersHelper configurableParametersHelper = serviceManager
					.getConfigurableParametersHelper();
			String isDMSIntegrationEnabled = configurableParametersHelper.getServerProperty("DMS_INTEGRATION_ENABLED");
			if(StringUtils.isBlank(isDMSIntegrationEnabled))
				return false;
			return BooleanUtils.toBoolean(configurableParametersHelper.getServerProperty("DMS_INTEGRATION_ENABLED"));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return true;
	}
}
