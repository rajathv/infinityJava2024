package com.temenos.dbx.product.bulkpaymentservices.backenddelegate.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.bulkpaymentservices.backenddelegate.api.BulkPaymentFileBackendDelegate;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentFileDTO;

public class BulkPaymentFileBackendDelegateImpl implements BulkPaymentFileBackendDelegate {
	  
	private static final Logger LOG = LogManager.getLogger(BulkPaymentFileBackendDelegateImpl.class);
	CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerBusinessDelegate.class);

	@Override
	public BulkPaymentFileDTO uploadBulkPaymentFile(BulkPaymentFileDTO bulkPaymentFileDTO, DataControllerRequest dcr) {

		String serviceName = ServiceId.DBPNPBULKPAYMENTSERVICES;
		String operationName = OperationName.UPLOADFILE;
		Map<String, Object> requestParameters;
		BulkPaymentFileDTO bulkPaymentDTO = new BulkPaymentFileDTO();

		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(bulkPaymentFileDTO).toString(), String.class, Object.class);
		} catch (Exception e) {
			LOG.error("Error occured while fetching the input params", e);						
			return null;
		}

		String uploadResponse = null;
		try {
			uploadResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dcr.getHeaderMap()).
					withDataControllerRequest(dcr).
					build().getResponse();
			
			JSONObject response = new JSONObject(uploadResponse);			
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
		
		String serviceName = ServiceId.DBPNPBULKPAYMENTSERVICES;
		String operationName = OperationName.FETCHUPLOADEDFILES;
		String response;
		List<BulkPaymentFileDTO> files;
		
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(null).
					withRequestHeaders(dcr.getHeaderMap()).
					withDataControllerRequest(dcr).
					build().getResponse();
			JSONObject responseObj = new JSONObject(response);
            JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
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
