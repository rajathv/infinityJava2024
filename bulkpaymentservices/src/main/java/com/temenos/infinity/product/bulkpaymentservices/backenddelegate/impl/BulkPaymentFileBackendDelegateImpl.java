package com.temenos.infinity.product.bulkpaymentservices.backenddelegate.impl;

import java.util.List;
import java.util.Map;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.infinity.product.bulkpaymentservices.backenddelegate.api.BulkPaymentFileBackendDelegate;
import com.temenos.infinity.product.bulkpaymentservices.dto.BulkPaymentFileDTO;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;

public class BulkPaymentFileBackendDelegateImpl implements BulkPaymentFileBackendDelegate {
	  
	private static final Logger LOG = LogManager.getLogger(BulkPaymentFileBackendDelegateImpl.class);
	private static final String UPLOAD_FILE_ERROR_MESSAGE = "errmsg_uploadFile";
    private static final String STORE_FILE_ERROR_MESSAGE = "errmsg_storeBulkFile";
    private static final String ERROR_MESSAGE = "errmsg";
	CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerBusinessDelegate.class);

	@Override
	public BulkPaymentFileDTO uploadBulkPaymentFile(BulkPaymentFileDTO bulkPaymentFileDTO, DataControllerRequest dcr) {

		String BULKPAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue("BULKPAYMENT_BACKEND");
		Map<String, Object> requestParameters;
		BulkPaymentFileDTO bulkPaymentDTO = new BulkPaymentFileDTO();
		String operationName = TemenosConstants.OP_UPLOAD_BULK_FILE;
		String serviceName;
		if(BULKPAYMENT_BACKEND!=null && BULKPAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			serviceName = "MOCKBulkPaymentAPIs";
		}
		else {
			serviceName = TemenosConstants.SERVICE_T24OS_BULK_PAYMENTS;
		}

		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(bulkPaymentFileDTO).toString(), String.class,
					Object.class);
		} catch (Exception e) {
			LOG.error("Error occured while fetching the input params", e);
			return null;
		}

		String uploadResponse = null;
		try {
			uploadResponse = DBPServiceExecutorBuilder.
					builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dcr.getHeaderMap()).
					withDataControllerRequest(dcr).
					build().getResponse();
			
			JSONObject response = new JSONObject(uploadResponse);

			if (response.has(UPLOAD_FILE_ERROR_MESSAGE) && StringUtils.isNotEmpty(response.getString(UPLOAD_FILE_ERROR_MESSAGE))) {	
				response.remove(ERROR_MESSAGE);
				response.put(ERROR_MESSAGE, response.getString(UPLOAD_FILE_ERROR_MESSAGE));
			}
			if (response.has(STORE_FILE_ERROR_MESSAGE) && StringUtils.isNotEmpty(response.getString(STORE_FILE_ERROR_MESSAGE))) {
				response.remove(ERROR_MESSAGE);
				response.put(ERROR_MESSAGE, response.getString(STORE_FILE_ERROR_MESSAGE));
			}
			bulkPaymentDTO = JSONUtils.parse(response.toString(), BulkPaymentFileDTO.class);

		} catch (JSONException e) {
			LOG.error("Unable to Upload Bulk Payment File", e);
			return null;
		} catch (Exception e) {
			LOG.error("Unable to store the file at Infinity", e);
			return null;
		}

		return bulkPaymentDTO;
	}

	@Override
	public List<BulkPaymentFileDTO> fetchBulkPaymentSampleFiles(DataControllerRequest dcr) {
		
		String serviceName = ServiceId.DBPNPBULKPAYMENTSERVICES;
		String operationName = OperationName.GETSAMPLEFILES;
		String response;
		List<BulkPaymentFileDTO> files;
		
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(null).
					build().getResponse();
			JSONObject responseObj = new JSONObject(response);
            JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
            files = JSONUtils.parseAsList(jsonArray.toString(), BulkPaymentFileDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Error in Downloading Bulk Payment sample Files", e);			
			return null;
		} 
		catch (Exception e) {
			LOG.error("Error occured in fetchBulkPaymentSampleFiles", e);			
			return null;
		}
		
		return files;
	}
	
	@Override
	public List<BulkPaymentFileDTO> fetchBulkPaymentUploadedFiles(String fromDate, String toDate, DataControllerRequest dcr) {


		String BULKPAYMENT_BACKEND= EnvironmentConfigurationsHandler.getValue("BULKPAYMENT_BACKEND");

		String serviceName;
		String operationName = TemenosConstants.OP_FETCH_UPLOADED_BULK_FILES;

		if(BULKPAYMENT_BACKEND!=null && BULKPAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			serviceName="MOCKBulkPaymentAPIs";
		}
		else{
			serviceName=TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		}
		String response;
		List<BulkPaymentFileDTO> files;
		String lastUpdateDate = null;
		Map<String, Object> headers = dcr.getHeaderMap();
		
		if(StringUtils.isNotEmpty(fromDate) && StringUtils.isNotEmpty(toDate)) 
		{	
			lastUpdateDate = String.join(" ", fromDate, toDate);
			headers.put("lastUpdateDate", lastUpdateDate);
		}
		else
		{
			headers.remove(lastUpdateDate);
		}	
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(null).
					withRequestHeaders(headers).
					withDataControllerRequest(dcr).
					build().getResponse();
			JSONObject responseObj = new JSONObject(response);
			JSONArray jsonArray = TemenosUtils.getFirstOccuringArray(responseObj);
            files = JSONUtils.parseAsList(jsonArray.toString(), BulkPaymentFileDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Error in Downloading Bulk Payment uploaded Files", e);			
			return null;
		} 
		catch (Exception e) {
			LOG.error("Error occured in fetchBulkPaymentUploadedFiles", e);			
			return null;
		}
		
		return files;
	}
}
