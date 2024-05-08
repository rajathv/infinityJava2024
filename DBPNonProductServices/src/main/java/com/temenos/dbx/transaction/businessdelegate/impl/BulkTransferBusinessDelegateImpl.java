package com.temenos.dbx.transaction.businessdelegate.impl;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.kony.constants.OperationName;
import com.kony.constants.ServiceId;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.transaction.businessdelegate.api.BulkTransferBusinessDelegate;

public class BulkTransferBusinessDelegateImpl implements BulkTransferBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(BulkTransferBusinessDelegateImpl.class);

	
	@Override
	public JSONArray createBulkTransfer(Map<String, Object> requestParameters, DataControllerRequest dataControllerRequest) {
		
		JSONArray resArray = new JSONArray();
		String serviceName = ServiceId.BULK_TRANSFER_SERVICE;
		String operationName = OperationName.BULK_TRANSFER_OPERATION;

		String bulkTransferResponse = null;

		try {
			LOG.debug("In Business Delegate Method");
			bulkTransferResponse = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null, operationName,
					requestParameters, null, dataControllerRequest);
			JSONObject bulkTransferJSON = new JSONObject(bulkTransferResponse);
			resArray = bulkTransferJSON.getJSONArray("LoopDataset");
			return resArray;
		} catch (JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while creating the bulktransfer: ", jsonExp);
			return null;
		} catch (Exception exp) {
			LOG.error("Excpetion occured while creating the bulktransfer: ", exp);
			return null;
		}
	}

}
